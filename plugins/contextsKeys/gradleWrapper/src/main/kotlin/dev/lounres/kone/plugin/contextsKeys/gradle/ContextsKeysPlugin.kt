/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugin.contextsKeys.gradle

import dependencyVersion
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import plguinDependencyArtifact
import plguinDependencyGroup
import runtimeDependencyArtifact
import runtimeDependencyGroup


@Suppress("unused")
class ContextsKeysPlugin : KotlinCompilerPluginSupportPlugin {
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
    
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        
        kotlinCompilation.defaultSourceSet.dependencies { api("$runtimeDependencyGroup:$runtimeDependencyArtifact:$dependencyVersion") }
        if (kotlinCompilation.defaultSourceSet.implementationConfigurationName == "metadataCompilationImplementation") {
            project.dependencies.add("commonMainImplementation", "$runtimeDependencyGroup:$runtimeDependencyArtifact:$dependencyVersion")
        }
        
        return project.provider { emptyList() }
    }
    
    override fun getCompilerPluginId(): String = "$plguinDependencyGroup.$plguinDependencyArtifact"
    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
        groupId = plguinDependencyGroup,
        artifactId = plguinDependencyArtifact,
        version = dependencyVersion,
    )
}