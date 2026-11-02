// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.settings

import org.prauga.toolkit.core.datastore.SettingsStore
import org.prauga.toolkit.core.model.ThemePreference
import kotlinx.coroutines.flow.Flow

class ThemeRepository(private val store: SettingsStore) {
    val theme: Flow<ThemePreference> = store.theme

    suspend fun setTheme(preference: ThemePreference) = store.setTheme(preference)
}
