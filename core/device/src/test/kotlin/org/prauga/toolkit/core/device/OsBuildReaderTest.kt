// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.prauga.toolkit.core.model.BootState
import org.prauga.toolkit.core.model.CustomRom

class OsBuildReaderTest {
    private fun reader(vararg properties: Pair<String, String>) =
        OsBuildReader(SystemProperties(properties.toMap()))

    @Test
    fun `a known ROM property names the ROM and its version`() {
        val rom = reader("ro.lineage.version" to "21.0-20260801-NIGHTLY-lito").detectRom()

        assertEquals(CustomRom("LineageOS", "21.0-20260801-NIGHTLY-lito"), rom)
    }

    @Test
    fun `a specific ROM wins over the generic modversion`() {
        val rom = reader("ro.modversion" to "x", "ro.crdroid.version" to "10.6").detectRom()

        assertEquals(CustomRom("crDroid", "10.6"), rom)
    }

    @Test
    fun `no ROM property reads as stock`() {
        assertNull(reader("ro.build.type" to "user").detectRom())
    }

    @Test
    fun `boot state reflects the bootloader lock and verified boot`() {
        val boot =
            reader(
                    "ro.boot.flash.locked" to "0",
                    "ro.boot.verifiedbootstate" to "orange",
                    "ro.crypto.type" to "file",
                )
                .readBootState()

        assertEquals(BootState(true, "orange", "file"), boot)
    }

    @Test
    fun `a locked bootloader reads as locked`() {
        assertEquals(
            false,
            reader("ro.boot.flash.locked" to "1").readBootState().bootloaderUnlocked,
        )
    }

    @Test
    fun `missing boot properties read as unknown`() {
        assertEquals(BootState(null, null, null), reader().readBootState())
    }
}
