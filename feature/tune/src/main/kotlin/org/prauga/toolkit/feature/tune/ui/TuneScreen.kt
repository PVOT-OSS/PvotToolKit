// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.tune.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.prauga.toolkit.core.designsystem.component.SectionPlaceholder
import org.prauga.toolkit.feature.tune.R

@Composable
internal fun TuneScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        title = stringResource(R.string.tune_title),
        description = stringResource(R.string.tune_description),
        modifier = modifier,
    )
}

@Preview
@Composable
private fun TuneScreenPreview() {
    PvotAppTheme { TuneScreen() }
}
