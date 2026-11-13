// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.network

import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.ContentConvertException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.SerializationException
import org.prauga.toolkit.core.network.dto.LinksDto
import org.prauga.toolkit.core.network.dto.NewsFeedDto

private const val BASE_URL = "https://stormbreaker.squadri.me"
private const val NEWS_URL = "$BASE_URL/api/v1/news.json"
private const val LINKS_URL = "$BASE_URL/api/v1/links.json"
private const val CACHE_ONLY = "only-if-cached, max-stale=${Int.MAX_VALUE}"

/** Throws IOException when unreachable, [ApiStatusException] on a non-2xx, SerializationException on a bad body. */
@Singleton
class ToolkitApi @Inject constructor(engine: HttpClientEngine) {
    private val client = toolkitHttpClient(engine)

    /** With [cacheOnly] the request is answered from the disk cache or refused with a 504. */
    suspend fun news(cacheOnly: Boolean = false): NewsFeedDto =
        fetch(NEWS_URL) { if (cacheOnly) header(HttpHeaders.CacheControl, CACHE_ONLY) }

    suspend fun links(): LinksDto = fetch(LINKS_URL)

    private suspend inline fun <reified T> fetch(
        url: String,
        crossinline block: HttpRequestBuilder.() -> Unit = {},
    ): T =
        try {
            client.get(url) { block() }.body()
        } catch (failure: ResponseException) {
            throw ApiStatusException(failure.response.status.value, failure)
        } catch (failure: ContentConvertException) {
            throw SerializationException(failure.message, failure)
        } catch (failure: NoTransformationFoundException) {
            throw SerializationException(failure.message, failure)
        }
}
