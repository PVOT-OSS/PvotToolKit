// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.library)
    alias(libs.plugins.toolkit.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android { namespace = "org.prauga.toolkit.core.batterymonitor" }

dependencies {
    api(project(":core:battery"))
    implementation(project(":core:model"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
}
