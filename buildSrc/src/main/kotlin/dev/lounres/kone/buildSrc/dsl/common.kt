/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.buildSrc.dsl

import org.gradle.api.plugins.AppliedPlugin
import org.gradle.api.plugins.PluginAware
import org.gradle.api.plugins.PluginManager
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderConvertible
import org.gradle.kotlin.dsl.apply
import org.gradle.plugin.use.PluginDependency


public fun PluginAware.apply(pluginDependency: PluginDependency) { apply(plugin = pluginDependency.pluginId) }
public fun PluginAware.apply(pluginDependency: Provider<PluginDependency>) { apply(plugin = pluginDependency.get().pluginId) }
public fun PluginAware.apply(pluginDependency: ProviderConvertible<PluginDependency>) { apply(plugin = pluginDependency.asProvider().get().pluginId) }
public fun PluginManager.withPlugin(pluginDep: PluginDependency, block: AppliedPlugin.() -> Unit) { withPlugin(pluginDep.pluginId, block) }
public fun PluginManager.withPlugin(pluginDepProvider: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) { withPlugin(pluginDepProvider.get().pluginId, block) }
public fun PluginManager.withPlugins(vararg ids: String, block: AppliedPlugin.() -> Unit) { ids.forEach { withPlugin(it, block) } }
public fun PluginManager.withPlugins(vararg pluginDeps: PluginDependency, block: AppliedPlugin.() -> Unit) { pluginDeps.forEach { withPlugin(it, block) } }
public fun PluginManager.withPlugins(vararg pluginDepProviders: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) { pluginDepProviders.forEach { withPlugin(it, block) } }
public inline fun <T> Iterable<T>.withEach(action: T.() -> Unit) { forEach { it.action() } }