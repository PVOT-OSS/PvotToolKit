// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.builds

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable
data object BuildsRoute

fun NavController.navigateToBuilds(navOptions: NavOptions? = null) = navigate(BuildsRoute, navOptions)

fun NavGraphBuilder.buildsScreen() {
    composable<BuildsRoute> { BuildsScreen() }
}
