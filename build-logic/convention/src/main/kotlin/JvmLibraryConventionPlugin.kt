// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("toolkit.kotlin.quality")

            extensions.configure<JavaPluginExtension> {
                sourceCompatibility = JvmVersions.JAVA
                targetCompatibility = JvmVersions.JAVA
            }

            extensions.configure<KotlinJvmProjectExtension> {
                compilerOptions { jvmTarget.set(JvmVersions.KOTLIN) }
            }
        }
}
