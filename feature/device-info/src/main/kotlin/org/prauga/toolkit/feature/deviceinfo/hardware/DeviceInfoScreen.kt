// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.prauga.pvot.coreui.PvotLoadingContent
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.toolkit.core.designsystem.component.ValueBlock
import org.prauga.toolkit.core.designsystem.component.ValueRow
import org.prauga.toolkit.core.designsystem.format.rememberUnitFormatter
import org.prauga.toolkit.feature.deviceinfo.R
import org.prauga.toolkit.feature.deviceinfo.ui.GroupTitle

/** Longer than this and the value gets its own line rather than starving the label. */
private const val INLINE_VALUE_LIMIT = 28

private val RowGap = 3.dp

@Composable
internal fun DeviceInfoScreen(
    modifier: Modifier = Modifier,
    viewModel: DeviceInfoViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    DeviceInfoContent(state = state, onEvent = viewModel::onEvent, modifier = modifier)
}

@Composable
private fun DeviceInfoContent(
    state: DeviceInfoUiState,
    onEvent: (DeviceInfoEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        DeviceInfoUiState.Loading ->
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PvotLoadingContent()
            }
        is DeviceInfoUiState.Ready ->
            DeviceInfoTabs(
                state = state,
                onSelectTab = { onEvent(DeviceInfoEvent.TabSelected(it)) },
                modifier = modifier,
            )
    }
}

@Composable
private fun DeviceInfoTabs(
    state: DeviceInfoUiState.Ready,
    onSelectTab: (DeviceInfoTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val stripState = rememberLazyListState()
    val resources = LocalResources.current
    val format = rememberUnitFormatter()

    LaunchedEffect(state.tab) {
        stripState.animateScrollToItem(state.tab.ordinal)
        listState.scrollToItem(0)
    }

    Column(modifier = modifier.fillMaxSize()) {
        HeaderCard(
            deviceName = state.deviceName,
            codename = state.codename,
            modifier =
                Modifier.padding(start = PvotSpacing.L, end = PvotSpacing.L, top = PvotSpacing.L),
        )
        TabStrip(selected = state.tab, stripState = stripState, onSelect = onSelectTab)

        PvotScreen(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PvotScreenDefaults.contentPadding(top = 0.dp),
            verticalArrangement = Arrangement.spacedBy(RowGap),
        ) {
            state.sections.forEachIndexed { sectionIndex, section ->
                item(key = "$sectionIndex#title") {
                    GroupTitle(section.title.resolve(resources, format))
                }
                itemsIndexed(
                    items = section.lines,
                    key = { _, line -> "$sectionIndex#${line.label}" },
                ) { index, line ->
                    InfoRow(
                        label = stringResource(line.label),
                        value = line.value.resolve(resources, format),
                        index = index,
                        count = section.lines.size,
                    )
                }
            }
        }
    }
}

/** The section switcher. Scrollable because the labels will not fit at 360dp. */
@Composable
private fun TabStrip(
    selected: DeviceInfoTab,
    stripState: LazyListState,
    onSelect: (DeviceInfoTab) -> Unit,
) {
    LazyRow(
        state = stripState,
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(DeviceInfoTab.entries) { entry ->
            TabChip(
                label = stringResource(entry.labelRes),
                selected = entry == selected,
                onClick = { onSelect(entry) },
            )
        }
    }
}

@Composable
private fun TabChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(selected = selected, onClick = onClick, label = { Text(label) })
}

@Composable
private fun HeaderCard(deviceName: String?, codename: String?, modifier: Modifier = Modifier) {
    PvotCard(modifier = modifier.fillMaxWidth().pvotReveal(0)) {
        Column(
            modifier = Modifier.padding(PvotSpacing.L),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = stringResource(R.string.device_info_kicker),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
            deviceName?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            codename?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** One readout, stacked when the value is too long to sit beside its label. */
@Composable
private fun InfoRow(label: String, value: String, index: Int, count: Int) {
    if (value.length > INLINE_VALUE_LIMIT || '\n' in value) {
        ValueBlock(label = label, value = value, index = index, count = count, revealIndex = null)
    } else {
        ValueRow(label = label, value = value, index = index, count = count, revealIndex = null)
    }
}
