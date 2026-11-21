/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.tasks

import dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper.KoneBenchmarkRunnerConfiguration
import org.gradle.api.GradleException
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.HostManager


class KoneBenchmarksNativeRunnerConfiguration(
    val configurationName: String,
    private val kotlinCompilation: KotlinNativeCompilation,
) : KoneBenchmarkRunnerConfiguration {
    companion object {
        fun namedDomainObjectFactory(kotlinCompilation: KotlinNativeCompilation) =
            NamedDomainObjectFactory { KoneBenchmarksNativeRunnerConfiguration(it, kotlinCompilation) }
        
        private val buildTypes: List<NativeBuildType> = NativeBuildType.entries
        private val executableNamePrefix = "koneBenchmarksRunner"
    }
    
    private var isFinalized: Boolean = false
    private var taskProviders: List<TaskProvider<out Exec>>? = null
    private val taskConfigurators = mutableListOf<Exec.() -> Unit>()
    
    override fun getName(): String = configurationName
    
    val taskName: Property<String> = kotlinCompilation.project.objects.property()
    
    fun configureRunnersTasks(block: Exec.() -> Unit) {
        val taskProviders = taskProviders
        if (taskProviders != null) {
            taskProviders.forEach { it.configure(block) }
        } else {
            taskConfigurators += block
        }
    }
    
    override val runnerName: Property<String> = kotlinCompilation.project.objects.property()
    
    override val rawArguments: MapProperty<String, String> = kotlinCompilation.project.objects.mapProperty()
    
    private fun generateRunnersTasks(): List<TaskProvider<Exec>> {
        if (kotlinCompilation.konanTarget != HostManager.host) return emptyList()
        
        val runnerName = runnerName.orNull ?: throw GradleException("'${::runnerName.name}' is not set for configuration '${name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'.")
        val taskNamePrefix = computeTaskName(taskName, kotlinCompilation)
        return buildTypes.map { buildType ->
            if (kotlinCompilation.target.binaries.findExecutable(executableNamePrefix, buildType) == null)
                kotlinCompilation.target.binaries.executable(executableNamePrefix, listOf(buildType)) {
                    entryPoint = "dev.lounres.kone.plugins.benchmarksRunners.generated.main"
                }
            
            val binary = kotlinCompilation.target.binaries.getExecutable(executableNamePrefix, buildType)
            val mode = when (buildType) {
                RELEASE -> "Release"
                DEBUG -> "Debug"
            }
            kotlinCompilation.project.tasks.register<Exec>("${taskNamePrefix}Using${mode}") {
                group = "kone benchmarks runners"
                description = "Execute runner '${runnerName}' via configuration '${this@KoneBenchmarksNativeRunnerConfiguration.name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'."
                
                dependsOn(binary.linkTaskProvider)
                
                commandLine = listOf(binary.outputFile.absolutePath)
                
                args = buildList {
                    add(runnerName.toCliArgument())
                    for ([argumentName, argumentValue] in rawArguments.get()) {
                        add(argumentName.toCliArgument())
                        add(argumentValue.toCliArgument())
                    }
                }
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