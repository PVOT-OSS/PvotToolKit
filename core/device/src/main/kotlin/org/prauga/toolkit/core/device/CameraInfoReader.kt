// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import org.prauga.toolkit.core.model.CameraFacing
import org.prauga.toolkit.core.model.CameraHardwareLevel
import org.prauga.toolkit.core.model.CameraInfo
import timber.log.Timber

/** Camera2 characteristics, which need no permission until a camera is opened. */
internal class CameraInfoReader(private val context: Context) {
    fun read(): List<CameraInfo> {
        val manager = context.getSystemService(CameraManager::class.java) ?: return emptyList()
        val ids =
            try {
                manager.cameraIdList
            } catch (e: CameraAccessException) {
                Timber.w(e, "Camera list unavailable")
                return emptyList()
            }
        return ids.mapNotNull { id -> describe(manager, id) }
    }

    private fun describe(manager: CameraManager, id: String): CameraInfo? {
        val characteristics =
            try {
                manager.getCameraCharacteristics(id)
            } catch (e: CameraAccessException) {
                Timber.w(e, "Camera %s characteristics unavailable", id)
                return null
            } catch (e: IllegalArgumentException) {
                Timber.w(e, "Camera %s vanished", id)
                return null
            }
        val pixelArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
        val physical = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE)
        val iso = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)

        return CameraInfo(
            id = id,
            facing = facing(characteristics.get(CameraCharacteristics.LENS_FACING)),
            widthPx = pixelArray?.width ?: 0,
            heightPx = pixelArray?.height ?: 0,
            apertures =
                characteristics
                    .get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)
                    ?.toList()
                    .orEmpty(),
            focalLengths =
                characteristics
                    .get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)
                    ?.toList()
                    .orEmpty(),
            sensorWidthMm = physical?.width,
            sensorHeightMm = physical?.height,
            isoMin = iso?.lower,
            isoMax = iso?.upper,
            hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true,
            hardwareLevel =
                hardwareLevel(
                    characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                ),
            maxDigitalZoom =
                characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM),
        )
    }

    private fun facing(facing: Int?): CameraFacing? =
        when (facing) {
            CameraCharacteristics.LENS_FACING_FRONT -> CameraFacing.FRONT
            CameraCharacteristics.LENS_FACING_BACK -> CameraFacing.BACK
            CameraCharacteristics.LENS_FACING_EXTERNAL -> CameraFacing.EXTERNAL
            else -> null
        }

    private fun hardwareLevel(level: Int?): CameraHardwareLevel? =
        when (level) {
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY -> CameraHardwareLevel.LEGACY
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED ->
                CameraHardwareLevel.LIMITED
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL -> CameraHardwareLevel.FULL
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3 -> CameraHardwareLevel.LEVEL_3
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL ->
                CameraHardwareLevel.EXTERNAL
            else -> null
        }
}
