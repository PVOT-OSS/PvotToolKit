// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.network.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import java.io.File
import javax.inject.Singleton
import okhttp3.Cache

private const val HTTP_CACHE_BYTES = 8L * 1024 * 1024

/** Static files behind an ETag, so OkHttp's disk cache is the persistence. */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun httpClientEngine(@ApplicationContext context: Context): HttpClientEngine =
        OkHttp.create {
            config {
                cache(Cache(File(context.cacheDir, "http"), HTTP_CACHE_BYTES))
                retryOnConnectionFailure(true)
            }
        }
}
