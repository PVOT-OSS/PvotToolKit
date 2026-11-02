// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.prauga.pvot.designsystem.theme.PvotAppTheme

/** The app's theme: StormBreaker's brand palette and type carried on the Pvot design system. */
@Composable
fun SBTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SBDarkColorScheme else SBLightColorScheme
    val extendedColors = if (darkTheme) SBDarkExtendedColors else SBLightExtendedColors

    CompositionLocalProvider(LocalSBExtendedColors provides extendedColors) {
        PvotAppTheme(
            darkTheme = darkTheme,
            dynamicColor = false,
            colorScheme = colorScheme,
            typography = SBTypography,
            content = content,
        )
    }
}

/** Entry point for the brand tokens Material 3 has no slot for. */
object SBTheme {
    val colors: SBExtendedColors
        @Composable get() = LocalSBExtendedColors.current
}
