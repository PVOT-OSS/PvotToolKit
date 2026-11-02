// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.library)
    alias(libs.plugins.toolkit.android.compose)
}

android { namespace = "org.prauga.toolkit.core.di" }

dependencies {
    api(project(":core:data"))
    api(project(":core:batterymonitor"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(libs.ktor.client.okhttp)
}
