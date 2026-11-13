// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more.ui

import org.prauga.toolkit.core.model.LinkItem
import org.prauga.toolkit.core.model.ThemePreference

internal sealed interface MoreEvent {
    data object DevicesClicked : MoreEvent

    data object NewsClicked : MoreEvent

    data object LicensesClicked : MoreEvent

    data class LinkClicked(val item: LinkItem) : MoreEvent

    data class ThemeSelected(val theme: ThemePreference) : MoreEvent

    data class BatteryMonitorToggled(val enabled: Boolean) : MoreEvent

    data class NotificationPermissionResult(val granted: Boolean) : MoreEvent
}
