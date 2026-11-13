// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import org.prauga.toolkit.core.data.vitals.VitalsSample
import org.prauga.toolkit.core.model.CpuCluster
import org.prauga.toolkit.core.model.CpuTopology

internal data class CpuDetailUiState(
    val cores: List<CpuCoreState> = emptyList(),
    val clusters: List<CpuCluster> = emptyList(),
)

internal fun cpuDetailUiState(topology: CpuTopology?, sample: VitalsSample): CpuDetailUiState {
    val clusters = topology?.clusters.orEmpty()
    return CpuDetailUiState(
        cores =
            sample.history.perCoreLoad.mapIndexed { core, loadHistory ->
                CpuCoreState(
                    index = core,
                    loadHistory = loadHistory,
                    load = sample.vitals.load?.perCore?.getOrNull(core),
                    khz = sample.vitals.cpu?.perCoreKhz?.getOrNull(core),
                    cluster = clusters.firstOrNull { core in it.cores },
                )
            },
        clusters = clusters,
    )
}
