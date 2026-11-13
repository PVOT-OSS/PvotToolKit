// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.news

import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.network.ToolkitApi

/** The newsroom, read from the site's static feed. */
@Singleton
class NewsRepository @Inject constructor(private val api: ToolkitApi) {
    // The final catch is deliberately broad: anything Ktor or the platform
    // raises that is not IO or a parse failure is reported as a server problem,
    // and the cause travels with it rather than being discarded.
    @Suppress("TooGenericExceptionCaught")
    suspend fun news(): DataResult<List<NewsPost>> =
        withContext(Dispatchers.IO) {
            try {
                DataResult.Success(api.news().toDomain())
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

    private suspend fun cached(): DataResult.Success<List<NewsPost>>? =
        try {
            DataResult.Success(api.news(cacheOnly = true).toDomain(), stale = true)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            null
        }
}
