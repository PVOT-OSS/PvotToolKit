// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import org.prauga.toolkit.feature.builds.buildsEntry
import org.prauga.toolkit.feature.deviceinfo.DeviceInfoRoute
import org.prauga.toolkit.feature.deviceinfo.deviceEntries
import org.prauga.toolkit.feature.devices.DevicesRoute
import org.prauga.toolkit.feature.devices.devicesEntry
import org.prauga.toolkit.feature.discover.navigation.discoverEntry
import org.prauga.toolkit.feature.licenses.LicensesRoute
import org.prauga.toolkit.feature.licenses.licensesEntry
import org.prauga.toolkit.feature.monitor.navigation.CpuDetailRoute
import org.prauga.toolkit.feature.monitor.navigation.MonitorRoute
import org.prauga.toolkit.feature.monitor.navigation.cpuDetailEntry
import org.prauga.toolkit.feature.monitor.navigation.monitorEntry
import org.prauga.toolkit.feature.more.moreEntry
import org.prauga.toolkit.feature.news.NewsRoute
import org.prauga.toolkit.feature.news.newsEntry
import org.prauga.toolkit.feature.tune.tuneEntry

/** Composes the feature entries and shows the current back stacks. */
@Composable
fun ToolkitNavDisplay(state: NavigationState, navigator: Navigator, modifier: Modifier = Modifier) {
    val entryProvider = entryProvider {
        discoverEntry(
            onOpenDevice = { navigator.navigate(DeviceInfoRoute) },
            onOpenMonitoring = { navigator.navigate(MonitorRoute) },
            onOpenNews = { navigator.navigate(NewsRoute) },
        )
        deviceEntries()
        tuneEntry()
        monitorEntry(onOpenCpuDetail = { navigator.navigate(CpuDetailRoute) })
        cpuDetailEntry()
        buildsEntry()
        moreEntry(
            onOpenDevices = { navigator.navigate(DevicesRoute) },
            onOpenNews = { navigator.navigate(NewsRoute) },
            onOpenLicenses = { navigator.navigate(LicensesRoute) },
        )
        newsEntry()
        devicesEntry()
        licensesEntry()
    }

    NavDisplay(
        entries = state.toDecoratedEntries(entryProvider, onBack = navigator::goBack),
        modifier = modifier,
        onBack = navigator::goBack,
    )
}
