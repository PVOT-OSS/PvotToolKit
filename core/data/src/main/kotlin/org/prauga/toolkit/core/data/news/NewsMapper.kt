// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.news

import java.time.LocalDate
import java.time.format.DateTimeParseException
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.network.dto.NewsFeedDto
import org.prauga.toolkit.core.network.dto.NewsPostDto
import timber.log.Timber

/** Maps the feed to domain posts, dropping any entry that cannot be represented. */
internal fun NewsFeedDto.toDomain(): List<NewsPost> = posts.mapNotNull { it.toDomainOrNull() }

private fun NewsPostDto.toDomainOrNull(): NewsPost? {
    val parsedDate =
        try {
            LocalDate.parse(date)
        } catch (failure: DateTimeParseException) {
            Timber.w(failure, "Dropped news post %s with an unparseable date", id)
            return null
        }
    return NewsPost(
        id = id,
        title = title,
        date = parsedDate,
        tag = tag,
        author = author,
        summary = summary,
        coverUrl = cover,
        url = url,
        readingMinutes = readingMinutes.coerceAtLeast(1),
    )
}
