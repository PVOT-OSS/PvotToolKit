// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.toolkit.android.application)
    alias(libs.plugins.toolkit.android.compose)
    alias(libs.plugins.toolkit.android.hilt)
}

android {
    namespace = "org.prauga.toolkit"

    defaultConfig { applicationId = "org.prauga.toolkit" }

    buildFeatures { buildConfig = true }

    buildTypes { release { optimization { enable = false } } }
}

dependencies {
    implementation(project(":feature:devices"))
    implementation(project(":feature:device-info"))
    implementation(project(":feature:builds"))
    implementation(project(":feature:discover"))
    implementation(project(":feature:monitor"))
    implementation(project(":feature:tune"))
    implementation(project(":feature:licenses"))
    implementation(project(":feature:more"))
    implementation(project(":feature:news"))

    implementation(project(":core:designsystem"))
    implementation(project(":core:battery-monitor"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.ui)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)
    implementation(libs.timber)
}
