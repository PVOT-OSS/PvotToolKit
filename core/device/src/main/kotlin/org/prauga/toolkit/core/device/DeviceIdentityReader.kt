// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.os.Build
import org.prauga.toolkit.core.model.DeviceIdentity

private const val LOCALVERSION_TAG = "stormbreaker"

internal object DeviceIdentityReader {
    fun read(): DeviceIdentity {
        val device: String? = Build.DEVICE
        val model: String? = Build.MODEL
        val manufacturer: String? = Build.MANUFACTURER
        val release: String? = Build.VERSION.RELEASE
        val kernel = kernelRelease()
        return DeviceIdentity(
            codename = device.knownOrNull(),
            model = model.knownOrNull(),
            manufacturer = manufacturer.knownOrNull(),
            androidRelease = release.knownOrNull(),
            sdkInt = Build.VERSION.SDK_INT,
            kernelRelease = kernel,
            isStormBreakerKernel = kernel?.contains(LOCALVERSION_TAG, ignoreCase = true) == true,
        )
    }

    /** `os.version` is uname's release field. */
    private fun kernelRelease(): String? = System.getProperty("os.version").knownOrNull()
}
