// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.tune

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object TuneRoute : NavKey

fun EntryProviderScope<NavKey>.tuneEntry() {
    entry<TuneRoute> { TuneScreen() }
}
