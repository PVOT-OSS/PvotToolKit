// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import org.prauga.toolkit.core.model.ThemePreference

internal data class MainUiState(val theme: ThemePreference = ThemePreference.SYSTEM) {
    fun isDarkTheme(systemDark: Boolean): Boolean =
        when (theme) {
            ThemePreference.SYSTEM -> systemDark
            ThemePreference.LIGHT -> false
            ThemePreference.DARK -> true
        }
}
