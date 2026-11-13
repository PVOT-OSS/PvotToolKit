// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.network.dto

import kotlinx.serialization.Serializable

/** Wire shape of `/api/v1/links.json`, and of the bundled fallback copy. */
@Serializable
data class LinksDto(val schema: Int = 0, val sections: List<LinkSectionDto> = emptyList())

@Serializable
data class LinkSectionDto(
    val id: String,
    val title: String,
    val items: List<LinkItemDto> = emptyList(),
)

@Serializable
data class LinkItemDto(
    val id: String,
    val label: String,
    val description: String? = null,
    val url: String? = null,
    val external: Boolean = false,
    val soon: Boolean = false,
)
