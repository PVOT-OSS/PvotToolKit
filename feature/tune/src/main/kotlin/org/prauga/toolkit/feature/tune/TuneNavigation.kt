// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.tune

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object TuneRoute

fun NavController.navigateToTune(navOptions: NavOptions? = null) = navigate(TuneRoute, navOptions)

fun NavGraphBuilder.tuneScreen() {
    composable<TuneRoute> { TuneScreen() }
}
