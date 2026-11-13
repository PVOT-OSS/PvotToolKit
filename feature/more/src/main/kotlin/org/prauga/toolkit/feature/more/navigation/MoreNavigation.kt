// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.feature.more.ui.MoreScreen

@Serializable data object MoreRoute : NavKey

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
