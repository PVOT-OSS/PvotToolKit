// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.model

sealed interface HdrType {
    data object DolbyVision : HdrType

    data object Hdr10 : HdrType

    data object Hdr10Plus : HdrType

    data object Hlg : HdrType

    data class Other(val code: Int) : HdrType
}
