// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.links

import android.content.res.AssetManager
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import org.prauga.toolkit.core.common.DataResult
import org.prauga.toolkit.core.common.coroutines.Dispatcher
import org.prauga.toolkit.core.common.coroutines.ToolkitDispatchers
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.network.ApiStatusException
import org.prauga.toolkit.core.network.ToolkitApi
import org.prauga.toolkit.core.network.ToolkitJson
import org.prauga.toolkit.core.network.dto.LinksDto
import timber.log.Timber

private const val BUNDLED_LINKS_ASSET = "links.json"

/** Community and project links, published by the site with a copy bundled here. */
@Singleton
class LinksRepository
internal constructor(private val api: ToolkitApi, private val readBundled: suspend () -> String) {
    @Inject
    constructor(
        api: ToolkitApi,
        assets: AssetManager,
        @Dispatcher(ToolkitDispatchers.IO) ioDispatcher: CoroutineDispatcher,
    ) : this(
        api,
        {
            withContext(ioDispatcher) {
                assets.open(BUNDLED_LINKS_ASSET).bufferedReader().use { it.readText() }
            }
        },
    )

    suspend fun links(): DataResult.Success<List<LinkSection>> =
        remote()?.let { DataResult.Success(it) } ?: DataResult.Success(bundled(), stale = true)

    private suspend fun remote(): List<LinkSection>? =
        try {
            api.links().toDomain().ifEmpty {
                Timber.w("Site published no links, falling back to the bundled copy")
                null
            }
        } catch (failure: IOException) {
            Timber.i(failure, "Links unreachable, falling back to the bundled copy")
            null
        } catch (failure: ApiStatusException) {
            Timber.w(
                failure,
                "Links answered HTTP %d, falling back to the bundled copy",
                failure.status,
            )
            null
        } catch (failure: SerializationException) {
            Timber.w(failure, "Links are malformed, falling back to the bundled copy")
            null
        }

    /**
     * A broken bundled copy is a build mistake, so it throws; BundledLinksTest guards the asset.
     */
    private suspend fun bundled(): List<LinkSection> {
        val sections = ToolkitJson.decodeFromString<LinksDto>(readBundled()).toDomain()
        check(sections.isNotEmpty()) { "$BUNDLED_LINKS_ASSET has no links to show" }
        return sections
    }
}
