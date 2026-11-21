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
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation


class KoneBenchmarksJvmRunnerConfiguration(
    val configurationName: String,
    private val kotlinCompilation: KotlinJvmCompilation,
) : KoneBenchmarkRunnerConfiguration {
    companion object {
        fun namedDomainObjectFactory(kotlinCompilation: KotlinJvmCompilation) =
            NamedDomainObjectFactory { KoneBenchmarksJvmRunnerConfiguration(it, kotlinCompilation) }
    }
    
    private var isFinalized: Boolean = false
    private var taskProviders: List<TaskProvider<out JavaExec>>? = null
    private val taskConfigurators = mutableListOf<JavaExec.() -> Unit>()
    
    override fun getName(): String = configurationName
    
    val taskName: Property<String> = kotlinCompilation.project.objects.property()
    
    fun configureRunnersTasks(block: JavaExec.() -> Unit) {
        val taskProviders = taskProviders
        if (taskProviders != null) {
            taskProviders.forEach { it.configure(block) }
        } else {
            taskConfigurators += block
        }
    }
    
    override val runnerName: Property<String> = kotlinCompilation.project.objects.property()
    
    override val rawArguments: MapProperty<String, String> = kotlinCompilation.project.objects.mapProperty()
    
    private fun generateRunnersTasks(): List<TaskProvider<JavaExec>> {
        val runnerName = runnerName.orNull ?: throw GradleException("'${::runnerName.name}' is not set for configuration '${name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'.")
        return listOf(
            kotlinCompilation.project.tasks.register<JavaExec>(computeTaskName(taskName, kotlinCompilation)) {
                group = "kone benchmarks runners"
                description = "Execute runner '${runnerName}' via configuration '${this@KoneBenchmarksJvmRunnerConfiguration.name}' from compilation '${kotlinCompilation.compilationName}' in target '${kotlinCompilation.target.targetName}'."

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