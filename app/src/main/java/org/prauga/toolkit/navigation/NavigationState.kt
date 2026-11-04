// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.toolkit.core.designsystem.component.BackButton
import org.prauga.toolkit.feature.devices.DevicesRoute
import org.prauga.toolkit.feature.licenses.LicensesRoute
import org.prauga.toolkit.feature.monitor.CpuDetailRoute
import org.prauga.toolkit.feature.news.NewsRoute

/** Screens opened on top of a tab: no nav bar, and a back arrow instead. */
private val FullScreenRoutes: Set<NavKey> =
    setOf(NewsRoute, DevicesRoute, LicensesRoute, CpuDetailRoute)

/** The selected tab and one back stack per tab, kept across config changes and process death. */
@Composable
fun rememberNavigationState(): NavigationState {
    val selectedTab = rememberSaveable { mutableStateOf(ToolkitDestination.DISCOVER) }
    val backStacks = ToolkitDestination.entries.associateWith { rememberNavBackStack(it.route) }
    return remember { NavigationState(selectedTab, backStacks) }
}

/** Read by the shell; changed only through [Navigator]. */
class NavigationState(
    selectedTab: MutableState<ToolkitDestination>,
    val backStacks: Map<ToolkitDestination, NavBackStack<NavKey>>,
) {
    /** The tab lit in the nav bar; its back stack is the one on screen. */
    var selectedTab: ToolkitDestination by selectedTab

    val currentStack: NavBackStack<NavKey>
        get() = backStacks.getValue(selectedTab)

    /** Which of Hardware, Sensors or Tests is on top; while set, the nav bar shows those three. */
    val openDeviceScreen: DeviceDestination?
        get() =
            currentStack.lastOrNull()?.let { top ->
                DeviceDestination.entries.firstOrNull { it.route == top }
            }

    /** True while a screen opened on top of a tab is showing, which hides the nav bar. */
    val fullScreen: Boolean
        get() = currentStack.lastOrNull() in FullScreenRoutes

    /** Discover's stack, then the current tab's, so Back always leaves through Discover. */
    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
        onBack: () -> Unit,
    ): List<NavEntry<NavKey>> {
        val decorated =
            backStacks.mapValues { (stackTab, stack) ->
                rememberDecoratedNavEntries(
                    backStack = stack,
                    entryDecorators =
                        listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                    entryProvider = { key -> entryProvider(key).framed(stackTab, key, onBack) },
                )
            }
        val inUse =
            if (selectedTab == ToolkitDestination.DISCOVER) {
                listOf(ToolkitDestination.DISCOVER)
            } else {
                listOf(ToolkitDestination.DISCOVER, selectedTab)
            }
        return inUse.flatMap { decorated.getValue(it) }
    }
}

/** Keyed by tab, since a screen can sit on two; opaque so predictive back hides what is behind. */
private fun NavEntry<NavKey>.framed(
    tab: ToolkitDestination,
    key: NavKey,
    onBack: () -> Unit,
): NavEntry<NavKey> {
    val entry = this
    return NavEntry(
        key = key,
        contentKey = "${tab.name}/${entry.contentKey}",
        metadata = entry.metadata,
    ) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (key in FullScreenRoutes) {
                BackButton(onClick = onBack, modifier = Modifier.padding(start = PvotSpacing.XS))
            }
            entry.Content()
        }
    }
}
