// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.devices

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.prauga.toolkit.core.designsystem.component.SectionPlaceholder

@Composable
fun DevicesScreen(modifier: Modifier = Modifier) {
    SectionPlaceholder(
        title = stringResource(R.string.devices_title),
        description = stringResource(R.string.devices_description),
        modifier = modifier.padding(),
    )
}

@Preview
@Composable
private fun DevicesScreenPreview() {
    PvotAppTheme { DevicesScreen() }
}
