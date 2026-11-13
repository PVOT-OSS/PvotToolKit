// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.library)
    alias(libs.plugins.toolkit.android.hilt)
}

android { namespace = "org.prauga.toolkit.core.batterymonitor" }

dependencies {
    implementation(project(":core:battery"))
    implementation(project(":core:common"))
    implementation(project(":core:datastore"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.timber)
}
