// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.data.vitals.VitalsSampler

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class CpuDetailViewModel
@Inject
constructor(deviceRepository: DeviceRepository, vitalsSampler: VitalsSampler) : ViewModel() {
    internal val state: StateFlow<CpuDetailUiState> =
        combine(flow { emit(deviceRepository.profile()) }, vitalsSampler.samples) { profile, sample
                ->
                cpuDetailUiState(profile.cpu, sample)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = CpuDetailUiState(),
            )
}
