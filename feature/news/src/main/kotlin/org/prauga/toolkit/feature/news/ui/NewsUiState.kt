// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.ui

import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.model.NewsPost

internal sealed interface NewsUiState {
    data object Loading : NewsUiState

    /** Loaded and empty is a real state, not a failure. */
    data object Empty : NewsUiState

    /** @param stale served from cache because the network was unreachable. */
    data class Ready(val posts: List<NewsPost>, val stale: Boolean) : NewsUiState

    data class Failed(val error: LoadError) : NewsUiState
}
