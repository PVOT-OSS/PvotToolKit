// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation3.runtime.NavKey
import org.prauga.toolkit.R
import org.prauga.toolkit.feature.builds.navigation.BuildsRoute
import org.prauga.toolkit.feature.discover.navigation.DiscoverRoute
import org.prauga.toolkit.feature.monitor.navigation.MonitorRoute
import org.prauga.toolkit.feature.more.navigation.MoreRoute
import org.prauga.toolkit.feature.tune.navigation.TuneRoute

/** Top-level destinations, in nav bar order. */
internal enum class ToolkitDestination(
    val route: NavKey,
    @param:DrawableRes val iconRes: Int,
    @param:StringRes val labelRes: Int,
    @param:StringRes val contentDescriptionRes: Int,
) {
    DISCOVER(
        route = DiscoverRoute,
        iconRes = R.drawable.ic_tab_discover,
        labelRes = R.string.tab_discover,
        contentDescriptionRes = R.string.tab_discover_description,
    ),
    TUNE(
        route = TuneRoute,
        iconRes = R.drawable.ic_tab_tune,
        labelRes = R.string.tab_tune,
        contentDescriptionRes = R.string.tab_tune_description,
    ),
    MONITOR(
        route = MonitorRoute,
        iconRes = R.drawable.ic_tab_monitor,
        labelRes = R.string.tab_monitor,
        contentDescriptionRes = R.string.tab_monitor_description,
    ),
    BUILDS(
        route = BuildsRoute,
        iconRes = R.drawable.ic_tab_builds,
        labelRes = R.string.tab_builds,
        contentDescriptionRes = R.string.tab_builds_description,
    ),
    MORE(
        route = MoreRoute,
        iconRes = R.drawable.ic_tab_more,
        labelRes = R.string.tab_more,
        contentDescriptionRes = R.string.tab_more_description,
    ),
}
