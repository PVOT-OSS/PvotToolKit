// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.prauga.toolkit.core.model.BootState
import org.prauga.toolkit.core.model.BuildDetails
import org.prauga.toolkit.core.model.CameraFacing
import org.prauga.toolkit.core.model.CameraInfo
import org.prauga.toolkit.core.model.CodecSummary
import org.prauga.toolkit.core.model.DeviceDetails
import org.prauga.toolkit.core.model.DeviceIdentity
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.DisplayInfo
import org.prauga.toolkit.core.model.MemoryDetails
import org.prauga.toolkit.core.model.OsBuild
import org.prauga.toolkit.core.model.SocInfo
import org.prauga.toolkit.feature.deviceinfo.R

class DeviceInfoSectionsTest {
    @Test
    fun `missing and blank values are dropped`() {
        val system = DeviceInfoTab.SYSTEM.sections(profile(), details(), vitals = null).single()

        val labels = system.lines.map { it.label }
        assertTrue(R.string.device_info_manufacturer in labels)
        assertFalse(R.string.device_info_brand in labels)
        assertFalse(R.string.device_info_build_type in labels)
        assertEquals(
            InfoText.Raw("36"),
            system.lines.single { it.label == R.string.device_info_api }.value,
        )
    }

    @Test
    fun `a section left without lines is omitted`() {
        val memory = DeviceInfoTab.MEMORY.sections(profile(), details(), vitals = null)

        assertEquals(
            listOf(InfoText.Res(R.string.device_info_section_memory)),
            memory.map { it.title },
        )
    }

    @Test
    fun `byte sizes stay raw for the formatter and an empty swap is hidden`() {
        val memory = DeviceInfoTab.MEMORY.sections(profile(), details(), vitals = null).single()

        assertEquals(
            listOf(
                InfoLine(R.string.device_info_ram_total, InfoText.Bytes(8L shl 30)),
                InfoLine(R.string.device_info_ram_available, InfoText.Bytes(3L shl 30)),
                InfoLine(R.string.device_info_ram_threshold, InfoText.Bytes(1L shl 28)),
                InfoLine(R.string.device_info_page_size, InfoText.Bytes(4096)),
            ),
            memory.lines,
        )
    }

    @Test
    fun `refresh modes are distinct, sorted and carry their unit`() {
        val screen = DeviceInfoTab.SCREEN.sections(profile(), details(), vitals = null).single()

        assertEquals(
            InfoText.Joined(
                listOf(
                    InfoText.Res(R.string.device_info_hertz, listOf(60f)),
                    InfoText.Res(R.string.device_info_hertz, listOf(120f)),
                ),
                ", ",
            ),
            screen.lines.single { it.label == R.string.device_info_refresh_modes }.value,
        )
    }

    @Test
    fun `each camera is its own section titled by id and facing`() {
        val cameras = DeviceInfoTab.CAMERAS.sections(profile(), details(), vitals = null)

        assertEquals(
            listOf(
                InfoText.Res(
                    R.string.device_info_section_camera,
                    listOf("0", InfoText.Res(R.string.device_info_cam_back)),
                ),
                InfoText.Res(
                    R.string.device_info_section_camera,
                    listOf("1", InfoText.Res(R.string.device_info_cam_unknown)),
                ),
            ),
            cameras.map { it.title },
        )
    }

    @Test
    fun `apertures read as f-numbers and an absent sensor size is left out`() {
        val back = DeviceInfoTab.CAMERAS.sections(profile(), details(), vitals = null).first()

        assertEquals(
            InfoText.Joined(
                listOf(
                    InfoText.Res(R.string.device_info_f_number, listOf(1.8f)),
                    InfoText.Res(R.string.device_info_f_number, listOf(2.2f)),
                ),
                ", ",
            ),
            back.lines.single { it.label == R.string.device_info_cam_aperture }.value,
        )
        assertFalse(back.lines.any { it.label == R.string.device_info_cam_sensor_size })
    }

    @Test
    fun `a camera without apertures has no aperture line`() {
        val front = DeviceInfoTab.CAMERAS.sections(profile(), details(), vitals = null).last()

        assertFalse(front.lines.any { it.label == R.string.device_info_cam_aperture })
    }

    @Test
    fun `the battery tab is empty until vitals arrive`() {
        assertTrue(DeviceInfoTab.BATTERY.sections(profile(), details(), vitals = null).isEmpty())
    }

    @Test
    fun `boot state reads as words, not booleans`() {
        val boot = DeviceInfoTab.BOOT.sections(profile(), details(), vitals = null).single()

        assertEquals(
            listOf(
                InfoLine(
                    R.string.device_info_bootloader_state,
                    InfoText.Res(R.string.device_info_unlocked),
                ),
                InfoLine(R.string.device_info_su, InfoText.Res(R.string.device_info_absent)),
            ),
            boot.lines,
        )
    }

    private fun profile() =
        DeviceProfile(
            identity =
                DeviceIdentity(
                    codename = "surya",
                    model = "M2007J20CG",
                    manufacturer = "Xiaomi",
                    androidRelease = "16",
                    sdkInt = 36,
                    kernelRelease = null,
                    isStormBreakerKernel = false,
                ),
            os =
                OsBuild(
                    androidRelease = "16",
                    sdkInt = 36,
                    securityPatch = null,
                    buildId = null,
                    fingerprint = null,
                    tags = null,
                    type = " ",
                    rom = null,
                ),
            boot = BootState(bootloaderUnlocked = true, verifiedBootState = null, encryption = ""),
            soc =
                SocInfo(
                    platform = null,
                    hardware = null,
                    manufacturer = null,
                    model = null,
                    supportedAbis = emptyList(),
                ),
            cpu = null,
            gpu = null,
            suBinaryPresent = false,
        )

    private fun details() =
        DeviceDetails(
            build =
                BuildDetails(
                    brand = "",
                    product = null,
                    board = null,
                    device = null,
                    hardware = null,
                    bootloader = null,
                    radio = null,
                    incremental = null,
                    host = null,
                    buildTimeMillis = 0,
                    javaVm = null,
                    kernelFull = null,
                ),
            display =
                DisplayInfo(
                    widthPx = 1080,
                    heightPx = 2400,
                    densityDpi = 440,
                    xDpi = 395f,
                    yDpi = 396f,
                    refreshHz = 120f,
                    supportedRefreshHz = listOf(120f, 60f, 120f),
                    hdrTypes = emptyList(),
                    diagonalInches = null,
                ),
            memory =
                MemoryDetails(
                    totalBytes = 8L shl 30,
                    availableBytes = 3L shl 30,
                    thresholdBytes = 1L shl 28,
                    swapTotalBytes = 0,
                    pageSizeBytes = 4096,
                ),
            sensors = emptyList(),
            cameras =
                listOf(
                    camera(
                        id = "0",
                        facing = CameraFacing.BACK,
                        apertures = listOf(2.2f, 1.8f, 2.2f),
                    ),
                    camera(id = "1", facing = null, apertures = emptyList()),
                ),
            codecs =
                CodecSummary(
                    decoders = 0,
                    encoders = 0,
                    hardwareAccelerated = 0,
                    notableFormats = emptyList(),
                ),
        )

    private fun camera(id: String, facing: CameraFacing?, apertures: List<Float>) =
        CameraInfo(
            id = id,
            facing = facing,
            widthPx = 4000,
            heightPx = 3000,
            apertures = apertures,
            focalLengths = emptyList(),
            sensorWidthMm = 6.4f,
            sensorHeightMm = null,
            isoMin = null,
            isoMax = null,
            hasFlash = false,
            hardwareLevel = null,
            maxDigitalZoom = null,
        )
}
