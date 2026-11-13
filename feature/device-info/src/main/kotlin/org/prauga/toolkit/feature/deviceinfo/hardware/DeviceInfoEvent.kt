// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

internal sealed interface DeviceInfoEvent {
    data class TabSelected(val tab: DeviceInfoTab) : DeviceInfoEvent
}
