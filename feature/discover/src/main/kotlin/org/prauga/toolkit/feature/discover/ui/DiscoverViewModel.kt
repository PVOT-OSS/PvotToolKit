// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.data.news.NewsRepository
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.model.Vitals

private const val HEADLINE_COUNT = 3

data class DiscoverUiState(
    val profile: DeviceProfile? = null,
    val vitals: Vitals? = null,
    val headlines: List<NewsPost> = emptyList(),
)

@HiltViewModel
class DiscoverViewModel
@Inject
constructor(
    private val deviceRepository: DeviceRepository,
    private val newsRepository: NewsRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(DiscoverUiState())
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val profile = deviceRepository.profile()
            _state.update { it.copy(profile = profile) }
        }
        viewModelScope.launch {
            val vitals = deviceRepository.vitals()
            _state.update { it.copy(vitals = vitals) }
        }
        viewModelScope.launch {
            val result = newsRepository.news()
            if (result is DataResult.Success) {
                _state.update { it.copy(headlines = result.data.take(HEADLINE_COUNT)) }
            }
        }
    }
}
