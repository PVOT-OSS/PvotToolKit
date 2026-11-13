// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.library)
    alias(libs.plugins.toolkit.android.hilt)
}

android { namespace = "org.prauga.toolkit.core.data" }

dependencies {
    api(project(":core:common"))
    api(project(":core:model"))
    api(project(":core:device"))
    implementation(project(":core:datastore"))
    implementation(project(":core:network"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
}
