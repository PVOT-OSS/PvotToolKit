// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.devices.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.core.designsystem.navigation.FullScreenMetadata
import org.prauga.toolkit.feature.devices.ui.DevicesScreen

@Serializable data object DevicesRoute : NavKey

fun EntryProviderScope<NavKey>.devicesEntry() {
    entry<DevicesRoute>(metadata = FullScreenMetadata) { DevicesScreen() }
}
