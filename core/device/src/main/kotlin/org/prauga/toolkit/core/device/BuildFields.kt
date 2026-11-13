// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.os.Build

// Build fills a missing property with the literal "unknown".
internal fun String?.knownOrNull(): String? =
    this?.takeIf { it.isNotBlank() && it != Build.UNKNOWN }
