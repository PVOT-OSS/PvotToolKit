// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import android.app.Activity
import android.os.Bundle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.platform.PvotBaseActivity
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.prauga.toolkit.core.data.settings.ThemeRepository
import org.prauga.toolkit.core.model.ThemePreference
import org.prauga.toolkit.ui.ToolkitApp

@AndroidEntryPoint
class MainActivity : PvotBaseActivity() {
    @Inject lateinit var themeRepository: ThemeRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setPvotContent {
            val darkTheme = isDarkTheme(themeRepository)
            SystemBarIcons(darkTheme)
            PvotAppTheme(darkTheme = darkTheme) { ToolkitApp() }
        }
    }
}

/** The bars are transparent, so their icons have to follow the app's theme, not the system's. */
@Composable
private fun SystemBarIcons(darkTheme: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = (view.context as Activity).window
    LaunchedEffect(darkTheme) {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}

/** Seeded from the system so the first frame matches the launch window. */
@Composable
private fun isDarkTheme(repository: ThemeRepository): Boolean {
    val systemDark = isSystemInDarkTheme()
    val preference by
        repository.theme.collectAsStateWithLifecycle(initialValue = ThemePreference.SYSTEM)
    return when (preference) {
        ThemePreference.SYSTEM -> systemDark
        ThemePreference.LIGHT -> false
        ThemePreference.DARK -> true
    }
}
