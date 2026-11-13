// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import android.os.Bundle
import android.view.Window
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.platform.PvotBaseActivity
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : PvotBaseActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setPvotContent {
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            val darkTheme = state.isDarkTheme(systemDark = isSystemInDarkTheme())
            SystemBarIcons(window, darkTheme)
            PvotAppTheme(darkTheme = darkTheme) { ToolkitApp() }
        }
    }
}

/** The bars are transparent, so their icons have to follow the app's theme, not the system's. */
@Composable
private fun SystemBarIcons(window: Window, darkTheme: Boolean) {
    LaunchedEffect(window, darkTheme) {
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = !darkTheme
            isAppearanceLightNavigationBars = !darkTheme
        }
    }
}
