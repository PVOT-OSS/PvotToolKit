// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import org.prauga.toolkit.core.model.BatteryHealth
import org.prauga.toolkit.core.model.BatteryVitals
import org.prauga.toolkit.core.model.CpuLoad
import org.prauga.toolkit.core.model.MemoryVitals
import org.prauga.toolkit.core.model.Vitals

private const val TENTHS_PER_DEGREE = 10f

@Singleton
class SystemVitalsReader @Inject constructor(@ApplicationContext context: Context) {
    private val appContext = context.applicationContext
    private val platform = PlatformVitalsReader(appContext)
    private val cpu = CpuReader()
    private val designCapacityMah: Int? by lazy { BatteryCapacity.designMah(appContext) }

    /** @param load supplied by the caller, which owns the two spaced samples. */
    fun read(load: CpuLoad? = null): Vitals =
        Vitals(
            cpu = cpu.read(),
            load = load,
            memory = readMemory(),
            battery = readBattery(),
            storage = platform.storage(),
            network = platform.network(),
            thermal = platform.thermalStatus(),
            uptimeMillis = platform.uptimeMillis(),
            sleep = platform.sleepStats(),
        )

    private fun readMemory(): MemoryVitals? {
        val manager = appContext.getSystemService<ActivityManager>() ?: return null
        val info = ActivityManager.MemoryInfo()
        manager.getMemoryInfo(info)
        if (info.totalMem <= 0) return null
        return MemoryVitals(
            usedBytes = (info.totalMem - info.availMem).coerceAtLeast(0),
            totalBytes = info.totalMem,
        )
    }

    /** The battery broadcast is sticky, so a null receiver returns it immediately. */
    private fun readBattery(): BatteryVitals? {
        val intent =
            appContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                ?: return null

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level < 0 || scale <= 0) return null

        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val tenthsC = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, Int.MIN_VALUE)

        return BatteryVitals(
            percent = (level * 100) / scale,
            temperatureC = tenthsC.takeIf { it != Int.MIN_VALUE }?.let { it / TENTHS_PER_DEGREE },
            charging =
                status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL,
            currentMicroAmps = readCurrentMicroAmps(),
            chargeCounterMicroAmpHours = platform.chargeCounterMicroAmpHours(),
            health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1).toBatteryHealth(),
            technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY),
            voltageMillivolts =
                intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1).takeIf { it > 0 },
            cycleCount = readCycleCount(intent),
            designCapacityMah = designCapacityMah,
        )
    }

    /** Added in Android 14; older releases keep it behind the same denied sysfs node. */
    private fun readCycleCount(intent: Intent): Int? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            intent.getIntExtra(BatteryManager.EXTRA_CYCLE_COUNT, -1).takeIf { it > 0 }
        } else {
            null
        }

    /** /sys/class/power_supply is denied, so this is the only route to draw. */
    private fun readCurrentMicroAmps(): Int? {
        val manager = appContext.getSystemService<BatteryManager>() ?: return null
        return manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).takeIf {
            it != Int.MIN_VALUE && it != 0
        }
    }

    private fun Int.toBatteryHealth(): BatteryHealth? =
        when (this) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.FAILURE
            else -> null
        }
}
