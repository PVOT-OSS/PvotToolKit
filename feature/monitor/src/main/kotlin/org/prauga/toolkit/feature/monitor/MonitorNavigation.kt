// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable data object MonitorRoute : NavKey

/** Detail pushed from the CPU card. */
@Serializable data object CpuDetailRoute : NavKey

fun EntryProviderScope<NavKey>.monitorEntry(onOpenCpuDetail: () -> Unit) {
    entry<MonitorRoute> { MonitorScreen(onOpenCpuDetail = onOpenCpuDetail) }
}

fun EntryProviderScope<NavKey>.cpuDetailEntry() {
    entry<CpuDetailRoute> { CpuDetailScreen() }
}
