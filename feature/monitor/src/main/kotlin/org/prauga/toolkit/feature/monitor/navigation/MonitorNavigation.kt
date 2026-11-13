// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import org.prauga.toolkit.core.designsystem.navigation.FullScreenMetadata
import org.prauga.toolkit.feature.monitor.ui.CpuDetailScreen
import org.prauga.toolkit.feature.monitor.ui.MonitorScreen

@Serializable data object MonitorRoute : NavKey

@Serializable data object CpuDetailRoute : NavKey

fun EntryProviderScope<NavKey>.monitorEntry(onOpenCpuDetail: () -> Unit) {
    entry<MonitorRoute> { MonitorScreen(onOpenCpuDetail = onOpenCpuDetail) }
}

fun EntryProviderScope<NavKey>.cpuDetailEntry() {
    entry<CpuDetailRoute>(metadata = FullScreenMetadata) { CpuDetailScreen() }
}
