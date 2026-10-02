/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.buildSrc.dsl

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME


public class KonePluginsCollector(private val project: Project) {
    public operator fun String.unaryPlus() {
        project.dependencies {
            add(PLUGIN_CLASSPATH_CONFIGURATION_NAME, project(":plugins:${this@unaryPlus}"))
        }
        project.pluginManager.withPlugins("org.jetbrains.kotlin.multiplatform") {
            project.configure<KotlinMultiplatformExtension> {
                sourceSets {
                    commonMain {
                        dependencies {
                            api(project(":plugins:${this@unaryPlus}:runtime"))
                        }
                    }
                }
            }
        }
        project.pluginManager.withPlugins("org.jetbrains.kotlin.jvm") {
            project.configure<KotlinJvmProjectExtension> {
                sourceSets {
                    named("main") {
                        dependencies {
                            api(project(":plugins:${this@unaryPlus}:runtime"))
                        }
                    }
                }
            }
        }
    }
}

public fun Project.konePlugins(block: KonePluginsCollector.() -> Unit) {
    KonePluginsCollector(project).block()
}