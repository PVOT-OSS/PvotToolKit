// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import java.util.Locale

internal fun Float.decimals(places: Int): String =
    String.format(Locale.getDefault(), "%.${places}f", this)

/** Joins a float list for display, dropping the noise of repeated identical entries. */
internal fun List<Float>.joinDistinct(places: Int, suffix: String = ""): String =
    distinct().sorted().joinToString(", ") { "${it.decimals(places)}$suffix" }
