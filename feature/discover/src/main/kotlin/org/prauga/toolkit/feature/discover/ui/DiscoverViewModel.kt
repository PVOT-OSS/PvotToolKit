// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.data.news.NewsRepository

@HiltViewModel
class DiscoverViewModel
@Inject
constructor(
    private val deviceRepository: DeviceRepository,
    private val newsRepository: NewsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState())
    internal val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    private val _effects = Channel<DiscoverEffect>(Channel.BUFFERED)
    internal val effects: Flow<DiscoverEffect> = _effects.receiveAsFlow()

    private var headlinesLoad: Job? = null

    init {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
        viewModelScope.launch {
            val vitals = deviceRepository.vitals()
            _state.update { it.copy(vitals = vitals) }
        }
        loadHeadlines()
    }

    internal fun onEvent(event: DiscoverEvent) {
        when (event) {
            DiscoverEvent.DeviceClicked -> send(DiscoverEffect.OpenDevice)
            DiscoverEvent.MonitoringClicked -> send(DiscoverEffect.OpenMonitoring)
            DiscoverEvent.NewsArchiveClicked -> send(DiscoverEffect.OpenNews)
            is DiscoverEvent.HeadlineClicked -> send(DiscoverEffect.OpenArticle(event.url))
            DiscoverEvent.RetryHeadlinesClicked -> loadHeadlines()
        }
    }

    private fun loadHeadlines() {
        if (headlinesLoad?.isActive == true) return
        _state.update { it.copy(headlines = HeadlinesState.Loading) }
        headlinesLoad =
            viewModelScope.launch {
                val headlines = newsRepository.news().toHeadlinesState()
                _state.update { it.copy(headlines = headlines) }
            }
    }

    private fun send(effect: DiscoverEffect) {
        viewModelScope.launch { _effects.send(effect) }
    }
}
