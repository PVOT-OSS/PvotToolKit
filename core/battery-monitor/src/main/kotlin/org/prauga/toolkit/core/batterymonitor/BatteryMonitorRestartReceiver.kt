// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.batterymonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Brings the monitor back after the two events that silently kill it. */
@AndroidEntryPoint
class BatteryMonitorRestartReceiver : BroadcastReceiver() {
    @Inject lateinit var store: BatterySessionStore

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> restartIfEnabled(context.applicationContext)
        }
    }

    private fun restartIfEnabled(context: Context) {
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                if (store.enabled.first()) {
                    BatteryMonitorService.start(context)
                }
            } catch (_: Exception) {} finally {
                pending.finish()
            }
        }
    }
}
