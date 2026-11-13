// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.device.CpuTopologyReader.CorePart

private val CPUINFO =
    """
    processor	: 0
    BogoMIPS	: 38.40
    Features	: fp asimd evtstrm aes pmull sha1 sha2 crc32 atomics
    CPU implementer	: 0x51
    CPU architecture: 8
    CPU part	: 0x805

    processor	: 6
    BogoMIPS	: 38.40
    CPU implementer	: 0x41
    CPU part	: 0xd0d

    processor	: 7
    CPU part	: 0xd44

    Hardware	: Qualcomm Technologies, Inc SM7225
    """
        .trimIndent()

class CpuTopologyReaderTest {
    @Test
    fun `parts are keyed by processor`() {
        val parts = CpuTopologyReader.parseCoreParts(CPUINFO)

        assertEquals(CorePart("0x805", "0x51"), parts[0])
        assertEquals(CorePart("0xd0d", "0x41"), parts[6])
    }

    @Test
    fun `a processor missing a field reports it as null`() {
        assertEquals(CorePart("0xd44", null), CpuTopologyReader.parseCoreParts(CPUINFO)[7])
    }

    @Test
    fun `text without processors yields no parts`() {
        assertTrue(CpuTopologyReader.parseCoreParts("Hardware\t: qcom").isEmpty())
    }

    @Test
    fun `features come from the first Features line`() {
        assertEquals(
            listOf("fp", "asimd", "evtstrm", "aes", "pmull", "sha1", "sha2", "crc32", "atomics"),
            CpuTopologyReader.parseFeatures(CPUINFO),
        )
    }

    @Test
    fun `missing features read as empty`() {
        assertTrue(CpuTopologyReader.parseFeatures("processor\t: 0").isEmpty())
    }
}
