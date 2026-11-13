// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.navigation

import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata

private data object FullScreenKey : NavMetadataKey<Boolean>

/** Entry metadata for a screen opened over a tab: the shell hides the nav bar and shows Back. */
val FullScreenMetadata: Map<String, Any> = metadata { put(FullScreenKey, true) }

fun Map<String, Any>.isFullScreen(): Boolean = this[FullScreenKey] == true
