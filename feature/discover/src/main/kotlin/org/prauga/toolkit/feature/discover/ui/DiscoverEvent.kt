// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover.ui

internal sealed interface DiscoverEvent {
    data object DeviceClicked : DiscoverEvent

    data object MonitoringClicked : DiscoverEvent

    data object NewsArchiveClicked : DiscoverEvent

    data class HeadlineClicked(val url: String) : DiscoverEvent

    data object RetryHeadlinesClicked : DiscoverEvent
}
