/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.jetbrains.kotlin.gradle.targets.js.ir.*
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsExec


class KoneBenchmarksJsRunnerConfiguration(
    val configurationName: String,
    private val kotlinCompilation: KotlinJsIrCompilation,
) : KoneBenchmarkRunnerConfiguration {
    companion object {
        fun namedDomainObjectFactory(kotlinCompilation: KotlinJsIrCompilation) =
            NamedDomainObjectFactory { KoneBenchmarksJsRunnerConfiguration(it, kotlinCompilation) }
    }
    
    private var isFinalized: Boolean = false
    private var taskProviders: List<TaskProvider<out DefaultTask>>? = null
    private val taskConfigurators = mutableListOf<DefaultTask.() -> Unit>()
    
    override fun getName(): String = configurationName
    
    val taskName: Property<String> = kotlinCompilation.project.objects.property()
    
    fun configureRunnersTasks(block: DefaultTask.() -> Unit) {
        val taskProviders = taskProviders
        if (taskProviders != null) {
            taskProviders.forEach { it.configure(block) }
        } else {
            taskConfigurators += block
        }
    }
    
    override val runnerName: Property<String> = kotlinCompilation.project.objects.property()
    
    override val rawArguments: MapProperty<String, String> = kotlinCompilation.project.objects.mapProperty()
    
    private fun generateRunnersTasks(): List<TaskProvider<out DefaultTask>> {
        val runnerName = runnerName.orNull ?: throw GradleException("'${::runnerName.name}' is not set for configuration '$name' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'.")
        val taskNamePrefix = computeTaskName(taskName, kotlinCompilation)
        return kotlinCompilation.target.subTargets.flatMap { subTarget ->
            when (subTarget) {
                is KotlinNodeJsIr -> {
                    kotlinCompilation.binaries.filterIsInstance<Executable>().map { binary ->
                        val mode = when (binary.mode) {
                            PRODUCTION -> "Production"
                            DEVELOPMENT -> "Development"
                        }
                        NodeJsExec.register(kotlinCompilation, "${taskNamePrefix}Using${mode}NodeJs") {
                            group = "kone benchmarks runners"
                            description = "Execute runner '$runnerName' via configuration '${this@KoneBenchmarksJsRunnerConfiguration.name}' by binary '${binary.name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'."
                            
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
                is KotlinBrowserJsIr -> {
//                    kotlinCompilation.binaries.filterIsInstance<Executable>().map { binary ->
//                        val mode = when (binary.mode) {
//                            KotlinJsBinaryMode.PRODUCTION -> "Production"
//                            KotlinJsBinaryMode.DEVELOPMENT -> "Development"
//                        }
//                        kotlinCompilation.project.tasks.register<KotlinWebpack>("${taskNamePrefix}Using${mode}Browser") {
//
//                        }
//                    }
                    emptyList()
                }
                is KotlinD8Ir -> emptyList()
                else -> emptyList()
            }
        }
    }
    
    override fun finalize() {
        if (!isFinalized) {
            isFinalized = true
            
            taskName.disallowChanges()
            runnerName.disallowChanges()
            rawArguments.disallowChanges()
            
            val generatedTaskProviders = generateRunnersTasks()
            taskProviders = generatedTaskProviders
            for (configurator in taskConfigurators) for (taskProvider in generatedTaskProviders) taskProvider.configure(configurator)
            taskConfigurators.clear()
        }
    }
}