// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.feature.deviceinfo.hardware.DeviceInfoScreen
import org.prauga.toolkit.feature.deviceinfo.sensors.SensorsScreen
import org.prauga.toolkit.feature.deviceinfo.tests.TestsScreen

@Serializable data object DeviceInfoRoute : NavKey

/** The device screens; the repository caches the reads, so switching between them costs nothing. */
fun EntryProviderScope<NavKey>.deviceEntries() {
    entry<DeviceInfoRoute> { DeviceInfoScreen() }
    entry<SensorsRoute> { SensorsScreen() }
    entry<TestsRoute> { TestsScreen() }
}
