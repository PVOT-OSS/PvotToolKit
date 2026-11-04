// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.prauga.pvot.designsystem.effects.pvotGlassBackdrop
import com.prauga.pvot.designsystem.effects.rememberPvotGlassBackdrop
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.navigation.PvotNavBar
import com.prauga.pvot.designsystem.navigation.PvotTabItem
import org.prauga.toolkit.navigation.DeviceDestination
import org.prauga.toolkit.navigation.Navigator
import org.prauga.toolkit.navigation.ToolkitDestination
import org.prauga.toolkit.navigation.ToolkitNavDisplay
import org.prauga.toolkit.navigation.rememberNavigationState

/** How the nav bar leaves and returns around screens opened on top of a tab. */
private fun <T> barMotion() = tween<T>(PvotDurations.Medium, easing = PvotMotion.Settle)

/** Pixels of scroll in one event before the nav bar reacts, so a jitter does not flip it. */
private const val SCROLL_THRESHOLD = 4f

/** The app shell: one glass nav bar floating over the screens, which are its backdrop. */
@Composable
fun ToolkitApp(modifier: Modifier = Modifier) {
    val navigationState = rememberNavigationState()
    val navigator = remember(navigationState) { Navigator(navigationState) }

    val device = navigationState.openDeviceScreen
    val current = navigationState.selectedTab

    val backdrop = rememberPvotGlassBackdrop()
    var navCompact by rememberSaveable { mutableStateOf(false) }
    val scrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -SCROLL_THRESHOLD) {
                    navCompact = true
                } else if (available.y > SCROLL_THRESHOLD) {
                    navCompact = false
                }
                return Offset.Zero
            }
        }
    }

    val deviceTabs = remember {
        DeviceDestination.entries.map {
            PvotTabItem(
                iconRes = it.iconRes,
                labelRes = it.labelRes,
                contentDescriptionRes = it.contentDescriptionRes,
            )
        }
    }

    val tabs = remember {
        ToolkitDestination.entries.map {
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
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        bottomBar = {
            AnimatedVisibility(
                visible = !navigationState.fullScreen,
                enter = slideInVertically(barMotion()) { it } + fadeIn(barMotion()),
                exit = slideOutVertically(barMotion()) { it } + fadeOut(barMotion()),
            ) {
                if (device != null) {
                    PvotNavBar(
                        selectedTab = device.ordinal,
                        onTabClick = { index ->
                            navCompact = false
                            navigator.switchDevice(DeviceDestination.entries[index])
                        },
                        tabs = deviceTabs,
                        backdrop = backdrop,
                        compact = navCompact,
                    )
                } else {
                    PvotNavBar(
                        selectedTab = current.ordinal,
                        onTabClick = { index ->
                            navCompact = false
                            navigator.selectTab(ToolkitDestination.entries[index])
                        },
                        tabs = tabs,
                        backdrop = backdrop,
                        compact = navCompact,
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier.fillMaxSize()
                    .pvotGlassBackdrop(backdrop)
                    .background(MaterialTheme.colorScheme.background)
        ) {
            ToolkitNavDisplay(
                state = navigationState,
                navigator = navigator,
                modifier =
                    Modifier.fillMaxSize()
                        .nestedScroll(scrollConnection)
                        .padding(top = innerPadding.calculateTopPadding()),
            )
        }
    }
}
