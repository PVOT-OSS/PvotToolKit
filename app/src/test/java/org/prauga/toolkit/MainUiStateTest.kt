// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.model.ThemePreference

class MainUiStateTest {
    @Test
    fun `system preference follows the system theme`() {
        val state = MainUiState(ThemePreference.SYSTEM)
        assertTrue(state.isDarkTheme(systemDark = true))
        assertFalse(state.isDarkTheme(systemDark = false))
    }

    @Test
    fun `light preference stays light on a dark system`() {
        assertFalse(MainUiState(ThemePreference.LIGHT).isDarkTheme(systemDark = true))
    }

    @Test
    fun `dark preference stays dark on a light system`() {
        assertTrue(MainUiState(ThemePreference.DARK).isDarkTheme(systemDark = false))
    }

    @Test
    fun `starts on the system theme before the preference loads`() {
        assertTrue(MainUiState().isDarkTheme(systemDark = true))
        assertFalse(MainUiState().isDarkTheme(systemDark = false))
    }
}
