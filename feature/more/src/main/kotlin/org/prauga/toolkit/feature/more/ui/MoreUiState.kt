// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more.ui

import org.prauga.toolkit.core.model.AppVersion
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.model.ThemePreference

internal data class MoreUiState(
    val appVersion: AppVersion,
    val sections: List<LinkSection> = emptyList(),
    val theme: ThemePreference = ThemePreference.SYSTEM,
    val batteryMonitorEnabled: Boolean = false,
)
