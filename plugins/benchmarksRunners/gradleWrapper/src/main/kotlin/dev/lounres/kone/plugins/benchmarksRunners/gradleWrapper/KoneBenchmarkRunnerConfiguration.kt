/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import org.gradle.api.Named
import org.gradle.api.Task
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property


interface KoneBenchmarkRunnerConfiguration<out T: Task> : Named {
    val taskName: Property<String>
    
    fun configureRunnersTasks(block: T.() -> Unit)
    
    val runnerName: Property<String>
    
    val rawArguments: MapProperty<String, String>
    
    fun finalize()
}

abstract class KoneBenchmarkRunnerConfigurationBase<out T: Task>(
    private val configurationName: String,
    objectFactory: ObjectFactory
) : KoneBenchmarkRunnerConfiguration<T> {
    private var isFinalized: Boolean = false
    private var taskProviders: List<TaskProvider<out T>>? = null
    private val taskConfigurators = mutableListOf<T.() -> Unit>()
    
    override fun getName(): String = configurationName
    
    final override val taskName: Property<String> = objectFactory.property()
    
    final override fun configureRunnersTasks(block: T.() -> Unit) {
        val taskProviders = taskProviders
        if (taskProviders != null) {
            taskProviders.forEach { it.configure(block) }
        } else {
            taskConfigurators += block
        }
    }
    
    final override val runnerName: Property<String> = objectFactory.property()
    
    final override val rawArguments: MapProperty<String, String> = objectFactory.mapProperty()
    
    protected abstract fun generateRunnersTasks(): List<TaskProvider<out T>>
    
    final override fun finalize() {
        if (!isFinalized) {
            isFinalized = true
            
            taskName.disallowChanges()
            runnerName.disallowChanges()
            rawArguments.disallowChanges()
            
            val generatedTaskProviders = generateRunnersTasks()
            taskProviders = generatedTaskProviders
            for (taskProvider in generatedTaskProviders) for (configurator in taskConfigurators) taskProvider.configure(configurator)
            taskConfigurators.clear()
        }
    }
}