// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotSpacing
import java.util.Locale
import org.prauga.toolkit.core.designsystem.component.ActionRow
import org.prauga.toolkit.core.designsystem.component.SettingsGroup
import org.prauga.toolkit.core.designsystem.component.ValueRow
import org.prauga.toolkit.core.designsystem.link.openArticle
import org.prauga.toolkit.core.model.BatteryHealth
import org.prauga.toolkit.core.model.BatteryVitals
import org.prauga.toolkit.core.model.BootState
import org.prauga.toolkit.core.model.CpuVitals
import org.prauga.toolkit.core.model.CustomRom
import org.prauga.toolkit.core.model.DeviceIdentity
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.MemoryVitals
import org.prauga.toolkit.core.model.NetworkVitals
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.model.OsBuild
import org.prauga.toolkit.core.model.SleepStats
import org.prauga.toolkit.core.model.SocInfo
import org.prauga.toolkit.core.model.StorageVitals
import org.prauga.toolkit.core.model.ThermalStatus
import org.prauga.toolkit.core.model.Vitals

private const val KHZ_PER_GHZ = 1_000_000f
private const val BYTES_PER_GB = 1_073_741_824f

@Composable
fun DiscoverScreen(
    onOpenDevice: () -> Unit,
    onOpenMonitoring: () -> Unit,
    onOpenNews: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    DiscoverContent(
        state = state,
        onOpenDevice = onOpenDevice,
        onOpenMonitoring = onOpenMonitoring,
        onOpenNews = onOpenNews,
        onHeadlineClick = { post -> context.openArticle(post.url, toolbarColor) },
        modifier = modifier,
    )
}

/** My device → what's happening → what's new. */
@Composable
private fun DiscoverContent(
    state: DiscoverUiState,
    onOpenDevice: () -> Unit,
    onOpenMonitoring: () -> Unit,
    onOpenNews: () -> Unit,
    onHeadlineClick: (NewsPost) -> Unit,
    modifier: Modifier = Modifier,
) {
    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
    ) {
        state.profile?.let { profile ->
            item(key = "identity") { IdentityCard(profile.identity, onOpenDevice) }
        }
        item(key = "update") { UpdateGroup() }
        item(key = "vitals") { VitalsGroup(state.vitals, onOpenMonitoring) }

        if (state.headlines.isNotEmpty()) {
            item(key = "news") {
                NewsGroup(
                    state.headlines,
                    onHeadlineClick = onHeadlineClick,
                    onOpenNews = onOpenNews,
                )
            }
        }
    }
}

@Composable
private fun IdentityCard(identity: DeviceIdentity, onClick: () -> Unit) {
    PvotCard(modifier = Modifier.fillMaxWidth().pvotReveal(0), onClick = onClick) {
        Column(
            modifier = Modifier.padding(PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.discover_kicker),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = stringResource(R.string.discover_tap_detail),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            identity.displayName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                text =
                    stringResource(
                        R.string.discover_identity_meta,
                        identity.codename ?: "—",
                        identity.androidRelease ?: "—",
                        identity.sdkInt,
                    ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant,
            )

            identity.kernelVersion?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            if (identity.kernelBuild.isNotEmpty()) {
                Text(
                    text = identity.kernelBuild,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            StatusChip(identity, modifier = Modifier.padding(top = 10.dp))
        }
    }
}

@Composable
private fun StatusChip(identity: DeviceIdentity, modifier: Modifier = Modifier) {
    val running = identity.isStormBreakerKernel
    Row(
        modifier =
            modifier
                .clip(PvotShapes.Pill)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .padding(horizontal = PvotSpacing.M, vertical = PvotSpacing.S),
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier.size(7.dp)
                    .clip(CircleShape)
                    .background(
                        if (running) MaterialTheme.colorScheme.tertiary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
        )
        Text(
            text =
                stringResource(
                    if (running) R.string.discover_kernel_running
                    else R.string.discover_kernel_stock
                ),
            style = MaterialTheme.typography.labelSmall,
            color =
                if (running) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Deliberately a placeholder rather than a fake "up to date". */
@Composable
private fun UpdateGroup() {
    SettingsGroup(title = stringResource(R.string.discover_update)) {
        ActionRow(
            label = stringResource(R.string.discover_update_unavailable_title),
            index = 0,
            count = 1,
            description = stringResource(R.string.discover_update_unavailable_body),
            enabled = false,
            onClick = {},
        )
    }
}

@Composable
private fun VitalsGroup(vitals: Vitals?, onOpenMonitoring: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.discover_vitals)) {
        val rows =
            buildList<@Composable (Int, Int) -> Unit> {
                vitals?.battery?.let { battery -> add { i, n -> BatteryRow(battery, i, n) } }
                vitals?.memory?.let { memory -> add { i, n -> MemoryRow(memory, i, n) } }
                vitals?.cpu?.let { cpu ->
                    add { i, n -> CpuRow(cpu, i, n) }
                    cpu.governors.takeIf { it.isNotEmpty() }?.let { governors ->
                        add { i, n ->
                            ValueRow(
                                stringResource(R.string.discover_governor),
                                governors.joinToString(", "),
                                i,
                                n,
                            )
                        }
                    }
                }
                add { i, n ->
                    ActionRow(
                        label = stringResource(R.string.discover_view_monitoring),
                        index = i,
                        count = n,
                        onClick = onOpenMonitoring,
                    )
                }
            }
        rows.forEachIndexed { index, row -> row(index, rows.size) }
    }

    if (vitals != null && vitals.cpu?.temperatureC == null) {
        Text(
            text = stringResource(R.string.discover_thermal_locked),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 4.dp, end = 4.dp),
        )
    }
}

@Composable
private fun BatteryRow(battery: BatteryVitals, index: Int, count: Int) {
    val value =
        if (battery.charging) {
            stringResource(R.string.discover_battery_charging, battery.percent)
        } else {
            stringResource(R.string.discover_percent, battery.percent)
        }
    val withTemp =
        battery.temperatureC?.let { "$value · " + stringResource(R.string.discover_celsius, it) }
            ?: value
    ValueRow(stringResource(R.string.discover_battery), withTemp, index, count)
}

@Composable
private fun MemoryRow(memory: MemoryVitals, index: Int, count: Int) {
    ValueRow(
        label = stringResource(R.string.discover_memory),
        value =
            stringResource(
                R.string.discover_memory_value,
                formatGigabytes(memory.usedBytes),
                formatGigabytes(memory.totalBytes),
            ),
        index = index,
        count = count,
    )
}

@Composable
private fun CpuRow(cpu: CpuVitals, index: Int, count: Int) {
    val current = cpu.perCoreKhz.filterNotNull().maxOrNull()
    val max = cpu.maxKhz
    val value =
        when {
            current == null -> {
                "—"
            }

            max != null -> {
                stringResource(
                    R.string.discover_cpu_value,
                    stringResource(R.string.discover_ghz, current / KHZ_PER_GHZ),
                    stringResource(R.string.discover_ghz, max / KHZ_PER_GHZ),
                )
            }

            else -> {
                stringResource(R.string.discover_ghz, current / KHZ_PER_GHZ)
            }
        }
    ValueRow(stringResource(R.string.discover_cpu), value, index, count)
}

@Composable
private fun NewsGroup(
    headlines: List<NewsPost>,
    onHeadlineClick: (NewsPost) -> Unit,
    onOpenNews: () -> Unit,
) {
    SettingsGroup(title = stringResource(R.string.discover_news)) {
        val count = headlines.size + 1
        headlines.forEachIndexed { index, post ->
            ActionRow(
                label = post.title,
                index = index,
                count = count,
                description = post.tag,
                onClick = { onHeadlineClick(post) },
            )
        }
        ActionRow(
            label = stringResource(R.string.discover_news_all),
            index = count - 1,
            count = count,
            onClick = onOpenNews,
        )
    }
}

private fun formatGigabytes(bytes: Long): String =
    String.format(Locale.getDefault(), "%.1f GB", bytes / BYTES_PER_GB)

@Preview
@Composable
private fun DiscoverPreview() {
    val identity =
        DeviceIdentity(
            codename = "billie",
            model = "BE2029",
            manufacturer = "OnePlus",
            androidRelease = "14",
            sdkInt = 34,
            kernelRelease = "4.19.322-StormBreaker",
            isStormBreakerKernel = true,
        )
    PvotAppTheme {
        DiscoverContent(
            state =
                DiscoverUiState(
                    profile =
                        DeviceProfile(
                            identity = identity,
                            os =
                                OsBuild(
                                    androidRelease = "14",
                                    sdkInt = 34,
                                    securityPatch = "2026-08-05",
                                    buildId = "UP1A.231005.007",
                                    fingerprint = null,
                                    tags = "release-keys",
                                    type = "user",
                                    rom = CustomRom("LineageOS", "21.0"),
                                ),
                            boot =
                                BootState(
                                    bootloaderUnlocked = true,
                                    verifiedBootState = "orange",
                                    encryption = "file",
                                ),
                            soc =
                                SocInfo("lito", "qcom", "Qualcomm", "SM7225", listOf("arm64-v8a")),
                            cpu = null,
                            gpu = null,
                            suBinaryPresent = true,
                        ),
                    vitals =
                        Vitals(
                            cpu =
                                CpuVitals(
                                    listOf(1_804_800, 820_000),
                                    2_208_000,
                                    listOf("schedutil"),
                                    null,
                                ),
                            load = null,
                            memory = MemoryVitals(6_600_000_000, 12_000_000_000),
                            battery =
                                BatteryVitals(
                                    72,
                                    31.4f,
                                    false,
                                    -420_000,
                                    3_100_000,
                                    BatteryHealth.GOOD,
                                    "Li-ion",
                                    4438,
                                    3,
                                    5052,
                                ),
                            storage = StorageVitals(84_000_000_000, 128_000_000_000, "f2fs"),
                            network = NetworkVitals(1_200_000_000, 240_000_000),
                            thermal = ThermalStatus.NONE,
                            uptimeMillis = 9_000_000,
                            sleep = SleepStats(9_000_000, 3_400_000),
                        ),
                ),
            onOpenDevice = {},
            onOpenMonitoring = {},
            onOpenNews = {},
            onHeadlineClick = {},
        )
    }
}
