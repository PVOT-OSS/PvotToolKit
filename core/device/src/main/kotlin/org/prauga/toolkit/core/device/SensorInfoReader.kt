// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import org.prauga.toolkit.core.model.SensorInfo

/** The sensor roster, which carries the vendor part names no sysfs node will give us. */
internal class SensorInfoReader(private val context: Context) {
    fun read(): List<SensorInfo> {
        val manager = context.getSystemService(SensorManager::class.java) ?: return emptyList()
        return manager
            .getSensorList(Sensor.TYPE_ALL)
            .map { sensor ->
                SensorInfo(
                    name = sensor.name,
                    vendor = sensor.vendor,
                    type = readableType(sensor),
                    power = sensor.power,
                    resolution = sensor.resolution,
                    maxRange = sensor.maximumRange,
                )
            }
            .sortedBy { it.type }
    }

    private fun readableType(sensor: Sensor): String =
        sensor.stringType.substringAfterLast('.').replace('_', ' ').replaceFirstChar {
            it.uppercase()
        }
}
