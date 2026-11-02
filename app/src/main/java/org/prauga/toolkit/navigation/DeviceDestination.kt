// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import org.prauga.toolkit.R
import org.prauga.toolkit.feature.deviceinfo.DeviceInfoRoute
import org.prauga.toolkit.feature.deviceinfo.SensorsRoute
import org.prauga.toolkit.feature.deviceinfo.TestsRoute

/** The nav bar swaps to these while the device screens are open. */
enum class DeviceDestination(
    val route: Any,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
) {
    HARDWARE(
        route = DeviceInfoRoute,
        iconRes = R.drawable.ic_tab_hardware,
        labelRes = R.string.tab_device_hw,
        contentDescriptionRes = R.string.tab_device_hw_description,
    ),
    SENSORS(
        route = SensorsRoute,
        iconRes = R.drawable.ic_tab_sensors,
        labelRes = R.string.tab_sensors,
        contentDescriptionRes = R.string.tab_sensors_description,
    ),
    TESTS(
        route = TestsRoute,
        iconRes = R.drawable.ic_tab_tests,
        labelRes = R.string.tab_tests,
        contentDescriptionRes = R.string.tab_tests_description,
    ),
}

/** The device destination this entry sits under, or null when outside the device graph. */
fun NavDestination?.deviceDestination(): DeviceDestination? {
    val hierarchy = this?.hierarchy ?: return null
    return DeviceDestination.entries.firstOrNull { destination ->
        hierarchy.any { it.hasRoute(destination.route::class) }
    }
}
