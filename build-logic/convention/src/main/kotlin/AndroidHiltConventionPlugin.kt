// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.dependencies

/** Hilt with KSP for an Android module that already has an Android plugin applied. */
class AndroidHiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) =
        with(target) {
            pluginManager.apply("com.google.devtools.ksp")
            pluginManager.apply("com.google.dagger.hilt.android")

            val kspLock =
                gradle.sharedServices.registerIfAbsent("kspSerial", KspSerialService::class.java) {
                    maxParallelUsages.set(1)
                }
            tasks
                .matching { it.name.startsWith("ksp") && it.name.endsWith("Kotlin") }
                .configureEach { usesService(kspLock) }

            dependencies {
                add("implementation", libs.lib("hilt-android"))
                add("ksp", libs.lib("hilt-compiler"))
            }
        }
}

/** KSP2 tasks running at once in one daemon resolve the classpath wrongly and drop Hilt output. */
internal abstract class KspSerialService : BuildService<BuildServiceParameters.None>
