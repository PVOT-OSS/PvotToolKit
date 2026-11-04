// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object DiscoverRoute : NavKey

fun EntryProviderScope<NavKey>.discoverEntry(
    onOpenDevice: () -> Unit,
    onOpenMonitoring: () -> Unit,
    onOpenNews: () -> Unit,
) {
    entry<DiscoverRoute> {
        DiscoverScreen(
            onOpenDevice = onOpenDevice,
            onOpenMonitoring = onOpenMonitoring,
            onOpenNews = onOpenNews,
        )
    }
}
