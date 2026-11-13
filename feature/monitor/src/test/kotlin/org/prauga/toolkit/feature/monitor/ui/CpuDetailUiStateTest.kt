// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.monitor.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.data.vitals.MetricHistory
import org.prauga.toolkit.core.data.vitals.VitalsSample
import org.prauga.toolkit.core.model.CpuCluster
import org.prauga.toolkit.core.model.CpuLoad
import org.prauga.toolkit.core.model.CpuTopology
import org.prauga.toolkit.core.model.CpuVitals
import org.prauga.toolkit.core.model.SleepStats
import org.prauga.toolkit.core.model.ThermalStatus
import org.prauga.toolkit.core.model.Vitals

class CpuDetailUiStateTest {
    private val little = cluster(id = 0, cores = listOf(0, 1))
    private val big = cluster(id = 2, cores = listOf(2))
    private val topology = CpuTopology(listOf(little, big), coreCount = 3, features = emptyList())

    private val vitals =
        Vitals(
            cpu = CpuVitals(listOf(300_000, null, 2_400_000), 2_400_000, emptyList(), null),
            load = CpuLoad(listOf(0.1f, null, 0.9f)),
            memory = null,
            battery = null,
            storage = null,
            network = null,
            thermal = ThermalStatus.NONE,
            uptimeMillis = 0,
            sleep = SleepStats(0, 0),
        )

    private val sample =
        VitalsSample(
            vitals,
            MetricHistory(perCoreLoad = listOf(listOf(0.1f), listOf(0.5f), listOf(0.9f))),
        )

    @Test
    fun `each core carries its series, reading and cluster`() {
        val state = cpuDetailUiState(topology, sample)

        assertEquals(listOf(0, 1, 2), state.cores.map { it.index })
        assertEquals(listOf(0.9f), state.cores[2].loadHistory)
        assertEquals(0.9f, state.cores[2].load)
        assertEquals(2_400_000, state.cores[2].khz)
        assertEquals(big, state.cores[2].cluster)
        assertEquals(little, state.cores[1].cluster)
        assertEquals(listOf(little, big), state.clusters)
    }

    @Test
    fun `a core without a reading shows none`() {
        val core = cpuDetailUiState(topology, sample).cores[1]

        assertNull(core.load)
        assertNull(core.khz)
    }

    @Test
    fun `without a topology cores have no cluster`() {
        val state = cpuDetailUiState(null, sample)

        assertTrue(state.clusters.isEmpty())
        assertTrue(state.cores.all { it.cluster == null })
    }

    private fun cluster(id: Int, cores: List<Int>) =
        CpuCluster(
            id = id,
            cores = cores,
            minKhz = null,
            maxKhz = null,
            hardwareMaxKhz = null,
            availableKhz = emptyList(),
            governor = null,
            availableGovernors = emptyList(),
            partId = null,
            implementer = null,
        )
}
