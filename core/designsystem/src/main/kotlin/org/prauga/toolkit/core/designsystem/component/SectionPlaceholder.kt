// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.coreui.PvotEmptyContent
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotSpacing

@Composable
fun SectionPlaceholder(title: String, description: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = PvotSpacing.L),
        verticalArrangement = Arrangement.spacedBy(PvotSpacing.S, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        PvotEmptyContent(message = description)
    }
}

@Preview
@Composable
private fun SectionPlaceholderPreview() {
    PvotAppTheme {
        SectionPlaceholder(title = "Title", description = "One line saying what will live here.")
    }
}
