// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package dev.danascape.kernelmanager.core.designsystem.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.prauga.pvot.designsystem.components.PvotScreenDefaults
import com.prauga.pvot.designsystem.navigation.LocalPvotNavBarSizes

/** PvotScreen's padding plus the system bar the floating nav bar sits above. */
@Composable
fun screenContentPadding(top: Dp = PvotScreenDefaults.TopPadding): PaddingValues =
    PvotScreenDefaults.contentPadding(
        top = top,
        bottom =
            LocalPvotNavBarSizes.current.barHeight +
                PvotScreenDefaults.BottomSpacing +
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
    )
