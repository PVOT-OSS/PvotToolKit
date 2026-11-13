// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover.ui

import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.model.NewsPost

private const val HEADLINE_COUNT = 3

internal sealed interface HeadlinesState {
    data object Loading : HeadlinesState

    data object Failed : HeadlinesState

    data class Ready(val posts: List<NewsPost>) : HeadlinesState
}

internal fun DataResult<List<NewsPost>>.toHeadlinesState(): HeadlinesState =
    when (this) {
        is DataResult.Success -> HeadlinesState.Ready(data.take(HEADLINE_COUNT))
        is DataResult.Failure -> HeadlinesState.Failed
    }
