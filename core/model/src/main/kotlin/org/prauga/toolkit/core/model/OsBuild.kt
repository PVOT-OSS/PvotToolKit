// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.core.model

data class OsBuild(
    val androidRelease: String?,
    val sdkInt: Int,
    val securityPatch: String?,
    val buildId: String?,
    val fingerprint: String?,
    val tags: String?,
    val type: String?,
    val rom: CustomRom?,
)

data class CustomRom(val name: String, val version: String)

/** Whether the device can take a custom kernel at all. */
data class BootState(
    val bootloaderUnlocked: Boolean?,
    val verifiedBootState: String?,
    val encryption: String?,
)

data class SocInfo(
    val platform: String?,
    val hardware: String?,
    val manufacturer: String?,
    val model: String?,
    val supportedAbis: List<String>,
)
