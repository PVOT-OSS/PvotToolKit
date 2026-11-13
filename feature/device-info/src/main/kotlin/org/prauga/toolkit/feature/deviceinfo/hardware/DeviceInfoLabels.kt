// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import androidx.annotation.StringRes
import org.prauga.toolkit.core.model.BatteryHealth
import org.prauga.toolkit.core.model.CameraFacing
import org.prauga.toolkit.core.model.CameraHardwareLevel
import org.prauga.toolkit.core.model.HdrType
import org.prauga.toolkit.feature.deviceinfo.R

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

internal fun HdrType.text(): InfoText =
    when (this) {
        HdrType.DolbyVision -> InfoText.Res(R.string.device_info_hdr_dolby_vision)
        HdrType.Hdr10 -> InfoText.Res(R.string.device_info_hdr_hdr10)
        HdrType.Hdr10Plus -> InfoText.Res(R.string.device_info_hdr_hdr10_plus)
        HdrType.Hlg -> InfoText.Res(R.string.device_info_hdr_hlg)
        is HdrType.Other -> InfoText.Res(R.string.device_info_hdr_other, listOf(code))
    }
