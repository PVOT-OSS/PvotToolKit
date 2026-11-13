// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

@Composable
internal fun rememberNavigator(): Navigator {
    val selectedTab = rememberSaveable { mutableStateOf(ToolkitDestination.DISCOVER) }
    val backStacks = ToolkitDestination.entries.associateWith { rememberNavBackStack(it.route) }
    return remember { Navigator(selectedTab, backStacks) }
}

internal class Navigator(
    private val selectedTab: MutableState<ToolkitDestination>,
    private val backStacks: Map<ToolkitDestination, NavBackStack<NavKey>>,
) {
    val state = NavigationState(selectedTab, backStacks)

    private val currentStack: NavBackStack<NavKey>
        get() = backStacks.getValue(selectedTab.value)

    /** A tab key switches to that tab as it was left; anything else opens on the current tab. */
    fun navigate(key: NavKey) {
        val tab = ToolkitDestination.entries.firstOrNull { it.route == key }
        if (tab != null) {
            selectedTab.value = tab
        } else {
            currentStack.add(key)
        }
    }

    /** A tap on the selected tab returns it to its first screen. */
    fun selectTab(tab: ToolkitDestination) {
        if (tab == selectedTab.value) {
            val stack = currentStack
            while (stack.size > 1) stack.removeAt(stack.lastIndex)
        } else {
            selectedTab.value = tab
        }
    }

    /** Swaps the open device screen rather than stacking another. */
    fun switchDevice(destination: DeviceDestination) {
        val stack = currentStack
        val top = stack.lastOrNull() ?: return
        if (DeviceDestination.of(top) != null && top != destination.route) {
            stack[stack.lastIndex] = destination.route
        }
    }

    /** At a tab's first screen, Back returns to Discover. */
    fun goBack() {
        val stack = currentStack
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        } else if (selectedTab.value != ToolkitDestination.DISCOVER) {
            selectedTab.value = ToolkitDestination.DISCOVER
        }
    }
}
