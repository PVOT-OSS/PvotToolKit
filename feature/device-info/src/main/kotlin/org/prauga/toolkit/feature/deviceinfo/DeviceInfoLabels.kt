// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import org.prauga.toolkit.core.model.BatteryHealth
import org.prauga.toolkit.core.model.CameraFacing
import org.prauga.toolkit.core.model.CameraHardwareLevel
import org.prauga.toolkit.core.model.HdrType

@StringRes
internal fun BatteryHealth.labelRes(): Int =
    when (this) {
        BatteryHealth.GOOD -> R.string.device_info_health_good
        BatteryHealth.OVERHEAT -> R.string.device_info_health_overheat
        BatteryHealth.DEAD -> R.string.device_info_health_dead
        BatteryHealth.OVER_VOLTAGE -> R.string.device_info_health_over_voltage
        BatteryHealth.COLD -> R.string.device_info_health_cold
        BatteryHealth.FAILURE -> R.string.device_info_health_failure
    }

@StringRes
internal fun CameraFacing?.labelRes(): Int =
    when (this) {
        CameraFacing.FRONT -> R.string.device_info_cam_front
        CameraFacing.BACK -> R.string.device_info_cam_back
        CameraFacing.EXTERNAL -> R.string.device_info_cam_external
        null -> R.string.device_info_cam_unknown
    }

@StringRes
internal fun CameraHardwareLevel.labelRes(): Int =
    when (this) {
        CameraHardwareLevel.LEGACY -> R.string.device_info_cam_level_legacy
        CameraHardwareLevel.LIMITED -> R.string.device_info_cam_level_limited
        CameraHardwareLevel.FULL -> R.string.device_info_cam_level_full
        CameraHardwareLevel.LEVEL_3 -> R.string.device_info_cam_level_3
        CameraHardwareLevel.EXTERNAL -> R.string.device_info_cam_level_external
    }

@Composable
internal fun HdrType.label(): String =
    when (this) {
        HdrType.DolbyVision -> stringResource(R.string.device_info_hdr_dolby_vision)
        HdrType.Hdr10 -> stringResource(R.string.device_info_hdr_hdr10)
        HdrType.Hdr10Plus -> stringResource(R.string.device_info_hdr_hdr10_plus)
        HdrType.Hlg -> stringResource(R.string.device_info_hdr_hlg)
        is HdrType.Other -> stringResource(R.string.device_info_hdr_other, code)
    }
