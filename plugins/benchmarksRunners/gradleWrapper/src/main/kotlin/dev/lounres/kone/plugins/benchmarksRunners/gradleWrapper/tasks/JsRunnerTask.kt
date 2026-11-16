/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarksRunnersGenerator
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec


object KoneBenchmarksJsRunnersGenerator : KoneBenchmarksRunnersGenerator<KotlinJsIrCompilation, NodeJsExec> {
    override fun generateRunnerTask(
        project: Project,
        compilation: KotlinJsIrCompilation,
        taskName: String,
        configuration: KoneBenchmarkRunnerConfiguration<KotlinJsIrCompilation, NodeJsExec>
    ): List<TaskProvider<NodeJsExec>> {
        return compilation.binaries.map { binary ->
            val runnerName = configuration.runnerName.orNull ?: throw GradleException("'${configuration::runnerName.name}' is not set for configuration '${configuration.name}'from compilation '${compilation.compilationName}' in target '${compilation.target.targetName}'.")
            NodeJsExec.register(compilation, "${taskName}Using${binary.name.replaceFirstChar { it.uppercaseChar() }}") {
                group = "kone benchmarks runners"
                description = "Execute runner '$runnerName' via configuration '${configuration.name}' by binary '${binary.name}' from compilation '${compilation.compilationName}' in target '${compilation.target.targetName}'."
                
                dependsOn(compilation.runtimeDependencyFiles)
                inputFileProperty.set(binary.mainFileSyncPath)
                
                args = buildList {
                    add(runnerName)
                    for ([argumentName, argumentValue] in configuration.arguments.get()) {
                        add(argumentName)
                        add(argumentValue)
                    }
                }
                
                standardOutput = System.out
                errorOutput = System.err
            }
        }
    }
}