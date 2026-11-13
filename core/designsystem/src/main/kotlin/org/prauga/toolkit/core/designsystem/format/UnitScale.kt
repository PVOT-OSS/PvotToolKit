// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.format

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToInt

private const val KHZ_PER_MHZ = 1000
private const val KHZ_PER_GHZ = 1_000_000
private const val BYTES_PER_STEP = 1024L
private const val PERCENT = 100
private const val MILLIS_PER_MINUTE = 60_000L
private const val MINUTES_PER_HOUR = 60L
private const val MINUTES_PER_DAY = 1440L

object UnitScale {
    fun frequency(khz: Int): Measure<FrequencyUnit> =
        if (khz >= KHZ_PER_GHZ) {
            val ghz = BigDecimal(khz).divide(BigDecimal(KHZ_PER_GHZ))
            Measure(ghz.rounded(FrequencyUnit.GHZ.decimals), FrequencyUnit.GHZ)
        } else {
            Measure((khz / KHZ_PER_MHZ).toDouble(), FrequencyUnit.MHZ)
        }

    // A value that rounds up to 1024 moves to the next unit, so 1023.6 KB reads 1.0 MB.
    fun bytes(bytes: Long): Measure<ByteUnit> {
        val units = ByteUnit.entries
        var exact = BigDecimal(bytes)
        var index = 0
        val step = BigDecimal(BYTES_PER_STEP)
        while (
            index < units.lastIndex &&
                exact.rounded(units[index].decimals) >= BYTES_PER_STEP.toDouble()
        ) {
            exact = exact.divide(step)
            index++
        }
        return Measure(exact.rounded(units[index].decimals), units[index])
    }

    fun percent(fraction: Float): Int = (fraction * PERCENT).roundToInt()

    fun duration(millis: Long): DurationParts {
        val totalMinutes = millis / MILLIS_PER_MINUTE
        return DurationParts(
            days = totalMinutes / MINUTES_PER_DAY,
            hours = (totalMinutes % MINUTES_PER_DAY) / MINUTES_PER_HOUR,
            minutes = totalMinutes % MINUTES_PER_HOUR,
        )
    }

    private fun BigDecimal.rounded(decimals: Int): Double =
        setScale(decimals, RoundingMode.HALF_UP).toDouble()
}
