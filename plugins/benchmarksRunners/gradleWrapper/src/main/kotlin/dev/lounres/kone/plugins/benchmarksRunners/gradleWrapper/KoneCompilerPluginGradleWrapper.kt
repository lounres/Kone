/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectFactory
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
            for (runnerConfigurationsContainer in extension.finalize().values) {
                for (configuration in runnerConfigurationsContainer.names.map { runnerConfigurationsContainer[it] }) {
                    configuration.generateRunnerTask()
                }
            }
        }
    }
    
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation in kotlinCompilation.project.the<KoneBenchmarksRunnersGradleExtension>().finalize()
}

open class KoneBenchmarksRunnersGradleExtension(val project: Project) {
    private class RunnerConfigurationNamedDomainObjectFactory<C : KotlinCompilation<*>, T : Task>(
        private val kotlinCompilation: C,
        private val runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>,
    ) : NamedDomainObjectFactory<KoneBenchmarkRunnerConfiguration<C, T>> {
        override fun create(name: String): KoneBenchmarkRunnerConfiguration<C, T> {
            return KoneBenchmarkRunnerConfiguration(
                kotlinCompilation = kotlinCompilation,
                runnersGenerator = runnersGenerator,
                name = name,
            )
        }
    }
    
    private val compilationToRunners = mutableMapOf<KotlinCompilation<*>, NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<*, *>>>()
    
    operator fun <C: KotlinCompilation<*>, T : Task> get(kotlinCompilation: C, runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>): NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<C, T>> =
        compilationToRunners
            .getOrPut(kotlinCompilation) {
                project.objects.domainObjectContainer(
                    KoneBenchmarkRunnerConfiguration::class.java.uncheckedCast(),
                    RunnerConfigurationNamedDomainObjectFactory(
                        kotlinCompilation = kotlinCompilation,
                        runnersGenerator = runnersGenerator,
                    ),
                ).uncheckedCast()
            }
            .uncheckedCast()
    
    inline operator fun <C: KotlinCompilation<*>, T : Task> C.invoke(runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>, configurator: NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<C, T>>.() -> Unit) {
        get<C, T>(this, runnersGenerator).configurator()
    }
    
    internal fun finalize(): Map<KotlinCompilation<*>, NamedDomainObjectContainer<KoneBenchmarkRunnerConfiguration<KotlinCompilation<*>, Task>>> = compilationToRunners
}

interface KoneBenchmarksRunnersGenerator<C : KotlinCompilation<*>, T : Task> {
    fun generateRunnerTask(
        project: Project,
        compilation: C,
        taskName: String,
        configuration: KoneBenchmarkRunnerConfiguration<C, T>,
    ): TaskProvider<T>
}

open class KoneBenchmarkRunnerConfiguration<out C : KotlinCompilation<*>, out T : Task>(
    private val kotlinCompilation: C,
    private val runnersGenerator: KoneBenchmarksRunnersGenerator<C, T>,
    val name: String,
) {
    private var taskProvider: TaskProvider<T>? = null
    
    private val taskConfigurators = mutableListOf<T.() -> Unit>()
    
    fun configureTask(block: T.() -> Unit) {
        val taskProvider = taskProvider
        if (taskProvider != null) {
            taskProvider.configure(block)
        } else {
            taskConfigurators += block
        }
    }
    
    val runnerName = kotlinCompilation.project.objects.property<String>()
    
    private val _arguments: MutableMap<String, String> = mutableMapOf()
    val arguments: MapProperty<String, String> = kotlinCompilation.project.objects.mapProperty()
    
    fun rawArgument(key: String, value: String) {
        arguments.put(key, value)
    }
    fun <T> argument(name: String, valueSerializer: SerializationStrategy<T>, value: T) {
        rawArgument(name, Json.encodeToString(valueSerializer, value))
    }
    inline fun <reified T> argument(name: String, value: T) {
        rawArgument(name, Json.encodeToString(value))
    }
    
    internal fun generateRunnerTask() {
        fun String.capitalize(): String = replaceFirstChar { it.uppercaseChar() }
        runnersGenerator.generateRunnerTask(
            project = kotlinCompilation.project,
            compilation = kotlinCompilation,
            taskName = "run${kotlinCompilation.target.targetName.capitalize()}${kotlinCompilation.name.capitalize()}Benchmark${name.capitalize()}",
            configuration = this,
        ).apply {
            taskProvider = this
            for (configurator in taskConfigurators) { configure(configurator) }
            taskConfigurators.clear()
        }
    }
}