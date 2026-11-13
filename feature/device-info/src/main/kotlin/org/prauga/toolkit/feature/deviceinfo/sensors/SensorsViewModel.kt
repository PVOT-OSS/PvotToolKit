// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.sensors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.data.device.DeviceRepository

/** Deliberately does not touch vitals: the screen shows none. */
@HiltViewModel
internal class SensorsViewModel @Inject constructor(deviceRepository: DeviceRepository) :
    ViewModel() {
    private val _state = MutableStateFlow<SensorsUiState>(SensorsUiState.Loading)
    val state: StateFlow<SensorsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val profile = async { deviceRepository.profile() }
            val details = async { deviceRepository.details() }
            _state.value =
                SensorsUiState.Ready(
                    deviceName = profile.await().identity.displayName,
                    sensors = details.await().sensors,
                )
        }
    }
}
