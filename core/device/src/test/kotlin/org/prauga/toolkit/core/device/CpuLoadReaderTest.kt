// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CpuLoadReaderTest {
    @get:Rule val folder = TemporaryFolder()

    private fun sampleAt(seconds: Long, vararg idleMicros: Long?) =
        CpuIdleSample(
            elapsedNanos = TimeUnit.SECONDS.toNanos(seconds),
            idleMicrosPerCore = idleMicros.toList(),
        )

    @Test
    fun `a fully idle core reads as zero busy`() {
        val load = requireNotNull(CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 1_000_000)))
        assertEquals(0f, requireNotNull(load.perCore.single()), 0.001f)
    }

    @Test
    fun `a core that never idles reads as fully busy`() {
        val load = requireNotNull(CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 0)))
        assertEquals(1f, requireNotNull(load.perCore.single()), 0.001f)
    }

    @Test
    fun `half the window spent idle reads as half busy`() {
        val load = requireNotNull(CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 500_000)))
        assertEquals(0.5f, requireNotNull(load.perCore.single()), 0.001f)
    }

    @Test
    fun `idle exceeding the window clamps to idle rather than going negative`() {
        val load = requireNotNull(CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 1_200_000)))
        assertEquals(0f, requireNotNull(load.perCore.single()), 0.001f)
    }

    @Test
    fun `average spans all cores`() {
        val load = requireNotNull(CpuLoadReader.load(sampleAt(0, 0, 0), sampleAt(1, 1_000_000, 0)))
        assertEquals(0.5f, requireNotNull(load.average), 0.001f)
    }

    @Test
    fun `samples taken out of order produce nothing`() {
        assertNull(CpuLoadReader.load(sampleAt(1, 0), sampleAt(0, 0)))
    }

    @Test
    fun `a changed core count produces nothing rather than a mismatched read`() {
        assertNull(CpuLoadReader.load(sampleAt(0, 0), sampleAt(1, 0, 0)))
    }

    @Test
    fun `a core without idle data reads as unknown rather than busy`() {
        val load = requireNotNull(CpuLoadReader.load(sampleAt(0, null, 0), sampleAt(1, null, 0)))
        assertEquals(listOf(null, 1f), load.perCore)
        assertEquals(1f, requireNotNull(load.average), 0.001f)
    }

    @Test
    fun `no core with idle data produces nothing`() {
        assertNull(CpuLoadReader.load(sampleAt(0, null), sampleAt(1, null)))
    }

    @Test
    fun `sampling keeps an unreadable core in its slot as null`() {
        val root = folder.newFolder("cpu")
        root.resolve("cpu0/cpuidle/state0").apply { mkdirs() }.resolve("time").writeText("150\n")
        root.resolve("cpu0/cpuidle/state1").apply { mkdirs() }.resolve("time").writeText("50\n")
        root.resolve("cpu1").mkdirs()
        root.resolve("cpu2/cpuidle/state0").apply { mkdirs() }.resolve("time").writeText("7\n")

        val sample = requireNotNull(CpuLoadReader.sample(root))

        assertEquals(listOf(200L, null, 7L), sample.idleMicrosPerCore)
    }

    @Test
    fun `a core missing one state's time reads as null`() {
        val root = folder.newFolder("cpu")
        root.resolve("cpu0/cpuidle/state0").apply { mkdirs() }.resolve("time").writeText("150\n")
        root.resolve("cpu0/cpuidle/state1").mkdirs()
        root.resolve("cpu1/cpuidle/state0").apply { mkdirs() }.resolve("time").writeText("9\n")

        val sample = requireNotNull(CpuLoadReader.sample(root))

        assertNull(sample.idleMicrosPerCore[0])
        assertNotNull(sample.idleMicrosPerCore[1])
    }

    @Test
    fun `no readable core produces no sample`() {
        val root = folder.newFolder("cpu")
        root.resolve("cpu0").mkdirs()

        assertNull(CpuLoadReader.sample(root))
    }
}
