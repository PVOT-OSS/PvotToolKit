// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.vitals

import org.prauga.toolkit.core.model.CpuLoad
import org.prauga.toolkit.core.model.NetworkVitals
import org.prauga.toolkit.core.model.SleepStats
import org.prauga.toolkit.core.model.ThermalStatus
import org.prauga.toolkit.core.model.Vitals

internal fun testVitals(
    uptimeMillis: Long = 0,
    perCore: List<Float?>? = null,
    network: NetworkVitals? = null,
): Vitals =
    Vitals(
        cpu = null,
        load = perCore?.let(::CpuLoad),
        memory = null,
        battery = null,
        storage = null,
        network = network,
        thermal = ThermalStatus.NONE,
        uptimeMillis = uptimeMillis,
        sleep = SleepStats(elapsedMillis = uptimeMillis, awakeMillis = uptimeMillis),
    )
