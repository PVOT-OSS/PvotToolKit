// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object MoreRoute : NavKey

/** Cross-feature destinations arrive as callbacks, supplied by :app. */
fun EntryProviderScope<NavKey>.moreEntry(
    onOpenDevices: () -> Unit,
    onOpenNews: () -> Unit,
    onOpenLicenses: () -> Unit,
) {
    entry<MoreRoute> {
        MoreScreen(
            onOpenDevices = onOpenDevices,
            onOpenNews = onOpenNews,
            onOpenLicenses = onOpenLicenses,
        )
    }
}
