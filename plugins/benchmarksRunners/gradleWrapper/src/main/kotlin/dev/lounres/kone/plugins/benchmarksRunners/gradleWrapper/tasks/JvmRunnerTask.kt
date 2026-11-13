/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGenerator
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation


object KoneBenchmarksJvmRunnersGenerator : KoneBenchmarksRunnersGenerator<KotlinJvmCompilation, JavaExec> {
    private fun Project.javaLauncherProvider(): Provider<JavaLauncher> = provider {
        val toolchainService = extensions.findByType(JavaToolchainService::class.java) ?: return@provider null
        val javaExtension = extensions.findByType(JavaPluginExtension::class.java) ?: return@provider null
        toolchainService.launcherFor(javaExtension.toolchain).orNull
    }
    
    override fun generateRunnerTask(
        project: Project,
        compilation: KotlinJvmCompilation,
        taskName: String,
        configuration: KoneBenchmarkRunnerConfiguration<KotlinJvmCompilation, JavaExec>,
    ): TaskProvider<JavaExec> =
        project.tasks.register<JavaExec>(taskName) {
            group = "koneBenchmarksRunners"
            description = "Execute runner '${configuration.runnerName}'."
            
            mainClass.set("dev.lounres.kone.plugins.benchmarksRunners.generated.__GENERATED__CALLABLES__Kt")
            
            classpath(compilation.output.allOutputs + compilation.runtimeDependencyFiles)
            
            dependsOn(compilation.compileTaskProvider)
            
            args = buildList {
                add(configuration.runnerName.get())
                for ((argumentName, argumentValue) in configuration.arguments.get()) {
                    add(argumentName)
                    add(argumentValue)
                }
            }
            
            javaLauncher.set(project.javaLauncherProvider())
        }
}