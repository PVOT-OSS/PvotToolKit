// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prauga.pvot.coreui.PvotSectionHeader
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.theme.PvotSpacing

private val OuterRadius = 16.dp
private val InnerRadius = 6.dp
private val RowGap = 3.dp
private val MinRowHeight = 56.dp

/** Corners for a row at [index] of [count]: the card radius outside, tight inside. */
fun groupShape(index: Int, count: Int): Shape {
    val top = if (index == 0) OuterRadius else InnerRadius
    val bottom = if (index == count - 1) OuterRadius else InnerRadius
    return RoundedCornerShape(
        topStart = top,
        topEnd = top,
        bottomStart = bottom,
        bottomEnd = bottom,
    )
}

/** A titled run of rows that read as one grouped surface. */
@Composable
fun SettingsGroup(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
    ) {
        PvotSectionHeader(
            title = title,
            modifier = Modifier.pvotReveal(0).padding(start = PvotSpacing.XS),
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(RowGap),
            content = content,
        )
    }
}

/** One row of a group. */
@Composable
private fun GroupedRow(
    index: Int,
    count: Int,
    revealIndex: Int?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    PvotCard(
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (revealIndex != null) Modifier.pvotReveal(revealIndex) else Modifier),
        onClick = onClick,
        shape = groupShape(index, count),
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(min = MinRowHeight)
                    .padding(horizontal = PvotSpacing.L, vertical = PvotSpacing.M),
            horizontalArrangement = Arrangement.spacedBy(PvotSpacing.M),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

/**
 * A label and a readout; pass a null [revealIndex] inside a lazy list so fast scrolling shows rows,
 * not fades.
 */
@Composable
fun ValueRow(
    label: String,
    value: String,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    revealIndex: Int? = index,
) {
    GroupedRow(
        index = index,
        count = count,
        revealIndex = revealIndex,
        onClick = null,
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
        )
    }
}

/** A label over its readout, for values too long to sit beside one. */
@Composable
fun ValueBlock(
    label: String,
    value: String,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier,
    revealIndex: Int? = index,
) {
    GroupedRow(
        index = index,
        count = count,
        revealIndex = revealIndex,
        onClick = null,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** A tappable row. */
@Composable
fun ActionRow(
    label: String,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    trailing: String? = null,
    enabled: Boolean = true,
    revealIndex: Int? = index,
) {
    GroupedRow(
        index = index,
        count = count,
        revealIndex = revealIndex,
        onClick = onClick.takeIf { enabled },
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color =
                    if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        trailing?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
