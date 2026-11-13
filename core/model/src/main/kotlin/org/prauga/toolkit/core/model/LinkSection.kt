// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.model

data class LinkSection(val id: String, val title: String, val items: List<LinkItem>)

data class LinkItem(
    val id: String,
    val label: String,
    val description: String?,
    val url: String?,
    val external: Boolean,
    val soon: Boolean,
) {
    val openable: Boolean
        get() = url != null && !soon
}
