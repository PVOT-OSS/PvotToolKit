// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.batterymonitor.BatterySessionStore
import org.prauga.toolkit.core.data.links.LinksRepository
import org.prauga.toolkit.core.data.settings.ThemeRepository
import org.prauga.toolkit.core.di.appContainer
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.model.ThemePreference

data class MoreUiState(
    val sections: List<LinkSection> = emptyList(),
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val batteryMonitorEnabled: Boolean = false,
)

class MoreViewModel(
    private val linksRepository: LinksRepository,
    private val themeRepository: ThemeRepository,
    private val batteryStore: BatterySessionStore,
) : ViewModel() {
    private val _state = MutableStateFlow(MoreUiState())
    val state: StateFlow<MoreUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val sections = linksRepository.links()
            _state.update { it.copy(sections = sections) }
        }
        viewModelScope.launch {
            themeRepository.theme.collect { theme -> _state.update { it.copy(theme = theme) } }
        }
        viewModelScope.launch {
            batteryStore.enabled.collect { enabled ->
                _state.update { it.copy(batteryMonitorEnabled = enabled) }
            }
        }
    }

    fun setTheme(preference: ThemePreference) {
        viewModelScope.launch { themeRepository.setTheme(preference) }
    }

    /** Only records the preference; starting the service is the UI's job. */
    fun setBatteryMonitorEnabled(enabled: Boolean) {
        viewModelScope.launch { batteryStore.setEnabled(enabled) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val container = checkNotNull(this[APPLICATION_KEY]).appContainer()
                MoreViewModel(
                    linksRepository = container.linksRepository,
                    themeRepository = container.themeRepository,
                    batteryStore = container.batterySessionStore,
                )
            }
        }
    }
}
