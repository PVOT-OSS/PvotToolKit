// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.prauga.toolkit.core.data.settings.ThemeRepository

private const val STOP_TIMEOUT_MILLIS = 5_000L

@HiltViewModel
class MainViewModel @Inject constructor(themeRepository: ThemeRepository) : ViewModel() {
    /** Starts on the system theme so the first frame matches the launch window. */
    internal val uiState: StateFlow<MainUiState> =
        themeRepository.theme
            .map(::MainUiState)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = MainUiState(),
            )
}
