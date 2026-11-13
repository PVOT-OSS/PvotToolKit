// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.deviceinfo.hardware

import androidx.annotation.StringRes
import org.prauga.toolkit.core.model.CameraInfo
import org.prauga.toolkit.core.model.DeviceDetails
import org.prauga.toolkit.core.model.DeviceProfile
import org.prauga.toolkit.core.model.Vitals
import org.prauga.toolkit.feature.deviceinfo.R

private const val LIST_SEPARATOR = ", "
private const val WORD_SEPARATOR = " "
private const val LINE_SEPARATOR = "\n"

internal fun DeviceInfoTab.sections(
    profile: DeviceProfile,
    details: DeviceDetails?,
    vitals: Vitals?,
): List<InfoSection> {
    val sections =
        when (this) {
            DeviceInfoTab.SYSTEM -> listOf(systemSection(profile, details))
            DeviceInfoTab.SOC -> listOf(socSection(profile), gpuSection(profile))
            DeviceInfoTab.SCREEN -> listOf(screenSection(details))
            DeviceInfoTab.MEMORY -> listOf(memorySection(details), storageSection(vitals))
            DeviceInfoTab.BATTERY -> listOf(batterySection(vitals))
            DeviceInfoTab.CAMERAS -> details?.cameras.orEmpty().map(::cameraSection)
            DeviceInfoTab.CODECS -> listOf(codecSection(details))
            DeviceInfoTab.BOOT -> listOf(bootSection(profile))
        }
    return sections.filter { it.lines.isNotEmpty() }
}

private fun systemSection(profile: DeviceProfile, details: DeviceDetails?): InfoSection {
    val os = profile.os
    val build = details?.build
    return section(
        R.string.device_info_section_system,
        R.string.device_info_manufacturer to profile.identity.manufacturer.raw(),
        R.string.device_info_brand to build?.brand.raw(),
        R.string.device_info_model to profile.identity.model.raw(),
        R.string.device_info_device to build?.device.raw(),
        R.string.device_info_product to build?.product.raw(),
        R.string.device_info_board to build?.board.raw(),
        R.string.device_info_release to os.androidRelease.raw(),
        R.string.device_info_api to os.sdkInt.toString().raw(),
        R.string.device_info_build to os.buildId.raw(),
        R.string.device_info_incremental to build?.incremental.raw(),
        R.string.device_info_build_type to os.type.raw(),
        R.string.device_info_tags to os.tags.raw(),
        R.string.device_info_security_patch to os.securityPatch.raw(),
        R.string.device_info_build_date to build?.buildTimeMillis?.let(InfoText::DateTime),
        R.string.device_info_build_host to build?.host.raw(),
        R.string.device_info_java_vm to build?.javaVm.raw(),
        R.string.device_info_bootloader to build?.bootloader.raw(),
        R.string.device_info_baseband to build?.radio.raw(),
        R.string.device_info_rom to
            os.rom?.let {
                InfoText.Joined(
                    listOf(InfoText.Raw(it.name), InfoText.Raw(it.version)),
                    WORD_SEPARATOR,
                )
            },
        R.string.device_info_kernel to profile.identity.kernelRelease.raw(),
        R.string.device_info_kernel_full to build?.kernelFull.raw(),
        R.string.device_info_fingerprint to os.fingerprint.raw(),
    )
}

private fun socSection(profile: DeviceProfile): InfoSection {
    val soc = profile.soc
    val cpu = profile.cpu
    val clusters =
        cpu?.clusters?.map { cluster ->
            InfoText.Res(
                R.string.device_info_cluster_value,
                listOf(
                    cluster.cores.size,
                    (cluster.hardwareMaxKhz ?: cluster.maxKhz)?.let(InfoText::Frequency)
                        ?: InfoText.Raw(""),
                ),
            )
        }
    return section(
        R.string.device_info_section_soc,
        R.string.device_info_soc_model to soc.model.raw(),
        R.string.device_info_soc_vendor to soc.manufacturer.raw(),
        R.string.device_info_platform to soc.platform.raw(),
        R.string.device_info_hardware to soc.hardware.raw(),
        R.string.device_info_cores to cpu?.coreCount?.toString().raw(),
        R.string.device_info_clusters to clusters?.joined(LINE_SEPARATOR),
        R.string.device_info_governor to
            cpu?.clusters?.mapNotNull { it.governor }?.distinct()?.map(InfoText::Raw)?.joined(),
        R.string.device_info_abis to soc.supportedAbis.map(InfoText::Raw).joined(WORD_SEPARATOR),
        R.string.device_info_instructions to
            cpu?.features?.map(InfoText::Raw)?.joined(WORD_SEPARATOR),
    )
}

private fun gpuSection(profile: DeviceProfile): InfoSection {
    val gpu = profile.gpu
    return section(
        R.string.device_info_section_gpu,
        R.string.device_info_renderer to gpu?.renderer.raw(),
        R.string.device_info_soc_vendor to gpu?.vendor.raw(),
        R.string.device_info_gl_version to gpu?.glVersion.raw(),
    )
}

private fun screenSection(details: DeviceDetails?): InfoSection {
    val display = details?.display
    return section(
        R.string.device_info_section_screen,
        R.string.device_info_resolution to
            display?.let {
                InfoText.Res(R.string.device_info_pixels, listOf(it.widthPx, it.heightPx))
            },
        R.string.device_info_aspect to display?.aspectRatio.raw(),
        R.string.device_info_density to
            display?.let { InfoText.Res(R.string.device_info_dpi_value, listOf(it.densityDpi)) },
        R.string.device_info_dpi to
            display?.let {
                InfoText.Res(R.string.device_info_dpi_physical, listOf(it.xDpi, it.yDpi))
            },
        R.string.device_info_diagonal to
            display?.diagonalInches?.let { InfoText.Res(R.string.device_info_inches, listOf(it)) },
        R.string.device_info_refresh to
            display?.let { InfoText.Res(R.string.device_info_hertz, listOf(it.refreshHz)) },
        R.string.device_info_refresh_modes to
            display?.supportedRefreshHz?.distinctSorted(R.string.device_info_hertz),
        R.string.device_info_hdr to display?.hdrTypes?.map { it.text() }?.joined(),
    )
}

private fun memorySection(details: DeviceDetails?): InfoSection {
    val memory = details?.memory
    return section(
        R.string.device_info_section_memory,
        R.string.device_info_ram_total to memory?.totalBytes?.let(InfoText::Bytes),
        R.string.device_info_ram_available to memory?.availableBytes?.let(InfoText::Bytes),
        R.string.device_info_ram_threshold to memory?.thresholdBytes?.let(InfoText::Bytes),
        R.string.device_info_swap to
            memory?.swapTotalBytes?.takeIf { it > 0 }?.let(InfoText::Bytes),
        R.string.device_info_page_size to memory?.pageSizeBytes?.let(InfoText::Bytes),
    )
}

private fun storageSection(vitals: Vitals?): InfoSection {
    val storage = vitals?.storage
    return section(
        R.string.device_info_section_storage,
        R.string.device_info_storage_used to storage?.usedBytes?.let(InfoText::Bytes),
        R.string.device_info_storage_total to storage?.totalBytes?.let(InfoText::Bytes),
        R.string.device_info_filesystem to storage?.fileSystem.raw(),
    )
}

private fun batterySection(vitals: Vitals?): InfoSection {
    val battery = vitals?.battery
    return section(
        R.string.device_info_section_battery,
        R.string.device_info_level to battery?.percent?.let(InfoText::Percent),
        R.string.device_info_health to battery?.health?.let { InfoText.Res(it.labelRes()) },
        R.string.device_info_technology to battery?.technology.raw(),
        R.string.device_info_temperature to battery?.temperatureC?.let(InfoText::Temperature),
        R.string.device_info_current to battery?.currentMicroAmps?.let(InfoText::Current),
        R.string.device_info_voltage to battery?.voltageMillivolts?.let(InfoText::Voltage),
        R.string.device_info_design_capacity to battery?.designCapacityMah?.let(InfoText::Capacity),
        R.string.device_info_charge_remaining to
            battery?.chargeCounterMicroAmpHours?.let(InfoText::Charge),
        R.string.device_info_cycle_count to battery?.cycleCount?.toString().raw(),
    )
}

private fun cameraSection(camera: CameraInfo): InfoSection =
    InfoSection(
        title =
            InfoText.Res(
                R.string.device_info_section_camera,
                listOf(camera.id, InfoText.Res(camera.facing.labelRes())),
            ),
        lines =
            lines(
                R.string.device_info_cam_resolution to
                    InfoText.Res(
                        R.string.device_info_megapixels,
                        listOf(camera.megapixels, camera.widthPx, camera.heightPx),
                    ),
                R.string.device_info_cam_aperture to
                    camera.apertures.distinctSorted(R.string.device_info_f_number),
                R.string.device_info_cam_focal to
                    camera.focalLengths.distinctSorted(R.string.device_info_millimetres),
                R.string.device_info_cam_sensor_size to
                    camera.sensorWidthMm?.let { width ->
                        camera.sensorHeightMm?.let { height ->
                            InfoText.Res(R.string.device_info_sensor_size, listOf(width, height))
                        }
                    },
                R.string.device_info_cam_pixel_size to
                    camera.pixelSizeMicrons?.let {
                        InfoText.Res(R.string.device_info_microns, listOf(it))
                    },
                R.string.device_info_cam_iso to
                    camera.isoMin?.let { min ->
                        camera.isoMax?.let { max ->
                            InfoText.Res(R.string.device_info_iso_range, listOf(min, max))
                        }
                    },
                R.string.device_info_cam_zoom to
                    camera.maxDigitalZoom?.let {
                        InfoText.Res(R.string.device_info_zoom, listOf(it))
                    },
                R.string.device_info_cam_level to
                    camera.hardwareLevel?.let { InfoText.Res(it.labelRes()) },
                R.string.device_info_cam_flash to
                    InfoText.Res(
                        if (camera.hasFlash) R.string.device_info_yes else R.string.device_info_no
                    ),
            ),
    )

private fun codecSection(details: DeviceDetails?): InfoSection {
    val codecs = details?.codecs
    return section(
        R.string.device_info_section_codecs,
        R.string.device_info_decoders to codecs?.decoders?.toString().raw(),
        R.string.device_info_encoders to codecs?.encoders?.toString().raw(),
        R.string.device_info_hw_accelerated to codecs?.hardwareAccelerated?.toString().raw(),
        R.string.device_info_formats to codecs?.notableFormats?.map(InfoText::Raw)?.joined(),
    )
}

private fun bootSection(profile: DeviceProfile): InfoSection {
    val boot = profile.boot
    return section(
        R.string.device_info_section_boot,
        R.string.device_info_bootloader_state to
            boot.bootloaderUnlocked?.let {
                InfoText.Res(if (it) R.string.device_info_unlocked else R.string.device_info_locked)
            },
        R.string.device_info_verified_boot to boot.verifiedBootState.raw(),
        R.string.device_info_encryption to boot.encryption.raw(),
        R.string.device_info_su to
            InfoText.Res(
                if (profile.suBinaryPresent) R.string.device_info_present
                else R.string.device_info_absent
            ),
    )
}

private fun section(@StringRes title: Int, vararg lines: Pair<Int, InfoText?>): InfoSection =
    InfoSection(title = InfoText.Res(title), lines = lines(*lines))

/** Drops lines whose value is missing or blank, so nothing renders as an empty readout. */
private fun lines(vararg lines: Pair<Int, InfoText?>): List<InfoLine> =
    lines.mapNotNull { (label, value) ->
        value?.takeUnless { it.isBlank() }?.let { InfoLine(label, it) }
    }

private fun String?.raw(): InfoText? = this?.let(InfoText::Raw)

private fun List<InfoText>.joined(separator: String = LIST_SEPARATOR): InfoText =
    InfoText.Joined(this, separator)

private fun List<Float>.distinctSorted(@StringRes template: Int): InfoText =
    distinct().sorted().map { InfoText.Res(template, listOf(it)) }.joined()
