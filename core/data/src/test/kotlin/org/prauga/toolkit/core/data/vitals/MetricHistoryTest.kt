// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.vitals

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.model.NetworkVitals

class MetricHistoryTest {
    @Test
    fun `history keeps only the newest window of samples`() {
        val history =
            (1..HISTORY_WINDOW + 5).fold(MetricHistory()) { history, step ->
                history.append(testVitals(perCore = listOf(step.toFloat())), previous = null)
            }

        assertEquals(HISTORY_WINDOW, history.totalLoad.size)
        assertEquals(6f, history.totalLoad.first())
        assertEquals((HISTORY_WINDOW + 5).toFloat(), history.totalLoad.last())
    }

    @Test
    fun `a core without a reading keeps its series unchanged`() {
        val history =
            MetricHistory()
                .append(testVitals(perCore = listOf(0.1f, 0.2f)), previous = null)
                .append(testVitals(perCore = listOf(0.3f, null)), previous = null)

        assertEquals(listOf(listOf(0.1f, 0.3f), listOf(0.2f)), history.perCoreLoad)
    }

    @Test
    fun `a sample without load keeps the per-core series`() {
        val first = MetricHistory().append(testVitals(perCore = listOf(0.5f)), previous = null)

        val history = first.append(testVitals(perCore = null), previous = null)

        assertEquals(first.perCoreLoad, history.perCoreLoad)
        assertEquals(first.totalLoad, history.totalLoad)
    }

    @Test
    fun `network rate is the byte delta over elapsed uptime`() {
        val previous = testVitals(uptimeMillis = 1_000, network = NetworkVitals(1_000, 500))
        val current = testVitals(uptimeMillis = 3_000, network = NetworkVitals(3_000, 1_500))

        val history = MetricHistory().append(current, previous)

        assertEquals(listOf(1_500f), history.networkBytesPerSecond)
    }

    @Test
    fun `no network rate without a previous reading`() {
        val current = testVitals(uptimeMillis = 3_000, network = NetworkVitals(3_000, 1_500))

        assertTrue(MetricHistory().append(current, previous = null).networkBytesPerSecond.isEmpty())
    }

    @Test
    fun `no network rate when uptime did not advance`() {
        val previous = testVitals(uptimeMillis = 3_000, network = NetworkVitals(1_000, 0))
        val current = testVitals(uptimeMillis = 3_000, network = NetworkVitals(2_000, 0))

        assertTrue(MetricHistory().append(current, previous).networkBytesPerSecond.isEmpty())
    }

    @Test
    fun `a counter reset reads as zero rather than negative`() {
        val previous = testVitals(uptimeMillis = 1_000, network = NetworkVitals(5_000, 0))
        val current = testVitals(uptimeMillis = 2_000, network = NetworkVitals(1_000, 0))

        assertEquals(listOf(0f), MetricHistory().append(current, previous).networkBytesPerSecond)
    }
}
