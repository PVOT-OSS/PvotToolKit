// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.svg.SvgDecoder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.prauga.toolkit.core.batterymonitor.BatteryMonitorService
import org.prauga.toolkit.core.batterymonitor.BatterySessionStore

@HiltAndroidApp
class ToolkitApplication : Application(), SingletonImageLoader.Factory {

    @Inject lateinit var batterySessionStore: BatterySessionStore

    override fun onCreate() {
        super.onCreate()
        restoreBatteryMonitor()
    }

    /** Second line of defence behind the restart receiver. */
    private fun restoreBatteryMonitor() {
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            if (BatteryMonitorService.running) return@launch
            if (batterySessionStore.enabled.first()) {
                BatteryMonitorService.start(this@ToolkitApplication)
            }
        }
    }

    /** Every news cover the site publishes is an SVG, which Coil cannot decode without this. */
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
                add(OkHttpNetworkFetcherFactory())
            }
            .build()
}
