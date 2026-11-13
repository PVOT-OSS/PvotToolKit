// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun Meter(
    fraction: Float,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val shape = RoundedCornerShape(50)
    val progress = fraction.coerceIn(0f, 1f)
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(6.dp)
                .progressSemantics(progress)
                .semantics { contentDescription?.let { this.contentDescription = it } }
                .clip(shape)
                .background(color.copy(alpha = 0.16f))
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().clip(shape).background(color)
        )
    }
}
