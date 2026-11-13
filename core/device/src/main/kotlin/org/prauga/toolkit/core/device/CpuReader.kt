// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.io.File
import org.prauga.toolkit.core.model.CpuVitals

private const val THERMAL_ROOT = "/sys/class/thermal"

private const val MILLI_DEGREES_PER_DEGREE = 1000f

// Read per policy: cpu0's cpuinfo_max_freq is a little core's, not the SoC's peak.
internal class CpuReader(
    private val cpuRoot: File = File(CPU_ROOT),
    private val thermalRoot: File = File(THERMAL_ROOT),
) {
    fun read(): CpuVitals? {
        val policies = listPolicyDirs(cpuRoot).map { (id, dir) -> readPolicy(id, dir) }
        if (policies.isEmpty()) return null

        val coreCount =
            (listCoreDirs(cpuRoot).keys + policies.flatMap { it.relatedCores })
                .maxOrNull()
                ?.plus(1) ?: 0

        return CpuVitals(
            perCoreKhz =
                List(coreCount) { core ->
                    policies.firstOrNull { core in it.onlineCores }?.currentKhz
                },
            maxKhz = policies.mapNotNull { it.maxKhz }.maxOrNull(),
            governors = policies.mapNotNull { it.governor }.distinct(),
            temperatureC = cpuTemperatureC(),
        )
    }

    private fun readPolicy(id: Int, dir: File): Policy {
        val related = dir.resolve("related_cpus").readTextOrNull()?.let(::parseCpuList)
        val affected = dir.resolve("affected_cpus").readTextOrNull()?.let(::parseCpuList)
        return Policy(
            relatedCores = related ?: listOf(id),
            onlineCores = affected ?: related ?: listOf(id),
            currentKhz = dir.resolve("scaling_cur_freq").readIntOrNull(),
            maxKhz = dir.resolve("cpuinfo_max_freq").readIntOrNull(),
            governor = dir.resolve("scaling_governor").readTextOrNull(),
        )
    }

    private fun cpuTemperatureC(): Float? {
        val zones =
            thermalRoot.listFiles { file -> file.name.startsWith("thermal_zone") } ?: return null
        return zones
            .firstNotNullOfOrNull { zone ->
                zone
                    .resolve("type")
                    .readTextOrNull()
                    ?.takeIf { it.contains("cpu", ignoreCase = true) }
                    ?.let { zone.resolve("temp").readIntOrNull() }
            }
            ?.div(MILLI_DEGREES_PER_DEGREE)
    }

    private class Policy(
        val relatedCores: List<Int>,
        val onlineCores: List<Int>,
        val currentKhz: Int?,
        val maxKhz: Int?,
        val governor: String?,
    )
}
