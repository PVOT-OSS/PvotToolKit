// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeviceReadersTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun profileNamesTheDeviceAndKeepsCpuLimitsPlausible() {
        val profile = DeviceProfileReader(context).read(SystemProperties.read())

        assertNotNull(profile.identity.codename)
        assertTrue(profile.os.sdkInt > 0)
        profile.cpu?.clusters?.forEach { cluster ->
            cluster.hardwareMaxKhz?.let {
                assertTrue("max looks wrong: $it", it in 100_000..10_000_000)
            }
        }
    }

    @Test
    fun vitalsStayWithinTheirRanges() {
        val vitals = SystemVitalsReader(context).read()

        assertTrue(vitals.uptimeMillis > 0)
        assertTrue(vitals.sleep.awakeMillis <= vitals.sleep.elapsedMillis)
        vitals.cpu?.perCoreKhz?.filterNotNull()?.forEach {
            assertTrue("frequency looks wrong: $it", it in 1..10_000_000)
        }
        vitals.memory?.let { assertTrue(it.usedBytes in 0..it.totalBytes) }
        vitals.battery?.let { assertTrue(it.percent in 0..100) }
    }

    @Test
    fun systemPropertiesAreReadable() {
        val properties = SystemProperties.read()

        assertNotNull("getprop returned nothing", properties["ro.build.fingerprint"])
    }
}
