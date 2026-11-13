// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
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
import kotlinx.serialization.json.Json
import org.prauga.toolkit.core.battery.BatterySample
import org.prauga.toolkit.core.battery.BatterySession
import org.prauga.toolkit.core.battery.BatteryTracker
import timber.log.Timber

private val Context.batteryDataStore: DataStore<Preferences> by
    preferencesDataStore(name = "battery_monitor")

private val SessionKey = stringPreferencesKey("session")
private val PreviousSampleKey = stringPreferencesKey("previous_sample")
private val EnabledKey = booleanPreferencesKey("enabled")

/** Persists the session so it survives process death and reboot. */
@Singleton
class BatterySessionLocalDataSource @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.batteryDataStore

    private val json = Json { ignoreUnknownKeys = true }

    val enabled: Flow<Boolean> =
        dataStore.data
            .catch { cause ->
                if (cause !is IOException) throw cause
                Timber.e(cause, "Battery monitor preferences unreadable")
                emit(emptyPreferences())
            }
            .map { it[EnabledKey] ?: false }

    /** Returns false when the switch could not be stored. */
    suspend fun setEnabled(value: Boolean): Boolean =
        try {
            dataStore.edit { it[EnabledKey] = value }
            true
        } catch (failure: IOException) {
            Timber.e(failure, "Could not store the battery monitor switch")
            false
        }

    /**
     * Folds [sample] into the session in one transaction, so concurrent samples cannot count twice.
     */
    suspend fun record(sample: BatterySample): BatterySession? {
        var recorded: BatterySession? = null
        try {
            dataStore.edit { preferences ->
                val session =
                    BatteryTracker.fold(
                        session = preferences[SessionKey]?.let { decode<BatterySession>(it) },
                        previous =
                            preferences[PreviousSampleKey]?.let { decode<BatterySample>(it) },
                        sample = sample,
                    )
                preferences[SessionKey] = json.encodeToString(session)
                preferences[PreviousSampleKey] = json.encodeToString(sample)
                recorded = session
            }
        } catch (failure: IOException) {
            Timber.e(failure, "Could not record a battery sample")
            return null
        }
        return recorded
    }

    /** A schema change must reset the session, not crash the service. */
    private inline fun <reified T> decode(stored: String): T? =
        try {
            json.decodeFromString<T>(stored)
        } catch (failure: IllegalArgumentException) {
            Timber.w(failure, "Stored %s unreadable, starting over", T::class.simpleName)
            null
        }
}
