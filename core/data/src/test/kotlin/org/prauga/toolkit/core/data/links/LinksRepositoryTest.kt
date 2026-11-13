// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.links

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.io.File
import java.io.IOException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.network.ToolkitApi

private const val REMOTE =
    """{"schema":1,"sections":[{"id":"remote","title":"Remote","items":[
      {"id":"x","label":"X","url":"https://example.invalid/"}]}]}"""

private val JsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

class LinksRepositoryTest {
    private val bundledAsset = File("src/main/assets/links.json").readText()

    private fun repository(bundled: String = bundledAsset, handler: MockRequestHandler) =
        LinksRepository(ToolkitApi(MockEngine(handler))) { bundled }

    @Test
    fun `the site's copy wins when reachable`() = runTest {
        val result = repository { respond(REMOTE, HttpStatusCode.OK, JsonHeaders) }.links()

        assertFalse(result.stale)
        assertEquals(listOf("remote"), result.data.map { it.id })
    }

    @Test
    fun `offline falls back to the bundled copy as stale`() = runTest {
        val result = repository { throw IOException("offline") }.links()

        assertTrue(result.stale)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `an error status falls back to the bundled copy`() = runTest {
        val result = repository { respond("", HttpStatusCode.NotFound) }.links()

        assertTrue(result.stale)
    }

    @Test
    fun `a published copy with no links falls back to the bundled copy`() = runTest {
        val result =
            repository { respond("""{"schema":1,"sections":[]}""", HttpStatusCode.OK, JsonHeaders) }
                .links()

        assertTrue(result.stale)
        assertTrue(result.data.isNotEmpty())
    }

    @Test
    fun `a broken bundled copy fails fast`() {
        val repository = repository(bundled = "{") { throw IOException("offline") }

        assertThrows(SerializationException::class.java) { runTest { repository.links() } }
    }

    @Test
    fun `an empty bundled copy fails fast`() {
        val repository =
            repository(bundled = """{"schema":1,"sections":[]}""") { throw IOException("offline") }

        assertThrows(IllegalStateException::class.java) { runTest { repository.links() } }
    }
}
