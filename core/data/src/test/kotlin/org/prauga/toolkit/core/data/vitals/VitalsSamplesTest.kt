// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.vitals

import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.prauga.toolkit.core.model.NetworkVitals

class VitalsSamplesTest {
    @Test
    fun `each sample carries the history up to and including it`() = runTest {
        val reads =
            ArrayDeque(
                listOf(
                    testVitals(
                        uptimeMillis = 1_000,
                        perCore = listOf(0.1f),
                        network = NetworkVitals(0, 0),
                    ),
                    testVitals(
                        uptimeMillis = 2_000,
                        perCore = listOf(0.2f),
                        network = NetworkVitals(100, 0),
                    ),
                    testVitals(
                        uptimeMillis = 3_000,
                        perCore = listOf(0.3f),
                        network = NetworkVitals(400, 0),
                    ),
                )
            )

        val samples = vitalsSamples { reads.removeFirst() }.take(3).toList()

        assertEquals(listOf(0.1f, 0.2f, 0.3f), samples.last().history.totalLoad)
        assertEquals(listOf(100f, 300f), samples.last().history.networkBytesPerSecond)
        assertEquals(listOf(0.1f), samples.first().history.totalLoad)
        assertEquals(3_000L, samples.last().vitals.uptimeMillis)
    }
}
