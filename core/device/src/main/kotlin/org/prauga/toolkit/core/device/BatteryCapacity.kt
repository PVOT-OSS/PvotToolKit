// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.content.Context
import timber.log.Timber

private const val POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile"

// No public API and SELinux denies the sysfs node; Settings reads PowerProfile the same way.
internal object BatteryCapacity {
    fun designMah(context: Context): Int? =
        try {
            val profileClass = Class.forName(POWER_PROFILE_CLASS)
            val profile = profileClass.getConstructor(Context::class.java).newInstance(context)
            val capacity = profileClass.getMethod("getBatteryCapacity").invoke(profile) as? Double
            capacity?.toInt()?.takeIf { it > 0 }
        } catch (e: ReflectiveOperationException) {
            Timber.w(e, "PowerProfile refused the design capacity")
            null
        } catch (e: SecurityException) {
            Timber.w(e, "PowerProfile refused the design capacity")
            null
        }
}
