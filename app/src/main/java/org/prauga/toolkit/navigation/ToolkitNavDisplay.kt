// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.toolkit.core.designsystem.component.BackButton
import org.prauga.toolkit.core.designsystem.navigation.isFullScreen
import org.prauga.toolkit.feature.builds.navigation.buildsEntry
import org.prauga.toolkit.feature.deviceinfo.navigation.DeviceInfoRoute
import org.prauga.toolkit.feature.deviceinfo.navigation.deviceEntries
import org.prauga.toolkit.feature.devices.navigation.DevicesRoute
import org.prauga.toolkit.feature.devices.navigation.devicesEntry
import org.prauga.toolkit.feature.discover.navigation.discoverEntry
import org.prauga.toolkit.feature.licenses.navigation.LicensesRoute
import org.prauga.toolkit.feature.licenses.navigation.licensesEntry
import org.prauga.toolkit.feature.monitor.navigation.CpuDetailRoute
import org.prauga.toolkit.feature.monitor.navigation.MonitorRoute
import org.prauga.toolkit.feature.monitor.navigation.cpuDetailEntry
import org.prauga.toolkit.feature.monitor.navigation.monitorEntry
import org.prauga.toolkit.feature.more.navigation.moreEntry
import org.prauga.toolkit.feature.news.navigation.NewsRoute
import org.prauga.toolkit.feature.news.navigation.newsEntry
import org.prauga.toolkit.feature.tune.navigation.tuneEntry

internal fun toolkitEntryProvider(navigator: Navigator): (NavKey) -> NavEntry<NavKey> =
    entryProvider {
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

@Composable
internal fun ToolkitNavDisplay(
    navigator: Navigator,
    entryProvider: (NavKey) -> NavEntry<NavKey>,
    modifier: Modifier = Modifier,
) {
    NavDisplay(
        entries =
            navigator.state.toDecoratedEntries { key ->
                entryProvider(key).framed(onBack = navigator::goBack)
            },
        modifier = modifier,
        onBack = navigator::goBack,
    )
}

/** Opaque so predictive back hides what is behind; full-screen entries get a back arrow. */
private fun NavEntry<NavKey>.framed(onBack: () -> Unit): NavEntry<NavKey> {
    val entry = this
    return NavEntry(navEntry = entry) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (entry.metadata.isFullScreen()) {
                BackButton(onClick = onBack, modifier = Modifier.padding(start = PvotSpacing.XS))
            }
            entry.Content()
        }
    }
}
