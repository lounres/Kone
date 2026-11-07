/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.kotlinCompilerTestUtils.services

import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.js.config.JSConfigurationKeys
import org.jetbrains.kotlin.platform.isJs
import org.jetbrains.kotlin.platform.jvm.isJvm
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.EnvironmentConfigurator
import org.jetbrains.kotlin.test.services.RuntimeClasspathProvider
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.targetPlatform
import java.io.File


private fun classpathFiles(property: String): List<File> {
    val property = System.getProperty(property) ?: error("Unable to get a valid classpath from '$property' property")
    return property.split(File.pathSeparator).map(::File)
}

private val jvmPluginRuntimeClasspath = classpathFiles("runtime.jvm.classpath")
private val jsPluginRuntimeClasspath = classpathFiles("runtime.js.classpath")

public class KonePluginRuntimeProvider(testServices: TestServices) : EnvironmentConfigurator(testServices) {
    override fun configureCompilerConfiguration(configuration: CompilerConfiguration, module: TestModule) {
        val targetPlatform = module.targetPlatform(testServices)
        when {
            targetPlatform.isJvm() -> configuration.addJvmClasspathRoots(jvmPluginRuntimeClasspath)
            targetPlatform.isJs() -> {
                val libraries = configuration.getList(JSConfigurationKeys.LIBRARIES)
                configuration.put(
                    JSConfigurationKeys.LIBRARIES,
                    libraries + jsPluginRuntimeClasspath.map { it.absolutePath },
                )
            }
            else -> error("Unsupported targetPlatform: $targetPlatform")
        }
    }
}

public class KonePluginRuntimeClasspathProvider(testServices: TestServices) : RuntimeClasspathProvider(testServices) {
    override fun runtimeClassPaths(module: TestModule): List<File> {
        val targetPlatform = module.targetPlatform(testServices)
        return when {
            targetPlatform.isJvm() -> jvmPluginRuntimeClasspath
            targetPlatform.isJs() -> jsPluginRuntimeClasspath
            else -> error("Unsupported targetPlatform: $targetPlatform")
        }
    }
}