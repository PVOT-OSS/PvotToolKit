// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import androidx.annotation.StringRes
import org.prauga.toolkit.core.model.BatteryHealth
import org.prauga.toolkit.core.model.ThermalStatus
import org.prauga.toolkit.feature.monitor.R

@StringRes
internal fun BatteryHealth.labelRes(): Int =
    when (this) {
        BatteryHealth.GOOD -> R.string.monitor_health_good
        BatteryHealth.OVERHEAT -> R.string.monitor_health_overheat
        BatteryHealth.DEAD -> R.string.monitor_health_dead
        BatteryHealth.OVER_VOLTAGE -> R.string.monitor_health_over_voltage
        BatteryHealth.COLD -> R.string.monitor_health_cold
        BatteryHealth.FAILURE -> R.string.monitor_health_failure
    }

@StringRes
internal fun ThermalStatus.labelRes(): Int? =
    when (this) {
        ThermalStatus.NONE -> R.string.monitor_thermal_none
        ThermalStatus.LIGHT -> R.string.monitor_thermal_light
        ThermalStatus.MODERATE -> R.string.monitor_thermal_moderate
        ThermalStatus.SEVERE -> R.string.monitor_thermal_severe
        ThermalStatus.CRITICAL -> R.string.monitor_thermal_critical
        ThermalStatus.EMERGENCY -> R.string.monitor_thermal_emergency
        ThermalStatus.SHUTDOWN -> R.string.monitor_thermal_shutdown
        ThermalStatus.UNKNOWN -> null
    }
