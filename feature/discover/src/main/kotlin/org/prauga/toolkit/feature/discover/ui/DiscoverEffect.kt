// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover.ui

internal sealed interface DiscoverEffect {
    data object OpenDevice : DiscoverEffect

    data object OpenMonitoring : DiscoverEffect

    data object OpenNews : DiscoverEffect

    data class OpenArticle(val url: String) : DiscoverEffect
}
