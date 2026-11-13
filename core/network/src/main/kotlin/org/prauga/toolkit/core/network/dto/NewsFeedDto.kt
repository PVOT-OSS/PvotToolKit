// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Wire shape of `/api/v1/news.json`. */
@Serializable
data class NewsFeedDto(val schema: Int = 0, val posts: List<NewsPostDto> = emptyList())

@Serializable
data class NewsPostDto(
    val id: String,
    val title: String,
    val date: String,
    val tag: String? = null,
    val author: String? = null,
    val summary: String? = null,
    val cover: String? = null,
    val url: String,
    @SerialName("reading_minutes") val readingMinutes: Int = 1,
)
