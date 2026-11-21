/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation


internal fun String.capitalize(): String = replaceFirstChar { it.uppercaseChar() }

internal fun KoneBenchmarkRunnerConfiguration.computeTaskName(taskName: Provider<String>, kotlinCompilation: KotlinCompilation<*>): String =
    taskName.orNull ?: "run${kotlinCompilation.target.targetName.capitalize()}${kotlinCompilation.name.capitalize()}Benchmark${name.capitalize()}"

internal fun String.toCliArgument(): String = "\"${this.replace("\"", "\\\"")}\""

internal fun Project.javaLauncherProvider(): Provider<JavaLauncher> = provider {
    val toolchainService = extensions.findByType(JavaToolchainService::class.java) ?: return@provider null
    val javaExtension = extensions.findByType(JavaPluginExtension::class.java) ?: return@provider null
    toolchainService.launcherFor(javaExtension.toolchain).orNull
}