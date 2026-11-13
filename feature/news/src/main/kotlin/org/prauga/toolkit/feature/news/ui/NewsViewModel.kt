// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.data.news.NewsRepository
import org.prauga.toolkit.core.model.NewsPost

sealed interface NewsUiState {
    data object Loading : NewsUiState

    /** Loaded and empty is a real state, not a failure. */
    data object Empty : NewsUiState

    /** @param stale served from cache because the network was unreachable. */
    data class Ready(val posts: List<NewsPost>, val stale: Boolean) : NewsUiState

    data class Failed(val error: LoadError) : NewsUiState
}

@HiltViewModel
class NewsViewModel @Inject constructor(private val repository: NewsRepository) : ViewModel() {
    private val _state = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val state: StateFlow<NewsUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            if (_state.value !is NewsUiState.Ready) {
                _state.value = NewsUiState.Loading
            }
            _state.value =
                when (val result = repository.news()) {
                    is DataResult.Success -> {
                        if (result.data.isEmpty()) {
                            NewsUiState.Empty
                        } else {
                            NewsUiState.Ready(result.data, result.stale)
                        }
                    }

                    is DataResult.Failure -> {
                        NewsUiState.Failed(result.error)
                    }
                }
        }
    }
}
