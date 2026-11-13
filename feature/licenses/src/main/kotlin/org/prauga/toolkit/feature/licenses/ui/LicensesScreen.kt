// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.licenses.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.theme.PvotAppTheme
import com.prauga.pvot.designsystem.theme.PvotSpacing
import org.prauga.toolkit.core.designsystem.component.ActionRow
import org.prauga.toolkit.core.designsystem.component.SettingsGroup
import org.prauga.toolkit.core.designsystem.link.openArticle
import org.prauga.toolkit.feature.licenses.R

@Composable
internal fun LicensesScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val toolbarColor = MaterialTheme.colorScheme.surface.toArgb()

    PvotScreen(
        modifier = modifier.fillMaxSize(),
        contentPadding = PvotScreenDefaults.contentPadding(),
    ) {
        item(key = "header") {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.S),
            ) {
                Text(
                    text = stringResource(R.string.licenses_kicker),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                Text(
                    text = stringResource(R.string.licenses_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = stringResource(R.string.licenses_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item(key = "list") {
            SettingsGroup(title = stringResource(R.string.licenses_group)) {
                ThirdPartyLicenses.forEachIndexed { index, entry ->
                    ActionRow(
                        label = entry.name,
                        index = index,
                        count = ThirdPartyLicenses.size,
                        description =
                            stringResource(
                                R.string.licenses_entry_detail,
                                entry.holder,
                                entry.license,
                            ),
                        onClick = { context.openArticle(entry.url, toolbarColor) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun LicensesPreview() {
    PvotAppTheme { LicensesScreen() }
}
