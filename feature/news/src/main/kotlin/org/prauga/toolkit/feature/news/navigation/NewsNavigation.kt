// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.news.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.feature.news.ui.NewsScreen

@Serializable data object NewsRoute : NavKey

fun EntryProviderScope<NavKey>.newsEntry() {
    entry<NewsRoute> { NewsScreen() }
}
