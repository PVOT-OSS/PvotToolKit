// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.designsystem.navigation.isFullScreen
import org.prauga.toolkit.feature.deviceinfo.navigation.DeviceInfoRoute
import org.prauga.toolkit.feature.deviceinfo.navigation.SensorsRoute
import org.prauga.toolkit.feature.devices.navigation.DevicesRoute
import org.prauga.toolkit.feature.discover.navigation.DiscoverRoute
import org.prauga.toolkit.feature.licenses.navigation.LicensesRoute
import org.prauga.toolkit.feature.monitor.navigation.CpuDetailRoute
import org.prauga.toolkit.feature.monitor.navigation.MonitorRoute
import org.prauga.toolkit.feature.more.navigation.MoreRoute
import org.prauga.toolkit.feature.news.navigation.NewsRoute

class NavigatorTest {
    private val navigator =
        Navigator(
            selectedTab = mutableStateOf(ToolkitDestination.DISCOVER),
            backStacks = ToolkitDestination.entries.associateWith { NavBackStack(it.route) },
        )
    private val state = navigator.state

    @Test
    fun `back at a tab's first screen returns to discover`() {
        navigator.selectTab(ToolkitDestination.MONITOR)

        navigator.goBack()

        assertEquals(ToolkitDestination.DISCOVER, state.selectedTab)
    }

    @Test
    fun `back at discover's first screen changes nothing`() {
        navigator.goBack()

        assertEquals(ToolkitDestination.DISCOVER, state.selectedTab)
        assertEquals(listOf<NavKey>(DiscoverRoute), state.currentStack)
    }

    @Test
    fun `back pops a screen opened on the current tab`() {
        navigator.selectTab(ToolkitDestination.MORE)
        navigator.navigate(NewsRoute)

        navigator.goBack()

        assertEquals(ToolkitDestination.MORE, state.selectedTab)
        assertEquals(listOf<NavKey>(MoreRoute), state.currentStack)
    }

    @Test
    fun `reselecting the current tab returns it to its first screen`() {
        navigator.selectTab(ToolkitDestination.MORE)
        navigator.navigate(DevicesRoute)
        navigator.navigate(NewsRoute)

        navigator.selectTab(ToolkitDestination.MORE)

        assertEquals(listOf<NavKey>(MoreRoute), state.currentStack)
    }

    @Test
    fun `selecting another tab keeps the stack it left`() {
        navigator.selectTab(ToolkitDestination.MONITOR)
        navigator.navigate(CpuDetailRoute)

        navigator.selectTab(ToolkitDestination.MORE)
        navigator.selectTab(ToolkitDestination.MONITOR)

        assertEquals(listOf(MonitorRoute, CpuDetailRoute), state.currentStack)
    }

    @Test
    fun `navigating to a tab key switches to that tab as it was left`() {
        navigator.selectTab(ToolkitDestination.MONITOR)
        navigator.navigate(CpuDetailRoute)
        navigator.selectTab(ToolkitDestination.DISCOVER)

        navigator.navigate(MonitorRoute)

        assertEquals(ToolkitDestination.MONITOR, state.selectedTab)
        assertEquals(listOf(MonitorRoute, CpuDetailRoute), state.currentStack)
        assertEquals(listOf<NavKey>(DiscoverRoute), stackOf(ToolkitDestination.DISCOVER))
    }

    @Test
    fun `switching device screens replaces the open one`() {
        navigator.navigate(DeviceInfoRoute)

        navigator.switchDevice(DeviceDestination.SENSORS)

        assertEquals(listOf(DiscoverRoute, SensorsRoute), state.currentStack)
        assertEquals(DeviceDestination.SENSORS, state.openDeviceScreen)
    }

    @Test
    fun `switching device screens leaves other screens alone`() {
        navigator.navigate(NewsRoute)

        navigator.switchDevice(DeviceDestination.SENSORS)

        assertEquals(listOf(DiscoverRoute, NewsRoute), state.currentStack)
        assertNull(state.openDeviceScreen)
    }

    @Test
    fun `switching device screens on an empty stack does nothing`() {
        val empty =
            Navigator(
                selectedTab = mutableStateOf(ToolkitDestination.DISCOVER),
                backStacks = ToolkitDestination.entries.associateWith { NavBackStack() },
            )

        empty.switchDevice(DeviceDestination.SENSORS)

        assertTrue(empty.state.currentStack.isEmpty())
    }

    @Test
    fun `screens opened over a tab are full screen`() {
        val entryProvider = toolkitEntryProvider(navigator)

        listOf(NewsRoute, DevicesRoute, LicensesRoute, CpuDetailRoute).forEach {
            assertTrue("$it", entryProvider(it).metadata.isFullScreen())
        }
        (ToolkitDestination.entries.map { it.route } + DeviceInfoRoute).forEach {
            assertFalse("$it", entryProvider(it).metadata.isFullScreen())
        }
    }

    private fun stackOf(tab: ToolkitDestination): List<NavKey> {
        navigator.selectTab(tab)
        return state.currentStack.toList()
    }
}
