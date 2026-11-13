// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.prauga.toolkit.core.model.ThemePreference
import timber.log.Timber

private val Context.settingsDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "settings")

private val ThemeKey = stringPreferencesKey("theme")

@Singleton
class SettingsLocalDataSource @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.settingsDataStore

    val theme: Flow<ThemePreference> =
        dataStore.data
            .catch { cause ->
                if (cause !is IOException) throw cause
                Timber.e(cause, "Settings unreadable")
                emit(emptyPreferences())
            }
            .map { preferences ->
                preferences[ThemeKey]?.let { stored ->
                    ThemePreference.entries.firstOrNull { it.name == stored }
                } ?: ThemePreference.SYSTEM
            }

    suspend fun setTheme(preference: ThemePreference) {
        try {
            dataStore.edit { it[ThemeKey] = preference.name }
        } catch (failure: IOException) {
            Timber.e(failure, "Could not store the theme")
        }
    }
}
