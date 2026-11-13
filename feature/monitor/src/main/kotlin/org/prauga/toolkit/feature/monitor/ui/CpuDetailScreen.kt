// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.toolkit.core.designsystem.component.MetricLine
import org.prauga.toolkit.core.designsystem.component.Sparkline
import org.prauga.toolkit.core.designsystem.format.rememberUnitFormatter
import org.prauga.toolkit.core.model.CpuCluster
import org.prauga.toolkit.feature.monitor.R

private const val LIST_SEPARATOR = ", "

private val CoreChartHeight = 44.dp

@Composable
internal fun CpuDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: CpuDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CpuDetailContent(state = state, modifier = modifier)
}

@Composable
private fun CpuDetailContent(state: CpuDetailUiState, modifier: Modifier = Modifier) {
    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
    ) {
        item(key = "header") {
            Column(
                modifier = Modifier.fillMaxWidth().pvotReveal(0),
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
            ) {
                Text(
                    text = stringResource(R.string.monitor_cpu_kicker),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = stringResource(R.string.monitor_detail_cpu),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.monitor_detail_note),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        items(items = state.cores, key = { "core-${it.index}" }) { core ->
            CoreChart(core = core, revealIndex = core.index + 1)
        }

        items(items = state.clusters, key = { "cluster-${it.id}" }) { cluster ->
            ClusterCard(cluster)
        }
    }
}

@Composable
private fun CoreChart(core: CpuCoreState, revealIndex: Int) {
    val format = rememberUnitFormatter()
    val load = core.load?.let { format.percent(it) } ?: format.unknown
    PvotCard(modifier = Modifier.fillMaxWidth().pvotReveal(revealIndex)) {
        Column(
            modifier = Modifier.padding(PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = stringResource(R.string.monitor_core, core.index),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text =
                        core.khz?.let {
                            stringResource(
                                R.string.monitor_core_reading,
                                load,
                                format.frequency(it),
                            )
                        } ?: load,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Sparkline(
                values = core.loadHistory,
                contentDescription = stringResource(R.string.monitor_core, core.index),
                domain = 0f..1f,
                height = CoreChartHeight,
            )
            core.cluster?.let {
                Text(
                    text =
                        stringResource(
                            R.string.monitor_cluster_peak_value,
                            it.id,
                            it.hardwareMaxKhz?.let(format::frequency) ?: format.unknown,
                        ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ClusterCard(cluster: CpuCluster) {
    val format = rememberUnitFormatter()
    PvotCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
        ) {
            Text(
                text = stringResource(R.string.monitor_cluster, cluster.id),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
            MetricLine(
                stringResource(R.string.monitor_freq_range),
                stringResource(
                    R.string.monitor_freq_range_value,
                    cluster.minKhz?.let(format::frequency) ?: format.unknown,
                    cluster.hardwareMaxKhz?.let(format::frequency) ?: format.unknown,
                ),
            )
            cluster.governor?.let { MetricLine(stringResource(R.string.monitor_governor), it) }
            if (cluster.availableGovernors.isNotEmpty()) {
                MetricLine(
                    stringResource(R.string.monitor_governors_available),
                    cluster.availableGovernors.joinToString(LIST_SEPARATOR),
                )
            }
            if (cluster.availableKhz.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)) {
                    Text(
                        text =
                            stringResource(R.string.monitor_opp_count, cluster.availableKhz.size),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text =
                            cluster.availableKhz.joinToString(
                                LIST_SEPARATOR,
                                transform = format::frequency,
                            ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CpuDetailPreview() {
    val cluster =
        CpuCluster(
            id = 0,
            cores = listOf(0, 1),
            minKhz = 300_000,
            maxKhz = 1_804_800,
            hardwareMaxKhz = 1_804_800,
            availableKhz = listOf(300_000, 1_017_600, 1_804_800),
            governor = "schedutil",
            availableGovernors = listOf("schedutil", "performance"),
            partId = null,
            implementer = null,
        )
    val cores =
        listOf(0.2f, 0.6f).mapIndexed { core, load ->
            CpuCoreState(core, listOf(0.1f, load / 2, load), load, 1_017_600, cluster)
        }
    PvotAppTheme { CpuDetailContent(state = CpuDetailUiState(cores, listOf(cluster))) }
}
