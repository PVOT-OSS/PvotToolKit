// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.vitals

import kotlin.math.abs
import org.prauga.toolkit.core.model.Vitals

internal const val HISTORY_WINDOW = 60

private const val MILLIS_PER_SECOND = 1000f

data class MetricHistory(
    val totalLoad: List<Float> = emptyList(),
    val perCoreLoad: List<List<Float>> = emptyList(),
    val memoryUsed: List<Float> = emptyList(),
    val batteryDrawMicroAmps: List<Float> = emptyList(),
    val networkBytesPerSecond: List<Float> = emptyList(),
) {
    fun append(vitals: Vitals, previous: Vitals?): MetricHistory {
        val cores = vitals.load?.perCore.orEmpty()
        return MetricHistory(
            totalLoad = totalLoad.window(vitals.load?.average),
            perCoreLoad =
                if (cores.isEmpty()) {
                    perCoreLoad
                } else {
                    List(cores.size) { core ->
                        perCoreLoad.getOrNull(core).orEmpty().window(cores[core])
                    }
                },
            memoryUsed = memoryUsed.window(vitals.memory?.usedFraction),
            batteryDrawMicroAmps =
                batteryDrawMicroAmps.window(
                    vitals.battery?.currentMicroAmps?.let { abs(it).toFloat() }
                ),
            networkBytesPerSecond = networkBytesPerSecond.window(networkRate(previous, vitals)),
        )
    }
}

// TrafficStats counts bytes since boot, so the rate is the delta over elapsed uptime.
private fun networkRate(previous: Vitals?, current: Vitals): Float? {
    val total = current.network?.let { it.rxBytes + it.txBytes } ?: return null
    val previousTotal = previous?.network?.let { it.rxBytes + it.txBytes } ?: return null
    val seconds = (current.uptimeMillis - previous.uptimeMillis) / MILLIS_PER_SECOND
    if (seconds <= 0f) return null
    return (total - previousTotal).coerceAtLeast(0) / seconds
}

private fun List<Float>.window(value: Float?): List<Float> =
    if (value == null) this else (this + value).takeLast(HISTORY_WINDOW)
