// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.licenses.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.feature.licenses.ui.LicensesScreen

@Serializable data object LicensesRoute : NavKey

fun EntryProviderScope<NavKey>.licensesEntry() {
    entry<LicensesRoute> { LicensesScreen() }
}
