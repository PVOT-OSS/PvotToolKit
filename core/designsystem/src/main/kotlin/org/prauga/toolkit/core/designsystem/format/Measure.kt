// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.format

data class Measure<U : Enum<U>>(val value: Double, val unit: U)
