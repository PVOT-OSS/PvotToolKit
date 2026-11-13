// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.model.SensorInfo

data class SensorsUiState(
    val deviceName: String? = null,
    val sensors: List<SensorInfo> = emptyList(),
)

/** Owns the sensor roster. Deliberately does not touch vitals: it shows none. */
@HiltViewModel
class SensorsViewModel @Inject constructor(private val deviceRepository: DeviceRepository) :
    ViewModel() {
    private val _state = MutableStateFlow(SensorsUiState())
    val state: StateFlow<SensorsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(deviceName = profile.identity.displayName) }
        }
        viewModelScope.launch {
            val details = deviceRepository.details()
            _state.update { it.copy(sensors = details.sensors) }
        }
    }
}
