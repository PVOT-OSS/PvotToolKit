// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.batterymonitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.common.coroutines.ApplicationScope

/** Brings the monitor back after the two events that silently kill it. */
@AndroidEntryPoint
class BatteryMonitorRestartReceiver : BroadcastReceiver() {
    @Inject lateinit var controller: BatteryMonitorController

    @Inject @ApplicationScope lateinit var scope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> restore()
        }
    }

    private fun restore() {
        val pending = goAsync()
        scope.launch {
            try {
                controller.restore()
            } finally {
                pending.finish()
            }
        }
    }
}
