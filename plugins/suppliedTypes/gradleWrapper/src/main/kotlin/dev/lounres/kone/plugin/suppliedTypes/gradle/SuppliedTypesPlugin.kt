/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.suppliedTypes.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import plguinDependency
import runtimeDependency


@Suppress("unused")
class SuppliedTypesPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.dependencies {
            add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, plguinDependency)
        }
        target.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
            target.configure<KotlinMultiplatformExtension> {
                sourceSets {
                    commonMain {
                        dependencies {
                            api(runtimeDependency)
                        }
                    }
                }
            }
        }
        target.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
            target.configure<KotlinJvmProjectExtension> {
                sourceSets {
                    named("main") {
                        dependencies {
                            api(runtimeDependency)
                        }
                    }
                }
            }
        }
    }
}