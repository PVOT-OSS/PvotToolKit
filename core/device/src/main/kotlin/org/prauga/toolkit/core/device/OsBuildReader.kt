// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.device

import android.os.Build
import org.prauga.toolkit.core.model.BootState
import org.prauga.toolkit.core.model.CustomRom
import org.prauga.toolkit.core.model.OsBuild
import org.prauga.toolkit.core.model.SocInfo

private val KNOWN_ROMS =
    listOf(
        "ro.lineage.version" to "LineageOS",
        "ro.crdroid.version" to "crDroid",
        "ro.evolution.version" to "Evolution X",
        "ro.aospa.version" to "Paranoid Android",
        "ro.pa.version" to "Paranoid Android",
        "ro.rr.version" to "Resurrection Remix",
        "ro.havoc.version" to "Havoc-OS",
        "ro.derp.version" to "DerpFest",
        "ro.omni.version" to "OmniROM",
        "ro.potato.version" to "PotatoOS",
        "ro.pixelexperience.version" to "Pixel Experience",
        "ro.modversion" to "Custom ROM",
    )

internal class OsBuildReader(private val properties: SystemProperties) {
    fun read(): OsBuild =
        OsBuild(
            androidRelease = Build.VERSION.RELEASE.knownOrNull(),
            sdkInt = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH.knownOrNull(),
            buildId = Build.ID.knownOrNull(),
            fingerprint = Build.FINGERPRINT.knownOrNull(),
            tags = Build.TAGS.knownOrNull(),
            type = Build.TYPE.knownOrNull(),
            rom = detectRom(),
        )

    internal fun detectRom(): CustomRom? =
        KNOWN_ROMS.firstNotNullOfOrNull { (key, name) ->
            properties[key]?.let { CustomRom(name, it) }
        }

    fun readBootState(): BootState =
        BootState(
            bootloaderUnlocked = properties["ro.boot.flash.locked"]?.let { it == "0" },
            verifiedBootState = properties["ro.boot.verifiedbootstate"],
            encryption = properties["ro.crypto.type"],
        )

    fun readSoc(): SocInfo =
        SocInfo(
            platform = properties["ro.board.platform"],
            hardware = properties["ro.hardware"] ?: Build.HARDWARE.knownOrNull(),
            manufacturer =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Build.SOC_MANUFACTURER.knownOrNull()
                } else {
                    null
                },
            model =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Build.SOC_MODEL.knownOrNull()
                } else {
                    null
                },
            supportedAbis = Build.SUPPORTED_ABIS.orEmpty().toList(),
        )
}
