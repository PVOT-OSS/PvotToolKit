// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.io.File
import org.prauga.toolkit.core.model.CpuCluster
import org.prauga.toolkit.core.model.CpuTopology

private const val CPUINFO = "/proc/cpuinfo"

internal object CpuTopologyReader {
    fun read(): CpuTopology? {
        val cpuRoot = File(CPU_ROOT)
        val policies = listPolicyDirs(cpuRoot)
        if (policies.isEmpty()) return null
        val coreIds = listCoreDirs(cpuRoot).keys

        val cpuinfo = File(CPUINFO).readTextOrNull().orEmpty()
        val parts = parseCoreParts(cpuinfo)
        val clusters =
            policies.map { (id, policy) ->
                val cores =
                    policy.resolve("related_cpus").readTextOrNull()?.let(::parseCpuList)
                        ?: listOf(id)
                CpuCluster(
                    id = id,
                    cores = cores,
                    minKhz = policy.resolve("scaling_min_freq").readIntOrNull(),
                    maxKhz = policy.resolve("scaling_max_freq").readIntOrNull(),
                    hardwareMaxKhz = policy.resolve("cpuinfo_max_freq").readIntOrNull(),
                    availableKhz =
                        policy
                            .resolve("scaling_available_frequencies")
                            .readTextOrNull()
                            ?.split(' ')
                            ?.mapNotNull(String::toIntOrNull)
                            .orEmpty(),
                    governor = policy.resolve("scaling_governor").readTextOrNull(),
                    availableGovernors =
                        policy
                            .resolve("scaling_available_governors")
                            .readTextOrNull()
                            ?.split(' ')
                            ?.filter { it.isNotBlank() }
                            .orEmpty(),
                    partId = parts[cores.firstOrNull()]?.part,
                    implementer = parts[cores.firstOrNull()]?.implementer,
                )
            }

        return CpuTopology(
            clusters = clusters,
            coreCount = coreIds.size.takeIf { it > 0 } ?: clusters.sumOf { it.cores.size },
            features = parseFeatures(cpuinfo),
        )
    }

    /** Maps core id to its part; /proc/cpuinfo lists them per processor. */
    internal fun parseCoreParts(text: String): Map<Int, CorePart> {
        val result = mutableMapOf<Int, CorePart>()
        var core: Int? = null
        var part: String? = null
        var implementer: String? = null

        text.lineSequence().forEach { line ->
            val (key, value) =
                line.split(':', limit = 2).map(String::trim).let {
                    if (it.size == 2) it[0] to it[1] else return@forEach
                }
            when (key) {
                "processor" -> {
                    core?.let { result[it] = CorePart(part, implementer) }
                    core = value.toIntOrNull()
                    part = null
                    implementer = null
                }

                "CPU part" -> {
                    part = value
                }

                "CPU implementer" -> {
                    implementer = value
                }
            }
        }
        core?.let { result[it] = CorePart(part, implementer) }
        return result
    }

    internal fun parseFeatures(text: String): List<String> =
        text
            .lineSequence()
            .firstOrNull { it.startsWith("Features") }
            ?.substringAfter(':')
            ?.trim()
            ?.split(' ')
            ?.filter { it.isNotBlank() }
            .orEmpty()

    internal data class CorePart(val part: String?, val implementer: String?)
}
