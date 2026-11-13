// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

internal class NavigationState(
    selectedTab: State<ToolkitDestination>,
    private val backStacks: Map<ToolkitDestination, List<NavKey>>,
) {
    val selectedTab: ToolkitDestination by selectedTab

    val currentStack: List<NavKey>
        get() = backStacks.getValue(selectedTab)

    /** Which of Hardware, Sensors or Tests is on top; while set, the nav bar shows those three. */
    val openDeviceScreen: DeviceDestination?
        get() = currentStack.lastOrNull()?.let(DeviceDestination::of)

    /** Discover's stack, then the current tab's, so Back always leaves through Discover. */
    @Composable
    fun toDecoratedEntries(entryProvider: (NavKey) -> NavEntry<NavKey>): List<NavEntry<NavKey>> {
        val decorated =
            backStacks.mapValues { (stackTab, stack) ->
                rememberDecoratedNavEntries(
                    backStack = stack,
                    entryDecorators =
                        listOf(
                            rememberSaveableStateHolderNavEntryDecorator(),
                            rememberViewModelStoreNavEntryDecorator(),
                        ),
                    entryProvider = { key -> entryProvider(key).keyedBy(stackTab, key) },
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

/** A screen can sit on two tabs' stacks, so its content key carries the tab. */
private fun NavEntry<NavKey>.keyedBy(tab: ToolkitDestination, key: NavKey): NavEntry<NavKey> {
    val entry = this
    return NavEntry(
        key = key,
        contentKey = "${tab.name}/${entry.contentKey}",
        metadata = entry.metadata,
    ) {
        entry.Content()
    }
}
