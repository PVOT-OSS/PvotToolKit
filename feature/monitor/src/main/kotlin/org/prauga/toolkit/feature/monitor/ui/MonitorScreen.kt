// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlin.math.abs
import org.prauga.toolkit.core.data.vitals.MetricHistory
import org.prauga.toolkit.core.designsystem.component.Meter
import org.prauga.toolkit.core.designsystem.component.MetricCard
import org.prauga.toolkit.core.designsystem.component.MetricLine
import org.prauga.toolkit.core.designsystem.component.Sparkline
import org.prauga.toolkit.core.designsystem.format.rememberUnitFormatter
import org.prauga.toolkit.core.model.BatteryVitals
import org.prauga.toolkit.core.model.CpuTopology
import org.prauga.toolkit.core.model.GpuInfo
import org.prauga.toolkit.core.model.MemoryVitals
import org.prauga.toolkit.core.model.NetworkVitals
import org.prauga.toolkit.core.model.SleepStats
import org.prauga.toolkit.core.model.StorageVitals
import org.prauga.toolkit.core.model.Vitals
import org.prauga.toolkit.feature.monitor.R

private const val LIST_SEPARATOR = ", "

@Composable
internal fun MonitorScreen(
    onOpenCpuDetail: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MonitorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openCpuDetail by rememberUpdatedState(onOpenCpuDetail)

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                MonitorEffect.OpenCpuDetail -> openCpuDetail()
            }
        }
    }

    MonitorContent(
        state = state,
        onCpuCardClick = { viewModel.onEvent(MonitorEvent.CpuCardClicked) },
        modifier = modifier,
    )
}

@Composable
private fun MonitorContent(
    state: MonitorUiState,
    onCpuCardClick: () -> Unit,
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
                CpuCard(topology, vitals, history, revealIndex = 1, onClick = onCpuCardClick)
            }
        }
        vitals?.memory?.let { memory ->
            item(key = "memory") { MemoryCard(memory, history, revealIndex = 2) }
        }
        vitals?.battery?.let { battery ->
            item(key = "battery") { BatteryCard(battery, history, revealIndex = 3) }
        }
        vitals?.network?.let { network ->
            item(key = "network") { NetworkCard(network, history, revealIndex = 4) }
        }
        vitals?.storage?.let { storage ->
            item(key = "storage") { StorageCard(storage, revealIndex = 5) }
        }
        vitals?.let { item(key = "uptime") { UptimeCard(it.sleep, revealIndex = 6) } }
        state.profile?.gpu?.let { gpu -> item(key = "gpu") { GpuCard(gpu, revealIndex = 7) } }
        vitals?.thermal?.labelRes()?.let { label ->
            item(key = "thermal") { ThermalCard(label, revealIndex = 8) }
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = PvotSpacing.S),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
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
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_cpu),
        value =
            vitals?.load?.average?.let { format.percent(it) }
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
            vitals?.cpu?.governors?.joinToString(LIST_SEPARATOR).orEmpty(),
        )
        topology.clusters.forEach { cluster ->
            MetricLine(
                stringResource(R.string.monitor_cluster_peak, cluster.id),
                cluster.hardwareMaxKhz?.let { format.frequency(it) } ?: format.unknown,
            )
        }
    }
}

@Composable
private fun MemoryCard(memory: MemoryVitals, history: MetricHistory, revealIndex: Int) {
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_memory),
        value = format.percent(memory.usedFraction),
        caption = stringResource(R.string.monitor_of_total, format.bytes(memory.totalBytes)),
        revealIndex = revealIndex,
    ) {
        Sparkline(values = history.memoryUsed, domain = 0f..1f)
        MetricLine(stringResource(R.string.monitor_used), format.bytes(memory.usedBytes))
    }
}

@Composable
private fun BatteryCard(battery: BatteryVitals, history: MetricHistory, revealIndex: Int) {
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_battery),
        value = format.percent(battery.percent),
        caption = battery.temperatureC?.let { format.temperature(it) },
        revealIndex = revealIndex,
    ) {
        Sparkline(values = history.batteryDrawMicroAmps, domain = null)

        battery.currentMicroAmps?.let {
            MetricLine(stringResource(R.string.monitor_draw), format.current(abs(it)))
        }
        battery.chargeCounterMicroAmpHours?.let {
            MetricLine(stringResource(R.string.monitor_capacity), format.charge(it))
        }
        battery.health?.let {
            MetricLine(stringResource(R.string.monitor_health), stringResource(it.labelRes()))
        }
    }
}

@Composable
private fun NetworkCard(network: NetworkVitals, history: MetricHistory, revealIndex: Int) {
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_network),
        value =
            stringResource(
                R.string.monitor_rate,
                format.bytes(history.networkBytesPerSecond.lastOrNull()?.toLong() ?: 0L),
            ),
        revealIndex = revealIndex,
    ) {
        Sparkline(values = history.networkBytesPerSecond, domain = null)
        MetricLine(stringResource(R.string.monitor_received), format.bytes(network.rxBytes))
        MetricLine(stringResource(R.string.monitor_sent), format.bytes(network.txBytes))
    }
}

/** Static, so a meter rather than a line implying movement. */
@Composable
private fun StorageCard(storage: StorageVitals, revealIndex: Int) {
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_storage),
        value = format.percent(storage.usedFraction),
        caption = stringResource(R.string.monitor_of_total, format.bytes(storage.totalBytes)),
        revealIndex = revealIndex,
    ) {
        Meter(fraction = storage.usedFraction)
        MetricLine(stringResource(R.string.monitor_used), format.bytes(storage.usedBytes))
        storage.fileSystem?.let { MetricLine(stringResource(R.string.monitor_filesystem), it) }
    }
}

/** A ratio, not a series. */
@Composable
private fun UptimeCard(sleep: SleepStats, revealIndex: Int) {
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_uptime),
        value = format.duration(sleep.elapsedMillis),
        revealIndex = revealIndex,
    ) {
        Meter(fraction = sleep.deepSleepFraction, color = MaterialTheme.colorScheme.tertiary)
        MetricLine(
            stringResource(R.string.monitor_deep_sleep),
            stringResource(
                R.string.monitor_deep_sleep_value,
                format.duration(sleep.deepSleepMillis),
                format.percent(sleep.deepSleepFraction),
            ),
        )
        MetricLine(stringResource(R.string.monitor_awake), format.duration(sleep.awakeMillis))
    }
}

/** Identification only — there is no series to draw. */
@Composable
private fun GpuCard(gpu: GpuInfo, revealIndex: Int) {
    val format = rememberUnitFormatter()
    MetricCard(
        title = stringResource(R.string.monitor_gpu),
        value = gpu.renderer ?: format.unknown,
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

@Composable
private fun ThermalCard(@StringRes label: Int, revealIndex: Int) {
    MetricCard(
        title = stringResource(R.string.monitor_thermal),
        value = stringResource(label),
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
    PvotAppTheme { MonitorContent(state = MonitorUiState(), onCpuCardClick = {}) }
}
