// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor

import androidx.annotation.StringRes
import org.prauga.toolkit.core.model.BatteryHealth

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
