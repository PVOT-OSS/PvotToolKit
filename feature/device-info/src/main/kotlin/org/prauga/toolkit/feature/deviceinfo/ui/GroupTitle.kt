// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.prauga.pvot.coreui.PvotSectionHeader
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** The group heading, matching SettingsGroup's but emitted as its own list item. */
@Composable
internal fun GroupTitle(title: String) {
    PvotSectionHeader(
        title = title,
        modifier =
            Modifier.fillMaxWidth()
                .padding(start = PvotSpacing.XS, top = PvotSpacing.L, bottom = PvotSpacing.S),
    )
}
