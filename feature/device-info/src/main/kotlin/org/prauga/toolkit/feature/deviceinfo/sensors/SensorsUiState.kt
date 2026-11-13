// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.sensors

import org.prauga.toolkit.core.model.SensorInfo

internal sealed interface SensorsUiState {
    data object Loading : SensorsUiState

    data class Ready(val deviceName: String?, val sensors: List<SensorInfo>) : SensorsUiState
}
