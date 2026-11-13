// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CpuReaderTest {
    @get:Rule val folder = TemporaryFolder()

    private val cpuRoot: File by lazy { folder.newFolder("cpu") }
    private val thermalRoot: File by lazy { folder.newFolder("thermal") }

    private fun reader() = CpuReader(cpuRoot, thermalRoot)

    private fun cores(count: Int) = repeat(count) { cpuRoot.resolve("cpu$it").mkdirs() }

    private fun policy(id: Int, vararg nodes: Pair<String, String>) {
        val dir = cpuRoot.resolve("cpufreq/policy$id").apply { mkdirs() }
        nodes.forEach { (name, value) -> dir.resolve(name).writeText("$value\n") }
    }

    @Test
    fun `frequencies come from each core's own policy`() {
        cores(8)
        policy(0, "related_cpus" to "0 1 2 3", "scaling_cur_freq" to "1000000")
        policy(4, "related_cpus" to "4 5 6 7", "scaling_cur_freq" to "2000000")

        val cpu = requireNotNull(reader().read())

        assertEquals(List(4) { 1_000_000 } + List(4) { 2_000_000 }, cpu.perCoreKhz)
    }

    @Test
    fun `the peak is the highest policy's hardware maximum, not cpu0's`() {
        cores(8)
        policy(0, "related_cpus" to "0 1 2 3", "cpuinfo_max_freq" to "1950000")
        policy(4, "related_cpus" to "4 5 6 7", "cpuinfo_max_freq" to "3110000")

        assertEquals(3_110_000, requireNotNull(reader().read()).maxKhz)
    }

    @Test
    fun `an offline core stays in place as null`() {
        cores(4)
        policy(
            0,
            "related_cpus" to "0 1 2 3",
            "affected_cpus" to "0 1 3",
            "scaling_cur_freq" to "1200000",
        )

        val cpu = requireNotNull(reader().read())

        assertEquals(listOf(1_200_000, 1_200_000, null, 1_200_000), cpu.perCoreKhz)
    }

    @Test
    fun `an unreadable policy leaves its cores null without shifting the others`() {
        cores(4)
        policy(0, "related_cpus" to "0 1")
        policy(2, "related_cpus" to "2 3", "scaling_cur_freq" to "900000")

        val cpu = requireNotNull(reader().read())

        assertEquals(listOf(null, null, 900_000, 900_000), cpu.perCoreKhz)
    }

    @Test
    fun `each policy's governor is reported once`() {
        cores(8)
        policy(0, "related_cpus" to "0-3", "scaling_governor" to "schedutil")
        policy(4, "related_cpus" to "4-6", "scaling_governor" to "performance")
        policy(7, "related_cpus" to "7", "scaling_governor" to "schedutil")

        assertEquals(listOf("schedutil", "performance"), requireNotNull(reader().read()).governors)
    }

    @Test
    fun `no cpufreq policies reads as nothing`() {
        cores(4)

        assertNull(reader().read())
    }

    @Test
    fun `cpu temperature comes from the first cpu thermal zone`() {
        cores(1)
        policy(0, "related_cpus" to "0")
        thermalRoot.resolve("thermal_zone0").apply {
            mkdirs()
            resolve("type").writeText("cpu-0-0\n")
            resolve("temp").writeText("45000\n")
        }

        assertEquals(45f, requireNotNull(reader().read()?.temperatureC), 0.001f)
    }

    @Test
    fun `cpu lists accept ranges and separators`() {
        assertEquals(listOf(0, 1, 2, 3, 6), parseCpuList("0-3,6"))
        assertEquals(listOf(4, 5, 6, 7), parseCpuList("4 5 6 7\n"))
        assertEquals(emptyList<Int>(), parseCpuList(""))
    }
}
