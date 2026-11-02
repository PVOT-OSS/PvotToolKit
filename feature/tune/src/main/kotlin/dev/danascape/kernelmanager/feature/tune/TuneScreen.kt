// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.feature.tune

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.danascape.kernelmanager.core.designsystem.component.SectionPlaceholder
import dev.danascape.kernelmanager.core.designsystem.theme.SBTheme

@Composable
fun TuneScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        kicker = stringResource(R.string.tune_kicker),
        title = stringResource(R.string.tune_title),
        description = stringResource(R.string.tune_description),
        modifier = modifier.padding(),
    )
}

@Preview
@Composable
private fun TuneScreenPreview() {
    SBTheme { TuneScreen() }
}
