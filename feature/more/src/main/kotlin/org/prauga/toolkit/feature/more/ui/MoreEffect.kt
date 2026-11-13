// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.more.ui

internal sealed interface MoreEffect {
    data object RequestNotificationPermission : MoreEffect

    data object OpenDevices : MoreEffect

    data object OpenNews : MoreEffect

    data object OpenLicenses : MoreEffect

    data class OpenLink(val url: String) : MoreEffect
}
