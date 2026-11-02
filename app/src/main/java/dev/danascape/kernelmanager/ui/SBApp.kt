// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.prauga.pvot.designsystem.navigation.PvotNavBar
import com.prauga.pvot.designsystem.navigation.PvotTabItem
import dev.danascape.kernelmanager.navigation.DeviceDestination
import dev.danascape.kernelmanager.navigation.SBDestination
import dev.danascape.kernelmanager.navigation.SBNavHost
import dev.danascape.kernelmanager.navigation.deviceDestination
import dev.danascape.kernelmanager.navigation.navigateToDevice
import dev.danascape.kernelmanager.navigation.navigateToTopLevel
import dev.danascape.kernelmanager.navigation.topLevelDestination

/** Pixels of scroll in one event before the nav bar reacts, so a jitter does not flip it. */
private const val SCROLL_THRESHOLD = 4f

/** The app shell: one floating nav bar over the top-level destinations. */
@Composable
fun SBApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()

    val device = backStackEntry?.destination.deviceDestination()
    val current = backStackEntry?.destination.topLevelDestination() ?: SBDestination.DISCOVER

    var navCompact by rememberSaveable { mutableStateOf(false) }
    val scrollConnection =
        remember {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (available.y < -SCROLL_THRESHOLD) {
                        navCompact = true
                    } else if (available.y > SCROLL_THRESHOLD) {
                        navCompact = false
                    }
                    return Offset.Zero
                }
            }
        }

    val deviceTabs =
        remember {
            DeviceDestination.entries.map {
                PvotTabItem(
                    iconRes = it.iconRes,
                    labelRes = it.labelRes,
                    contentDescriptionRes = it.contentDescriptionRes,
                )
            }
        }

    val tabs =
        remember {
            SBDestination.entries.map {
                PvotTabItem(
                    iconRes = it.iconRes,
                    labelRes = it.labelRes,
                    contentDescriptionRes = it.contentDescriptionRes,
                    expandedIconRes = it.expandedIconRes,
                    expandedLabelRes = it.expandedLabelRes,
                )
            }
        }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (device != null) {
                PvotNavBar(
                    selectedTab = device.ordinal,
                    onTabClick = { index ->
                        navCompact = false
                        navController.navigateToDevice(DeviceDestination.entries[index])
                    },
                    tabs = deviceTabs,
                    compact = navCompact,
                )
            } else {
                PvotNavBar(
                    selectedTab = current.ordinal,
                    onTabClick = { index ->
                        navCompact = false
                        navController.navigateToTopLevel(SBDestination.entries[index])
                    },
                    tabs = tabs,
                    compact = navCompact,
                )
            }
        },
    ) { innerPadding ->
        SBNavHost(
            navController = navController,
            modifier =
                Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollConnection)
                    .padding(top = innerPadding.calculateTopPadding()),
        )
    }
}
