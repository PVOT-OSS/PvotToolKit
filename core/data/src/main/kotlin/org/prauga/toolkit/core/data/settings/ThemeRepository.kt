// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.settings

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import org.prauga.toolkit.core.datastore.SettingsLocalDataSource
import org.prauga.toolkit.core.model.ThemePreference

@Singleton
class ThemeRepository
@Inject
internal constructor(private val localDataSource: SettingsLocalDataSource) {
    val theme: Flow<ThemePreference> = localDataSource.theme

    suspend fun setTheme(preference: ThemePreference) = localDataSource.setTheme(preference)
}
