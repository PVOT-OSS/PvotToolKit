// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.model.DeviceDetails
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.Vitals

private const val TAB_KEY = "tab"
private const val STOP_TIMEOUT_MILLIS = 5_000L

// Vitals cost a sampling window, so they are read only once a tab that shows them opens.
private val TabsNeedingVitals = setOf(DeviceInfoTab.MEMORY, DeviceInfoTab.BATTERY)

@HiltViewModel
internal class DeviceInfoViewModel
@Inject
constructor(
    private val deviceRepository: DeviceRepository,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val readings = MutableStateFlow(DeviceReadings())
    private val tab = savedStateHandle.getStateFlow(TAB_KEY, DeviceInfoTab.SYSTEM)
    private var vitalsJob: Job? = null

    val state: StateFlow<DeviceInfoUiState> =
        combine(readings, tab, ::uiState)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                DeviceInfoUiState.Loading,
            )

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            readings.update { it.copy(profile = profile) }
        }
        viewModelScope.launch {
            val details = deviceRepository.details()
            readings.update { it.copy(details = details) }
        }
        readVitalsFor(tab.value)
    }

    fun onEvent(event: DeviceInfoEvent) {
        when (event) {
            is DeviceInfoEvent.TabSelected -> {
                savedStateHandle[TAB_KEY] = event.tab
                readVitalsFor(event.tab)
            }
        }
    }

    private fun readVitalsFor(tab: DeviceInfoTab) {
        if (tab !in TabsNeedingVitals || vitalsJob != null) return
        vitalsJob =
            viewModelScope.launch {
                val vitals = deviceRepository.vitals()
                readings.update { it.copy(vitals = vitals) }
            }
    }

    private fun uiState(readings: DeviceReadings, tab: DeviceInfoTab): DeviceInfoUiState {
        val profile = readings.profile ?: return DeviceInfoUiState.Loading
        return DeviceInfoUiState.Ready(
            deviceName = profile.identity.displayName,
            codename = profile.identity.codename,
            tab = tab,
            sections = tab.sections(profile, readings.details, readings.vitals),
        )
    }

    private data class DeviceReadings(
        val profile: DeviceProfile? = null,
        val details: DeviceDetails? = null,
        val vitals: Vitals? = null,
    )
}
