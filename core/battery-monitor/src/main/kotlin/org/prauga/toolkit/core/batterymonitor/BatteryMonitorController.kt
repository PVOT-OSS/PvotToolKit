// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.batterymonitor

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import org.prauga.toolkit.core.datastore.BatterySessionLocalDataSource
import timber.log.Timber

/** The one place that turns the monitor on or off; the stored preference is the source of truth. */
@Singleton
class BatteryMonitorController
@Inject
internal constructor(
    @ApplicationContext private val context: Context,
    private val store: BatterySessionLocalDataSource,
) {
    val enabled: Flow<Boolean> = store.enabled

    suspend fun setEnabled(enabled: Boolean) {
        if (!store.setEnabled(enabled)) return
        if (enabled) start() else stop()
    }

    suspend fun restore() {
        if (store.enabled.first()) start()
    }

    private fun start() {
        try {
            ContextCompat.startForegroundService(context, serviceIntent())
        } catch (refused: IllegalStateException) {
            Timber.w(refused, "Platform refused to start the battery monitor")
        }
    }

    private fun stop() {
        context.stopService(serviceIntent())
    }

    private fun serviceIntent(): Intent = Intent(context, BatteryMonitorService::class.java)
}
