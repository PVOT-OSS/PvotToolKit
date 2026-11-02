// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.sbkm.android.library)
    alias(libs.plugins.sbkm.android.compose)
}

android {
    namespace = "dev.danascape.kernelmanager.core.designsystem"
}

dependencies {
    api(project(":design-system"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.browser)
}
