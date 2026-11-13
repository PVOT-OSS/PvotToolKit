// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.model.DeviceDetails
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.Vitals

data class DeviceInfoUiState(
    val profile: DeviceProfile? = null,
    val details: DeviceDetails? = null,
    val vitals: Vitals? = null,
)

/** Owns the hardware and build readouts behind the Device HW screen. */
@HiltViewModel
class DeviceInfoViewModel @Inject constructor(private val deviceRepository: DeviceRepository) :
    ViewModel() {
    private val _state = MutableStateFlow(DeviceInfoUiState())
    val state: StateFlow<DeviceInfoUiState> = _state.asStateFlow()

    private var vitalsJob: Job? = null

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
        viewModelScope.launch {
            val details = deviceRepository.details()
            _state.update { it.copy(details = details) }
        }
    }

    /**
     * Reads the live vitals, once.
     *
     * Only two of the tabs show them and the read costs a sampling window, so the screen asks for
     * them when they are about to be displayed rather than on open.
     */
    fun ensureVitals() {
        if (vitalsJob != null) return
        vitalsJob =
            viewModelScope.launch {
                val vitals = deviceRepository.vitals()
                _state.update { it.copy(vitals = vitals) }
            }
    }
}
