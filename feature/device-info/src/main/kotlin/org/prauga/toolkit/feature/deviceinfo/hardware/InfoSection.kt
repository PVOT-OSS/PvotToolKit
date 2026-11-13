// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

/** A titled block of readouts, resolved once so the list can emit its rows lazily. */
internal data class InfoSection(val title: String, val lines: List<InfoLine>)

internal data class InfoLine(val label: String, val value: String)
