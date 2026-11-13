// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.links

import org.prauga.toolkit.core.model.LinkItem
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.network.dto.LinkItemDto
import org.prauga.toolkit.core.network.dto.LinksDto

/** Drops items with nothing to show and sections left empty by that. */
internal fun LinksDto.toDomain(): List<LinkSection> =
    sections
        .map { section ->
            LinkSection(
                id = section.id,
                title = section.title,
                items = section.items.filter { it.url != null || it.soon }.map { it.toDomain() },
            )
        }
        .filter { it.items.isNotEmpty() }

private fun LinkItemDto.toDomain(): LinkItem =
    LinkItem(
        id = id,
        label = label,
        description = description,
        url = url,
        external = external,
        soon = soon,
    )
