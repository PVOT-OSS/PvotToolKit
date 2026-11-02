// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.links

import android.content.res.AssetManager
import org.prauga.toolkit.core.model.LinkSection
import org.prauga.toolkit.core.network.ToolkitEndpoints
import org.prauga.toolkit.core.network.ToolkitJson
import org.prauga.toolkit.core.network.dto.LinksDto
import org.prauga.toolkit.core.network.dto.toDomain
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal const val BUNDLED_LINKS_ASSET = "links.json"

/** Community and project links, published by the site with a copy bundled here. */
class LinksRepository(
    private val client: HttpClient,
    private val assets: AssetManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    suspend fun links(): List<LinkSection> = withContext(ioDispatcher) { remote() ?: bundled() }

    private suspend fun remote(): List<LinkSection>? =
        try {
            client.get(ToolkitEndpoints.LINKS).body<LinksDto>().toDomain().takeIf { it.isNotEmpty() }
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
