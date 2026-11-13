// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package org.prauga.toolkit.feature.licenses.ui

private const val AOSP = "The Android Open Source Project"
private const val JETBRAINS = "JetBrains s.r.o. and contributors"
private const val SQUARE = "Square, Inc."
private const val APACHE_2 = "Apache-2.0"

internal val ThirdPartyLicenses: List<License> =
    listOf(
        License("AndroidX", AOSP, APACHE_2, "https://github.com/androidx/androidx"),
        License(
            "Jetpack Compose and Material 3",
            AOSP,
            APACHE_2,
            "https://developer.android.com/jetpack/androidx/releases/compose",
        ),
        License(
            "Navigation 3",
            AOSP,
            APACHE_2,
            "https://developer.android.com/jetpack/androidx/releases/navigation3",
        ),
        License(
            "DataStore",
            AOSP,
            APACHE_2,
            "https://developer.android.com/jetpack/androidx/releases/datastore",
        ),
        License(
            "Dagger and Hilt",
            "The Dagger Authors",
            APACHE_2,
            "https://github.com/google/dagger",
        ),
        License(
            "Accompanist Drawable Painter",
            AOSP,
            APACHE_2,
            "https://github.com/google/accompanist",
        ),
        License(
            "Compose Multiplatform",
            JETBRAINS,
            APACHE_2,
            "https://github.com/JetBrains/compose-multiplatform",
        ),
        License(
            "Kotlin standard library",
            JETBRAINS,
            APACHE_2,
            "https://github.com/JetBrains/kotlin",
        ),
        License(
            "kotlinx.coroutines",
            JETBRAINS,
            APACHE_2,
            "https://github.com/Kotlin/kotlinx.coroutines",
        ),
        License(
            "kotlinx.serialization",
            JETBRAINS,
            APACHE_2,
            "https://github.com/Kotlin/kotlinx.serialization",
        ),
        License("kotlinx-io", JETBRAINS, APACHE_2, "https://github.com/Kotlin/kotlinx-io"),
        License("Ktor", JETBRAINS, APACHE_2, "https://github.com/ktorio/ktor"),
        License("OkHttp", SQUARE, APACHE_2, "https://github.com/square/okhttp"),
        License("Okio", SQUARE, APACHE_2, "https://github.com/square/okio"),
        License("Coil", "Coil Contributors", APACHE_2, "https://github.com/coil-kt/coil"),
        License(
            "AndroidSVG",
            "Paul LeBeau, Cave Rock Software Ltd.",
            APACHE_2,
            "https://github.com/BigBadaboom/androidsvg",
        ),
        License("Timber", "Jake Wharton", APACHE_2, "https://github.com/JakeWharton/timber"),
        License(
            "Guava ListenableFuture",
            "The Guava Authors",
            APACHE_2,
            "https://github.com/google/guava",
        ),
        License(
            "JSR 305 annotations",
            "FindBugs",
            APACHE_2,
            "https://code.google.com/archive/p/jsr-305/",
        ),
        License(
            "javax.inject",
            "The JSR-330 Expert Group",
            APACHE_2,
            "https://github.com/javax-inject/javax-inject",
        ),
        License(
            "Jakarta Dependency Injection",
            "Eclipse Foundation",
            APACHE_2,
            "https://github.com/jakartaee/inject",
        ),
        License(
            "JetBrains Java Annotations",
            "JetBrains s.r.o.",
            APACHE_2,
            "https://github.com/JetBrains/java-annotations",
        ),
        License(
            "JSpecify",
            "The JSpecify Authors",
            APACHE_2,
            "https://github.com/jspecify/jspecify",
        ),
        License("SLF4J API", "QOS.ch Sarl", "MIT", "https://github.com/qos-ch/slf4j"),
        License("PvotLib", "Saalim Quadri", APACHE_2, "https://github.com/PVOT-OSS/PvotLib"),
    )
