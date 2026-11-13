// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

class CpuIdleSample
internal constructor(internal val elapsedNanos: Long, internal val idleMicrosPerCore: List<Long?>)
