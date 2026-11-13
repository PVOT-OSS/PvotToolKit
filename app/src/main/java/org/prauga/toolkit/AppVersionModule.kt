// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.prauga.toolkit.core.model.AppVersion

@Module
@InstallIn(SingletonComponent::class)
internal object AppVersionModule {
    @Provides
    fun appVersion(): AppVersion =
        AppVersion(name = BuildConfig.VERSION_NAME, code = BuildConfig.VERSION_CODE)
}
