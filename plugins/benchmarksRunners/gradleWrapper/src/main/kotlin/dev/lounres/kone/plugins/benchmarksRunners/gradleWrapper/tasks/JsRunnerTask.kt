/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfigurationBase
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec


class KoneBenchmarksJsRunnerConfiguration(
    configurationName: String,
    private val kotlinCompilation: KotlinJsIrCompilation,
) : KoneBenchmarkRunnerConfigurationBase<NodeJsExec>(configurationName, kotlinCompilation.project.objects) {
    companion object {
        fun namedDomainObjectFactory(kotlinCompilation: KotlinJsIrCompilation) =
            NamedDomainObjectFactory { KoneBenchmarksJsRunnerConfiguration(it, kotlinCompilation) }
    }
    
    override fun generateRunnersTasks(): List<TaskProvider<NodeJsExec>> {
//        compilation.target.subTargets.mapNotNull { subTarget ->
//            when (subTarget) {
//                is KotlinNodeJsIr -> {
//
//                }
//                else -> null
//            }
//        }
        return kotlinCompilation.binaries.map { binary ->
            val runnerName = runnerName.orNull ?: throw GradleException("'${::runnerName.name}' is not set for configuration '$name' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'.")
            NodeJsExec.register(kotlinCompilation, "${computeTaskName(kotlinCompilation)}Using${binary.name.replaceFirstChar { it.uppercaseChar() }}") {
                group = "kone benchmarks runners"
                description = "Execute runner '$runnerName' via configuration '$name' by binary '${binary.name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'."
                
                dependsOn(compilation.runtimeDependencyFiles)
                inputFileProperty.set(binary.mainFileSyncPath)
                
                args = buildList {
                    add(runnerName.toCliArgument())
                    for ([argumentName, argumentValue] in rawArguments.get()) {
                        add(argumentName.toCliArgument())
                        add(argumentValue.toCliArgument())
                    }
                }
                
                standardOutput = System.out
                errorOutput = System.err
            }
        }
    }
}