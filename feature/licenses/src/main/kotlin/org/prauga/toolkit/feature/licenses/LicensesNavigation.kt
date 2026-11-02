// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.licenses

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

@Serializable data object LicensesRoute

fun NavController.navigateToLicenses(navOptions: NavOptions? = null) =
    navigate(LicensesRoute, navOptions)

fun NavGraphBuilder.licensesScreen() {
    composable<LicensesRoute> { LicensesScreen() }
}
