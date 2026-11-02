// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import com.prauga.pvot.designsystem.theme.PvotAppTheme

/** The app's theme: StormBreaker's brand palette and type carried on the Pvot design system. */
@Composable
fun SBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    PvotAppTheme(
        darkTheme = darkTheme,
        dynamicColor = false,
        colorScheme = if (darkTheme) SBDarkColorScheme else SBLightColorScheme,
        typography = SBTypography,
        content = content,
    )
}
