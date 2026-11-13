// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.links

import android.content.res.AssetManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.network.ToolkitApi
import org.prauga.toolkit.core.network.ToolkitJson
import org.prauga.toolkit.core.network.dto.LinksDto

internal const val BUNDLED_LINKS_ASSET = "links.json"

/** Community and project links, published by the site with a copy bundled here. */
@Singleton
class LinksRepository
@Inject
constructor(private val api: ToolkitApi, private val assets: AssetManager) {
    suspend fun links(): List<LinkSection> = withContext(Dispatchers.IO) { remote() ?: bundled() }

    private suspend fun remote(): List<LinkSection>? =
        try {
            api.links().toDomain().takeIf { it.isNotEmpty() }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            null
        }

    private fun bundled(): List<LinkSection> =
        try {
            assets.open(BUNDLED_LINKS_ASSET).bufferedReader().use { reader ->
                ToolkitJson.decodeFromString<LinksDto>(reader.readText()).toDomain()
            }
        } catch (_: Exception) {
            emptyList()
        }
}
