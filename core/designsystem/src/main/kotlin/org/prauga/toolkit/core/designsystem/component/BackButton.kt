// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import org.prauga.toolkit.core.designsystem.R

@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Preview
@Composable
private fun BackButtonPreview() {
    PvotAppTheme { BackButton(onClick = {}) }
}
