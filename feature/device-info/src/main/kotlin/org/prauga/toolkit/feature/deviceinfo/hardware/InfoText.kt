// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import android.content.res.Resources
import androidx.annotation.StringRes
import org.prauga.toolkit.core.designsystem.format.UnitFormatter

/** A readout kept unresolved so the view model can build it without resources. */
internal sealed interface InfoText {
    data class Raw(val text: String) : InfoText

    /** [args] that are themselves [InfoText] are resolved before formatting. */
    data class Res(@param:StringRes val id: Int, val args: List<Any> = emptyList()) : InfoText

    data class Joined(val parts: List<InfoText>, val separator: String) : InfoText

    data class Bytes(val bytes: Long) : InfoText

    data class Frequency(val khz: Int) : InfoText

    data class Percent(val value: Int) : InfoText

    data class Temperature(val celsius: Float) : InfoText

    data class Current(val microAmps: Int) : InfoText

    data class Voltage(val milliVolts: Int) : InfoText

    data class Capacity(val milliAmpHours: Int) : InfoText

    data class Charge(val microAmpHours: Int) : InfoText

    data class DateTime(val epochMillis: Long) : InfoText

    fun isBlank(): Boolean =
        when (this) {
            is Raw -> text.isBlank()
            is Joined -> parts.all { it.isBlank() }
            is Res,
            is Bytes,
            is Frequency,
            is Percent,
            is Temperature,
            is Current,
            is Voltage,
            is Capacity,
            is Charge,
            is DateTime -> false
        }

    fun resolve(resources: Resources, format: UnitFormatter): String =
        when (this) {
            is Raw -> text
            is Res -> {
                val formatArgs =
                    args.map { if (it is InfoText) it.resolve(resources, format) else it }
                resources.getString(id, *formatArgs.toTypedArray())
            }
            is Joined -> parts.joinToString(separator) { it.resolve(resources, format) }
            is Bytes -> format.bytes(bytes)
            is Frequency -> format.frequency(khz)
            is Percent -> format.percent(value)
            is Temperature -> format.temperature(celsius)
            is Current -> format.current(microAmps)
            is Voltage -> format.voltage(milliVolts)
            is Capacity -> format.capacity(milliAmpHours)
            is Charge -> format.charge(microAmpHours)
            is DateTime -> format.dateTime(epochMillis)
        }
}
