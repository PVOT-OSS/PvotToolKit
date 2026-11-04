// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.navigation3.runtime.NavKey

/** Every navigation move, applied to [NavigationState]. */
class Navigator(private val state: NavigationState) {
    /** A tab key switches tabs; anything else opens on the current tab. */
    fun navigate(key: NavKey) {
        val tab = ToolkitDestination.entries.firstOrNull { it.route == key }
        if (tab != null) {
            state.selectedTab = tab
        } else {
            state.currentStack.add(key)
        }
    }

    /** A tap on the selected tab returns it to its first screen. */
    fun selectTab(tab: ToolkitDestination) {
        if (tab == state.selectedTab) {
            val stack = state.currentStack
            while (stack.size > 1) stack.removeAt(stack.lastIndex)
        } else {
            state.selectedTab = tab
        }
    }

    /** Swaps the open device screen rather than stacking another. */
    fun switchDevice(destination: DeviceDestination) {
        val stack = state.currentStack
        if (stack.lastOrNull() != destination.route) stack[stack.lastIndex] = destination.route
    }

    /** At a tab's first screen, Back returns to Discover. */
    fun goBack() {
        val stack = state.currentStack
        if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
        } else if (state.selectedTab != ToolkitDestination.DISCOVER) {
            state.selectedTab = ToolkitDestination.DISCOVER
        }
    }
}
