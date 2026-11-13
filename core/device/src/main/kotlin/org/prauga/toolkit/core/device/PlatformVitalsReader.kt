// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.content.Context
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Environment
import android.os.PowerManager
import android.os.StatFs
import android.os.SystemClock
import androidx.core.content.getSystemService
import java.io.File
import org.prauga.toolkit.core.model.NetworkVitals
import org.prauga.toolkit.core.model.SleepStats
import org.prauga.toolkit.core.model.StorageVitals
import org.prauga.toolkit.core.model.ThermalStatus
import timber.log.Timber

/** The parts the framework gives up without a permission. */
internal class PlatformVitalsReader(context: Context) {
    private val appContext = context.applicationContext

    /** Milliseconds since boot, replacing the denied /proc/uptime. */
    fun uptimeMillis(): Long = SystemClock.elapsedRealtime()

    /** Deep sleep versus awake, from the gap between the two clocks. */
    fun sleepStats(): SleepStats =
        SleepStats(
            elapsedMillis = SystemClock.elapsedRealtime(),
            awakeMillis = SystemClock.uptimeMillis(),
        )

    /** Remaining charge in microamp-hours; scales to a capacity estimate by level. */
    fun chargeCounterMicroAmpHours(): Int? {
        val manager = appContext.getSystemService<BatteryManager>() ?: return null
        return manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER).takeIf {
            it != Int.MIN_VALUE && it > 0
        }
    }

    fun thermalStatus(): ThermalStatus {
        val power = appContext.getSystemService<PowerManager>() ?: return ThermalStatus.UNKNOWN
        return when (power.currentThermalStatus) {
            PowerManager.THERMAL_STATUS_NONE -> ThermalStatus.NONE
            PowerManager.THERMAL_STATUS_LIGHT -> ThermalStatus.LIGHT
            PowerManager.THERMAL_STATUS_MODERATE -> ThermalStatus.MODERATE
            PowerManager.THERMAL_STATUS_SEVERE -> ThermalStatus.SEVERE
            PowerManager.THERMAL_STATUS_CRITICAL -> ThermalStatus.CRITICAL
            PowerManager.THERMAL_STATUS_EMERGENCY -> ThermalStatus.EMERGENCY
            PowerManager.THERMAL_STATUS_SHUTDOWN -> ThermalStatus.SHUTDOWN
            else -> ThermalStatus.UNKNOWN
        }
    }

    fun storage(): StorageVitals? =
        try {
            val stat = StatFs(Environment.getDataDirectory().absolutePath)
            val total = stat.blockCountLong * stat.blockSizeLong
            val free = stat.availableBlocksLong * stat.blockSizeLong
            if (total <= 0) null else StorageVitals(total - free, total, dataFileSystem())
        } catch (e: IllegalArgumentException) {
            Timber.w(e, "statfs failed on the data partition")
            null
        }

    fun network(): NetworkVitals? {
        val rx = TrafficStats.getTotalRxBytes()
        val tx = TrafficStats.getTotalTxBytes()
        if (rx == TrafficStats.UNSUPPORTED.toLong() || tx == TrafficStats.UNSUPPORTED.toLong()) {
            return null
        }
        return NetworkVitals(rx, tx)
    }

    /** /proc/mounts is one of the few /proc files still readable. */
    private fun dataFileSystem(): String? =
        File("/proc/mounts")
            .readTextOrNull()
            ?.lineSequence()
            ?.map { it.split(' ') }
            ?.firstOrNull { it.size > 2 && it[1] == "/data" }
            ?.get(2)

    fun suBinaryPresent(): Boolean =
        SU_PATHS.any { path ->
            try {
                File(path).exists()
            } catch (e: SecurityException) {
                Timber.d(e, "Denied %s", path)
                false
            }
        }

    private companion object {
        val SU_PATHS =
            listOf(
                "/system/bin/su",
                "/system/xbin/su",
                "/sbin/su",
                "/su/bin/su",
                "/debug_ramdisk/su",
                "/vendor/bin/su",
            )
    }
}
