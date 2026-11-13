// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.prauga.toolkit.core.designsystem.component.Meter
import org.prauga.toolkit.core.designsystem.component.MetricCard
import org.prauga.toolkit.core.designsystem.component.MetricLine
import org.prauga.toolkit.core.designsystem.component.Sparkline
import org.prauga.toolkit.core.model.BatteryVitals
import org.prauga.toolkit.core.model.CpuTopology
import org.prauga.toolkit.core.model.GpuInfo
import org.prauga.toolkit.core.model.MemoryVitals
import org.prauga.toolkit.core.model.SleepStats
import org.prauga.toolkit.core.model.StorageVitals
import org.prauga.toolkit.core.model.Vitals

@Composable
fun MonitorScreen(
    onOpenCpuDetail: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LifecycleResumeEffect(viewModel) {
        viewModel.startSampling()
        onPauseOrDispose { viewModel.stopSampling() }
    }

    MonitorContent(state = state, onOpenCpuDetail = onOpenCpuDetail, modifier = modifier)
}

@Composable
private fun MonitorContent(
    state: MonitorUiState,
    onOpenCpuDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vitals = state.vitals
    val history = state.history

    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
    ) {
        item(key = "header") { Header() }

        state.profile?.cpu?.let { topology ->
            item(key = "cpu") {
                CpuCard(topology, vitals, history, revealIndex = 1, onClick = onOpenCpuDetail)
            }
        }
        vitals?.memory?.let { memory ->
            item(key = "memory") { MemoryCard(memory, history, revealIndex = 2) }
        }
        vitals?.battery?.let { battery ->
            item(key = "battery") { BatteryCard(battery, history, revealIndex = 3) }
        }
        vitals?.let { item(key = "network") { NetworkCard(it, history, revealIndex = 4) } }
        vitals?.storage?.let { storage ->
            item(key = "storage") { StorageCard(storage, revealIndex = 5) }
        }
        vitals?.let { item(key = "uptime") { UptimeCard(it.sleep, revealIndex = 6) } }
        state.profile?.gpu?.let { gpu -> item(key = "gpu") { GpuCard(gpu, revealIndex = 7) } }
        vitals?.let { item(key = "thermal") { ThermalCard(it, revealIndex = 8) } }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = stringResource(R.string.monitor_kicker),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
        )
        Text(
            text = stringResource(R.string.monitor_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.monitor_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CpuCard(
    topology: CpuTopology,
    vitals: Vitals?,
    history: MetricHistory,
    revealIndex: Int,
    onClick: () -> Unit,
) {
    MetricCard(
        title = stringResource(R.string.monitor_cpu),
        value =
            vitals?.load?.average?.let { percent(it) }
                ?: stringResource(R.string.monitor_collecting),
        caption = stringResource(R.string.monitor_tap_detail),
        revealIndex = revealIndex,
        onClick = onClick,
    ) {
        Sparkline(values = history.totalLoad, domain = 0f..1f)

        MetricLine(
            stringResource(
                R.string.monitor_cores_summary,
                topology.coreCount,
                topology.clusters.size,
            ),
            vitals?.cpu?.governors?.joinToString(", ").orEmpty(),
        )
        topology.clusters.forEach { cluster ->
            MetricLine(
                stringResource(R.string.monitor_cluster_peak, cluster.id),
                cluster.hardwareMaxKhz?.let { frequency(it) } ?: "—",
            )
        }
    }
}

@Composable
private fun MemoryCard(memory: MemoryVitals, history: MetricHistory, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_memory),
        value = percent(memory.usedFraction),
        caption = stringResource(R.string.monitor_of_total, bytes(memory.totalBytes)),
        revealIndex = revealIndex,
    ) {
        Sparkline(values = history.memoryUsed, domain = 0f..1f)
        MetricLine(stringResource(R.string.monitor_used), bytes(memory.usedBytes))
    }
}

@Composable
private fun BatteryCard(battery: BatteryVitals, history: MetricHistory, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_battery),
        value = stringResource(R.string.monitor_percent, battery.percent),
        caption = battery.temperatureC?.let { stringResource(R.string.monitor_celsius, it) },
        revealIndex = revealIndex,
    ) {
        Sparkline(values = history.batteryDrawMilliAmps, domain = null)

        battery.currentMicroAmps?.let {
            MetricLine(
                stringResource(R.string.monitor_draw),
                stringResource(R.string.monitor_milliamps, kotlin.math.abs(it) / 1000),
            )
        }
        battery.chargeCounterMicroAmpHours?.let {
            MetricLine(
                stringResource(R.string.monitor_capacity),
                stringResource(R.string.monitor_milliamp_hours, it / 1000),
            )
        }
        battery.health?.let {
            MetricLine(stringResource(R.string.monitor_health), stringResource(it.labelRes()))
        }
    }
}

@Composable
private fun NetworkCard(vitals: Vitals, history: MetricHistory, revealIndex: Int) {
    val network = vitals.network ?: return
    MetricCard(
        title = stringResource(R.string.monitor_network),
        value =
            stringResource(
                R.string.monitor_rate,
                bytes(history.networkBytesPerSecond.lastOrNull()?.toLong() ?: 0L),
            ),
        revealIndex = revealIndex,
    ) {
        Sparkline(values = history.networkBytesPerSecond, domain = null)
        MetricLine(stringResource(R.string.monitor_received), bytes(network.rxBytes))
        MetricLine(stringResource(R.string.monitor_sent), bytes(network.txBytes))
    }
}

/** Static, so a meter rather than a line implying movement. */
@Composable
private fun StorageCard(storage: StorageVitals, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_storage),
        value = percent(storage.usedFraction),
        caption = stringResource(R.string.monitor_of_total, bytes(storage.totalBytes)),
        revealIndex = revealIndex,
    ) {
        Meter(fraction = storage.usedFraction)
        MetricLine(stringResource(R.string.monitor_used), bytes(storage.usedBytes))
        storage.fileSystem?.let { MetricLine(stringResource(R.string.monitor_filesystem), it) }
    }
}

/** A ratio, not a series. */
@Composable
private fun UptimeCard(sleep: SleepStats, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_uptime),
        value = duration(sleep.elapsedMillis),
        revealIndex = revealIndex,
    ) {
        Meter(fraction = sleep.deepSleepFraction, color = MaterialTheme.colorScheme.tertiary)
        MetricLine(
            stringResource(R.string.monitor_deep_sleep),
            "${duration(sleep.deepSleepMillis)} · ${percent(sleep.deepSleepFraction)}",
        )
        MetricLine(stringResource(R.string.monitor_awake), duration(sleep.awakeMillis))
    }
}

/** Identification only — there is no series to draw. */
@Composable
private fun GpuCard(gpu: GpuInfo, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_gpu),
        value = gpu.renderer ?: "—",
        revealIndex = revealIndex,
    ) {
        gpu.vendor?.let { MetricLine(stringResource(R.string.monitor_gpu_vendor), it) }
        Text(
            text = stringResource(R.string.monitor_gpu_locked),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** A discrete state, so a chip rather than a chart. */
@Composable
private fun ThermalCard(vitals: Vitals, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_thermal),
        value = vitals.thermal.name.lowercase().replaceFirstChar(Char::uppercase),
        revealIndex = revealIndex,
    ) {
        Text(
            text = stringResource(R.string.monitor_thermal_note),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview
@Composable
private fun MonitorPreview() {
    PvotAppTheme { MonitorContent(state = MonitorUiState(), onOpenCpuDetail = {}) }
}
