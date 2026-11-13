// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.builds.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.feature.builds.ui.BuildsScreen

@Serializable data object BuildsRoute : NavKey

fun EntryProviderScope<NavKey>.buildsEntry() {
    entry<BuildsRoute> { BuildsScreen() }
}
