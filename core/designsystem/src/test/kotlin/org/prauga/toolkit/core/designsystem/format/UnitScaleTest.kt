// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.designsystem.format

import org.junit.Assert.assertEquals
import org.junit.Test

private const val MINUTE = 60_000L
private const val HOUR = 60 * MINUTE
private const val DAY = 24 * HOUR

class UnitScaleTest {
    @Test
    fun `frequencies below a gigahertz read in whole megahertz`() {
        assertEquals(Measure(999.0, FrequencyUnit.MHZ), UnitScale.frequency(999_999))
        assertEquals(Measure(300.0, FrequencyUnit.MHZ), UnitScale.frequency(300_000))
    }

    @Test
    fun `a gigahertz and above reads in gigahertz to two places`() {
        assertEquals(Measure(1.0, FrequencyUnit.GHZ), UnitScale.frequency(1_000_000))
        assertEquals(Measure(2.21, FrequencyUnit.GHZ), UnitScale.frequency(2_208_000))
        assertEquals(Measure(1.8, FrequencyUnit.GHZ), UnitScale.frequency(1_804_800))
    }

    @Test
    fun `gigahertz round half up`() {
        assertEquals(Measure(2.0, FrequencyUnit.GHZ), UnitScale.frequency(1_995_000))
        assertEquals(Measure(1.99, FrequencyUnit.GHZ), UnitScale.frequency(1_994_999))
    }

    @Test
    fun `bytes step to the next unit at 1024`() {
        assertEquals(Measure(0.0, ByteUnit.B), UnitScale.bytes(0))
        assertEquals(Measure(1023.0, ByteUnit.B), UnitScale.bytes(1023))
        assertEquals(Measure(1.0, ByteUnit.KB), UnitScale.bytes(1024))
        assertEquals(Measure(1.0, ByteUnit.MB), UnitScale.bytes(1_048_576))
        assertEquals(Measure(1.0, ByteUnit.GB), UnitScale.bytes(1_073_741_824))
        assertEquals(Measure(1.0, ByteUnit.TB), UnitScale.bytes(1_099_511_627_776))
    }

    @Test
    fun `byte values round to the precision of their unit`() {
        assertEquals(Measure(4.0, ByteUnit.KB), UnitScale.bytes(4096))
        assertEquals(Measure(2.0, ByteUnit.KB), UnitScale.bytes(1536))
        assertEquals(Measure(1.5, ByteUnit.MB), UnitScale.bytes(1_572_864))
        assertEquals(Measure(11.2, ByteUnit.GB), UnitScale.bytes(12_000_000_000))
    }

    @Test
    fun `a value that rounds up to 1024 moves to the next unit`() {
        assertEquals(Measure(1023.0, ByteUnit.KB), UnitScale.bytes(1023 * 1024 + 511))
        assertEquals(Measure(1.0, ByteUnit.MB), UnitScale.bytes(1023 * 1024 + 512))
    }

    @Test
    fun `percent rounds the fraction to a whole number`() {
        assertEquals(0, UnitScale.percent(0f))
        assertEquals(55, UnitScale.percent(0.549f))
        assertEquals(55, UnitScale.percent(0.55f))
        assertEquals(100, UnitScale.percent(1f))
    }

    @Test
    fun `durations split into days, hours and minutes`() {
        assertEquals(DurationParts(0, 0, 59), UnitScale.duration(59 * MINUTE + 59_999))
        assertEquals(DurationParts(0, 1, 0), UnitScale.duration(HOUR))
        assertEquals(DurationParts(0, 23, 59), UnitScale.duration(DAY - 1))
        assertEquals(DurationParts(1, 0, 0), UnitScale.duration(DAY))
        assertEquals(DurationParts(2, 3, 4), UnitScale.duration(2 * DAY + 3 * HOUR + 4 * MINUTE))
    }
}
