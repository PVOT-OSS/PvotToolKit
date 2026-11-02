// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.deviceinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotSpacing
import dev.danascape.kernelmanager.core.designsystem.component.ValueBlock

private val SensorRowGap = 3.dp

@Composable
fun SensorsScreen(
    modifier: Modifier = Modifier,
    viewModel: SensorsViewModel = viewModel(factory = SensorsViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SensorsContent(state = state, modifier = modifier)
}

@Composable
private fun SensorsContent(
    state: SensorsUiState,
    modifier: Modifier = Modifier,
) {
    val sensors = state.sensors

    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
        verticalArrangement = Arrangement.spacedBy(SensorRowGap),
    ) {
        item(key = "header") {
            SensorHeader(count = sensors.size, deviceName = state.deviceName)
        }
        if (sensors.isNotEmpty()) {
            item(key = "title") {
                GroupTitle(stringResource(R.string.sensors_available))
            }
            itemsIndexed(items = sensors, key = { _, sensor -> sensor.name }) { index, sensor ->
                ValueBlock(
                    label = sensor.name,
                    value = stringResource(R.string.sensors_subtitle, sensor.type, sensor.vendor),
                    index = index,
                    count = sensors.size,
                    revealIndex = null,
                )
            }
        }
    }
}

@Composable
private fun SensorHeader(
    count: Int,
    deviceName: String?,
) {
    PvotCard(
        modifier =
            Modifier
                .fillMaxWidth()
                .pvotReveal(0),
    ) {
        Column(
            modifier = Modifier.padding(PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.sensors_kicker),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
            Text(
                text = pluralStringResource(R.plurals.sensors_count, count, count),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            deviceName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
