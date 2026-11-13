// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.format

import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalResources
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import org.prauga.toolkit.core.designsystem.R

private const val MICROS_PER_MILLI = 1000
private const val MILLIS_PER_UNIT = 1000f

class UnitFormatter(private val resources: Resources) {
    private val dateTimeFormatter: DateTimeFormatter =
        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            .withLocale(resources.configuration.locales[0])
            .withZone(ZoneId.systemDefault())

    val unknown: String
        get() = resources.getString(R.string.format_unknown)

    fun frequency(khz: Int): String {
        val measure = UnitScale.frequency(khz)
        return resources.getString(measure.unit.labelRes(), measure.value)
    }

    fun bytes(bytes: Long): String {
        val measure = UnitScale.bytes(bytes)
        return resources.getString(measure.unit.labelRes(), measure.value)
    }

    fun percent(fraction: Float): String = percent(UnitScale.percent(fraction))

    fun percent(value: Int): String = resources.getString(R.string.format_percent, value)

    fun duration(millis: Long): String {
        val parts = UnitScale.duration(millis)
        return when {
            parts.days > 0 ->
                resources.getString(R.string.format_duration_days, parts.days, parts.hours)
            parts.hours > 0 ->
                resources.getString(R.string.format_duration_hours, parts.hours, parts.minutes)
            else -> resources.getString(R.string.format_duration_minutes, parts.minutes)
        }
    }

    fun temperature(celsius: Float): String = resources.getString(R.string.format_celsius, celsius)

    fun current(microAmps: Int): String =
        resources.getString(R.string.format_milliamps, microAmps / MICROS_PER_MILLI)

    fun charge(microAmpHours: Int): String = capacity(microAmpHours / MICROS_PER_MILLI)

    fun capacity(milliAmpHours: Int): String =
        resources.getString(R.string.format_milliamp_hours, milliAmpHours)

    fun voltage(milliVolts: Int): String =
        resources.getString(R.string.format_volts, milliVolts / MILLIS_PER_UNIT)

    fun dateTime(epochMillis: Long): String =
        dateTimeFormatter.format(Instant.ofEpochMilli(epochMillis))
}

@Composable
fun rememberUnitFormatter(): UnitFormatter {
    val resources = LocalResources.current
    val configuration = LocalConfiguration.current
    return remember(resources, configuration) { UnitFormatter(resources) }
}

@StringRes
private fun FrequencyUnit.labelRes(): Int =
    when (this) {
        FrequencyUnit.MHZ -> R.string.format_mhz
        FrequencyUnit.GHZ -> R.string.format_ghz
    }

@StringRes
private fun ByteUnit.labelRes(): Int =
    when (this) {
        ByteUnit.B -> R.string.format_bytes
        ByteUnit.KB -> R.string.format_kilobytes
        ByteUnit.MB -> R.string.format_megabytes
        ByteUnit.GB -> R.string.format_gigabytes
        ByteUnit.TB -> R.string.format_terabytes
    }
