// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins { alias(libs.plugins.toolkit.android.feature) }

android { namespace = "org.prauga.toolkit.feature.news" }

dependencies {
    implementation(libs.coil.compose)
    testImplementation(project(":core:network"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
}
