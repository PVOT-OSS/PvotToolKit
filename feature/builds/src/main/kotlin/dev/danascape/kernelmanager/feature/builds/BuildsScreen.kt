// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.builds

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.danascape.kernelmanager.feature.builds.R
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

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
    SBTheme { BuildsScreen() }
}
