// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.news

import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.SerializationException
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.LoadError
import org.prauga.toolkit.core.model.NewsPost
import org.prauga.toolkit.core.network.ApiStatusException
import org.prauga.toolkit.core.network.ToolkitApi
import timber.log.Timber

/** The newsroom, read from the site's static feed. */
@Singleton
class NewsRepository @Inject constructor(private val api: ToolkitApi) {
    suspend fun news(): DataResult<List<NewsPost>> =
        try {
            DataResult.Success(api.news().toDomain())
        } catch (failure: IOException) {
            Timber.i(failure, "News feed unreachable, falling back to the cached copy")
            cached() ?: DataResult.Failure(LoadError.OFFLINE)
        } catch (failure: ApiStatusException) {
            Timber.w(failure, "News feed answered HTTP %d", failure.status)
            DataResult.Failure(LoadError.SERVER)
        } catch (failure: SerializationException) {
            Timber.w(failure, "News feed is malformed")
            DataResult.Failure(LoadError.MALFORMED)
        }

    private suspend fun cached(): DataResult.Success<List<NewsPost>>? =
        try {
            DataResult.Success(api.news(cacheOnly = true).toDomain(), stale = true)
        } catch (failure: ApiStatusException) {
            Timber.i("No cached news feed (HTTP %d)", failure.status)
            null
        } catch (failure: IOException) {
            Timber.w(failure, "Cached news feed unreadable")
            null
        } catch (failure: SerializationException) {
            Timber.w(failure, "Cached news feed is malformed")
            null
        }
}
