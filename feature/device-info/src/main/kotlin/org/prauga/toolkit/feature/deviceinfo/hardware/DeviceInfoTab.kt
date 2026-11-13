// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import androidx.annotation.StringRes
import org.prauga.toolkit.feature.deviceinfo.R

internal enum class DeviceInfoTab(@param:StringRes val labelRes: Int) {
    SYSTEM(R.string.device_info_section_system),
    SOC(R.string.device_info_section_soc),
    SCREEN(R.string.device_info_section_screen),
    MEMORY(R.string.device_info_section_memory),
    BATTERY(R.string.device_info_section_battery),
    CAMERAS(R.string.device_info_section_cameras),
    CODECS(R.string.device_info_section_codecs),
    BOOT(R.string.device_info_section_boot),
}
