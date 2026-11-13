// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.io.File
import java.util.concurrent.TimeUnit
import org.prauga.toolkit.core.model.CpuLoad

/** Derives CPU utilisation from idle residency. */
object CpuLoadReader {
    fun sample(): CpuIdleSample? = sample(File(CPU_ROOT))

    internal fun sample(cpuRoot: File): CpuIdleSample? {
        val cores = listCoreDirs(cpuRoot)
        val coreCount = cores.keys.maxOrNull()?.plus(1) ?: return null
        val idle = List(coreCount) { core -> cores[core]?.let(::summedIdleMicros) }
        if (idle.all { it == null }) return null
        return CpuIdleSample(elapsedNanos = System.nanoTime(), idleMicrosPerCore = idle)
    }

    fun load(first: CpuIdleSample, second: CpuIdleSample): CpuLoad? {
        val windowMicros = TimeUnit.NANOSECONDS.toMicros(second.elapsedNanos - first.elapsedNanos)
        if (windowMicros <= 0) return null
        if (first.idleMicrosPerCore.size != second.idleMicrosPerCore.size) return null

        val perCore =
            first.idleMicrosPerCore.zip(second.idleMicrosPerCore) { before, after ->
                if (before == null || after == null) return@zip null
                (1f - (after - before).toFloat() / windowMicros).coerceIn(0f, 1f)
            }
        return CpuLoad(perCore).takeIf { load -> load.perCore.any { it != null } }
    }

    // A core with no readable idle time would otherwise sum to zero and read as fully busy.
    private fun summedIdleMicros(core: File): Long? {
        val states =
            core.resolve("cpuidle").listFiles { file -> file.name.startsWith("state") }
        if (states.isNullOrEmpty()) return null
        return states.sumOf { state ->
            state.resolve("time").readTextOrNull()?.toLongOrNull() ?: return null
        }
    }
}
