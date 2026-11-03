// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.settings

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import org.prauga.toolkit.core.datastore.SettingsStore
import org.prauga.toolkit.core.model.ThemePreference

@Singleton
class ThemeRepository @Inject constructor(private val store: SettingsStore) {
    val theme: Flow<ThemePreference> = store.theme

    suspend fun setTheme(preference: ThemePreference) = store.setTheme(preference)
}
