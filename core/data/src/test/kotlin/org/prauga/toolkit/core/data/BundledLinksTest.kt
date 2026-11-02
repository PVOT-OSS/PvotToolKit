// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.network.ToolkitJson
import org.prauga.toolkit.core.network.dto.LinksDto
import org.prauga.toolkit.core.network.dto.toDomain

/** The bundled links.json is what renders on first run with no network. */
class BundledLinksTest {
    private val asset = File("src/main/assets/links.json")

    @Test
    fun `the shipped asset exists and parses`() {
        assertTrue("missing ${asset.absolutePath}", asset.exists())

        val sections = ToolkitJson.decodeFromString<LinksDto>(asset.readText()).toDomain()

        assertTrue("no sections survived mapping", sections.isNotEmpty())
        assertTrue(
            "every section must have at least one item",
            sections.all { it.items.isNotEmpty() },
        )
        assertTrue(
            "every item must be openable or explicitly marked soon",
            sections.flatMap { it.items }.all { it.openable || it.soon },
        )
    }
}
