// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.navOptions
import org.prauga.toolkit.feature.builds.buildsScreen
import org.prauga.toolkit.feature.deviceinfo.DeviceGraphRoute
import org.prauga.toolkit.feature.deviceinfo.deviceGraph
import org.prauga.toolkit.feature.deviceinfo.navigateToDeviceInfo
import org.prauga.toolkit.feature.devices.devicesScreen
import org.prauga.toolkit.feature.devices.navigateToDevices
import org.prauga.toolkit.feature.discover.DiscoverRoute
import org.prauga.toolkit.feature.discover.discoverScreen
import org.prauga.toolkit.feature.licenses.licensesScreen
import org.prauga.toolkit.feature.licenses.navigateToLicenses
import org.prauga.toolkit.feature.monitor.MonitorRoute
import org.prauga.toolkit.feature.monitor.cpuDetailScreen
import org.prauga.toolkit.feature.monitor.monitorScreen
import org.prauga.toolkit.feature.monitor.navigateToCpuDetail
import org.prauga.toolkit.feature.monitor.navigateToMonitor
import org.prauga.toolkit.feature.more.MoreRoute
import org.prauga.toolkit.feature.more.moreScreen
import org.prauga.toolkit.feature.news.navigateToNews
import org.prauga.toolkit.feature.news.newsScreen
import org.prauga.toolkit.feature.tune.tuneScreen
import kotlinx.serialization.Serializable

/** Discover's nested graph, so the device screen keeps Discover selected. */
@Serializable data object DiscoverGraphRoute

/** More's nested graph. */
@Serializable data object MoreGraphRoute

/** Monitor's nested graph, so the CPU detail screen keeps Monitor selected. */
@Serializable data object MonitorGraphRoute

/** Composes the feature graphs. */
@Composable
fun SBNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = DiscoverGraphRoute,
        modifier = modifier,
    ) {
        navigation<DiscoverGraphRoute>(startDestination = DiscoverRoute) {
            discoverScreen(
                onOpenDevice = { navController.navigateToDeviceInfo() },
                onOpenMonitoring = { navController.navigateToMonitor() },
                onOpenNews = { navController.navigateToNews() },
            )
            deviceGraph()
        }
        tuneScreen()
        navigation<MonitorGraphRoute>(startDestination = MonitorRoute) {
            monitorScreen(onOpenCpuDetail = { navController.navigateToCpuDetail() })
            cpuDetailScreen()
        }
        buildsScreen()

        navigation<MoreGraphRoute>(startDestination = MoreRoute) {
            moreScreen(
                onOpenDevices = { navController.navigateToDevices() },
                onOpenNews = { navController.navigateToNews() },
                onOpenLicenses = { navController.navigateToLicenses() },
            )
            newsScreen()
            devicesScreen()
            licensesScreen()
        }
    }
}

/** Switches between the device screens without stacking them. */
fun NavHostController.navigateToDevice(destination: DeviceDestination) {
    navigate(
        destination.route,
        navOptions {
            popUpTo(DeviceGraphRoute) { saveState = true }
            launchSingleTop = true
            restoreState = true
        },
    )
}

/** Switches top-level tabs. */
fun NavHostController.navigateToTopLevel(destination: SBDestination) {
    navigate(
        destination.route,
        navOptions {
            popUpTo(graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        },
    )
}
