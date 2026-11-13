// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import org.prauga.toolkit.core.data.vitals.MetricHistory
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.Vitals

internal data class MonitorUiState(
    val profile: DeviceProfile? = null,
    val vitals: Vitals? = null,
    val history: MetricHistory = MetricHistory(),
)
