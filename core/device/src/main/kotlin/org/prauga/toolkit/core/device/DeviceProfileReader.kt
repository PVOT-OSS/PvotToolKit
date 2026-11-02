// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.content.Context
import org.prauga.toolkit.core.model.DeviceProfile

/** Reads everything fixed for the life of the process, in one pass. */
class DeviceProfileReader(context: Context) {
    private val platform = PlatformVitalsReader(context)

    fun read(): DeviceProfile {
        val properties = SystemProperties.read()
        val osBuild = OsBuildReader(properties)
        return DeviceProfile(
            identity = DeviceIdentityReader.read(),
            os = osBuild.read(),
            boot = osBuild.readBootState(),
            soc = osBuild.readSoc(),
            cpu = CpuTopologyReader.read(),
            gpu = GpuInfoReader.read(),
            suBinaryPresent = platform.suBinaryPresent(),
        )
    }
}
