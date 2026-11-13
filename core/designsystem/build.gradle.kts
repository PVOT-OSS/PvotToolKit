// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.library)
    alias(libs.plugins.toolkit.android.compose)
}

android { namespace = "org.prauga.toolkit.core.designsystem" }

dependencies {
    api(project(":design-system"))
    api(project(":core-ui"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.browser)
    implementation(libs.timber)
    testImplementation(libs.junit)
}
