/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGenerator
import org.gradle.api.GradleException
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
    ): List<TaskProvider<JavaExec>> {
        val runnerName = configuration.runnerName.orNull ?: throw GradleException("'${configuration::runnerName.name}' is not set for configuration '${configuration.name}' from compilation '${compilation.compilationName}' in target '${compilation.target.targetName}'.")
        return listOf(
            project.tasks.register<JavaExec>(taskName) {
                group = "kone benchmarks runners"
                description = "Execute runner '${runnerName}' via configuration '${configuration.name}' from compilation '${compilation.compilationName}' in target '${compilation.target.targetName}'."
                
                mainClass.set("dev.lounres.kone.plugins.benchmarksRunners.generated.__GENERATED__CALLABLES__Kt")
                
                classpath(compilation.output.allOutputs + compilation.runtimeDependencyFiles)
                
                dependsOn(compilation.compileTaskProvider)
                
                args = buildList {
                    add(runnerName.toCliArgument())
                    for ([argumentName, argumentValue] in configuration.rawArguments.get()) {
                        add(argumentName.toCliArgument())
                        add(argumentValue.toCliArgument())
                    }
                }
                
                javaLauncher.set(project.javaLauncherProvider())
            },
        )
        }
}