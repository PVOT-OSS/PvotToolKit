// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://pvot-oss.github.io/PvotLib/maven") {
            content { includeGroup("com.prauga.pvot") }
        }
    }
}

rootProject.name = "Toolkit"

include(":app")

include(":core:common")

include(":core:model")

include(":core:network")

include(":core:datastore")

include(":core:battery")

include(":core:batterymonitor")

include(":core:device")

include(":core:data")

include(":core:designsystem")

include(":feature:discover")

include(":feature:tune")

include(":feature:monitor")

include(":feature:builds")

include(":feature:news")

include(":feature:devices")

include(":feature:deviceinfo")

include(":feature:more")

include(":feature:licenses")
