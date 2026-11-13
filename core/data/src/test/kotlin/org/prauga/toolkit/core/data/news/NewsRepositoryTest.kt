// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.news

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.network.ToolkitApi

private const val FEED =
    """{"schema":1,"posts":[{"id":"a","title":"A","date":"2026-08-11","url":"https://example.invalid/a/"}]}"""

private val JsonHeaders = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())

class NewsRepositoryTest {
    private fun repository(handler: MockRequestHandler) =
        NewsRepository(ToolkitApi(MockEngine(handler)))

    @Test
    fun `a reachable feed is fresh`() = runTest {
        val result = repository { respond(FEED, HttpStatusCode.OK, JsonHeaders) }.news()

        assertEquals(DataResult.Success(listOf("a"), stale = false), result.ids())
    }

    @Test
    fun `offline serves the cached feed as stale`() = runTest {
        val result =
            repository { request ->
                    if (request.isCacheOnly()) {
                        respond(FEED, HttpStatusCode.OK, JsonHeaders)
                    } else {
                        throw IOException("offline")
                    }
                }
                .news()

        assertEquals(DataResult.Success(listOf("a"), stale = true), result.ids())
    }

    @Test
    fun `offline with nothing cached is an offline failure`() = runTest {
        val result =
            repository { request ->
                    if (request.isCacheOnly()) {
                        respond("", HttpStatusCode.GatewayTimeout)
                    } else {
                        throw IOException("offline")
                    }
                }
                .news()

        assertEquals(DataResult.Failure(LoadError.OFFLINE), result)
    }

    @Test
    fun `an unreadable body is malformed`() = runTest {
        val result = repository { respond("{\"posts\":", HttpStatusCode.OK, JsonHeaders) }.news()

        assertEquals(DataResult.Failure(LoadError.MALFORMED), result)
    }

    @Test
    fun `an error status is a server failure`() = runTest {
        val result = repository { respond("", HttpStatusCode.InternalServerError) }.news()

        assertEquals(DataResult.Failure(LoadError.SERVER), result)
    }

    @Test
    fun `cancellation is rethrown rather than reported as a failure`() = runTest {
        val repository = repository { throw CancellationException("cancelled") }

        val thrown =
            try {
                repository.news()
                null
            } catch (cancellation: CancellationException) {
                cancellation
            }

        assertNotNull(thrown)
    }

    private fun HttpRequestData.isCacheOnly(): Boolean =
        headers[HttpHeaders.CacheControl].orEmpty().contains("only-if-cached")

    private fun DataResult<List<NewsPost>>.ids(): DataResult<List<String>> =
        when (this) {
            is DataResult.Success -> DataResult.Success(data.map { it.id }, stale)
            is DataResult.Failure -> this
        }
}
