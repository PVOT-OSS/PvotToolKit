// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.news

import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.network.ToolkitEndpoints
import org.prauga.toolkit.core.network.dto.NewsFeedDto
import org.prauga.toolkit.core.network.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException

/** The newsroom, read from the site's static feed. */
class NewsRepository(
    private val client: HttpClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    // The final catch is deliberately broad: anything Ktor or the platform
    // raises that is not IO or a parse failure is reported as a server problem,
    // and the cause travels with it rather than being discarded.
    @Suppress("TooGenericExceptionCaught")
    suspend fun news(): DataResult<List<NewsPost>> =
        withContext(ioDispatcher) {
            try {
                DataResult.Success(client.get(ToolkitEndpoints.NEWS).body<NewsFeedDto>().toDomain())
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (io: IOException) {
                cached() ?: DataResult.Failure(LoadError.OFFLINE, io)
            } catch (malformed: SerializationException) {
                DataResult.Failure(LoadError.MALFORMED, malformed)
            } catch (other: Exception) {
                DataResult.Failure(LoadError.SERVER, other)
            }
        }

    /** `only-if-cached` is answered from disk or refused; it never hits the network. */
    private suspend fun cached(): DataResult.Success<List<NewsPost>>? =
        try {
            val feed: NewsFeedDto =
                client
                    .get(ToolkitEndpoints.NEWS) {
                        header(
                            HttpHeaders.CacheControl,
                            "only-if-cached, max-stale=${Int.MAX_VALUE}",
                        )
                    }
                    .body()
            DataResult.Success(feed.toDomain(), stale = true)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            null
        }
}
