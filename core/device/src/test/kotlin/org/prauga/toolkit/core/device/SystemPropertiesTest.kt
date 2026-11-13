// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SystemPropertiesTest {
    private fun parse(vararg lines: String) = SystemProperties.parse(lines.asSequence())

    @Test
    fun `getprop lines become key value pairs`() {
        val values = parse("[ro.board.platform]: [lito]", "[ro.boot.flash.locked]: [0]")

        assertEquals(mapOf("ro.board.platform" to "lito", "ro.boot.flash.locked" to "0"), values)
    }

    @Test
    fun `values keep brackets and colons inside them`() {
        val values = parse("[ro.build.description]: [lito-user 14 [x]: y release-keys]")

        assertEquals("lito-user 14 [x]: y release-keys", values["ro.build.description"])
    }

    @Test
    fun `surrounding whitespace is ignored and malformed lines are dropped`() {
        val values = parse("  [ro.hardware]: [qcom]  ", "not a property", "[broken: [x]")

        assertEquals(mapOf("ro.hardware" to "qcom"), values)
    }

    @Test
    fun `a blank value reads as absent`() {
        val properties = SystemProperties(parse("[ro.crypto.type]: []"))

        assertNull(properties["ro.crypto.type"])
    }
}
