// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.data.device

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.prauga.toolkit.core.common.coroutines.Dispatcher
import org.prauga.toolkit.core.common.coroutines.ToolkitDispatchers
import org.prauga.toolkit.core.device.CpuLoadReader
import org.prauga.toolkit.core.device.DeviceDetailsReader
import org.prauga.toolkit.core.device.DeviceProfileReader
import org.prauga.toolkit.core.device.SystemProperties
import org.prauga.toolkit.core.device.SystemVitalsReader
import org.prauga.toolkit.core.model.DeviceDetails
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.Vitals

private const val LOAD_WINDOW_MILLIS = 500L

@Singleton
class DeviceRepository
@Inject
constructor(
    private val profileReader: DeviceProfileReader,
    private val detailsReader: DeviceDetailsReader,
    private val vitalsReader: SystemVitalsReader,
    @Dispatcher(ToolkitDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) {
    private val properties = SuspendLazy { withContext(ioDispatcher) { SystemProperties.read() } }

    private val cachedProfile = SuspendLazy {
        val properties = properties.get()
        withContext(ioDispatcher) { profileReader.read(properties) }
    }

    private val cachedDetails = SuspendLazy {
        val properties = properties.get()
        withContext(ioDispatcher) { detailsReader.read(properties) }
    }

    suspend fun profile(): DeviceProfile = cachedProfile.get()

    suspend fun details(): DeviceDetails = cachedDetails.get()

    /** Suspends for the sampling window: utilisation needs two spaced samples. */
    suspend fun vitals(): Vitals =
        withContext(ioDispatcher) {
            val first = CpuLoadReader.sample()
            delay(LOAD_WINDOW_MILLIS)
            val load =
                first?.let { start ->
                    CpuLoadReader.sample()?.let { CpuLoadReader.load(start, it) }
                }
            vitalsReader.read(load)
        }
}
