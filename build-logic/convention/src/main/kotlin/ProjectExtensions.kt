// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

internal val Project.libs: VersionCatalog
    get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

internal object SdkVersions {
    const val COMPILE = 37
    const val COMPILE_MINOR = 1
    const val TARGET = 37
    const val MIN = 29
}

internal object JvmVersions {
    const val TARGET = 21
    val JAVA: JavaVersion = JavaVersion.toVersion(TARGET)
    val KOTLIN: JvmTarget = JvmTarget.fromTarget(TARGET.toString())
}

/** A catalog library by alias, failing with the alias named rather than an empty Optional. */
internal fun VersionCatalog.lib(alias: String): Provider<MinimalExternalModuleDependency> =
    findLibrary(alias).orElseThrow {
        IllegalArgumentException("No library '$alias' in libs.versions.toml")
    }
