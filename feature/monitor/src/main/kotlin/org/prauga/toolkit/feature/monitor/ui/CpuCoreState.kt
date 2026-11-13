// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import org.prauga.toolkit.core.model.CpuCluster

internal data class CpuCoreState(
    val index: Int,
    val loadHistory: List<Float>,
    val load: Float?,
    val khz: Int?,
    val cluster: CpuCluster?,
)
