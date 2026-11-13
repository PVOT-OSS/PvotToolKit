// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover.ui

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.model.NewsPost

class HeadlinesStateTest {
    private val posts =
        (1..5).map { id ->
            NewsPost(
                id = "$id",
                title = "Post $id",
                date = LocalDate.of(2026, 11, id),
                tag = null,
                author = null,
                summary = null,
                coverUrl = null,
                url = "https://example.org/$id",
                readingMinutes = 1,
            )
        }

    @Test
    fun `a loaded feed shows its newest three posts`() {
        assertEquals(
            HeadlinesState.Ready(posts.take(3)),
            DataResult.Success(posts).toHeadlinesState(),
        )
    }

    @Test
    fun `a cached feed still shows its posts`() {
        assertEquals(
            HeadlinesState.Ready(posts.take(3)),
            DataResult.Success(posts, stale = true).toHeadlinesState(),
        )
    }

    @Test
    fun `a failed feed is reported rather than hidden`() {
        LoadError.entries.forEach { error ->
            assertEquals(HeadlinesState.Failed, DataResult.Failure(error).toHeadlinesState())
        }
    }
}
