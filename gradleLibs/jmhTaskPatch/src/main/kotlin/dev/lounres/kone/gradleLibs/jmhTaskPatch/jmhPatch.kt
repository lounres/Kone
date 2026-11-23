/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.gradleLibs.jmhTaskPatch

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import java.io.File


private fun String.capitalize(): String = replaceFirstChar { it.uppercaseChar() }

private fun Project.javaLauncherProvider(): Provider<JavaLauncher> = provider {
    val toolchainService = extensions.findByType(JavaToolchainService::class.java) ?: return@provider null
    val javaExtension = extensions.findByType(JavaPluginExtension::class.java) ?: return@provider null
    toolchainService.launcherFor(javaExtension.toolchain).orNull
}

private fun Project.javaCompilerProvider(): Provider<JavaCompiler> = provider {
    val toolchainService = extensions.findByType(JavaToolchainService::class.java) ?: return@provider null
    val javaExtension = extensions.findByType(JavaPluginExtension::class.java) ?: return@provider null
    toolchainService.compilerFor(javaExtension.toolchain).orNull
}

public class JmhPatch(
    public val jmhCompilationTask: TaskProvider<JavaCompile>,
    public val jmhClassesDirectory: File,
    public val jmhResourcesDirectory: File,
)

public fun Project.jmhPatchFor(
    patchName: String,
    compilation: KotlinJvmCompilation,
    buildDir: File,
): JmhPatch {
    val jmhWorkerConfiguration = project.configurations.create("jmhGenerationClasspathFor${patchName.capitalize()}").apply {
        description = "JMH patch '$patchName' generator classpath for target '${compilation.target.targetName}' compilation '${compilation.compilationName}'"
        
        val dependencies = project.dependencies
        defaultDependencies {
            add(dependencies.create("org.openjdk.jmh:jmh-generator-bytecode:1.37")) // TODO: Replace with settings
        }
    }
    val jmhSourcesDir = buildDir.resolve("sources")
    val jmhResourcesDirectory = buildDir.resolve("resources")
    val jmhClassesDirectory =buildDir.resolve("classes")
    val jmhGeneratorTask = project.tasks.register<JmhGeneratorTask>("generateJmhFor${patchName.capitalize()}") {
        description = "Generate JMH patch '$patchName' for target '${compilation.target.targetName}' compilation '${compilation.compilationName}'"
        dependsOn(compilation.compileAllTaskName)
        runtimeClasspath = jmhWorkerConfiguration
        inputCompileClasspath = compilation.compileDependencyFiles
        inputClassesDirs = compilation.output.allOutputs
        outputResourcesDir = jmhResourcesDirectory
        outputSourcesDir = jmhSourcesDir
        javaLauncher.set(project.javaLauncherProvider())
    }
    val runtimeClasspath = compilation.output.allOutputs + compilation.runtimeDependencyFiles
    val jmhCompilationTask = project.tasks.register<JavaCompile>("compileJmhFor${patchName.capitalize()}") {
        description = "Compile JMH patch '$patchName' source files for target '${compilation.target.targetName}' compilation '${compilation.compilationName}'"
        dependsOn(jmhGeneratorTask)
        classpath = runtimeClasspath
        source = project.fileTree(jmhSourcesDir)
        destinationDirectory.set(jmhClassesDirectory)
        javaCompiler.set(project.javaCompilerProvider())
    }
    return JmhPatch(
        jmhCompilationTask = jmhCompilationTask,
        jmhClassesDirectory = jmhClassesDirectory,
        jmhResourcesDirectory = jmhResourcesDirectory,
    )
}

public fun JavaExec.applyJmhPatch(patch: JmhPatch) {
    dependsOn(patch.jmhCompilationTask)
    classpath(patch.jmhResourcesDirectory, patch.jmhClassesDirectory)
}