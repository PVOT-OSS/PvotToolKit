// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.di

import android.content.Context
import io.ktor.client.HttpClient
import org.prauga.toolkit.core.batterymonitor.BatterySessionStore
import org.prauga.toolkit.core.data.device.DeviceRepository
import org.prauga.toolkit.core.data.links.LinksRepository
import org.prauga.toolkit.core.data.news.NewsRepository
import org.prauga.toolkit.core.data.settings.ThemeRepository
import org.prauga.toolkit.core.datastore.SettingsStore
import org.prauga.toolkit.core.device.DeviceDetailsReader
import org.prauga.toolkit.core.device.DeviceProfileReader
import org.prauga.toolkit.core.device.SystemVitalsReader
import org.prauga.toolkit.core.network.createHttpClient

/** Hand-rolled dependency container. */
class AppContainer(context: Context) {
    private val appContext: Context = context.applicationContext

    private val httpClient: HttpClient by lazy { createHttpClient(appContext.cacheDir) }

    val newsRepository: NewsRepository by lazy { NewsRepository(httpClient) }

    val deviceRepository: DeviceRepository by lazy {
        DeviceRepository(
            profileReader = DeviceProfileReader(appContext),
            detailsReader = DeviceDetailsReader(appContext),
            vitalsReader = SystemVitalsReader(appContext),
        )
    }

    val linksRepository: LinksRepository by lazy { LinksRepository(httpClient, appContext.assets) }

    val themeRepository: ThemeRepository by lazy { ThemeRepository(SettingsStore(appContext)) }

    val batterySessionStore: BatterySessionStore by lazy { BatterySessionStore(appContext) }
}

/** Implemented by the Application so features can reach the container. */
interface AppContainerOwner {
    val appContainer: AppContainer
}

/** The container for the current context, for use in ViewModel factories. */
fun Context.appContainer(): AppContainer = (applicationContext as AppContainerOwner).appContainer
