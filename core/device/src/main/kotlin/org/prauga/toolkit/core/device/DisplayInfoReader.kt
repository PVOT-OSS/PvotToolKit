// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.content.Context
import android.hardware.display.DisplayManager
import android.util.DisplayMetrics
import android.view.Display
import kotlin.math.hypot
import org.prauga.toolkit.core.model.DisplayInfo
import org.prauga.toolkit.core.model.HdrType

/** Panel geometry and refresh capability, all of it public API. */
internal class DisplayInfoReader(private val context: Context) {
    @Suppress("DEPRECATION")
    fun read(): DisplayInfo? {
        val manager = context.getSystemService(DisplayManager::class.java) ?: return null
        val display = manager.getDisplay(Display.DEFAULT_DISPLAY) ?: return null

        val metrics = DisplayMetrics()
        display.getRealMetrics(metrics)

        val diagonal =
            if (metrics.xdpi > 0f && metrics.ydpi > 0f) {
                hypot(metrics.widthPixels / metrics.xdpi, metrics.heightPixels / metrics.ydpi)
            } else {
                null
            }

        return DisplayInfo(
            widthPx = metrics.widthPixels,
            heightPx = metrics.heightPixels,
            densityDpi = metrics.densityDpi,
            xDpi = metrics.xdpi,
            yDpi = metrics.ydpi,
            refreshHz = display.refreshRate,
            supportedRefreshHz = display.supportedModes.map { it.refreshRate }.distinct().sorted(),
            hdrTypes = hdrTypes(display),
            diagonalInches = diagonal,
        )
    }

    @Suppress("DEPRECATION")
    private fun hdrTypes(display: Display): List<HdrType> {
        val capabilities = display.hdrCapabilities ?: return emptyList()
        return capabilities.supportedHdrTypes.map { type ->
            when (type) {
                Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION -> HdrType.DolbyVision
                Display.HdrCapabilities.HDR_TYPE_HDR10 -> HdrType.Hdr10
                Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS -> HdrType.Hdr10Plus
                Display.HdrCapabilities.HDR_TYPE_HLG -> HdrType.Hlg
                else -> HdrType.Other(type)
            }
        }
    }
}
