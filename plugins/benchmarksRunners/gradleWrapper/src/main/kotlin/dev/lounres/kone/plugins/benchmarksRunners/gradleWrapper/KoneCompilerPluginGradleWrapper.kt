/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.extensions.stdlib.uncheckedCast
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation


class KoneCompilerPluginGradleWrapper : DevKitSupportPlugin(KonePluginInfo.PLUGIN_INFO) {
    override fun apply(target: Project) {
        val extension = KoneBenchmarksRunnersGradleExtension(target)
        target.extensions.add("koneBenchmarksRunners", extension)
        
        target.afterEvaluate {
            extension.finalize()
        }
    }
    
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        !kotlinCompilation.project.the<KoneBenchmarksRunnersGradleExtension>().finalize()[kotlinCompilation].isNullOrEmpty()
}

open class KoneBenchmarksRunnersGradleExtension(val project: Project) {
    private var isFinalized: Boolean = false
    
    private val compilationToRunners = mutableMapOf<KotlinCompilation<*>, NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<*, *>>>()
    
    operator fun <C: KotlinCompilation<*>, T : Task> get(kotlinCompilation: C, runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>): NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<C, T>> =
        compilationToRunners
            .getOrPut(kotlinCompilation) {
                project.objects.domainObjectContainer(KoneBenchmarkRunnerConfiguration::class.java.uncheckedCast()) { name ->
                    KoneBenchmarkRunnerConfiguration(
                        kotlinCompilation = kotlinCompilation,
                        runnersGenerator = runnersGenerator,
                        name = name,
                    )
                }.uncheckedCast()
            }
            .uncheckedCast()
    
    inline operator fun <C: KotlinCompilation<*>, T : Task> C.invoke(runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>, configurator: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<C, T>>.() -> Unit) {
        get<C, T>(this, runnersGenerator).configurator()
    }
    
    @IgnorableReturnValue
    internal fun finalize(): Map<KotlinCompilation<*>, NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinCompilation<*>, Task>>> {
        if (!isFinalized) {
            isFinalized = true
            for (runnerConfigurationsContainer in compilationToRunners.values) {
                for (configuration in runnerConfigurationsContainer.names.map { runnerConfigurationsContainer[it] }) {
                    configuration.finalize()
                }
            }
        }
        return compilationToRunners
    }
}

interface KoneBenchmarksRunnersGenerator<C : KotlinCompilation<*>, T : Task> {
    fun generateRunnerTask(
        project: Project,
        compilation: C,
        taskName: String,
        configuration: KoneBenchmarkRunnerConfiguration<C, T>,
    ): List<TaskProvider<T>>
}

open class KoneBenchmarkRunnerConfiguration<out C : KotlinCompilation<*>, out T : Task>(
    private val kotlinCompilation: C,
    private val runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>,
    val name: String,
) {
    private var isFinalized: Boolean = false
    private var taskProviders: List<TaskProvider<T>>? = null
    private val taskConfigurators = mutableListOf<T.() -> Unit>()
    
    val taskName = kotlinCompilation.project.objects.property<String>()
    
    fun configureTask(block: T.() -> Unit) {
        val taskProviders = taskProviders
        if (taskProviders != null) {
            taskProviders.forEach { it.configure(block) }
        } else {
            taskConfigurators += block
        }
    }
    
    val runnerName = kotlinCompilation.project.objects.property<String>()
    
    val rawArguments: MapProperty<String, String> = kotlinCompilation.project.objects.mapProperty()
    
    fun rawArgument(key: String, value: String) {
        rawArguments.put(key, value)
    }
    fun <T> argument(name: String, valueSerializer: SerializationStrategy<T>, value: T) {
        rawArgument(name, Json.encodeToString(valueSerializer, value))
    }
    inline fun <reified T> argument(name: String, value: T) {
        rawArgument(name, Json.encodeToString(value))
    }
    
    fun finalize() {
        if (!isFinalized) {
            isFinalized = true
            
            taskName.disallowChanges()
            runnerName.disallowChanges()
            rawArguments.disallowChanges()
            
            fun String.capitalize(): String = replaceFirstChar { it.uppercaseChar() }
            runnersGenerator.generateRunnerTask(
                project = kotlinCompilation.project,
                compilation = kotlinCompilation,
                taskName = taskName.orNull ?: "run${kotlinCompilation.target.targetName.capitalize()}${kotlinCompilation.name.capitalize()}Benchmark${name.capitalize()}",
                configuration = this,
            ).also {
                taskProviders = it
                for (taskProvider in it) for (configurator in taskConfigurators) taskProvider.configure(configurator)
                taskConfigurators.clear()
            }
        }
    }
}