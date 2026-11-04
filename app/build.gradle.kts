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

    buildTypes { release { optimization { enable = false } } }
}

dependencies {
    // The shell owns navigation and the Application; every screen lives behind
    // a feature module.
    implementation(project(":feature:devices"))
    implementation(project(":feature:deviceinfo"))
    implementation(project(":feature:builds"))
    implementation(project(":feature:discover"))
    implementation(project(":feature:monitor"))
    implementation(project(":feature:tune"))
    implementation(project(":feature:licenses"))
    implementation(project(":feature:more"))
    implementation(project(":feature:news"))

    implementation(project(":core:designsystem"))
    implementation(project(":core:batterymonitor"))
    implementation(project(":core:data"))
    implementation(project(":core:model"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.ui)

    // Coil's singleton loader is configured in ToolkitApplication.
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.coil.svg)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
