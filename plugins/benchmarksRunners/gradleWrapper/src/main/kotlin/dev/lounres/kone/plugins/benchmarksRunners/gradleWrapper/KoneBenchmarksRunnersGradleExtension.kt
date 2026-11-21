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
    private data class RunnersConfigurations(
        val container: ExtensiblePolymorphicDomainObjectContainer<KoneBenchmarkRunnerConfiguration>,
        val registeredClasses: MutableSet<Class<*>>
    )
    private val compilationToRunnersConfigurations = mutableMapOf<KotlinCompilation<*>, RunnersConfigurations>()
    
    operator fun <C: KoneBenchmarkRunnerConfiguration> get(
        kotlinCompilation: KotlinCompilation<*>,
        configurationClass: Class<C>,
        configurationFactory: NamedDomainObjectFactory<C>,
    ): NamedDomainObjectContainer<C> =
        compilationToRunnersConfigurations
            .getOrPut(kotlinCompilation) {
                RunnersConfigurations(
                    container = project.objects.polymorphicDomainObjectContainer(KoneBenchmarkRunnerConfiguration::class.java),
                    registeredClasses = mutableSetOf()
                )
            }
            .run {
                if (configurationClass !in registeredClasses) {
                    container.registerFactory(configurationClass, configurationFactory)
                    registeredClasses.add(configurationClass)
                }
                container.containerWithType(configurationClass)
            }
    
    operator fun <C: KoneBenchmarkRunnerConfiguration> get(
        kotlinCompilation: KotlinCompilation<*>,
        configurationKClass: KClass<C>,
        configurationFactory: NamedDomainObjectFactory<C>,
    ): NamedDomainObjectContainer<C> = get(
        kotlinCompilation = kotlinCompilation,
        configurationClass = configurationKClass.java,
        configurationFactory = configurationFactory,
    )
    
    inline operator fun <reified C: KoneBenchmarkRunnerConfiguration> get(
        kotlinCompilation: KotlinCompilation<*>,
        configurationFactory: NamedDomainObjectFactory<C>,
    ): NamedDomainObjectContainer<C> = get(
        kotlinCompilation = kotlinCompilation,
        configurationKClass = C::class,
        configurationFactory = configurationFactory,
    )
    
    inline operator fun <C: KoneBenchmarkRunnerConfiguration> KotlinCompilation<*>.invoke(
        configurationClass: Class<C>,
        configurationFactory: NamedDomainObjectFactory<C>,
        configurator: NamedDomainObjectContainer<C>.() -> Unit
    ): NamedDomainObjectContainer<C> = get(this, configurationClass, configurationFactory).apply(configurator)
    
    inline operator fun <C: KoneBenchmarkRunnerConfiguration> KotlinCompilation<*>.invoke(
        configurationClass: KClass<C>,
        configurationFactory: NamedDomainObjectFactory<C>,
        configurator: NamedDomainObjectContainer<C>.() -> Unit
    ): NamedDomainObjectContainer<C> = get(this, configurationClass, configurationFactory).apply(configurator)
    
    inline operator fun <reified C: KoneBenchmarkRunnerConfiguration> KotlinCompilation<*>.invoke(
        configurationFactory: NamedDomainObjectFactory<C>,
        configurator: NamedDomainObjectContainer<C>.() -> Unit
    ): NamedDomainObjectContainer<C> = get(this, configurationFactory).apply(configurator)
    
    fun finalizeCurrentConfigurations() {
        for ((runnerConfigurationsContainer = container) in compilationToRunnersConfigurations.values) {
            for (configuration in runnerConfigurationsContainer.names.map { runnerConfigurationsContainer[it] }) {
                configuration.finalize()
            }
        }
    }
}