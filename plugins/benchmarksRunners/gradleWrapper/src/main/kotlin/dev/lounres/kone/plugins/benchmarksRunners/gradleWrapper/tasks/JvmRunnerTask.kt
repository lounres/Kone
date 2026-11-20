/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfigurationBase
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation


class KoneBenchmarksJvmRunnerConfiguration(
    configurationName: String,
    private val kotlinCompilation: KotlinJvmCompilation,
) : KoneBenchmarkRunnerConfigurationBase<JavaExec>(configurationName, kotlinCompilation.project.objects) {
    companion object {
        fun namedDomainObjectFactory(kotlinCompilation: KotlinJvmCompilation) =
            NamedDomainObjectFactory { KoneBenchmarksJvmRunnerConfiguration(it, kotlinCompilation) }
    }
    
    override fun generateRunnersTasks(): List<TaskProvider<JavaExec>> {
        val runnerName = runnerName.orNull ?: throw GradleException("'${::runnerName.name}' is not set for configuration '${name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'.")
        return listOf(
            kotlinCompilation.project.tasks.register<JavaExec>(computeTaskName(kotlinCompilation)) {
                group = "kone benchmarks runners"
                description = "Execute runner '${runnerName}' via configuration '${kotlinCompilation.name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'."

                mainClass.set("dev.lounres.kone.plugins.benchmarksRunners.generated.__GENERATED__CALLABLES__Kt")

                classpath(kotlinCompilation.output.allOutputs + kotlinCompilation.runtimeDependencyFiles)

                dependsOn(kotlinCompilation.compileTaskProvider)

                args = buildList {
                    add(runnerName.toCliArgument())
                    for ([argumentName, argumentValue] in rawArguments.get()) {
                        add(argumentName.toCliArgument())
                        add(argumentValue.toCliArgument())
                    }
                }

                javaLauncher.set(project.javaLauncherProvider())
            },
        )
    }
}