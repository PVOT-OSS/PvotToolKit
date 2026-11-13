// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.batterymonitor.BatteryMonitorController
import org.prauga.toolkit.core.data.links.LinksRepository
import org.prauga.toolkit.core.data.settings.ThemeRepository
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.model.ThemePreference
import timber.log.Timber

data class MoreUiState(
    val sections: List<LinkSection> = emptyList(),
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val batteryMonitorEnabled: Boolean = false,
)

@HiltViewModel
class MoreViewModel
@Inject
constructor(
    private val linksRepository: LinksRepository,
    private val themeRepository: ThemeRepository,
    private val batteryMonitor: BatteryMonitorController,
) : ViewModel() {
    private val _state = MutableStateFlow(MoreUiState())
    val state: StateFlow<MoreUiState> = _state.asStateFlow()

    private val _effects = Channel<MoreEffect>(Channel.BUFFERED)
    internal val effects: Flow<MoreEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val sections = linksRepository.links().data
            _state.update { it.copy(sections = sections) }
        }
        viewModelScope.launch {
            themeRepository.theme.collect { theme -> _state.update { it.copy(theme = theme) } }
        }
        viewModelScope.launch {
            batteryMonitor.enabled.collect { enabled ->
                _state.update { it.copy(batteryMonitorEnabled = enabled) }
            }
        }
    }

    fun setTheme(preference: ThemePreference) {
        viewModelScope.launch { themeRepository.setTheme(preference) }
    }

    fun setBatteryMonitorEnabled(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) _effects.send(MoreEffect.RequestNotificationPermission)
            batteryMonitor.setEnabled(enabled)
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        if (!granted) Timber.i("Notification permission denied; the battery monitor runs without its readout")
    }
}
