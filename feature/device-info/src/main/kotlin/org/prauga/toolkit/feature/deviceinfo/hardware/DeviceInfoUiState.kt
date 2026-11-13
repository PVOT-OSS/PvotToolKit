// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

internal sealed interface DeviceInfoUiState {
    data object Loading : DeviceInfoUiState

    data class Ready(
        val deviceName: String?,
        val codename: String?,
        val tab: DeviceInfoTab,
        val sections: List<InfoSection>,
    ) : DeviceInfoUiState
}
