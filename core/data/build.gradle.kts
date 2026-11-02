// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android { namespace = "org.prauga.toolkit.core.data" }

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(project(":core:device"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(libs.ktor.client.okhttp)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}
