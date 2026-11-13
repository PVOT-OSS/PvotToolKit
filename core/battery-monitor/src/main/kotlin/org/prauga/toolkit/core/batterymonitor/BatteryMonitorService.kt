// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.batterymonitor

import android.Manifest
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.battery.BatterySession
import org.prauga.toolkit.core.common.coroutines.Dispatcher
import org.prauga.toolkit.core.common.coroutines.ToolkitDispatchers
import org.prauga.toolkit.core.datastore.BatterySessionLocalDataSource
import timber.log.Timber

private const val NOTIFICATION_ID = 1001

private const val SAMPLE_INTERVAL_MILLIS = 60_000L

/** Accumulates battery statistics for as long as the user leaves it on. */
@AndroidEntryPoint
class BatteryMonitorService : Service() {

    @Inject internal lateinit var store: BatterySessionLocalDataSource

    @Inject
    @Dispatcher(ToolkitDispatchers.IO)
    internal lateinit var dispatcher: CoroutineDispatcher

    private lateinit var scope: CoroutineScope
    private lateinit var sampler: BatterySampler
    private lateinit var notification: BatteryNotification
    private var loop: Job? = null

    private val transitions =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                scope.launch { sampleOnce() }
            }
        }

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(SupervisorJob() + dispatcher)
        sampler = BatterySampler(this)
        notification = BatteryNotification(this)
        notification.ensureChannel()

        ContextCompat.registerReceiver(
            this,
            transitions,
            IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
            },
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startInForeground()
        if (loop?.isActive != true) {
            loop =
                scope.launch {
                    while (isActive) {
                        sampleOnce()
                        delay(SAMPLE_INTERVAL_MILLIS)
                    }
                }
        }
        return START_STICKY
    }

    private suspend fun sampleOnce() {
        val sample = sampler.sample() ?: return
        val session = store.record(sample) ?: return
        updateNotification(session)
    }

    private fun startInForeground() {
        val built = notification.build(null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                built,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            ServiceCompat.startForeground(this, NOTIFICATION_ID, built, 0)
        }
    }

    /** A denied POST_NOTIFICATIONS silences the readout but not the service. */
    private fun updateNotification(session: BatterySession) {
        if (!canPostNotifications()) return
        val built = notification.build(session)
        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, built)
        } catch (denied: SecurityException) {
            Timber.w(denied, "Battery readout not posted")
        }
    }

    private fun canPostNotifications(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        unregisterReceiver(transitions)
        loop?.cancel()
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
