// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins { alias(libs.plugins.toolkit.android.feature) }

android { namespace = "org.prauga.toolkit.feature.more" }

dependencies {
    implementation(project(":core:battery-monitor"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.timber)
}
