/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper

import org.gradle.api.*
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import kotlin.reflect.KClass


open class KoneBenchmarksRunnersGradleExtension(val project: Project) {
    private val _compilationToRunnersConfigurations = mutableMapOf<KotlinCompilation<*>, ExtensiblePolymorphicDomainObjectContainer<KoneBenchmarkRunnerConfiguration<*>>>()
    val compilationToRunnersConfigurations: Map<KotlinCompilation<*>, ExtensiblePolymorphicDomainObjectContainer<KoneBenchmarkRunnerConfiguration<*>>>
        get() = _compilationToRunnersConfigurations
    
    operator fun <T : Task, RG: KoneBenchmarkRunnerConfiguration<T>> get(
        kotlinCompilation: KotlinCompilation<*>,
        configurationClass: Class<RG>,
        configurationFactory: NamedDomainObjectFactory<out RG>,
    ): NamedDomainObjectContainer<RG> =
        _compilationToRunnersConfigurations
            .getOrPut(kotlinCompilation) {
                project.objects.polymorphicDomainObjectContainer(KoneBenchmarkRunnerConfiguration::class.java)
            }
            .apply {
                registerFactory(configurationClass, configurationFactory)
            }
            .containerWithType(configurationClass)
    
    operator fun <T : Task, RG: KoneBenchmarkRunnerConfiguration<T>> get(
        kotlinCompilation: KotlinCompilation<*>,
        configurationKClass: KClass<RG>,
        configurationFactory: NamedDomainObjectFactory<out RG>,
    ): NamedDomainObjectContainer<RG> = get(
        kotlinCompilation = kotlinCompilation,
        configurationClass = configurationKClass.java,
        configurationFactory = configurationFactory,
    )
    
    inline operator fun <T : Task, reified RG: KoneBenchmarkRunnerConfiguration<T>> get(
        kotlinCompilation: KotlinCompilation<*>,
        configurationFactory: NamedDomainObjectFactory<out RG>,
    ): NamedDomainObjectContainer<RG> = get(
        kotlinCompilation = kotlinCompilation,
        configurationKClass = RG::class,
        configurationFactory = configurationFactory,
    )
    
    inline operator fun <T : Task, RG: KoneBenchmarkRunnerConfiguration<T>> KotlinCompilation<*>.invoke(
        configurationClass: Class<RG>,
        configurationFactory: NamedDomainObjectFactory<out RG>,
        configurator: NamedDomainObjectContainer<RG>.() -> Unit
    ): NamedDomainObjectContainer<RG> = get(this, configurationClass, configurationFactory).apply(configurator)
    
    inline operator fun <T : Task, RG: KoneBenchmarkRunnerConfiguration<T>> KotlinCompilation<*>.invoke(
        configurationClass: KClass<RG>,
        configurationFactory: NamedDomainObjectFactory<out RG>,
        configurator: NamedDomainObjectContainer<RG>.() -> Unit
    ): NamedDomainObjectContainer<RG> = get(this, configurationClass, configurationFactory).apply(configurator)
    
    inline operator fun <T : Task, reified RG: KoneBenchmarkRunnerConfiguration<T>> KotlinCompilation<*>.invoke(
        configurationFactory: NamedDomainObjectFactory<out RG>,
        configurator: NamedDomainObjectContainer<RG>.() -> Unit
    ): NamedDomainObjectContainer<RG> = get(this, configurationFactory).apply(configurator)
    
    fun finalizeCurrentConfigurations() {
        for (runnerConfigurationsContainer in _compilationToRunnersConfigurations.values) {
            for (configuration in runnerConfigurationsContainer.names.map { runnerConfigurationsContainer[it] }) {
                configuration.finalize()
            }
        }
    }
}