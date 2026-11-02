// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.builds

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.feature.builds.R

@Composable
fun BuildsScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        title = stringResource(R.string.builds_title),
        description = stringResource(R.string.builds_description),
        modifier = modifier.padding(),
    )
}

@Preview
@Composable
private fun BuildsScreenPreview() {
    PvotAppTheme { BuildsScreen() }
}
