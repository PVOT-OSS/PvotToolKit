// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.ui

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
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.data.news.NewsRepository
import org.prauga.toolkit.core.model.NewsPost

@HiltViewModel
internal class NewsViewModel @Inject constructor(private val repository: NewsRepository) :
    ViewModel() {
    private val _state = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    private val _effects = Channel<NewsEffect>(Channel.BUFFERED)
    val effects: Flow<NewsEffect> = _effects.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        load()
    }

    fun onEvent(event: NewsEvent) {
        when (event) {
            NewsEvent.Retry -> load()
            is NewsEvent.ArticleClicked ->
                viewModelScope.launch { _effects.send(NewsEffect.OpenArticle(event.url)) }
        }
    }

    private fun load() {
        if (loadJob?.isActive == true) return
        loadJob =
            viewModelScope.launch {
                _state.update { current -> current as? NewsUiState.Ready ?: NewsUiState.Loading }
                _state.value = repository.news().toUiState()
            }
    }

    private fun DataResult<List<NewsPost>>.toUiState(): NewsUiState =
        when (this) {
            is DataResult.Success ->
                if (data.isEmpty()) NewsUiState.Empty else NewsUiState.Ready(data, stale)
            is DataResult.Failure -> NewsUiState.Failed(error)
        }
}
