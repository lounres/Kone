/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport.gradleWrapper

import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaCompiler
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.argument
import org.gradle.kotlin.dsl.koneBenchmarksRunners
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.compiler.plugin.devkit.DevKitSupportPlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBinaryMode
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import kotlin.io.path.absolutePathString
import kotlin.io.path.createFile
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists


class KoneCompilerPluginGradleWrapper : DevKitSupportPlugin(KonePluginInfo.PLUGIN_INFO) {
    override fun apply(target: Project) {
        target.pluginManager.withPlugin("dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper") {
            val extension = BenchmarksExtension(target)
            target.extensions.add("benchmark", extension)
            
            target.afterEvaluate {
                extension.finalize()
            }
        }
    }
    
    override fun Project.applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        kotlinCompilation.compileTaskProvider.configure {
            compilerOptions.freeCompilerArgs.add(
                "-Xcompiler-plugin-order=dev.lounres.kone.plugins.benchmarksRunnersKotlinxBenchmarkSupport>dev.lounres.kone.plugins.benchmarksRunners"
            )
        }
        return provider { emptyList() }
    }
}

internal fun <T> Any.tryGetClass(className: String): Class<T>? {
    val classLoader = javaClass.classLoader
    return try {
        @Suppress("UNCHECKED_CAST")
        Class.forName(className, false, classLoader) as Class<T>
    } catch (e: ClassNotFoundException) {
        null
    }
}

class BenchmarksExtension(
    val project: Project
) {
    private var isFinalized = false
    
    var buildDir: String = "benchmarks"
        set(value) {
            require(!isFinalized) { "BenchmarksExtension is finalized. Cannot change 'buildDir'." }
            field = value
        }
    var reportsDir: String = "reports/benchmarks"
        set(value) {
            require(!isFinalized) { "BenchmarksExtension is finalized. Cannot change 'reportsDir'." }
            field = value
        }
    
    fun configurations(configureClosure: Closure<NamedDomainObjectContainer<BenchmarkConfiguration>>): NamedDomainObjectContainer<BenchmarkConfiguration> =
        configurations.configure(configureClosure)
    
    val configurations: NamedDomainObjectContainer<BenchmarkConfiguration> =
        project.objects.domainObjectContainer(BenchmarkConfiguration::class.java) { name ->
            BenchmarkConfiguration(this, name)
        }
    
    fun targets(configureClosure: Closure<NamedDomainObjectContainer<BenchmarkTarget>>): NamedDomainObjectContainer<BenchmarkTarget> =
        targets.configure(configureClosure)
    
    val targets: NamedDomainObjectContainer<BenchmarkTarget> =
        project.objects.domainObjectContainer(BenchmarkTarget::class.java) { name ->
            val multiplatformClass = tryGetClass<KotlinMultiplatformExtension>("org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension")
            val multiplatform = multiplatformClass?.let { project.extensions.findByType(it) }
            val javaExtension = project.extensions.findByType(JavaPluginExtension::class.java)
            
            when {
                multiplatform != null -> {
                    val target = multiplatform.targets.findByName(name)
                    when (
                        val compilation = target?.compilations?.findByName(KotlinCompilation.MAIN_COMPILATION_NAME)
                            ?: multiplatform.targets.flatMap { it.compilations }.find { it.defaultSourceSet.name == name }
                    ) {
                        null -> {
                            project.logger.warn("Warning: Cannot find a benchmark compilation '$name', ignoring.")
                            BenchmarkTarget(this, name) // ignore
                        }
                        
                        is KotlinJvmCompilation -> {
                            KotlinJvmBenchmarkTarget(this, name, compilation)
                        }
                        
                        is KotlinJsCompilation -> {
                            check(compilation is KotlinJsIrCompilation) {
                                "Legacy Kotlin/JS backend is not supported. Please migrate to the Kotlin/JS IR compiler backend."
                            }
                            if (compilation.target.platformType != KotlinPlatformType.wasm) {
                                JsBenchmarkTarget(this, name, compilation)
                            } else {
                                WasmBenchmarkTarget(this, name, compilation)
                            }
                        }
                        
                        is KotlinNativeCompilation -> {
                            NativeBenchmarkTarget(this, name, compilation)
                        }
                        
                        else -> {
                            project.logger.warn("Warning: Unsupported compilation '$compilation', ignoring.")
                            BenchmarkTarget(this, name) // ignore
                        }
                    }
                    
                }
                
                javaExtension != null -> {
                    when (val sourceSet = javaExtension.sourceSets.findByName(name)) {
                        null -> {
                            project.logger.warn("Warning: Cannot find a benchmark sourceSet '$name', ignoring.")
                            BenchmarkTarget(this, name) // ignore
                        }
                        
                        else -> {
                            JavaBenchmarkTarget(this, name, sourceSet)
                        }
                    }
                }
                
                else -> {
                    project.logger.warn("Warning: No Java or Kotlin Multiplatform plugin found, ignoring.")
                    BenchmarkTarget(this, name) // ignore
                }
            }
        }
    
    internal fun finalize() {
        @Suppress("UnstableApiUsage")
        if (!isFinalized) {
            isFinalized = true
            targets.disallowChanges()
            configurations.disallowChanges()
            for (target in targets) when (target) {
                is JavaBenchmarkTarget -> {}
                is KotlinJvmBenchmarkTarget -> { // TODO: Refactor this mess.
                    fun String.capitalize(): String = replaceFirstChar { it.uppercaseChar() }
                    fun Project.javaLauncherProvider(): Provider<JavaLauncher> = provider {
                        val toolchainService = extensions.findByType(JavaToolchainService::class.java) ?: return@provider null
                        val javaExtension = extensions.findByType(JavaPluginExtension::class.java) ?: return@provider null
                        toolchainService.launcherFor(javaExtension.toolchain).orNull
                    }
                    fun Project.javaCompilerProvider(): Provider<JavaCompiler> = provider {
                        val toolchainService = extensions.findByType(JavaToolchainService::class.java) ?: return@provider null
                        val javaExtension = extensions.findByType(JavaPluginExtension::class.java) ?: return@provider null
                        toolchainService.compilerFor(javaExtension.toolchain).orNull
                    }
                    val jmhWorkerConfiguration = project.configurations.create("jmhGenerationClasspathFor${target.compilation.target.targetName.capitalize()}${target.compilation.compilationName.capitalize()}${target.name.capitalize()}").apply {
                        description = "JMH Generator classpath for target '${target.compilation.target.targetName}' compilation '${target.compilation.compilationName}' benchmark target '${target.name}'"
                        
                        val dependencies = project.dependencies
                        defaultDependencies {
                            add(dependencies.create("org.openjdk.jmh:jmh-generator-bytecode:1.37")) // TODO: Replace with settings
                        }
                    }
                    val jmhBuildDir = project.layout.buildDirectory.dir("${target.extension.buildDir}/${target.name}").get().asFile
                    val jmhSourcesDir = project.file("$jmhBuildDir/sources")
                    val jmhResourcesDir = project.file("$jmhBuildDir/resources")
                    val jmhClasses = project.file("$jmhBuildDir/classes")
                    val jmhGeneratorTask = project.tasks.register<JmhGeneratorTask>("generateJmhFor${target.compilation.target.targetName.capitalize()}${target.compilation.compilationName.capitalize()}${target.name.capitalize()}") {
                        description = "Generate JMH for target '${target.compilation.target.targetName}' compilation '${target.compilation.compilationName}' benchmark target '${target.name}'"
                        dependsOn(target.compilation.compileAllTaskName)
                        runtimeClasspath = jmhWorkerConfiguration
                        inputCompileClasspath = target.compilation.compileDependencyFiles
                        inputClassesDirs = target.compilation.output.allOutputs
                        outputResourcesDir = jmhResourcesDir
                        outputSourcesDir = jmhSourcesDir
                        javaLauncher.set(project.javaLauncherProvider())
                    }
                    val runtimeClasspath = target.compilation.output.allOutputs + target.compilation.runtimeDependencyFiles
                    val jmhCompilationTask = project.tasks.register<JavaCompile>("compileJmhFor${target.compilation.target.targetName.capitalize()}${target.compilation.compilationName.capitalize()}${target.name.capitalize()}") {
                        description = "Compile JMH source files for target '${target.compilation.target.targetName}' compilation '${target.compilation.compilationName}' benchmark target '${target.name}'"
                        dependsOn(jmhGeneratorTask)
                        classpath = runtimeClasspath
                        source = project.fileTree(jmhSourcesDir)
                        destinationDirectory.set(jmhClasses)
                        javaCompiler.set(project.javaCompilerProvider())
                    }
                    target.compilation.koneBenchmarksRunners {
                        for (configuration in configurations) {
                            create(configuration.name) {
                                runnerName.set("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated.runKotlinxBenchmarkSupport")
                                argument<String>("name", target.name)
                                val reportFile = project.layout.buildDirectory.get().asFile.toPath().resolve(reportsDir).resolve(target.name).resolve(configuration.name)
                                argument<String>("reportFile", reportFile.absolutePathString())
                                argument<String>("traceFormat", if (project.providers.systemProperty("idea.active").orNull.toBoolean()) "xml" else "text")
                                argument<String?>("reportFormat", configuration.reportFormat)
                                argument<Map<String, List<String>>>("params", configuration.params.mapValues { it.value.map { it.toString() } })
                                argument<List<String>>("include", configuration.includes)
                                argument<List<String>>("exclude", configuration.excludes)
                                argument<Int?>("iterations", configuration.iterations)
                                argument<Int?>("warmups", configuration.warmups)
                                argument<Long?>("iterationTime", configuration.iterationTime)
                                argument<String?>("iterationTimeUnit", configuration.iterationTimeUnit)
                                argument<Map<String, String>>("advanced", configuration.advanced.mapValues { it.value.toString() })
                                argument<String?>("outputTimeUnit", configuration.outputTimeUnit)
                                argument<Int?>("threads", configuration.threads)
                                argument<String?>("mode", configuration.mode)
                                argument<String?>("compilationMode", null)
                                argument<String>("configurationName", configuration.name)
                                finalize()
                                configureRunnersTasks {
                                    dependsOn(jmhCompilationTask)
                                    classpath(jmhResourcesDir, jmhClasses)
                                    
                                    doFirst {
                                        if (!reportFile.exists()) {
                                            reportFile.createParentDirectories()
                                            reportFile.createFile()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is JsBenchmarkTarget -> {
                    target.compilation.koneBenchmarksRunners {
                        for (configuration in configurations) {
                            create(configuration.name) {
                                runnerName.set("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated.runKotlinxBenchmarkSupport")
                                argument<String>("name", target.name)
                                val reportFile = project.layout.buildDirectory.get().asFile.toPath().resolve(reportsDir).resolve(target.name).resolve(configuration.name)
                                argument<String>("reportFile", reportFile.absolutePathString())
                                argument<String>("traceFormat", if (project.providers.systemProperty("idea.active").orNull.toBoolean()) "xml" else "text")
                                argument<String?>("reportFormat", configuration.reportFormat)
                                argument<Map<String, List<String>>>("params", configuration.params.mapValues { it.value.map { it.toString() } })
                                argument<List<String>>("include", configuration.includes)
                                argument<List<String>>("exclude", configuration.excludes)
                                argument<Int?>("iterations", configuration.iterations)
                                argument<Int?>("warmups", configuration.warmups)
                                argument<Long?>("iterationTime", configuration.iterationTime)
                                argument<String?>("iterationTimeUnit", configuration.iterationTimeUnit)
                                argument<Map<String, String>>("advanced", configuration.advanced.mapValues { it.value.toString() })
                                argument<String?>("outputTimeUnit", configuration.outputTimeUnit)
                                argument<Int?>("threads", configuration.threads)
                                argument<String?>("mode", configuration.mode)
                                argument<String?>("compilationMode", null)
                                argument<String>("configurationName", configuration.name)
                                finalize()
                                configureRunnersTasks {
                                    doFirst {
                                        if (!reportFile.exists()) {
                                            reportFile.createParentDirectories()
                                            reportFile.createFile()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is WasmBenchmarkTarget -> {
                    target.compilation.koneBenchmarksRunners {
                        for (configuration in configurations) {
                            create(configuration.name) {
                                runnerName.set("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated.runKotlinxBenchmarkSupport")
                                argument<String>("name", target.name)
                                val reportFile = project.layout.buildDirectory.get().asFile.toPath().resolve(reportsDir).resolve(target.name).resolve(configuration.name)
                                argument<String>("reportFile", reportFile.absolutePathString())
                                argument<String>("traceFormat", if (project.providers.systemProperty("idea.active").orNull.toBoolean()) "xml" else "text")
                                argument<String?>("reportFormat", configuration.reportFormat)
                                argument<Map<String, List<String>>>("params", configuration.params.mapValues { it.value.map { it.toString() } })
                                argument<List<String>>("include", configuration.includes)
                                argument<List<String>>("exclude", configuration.excludes)
                                argument<Int?>("iterations", configuration.iterations)
                                argument<Int?>("warmups", configuration.warmups)
                                argument<Long?>("iterationTime", configuration.iterationTime)
                                argument<String?>("iterationTimeUnit", configuration.iterationTimeUnit)
                                argument<Map<String, String>>("advanced", configuration.advanced.mapValues { it.value.toString() })
                                argument<String?>("outputTimeUnit", configuration.outputTimeUnit)
                                argument<Int?>("threads", configuration.threads)
                                argument<String?>("mode", configuration.mode)
                                argument<String?>("compilationMode", null)
                                argument<String>("configurationName", configuration.name)
                                finalize()
                                configureRunnersTasks {
                                    doFirst {
                                        if (!reportFile.exists()) {
                                            reportFile.createParentDirectories()
                                            reportFile.createFile()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                is NativeBenchmarkTarget -> {
                    target.compilation.koneBenchmarksRunners {
                        for (configuration in configurations) {
                            create(configuration.name) {
                                runnerName.set("dev.lounres.kone.misc.benchmarks.runners.support.kotlinxBenchmark.generated.runKotlinxBenchmarkSupport")
                                argument<String>("name", target.name)
                                val reportFile = project.layout.buildDirectory.get().asFile.toPath().resolve(reportsDir).resolve(target.name).resolve(configuration.name)
                                argument<String>("reportFile", reportFile.absolutePathString())
                                argument<String>("traceFormat", if (project.providers.systemProperty("idea.active").orNull.toBoolean()) "xml" else "text")
                                argument<String?>("reportFormat", configuration.reportFormat)
                                argument<Map<String, List<String>>>("params", configuration.params.mapValues { it.value.map { it.toString() } })
                                argument<List<String>>("include", configuration.includes)
                                argument<List<String>>("exclude", configuration.excludes)
                                argument<Int?>("iterations", configuration.iterations)
                                argument<Int?>("warmups", configuration.warmups)
                                argument<Long?>("iterationTime", configuration.iterationTime)
                                argument<String?>("iterationTimeUnit", configuration.iterationTimeUnit)
                                argument<Map<String, String>>("advanced", configuration.advanced.mapValues { it.value.toString() })
                                argument<String?>("outputTimeUnit", configuration.outputTimeUnit)
                                argument<Int?>("threads", configuration.threads)
                                argument<String?>("mode", configuration.mode)
                                argument<String?>("compilationMode", null)
                                argument<String>("configurationName", configuration.name)
                                finalize()
                                configureRunnersTasks {
                                    doFirst {
                                        if (!reportFile.exists()) {
                                            reportFile.createParentDirectories()
                                            reportFile.createFile()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

open class BenchmarkConfiguration
internal constructor(
    internal val extension: BenchmarksExtension,
    val name: String,
) {
    var iterations: Int? = null
    var warmups: Int? = null
    var iterationTime: Long? = null
    var iterationTimeUnit: String? = null
    var mode: String? = null
    var outputTimeUnit: String? = null
    var reportFormat: String? = null
    var threads: Int? = null
    
    var includes: MutableList<String> = mutableListOf()
    var excludes: MutableList<String> = mutableListOf()
    var params: MutableMap<String, MutableList<Any?>> = mutableMapOf()
    var advanced: MutableMap<String, Any?> = mutableMapOf()
    
//    @KotlinxBenchmarkPluginExperimentalApi
//    public var customEngine: CustomEngine? = null
    
    fun include(pattern: String) {
        includes.add(pattern)
    }
    
    fun exclude(pattern: String) {
        excludes.add(pattern)
    }
    
    fun param(name: String, vararg value: Any?) {
        val values = params.getOrPut(name) { mutableListOf() }
        values.addAll(value)
    }
    
    fun advanced(name: String, value: Any?) {
        advanced[name] = value
    }
    
    internal fun capitalizedName() = if (name == "main") "" else name.replaceFirstChar { it.titlecase() }
    
    internal fun prefixName(suffix: String) = if (name == "main") suffix else name + suffix.replaceFirstChar { it.titlecase() }
    
    internal fun reportFileExt(): String = reportFormat?.lowercase() ?: "json"
}

open class BenchmarkTarget
internal constructor(
    internal val extension: BenchmarksExtension,
    val name: String,
) {
    var workingDir: String? = null
}

abstract class JvmBenchmarkTarget
internal constructor(
    extension: BenchmarksExtension,
    name: String
) : BenchmarkTarget(extension, name) {
    var jmhVersion: String = extension.project.providers.gradleProperty("benchmarks_jmh_version").orElse("1.37").get()
}

class JavaBenchmarkTarget
internal constructor(
    extension: BenchmarksExtension,
    name: String,
    internal val sourceSet: SourceSet
) : JvmBenchmarkTarget(extension, name)

open class KotlinJvmBenchmarkTarget
internal constructor(
    extension: BenchmarksExtension,
    name: String,
    internal val compilation: KotlinJvmCompilation
) : JvmBenchmarkTarget(extension, name)

enum class JsBenchmarksExecutor {
    BenchmarkJs,
    BuiltIn
}

class JsBenchmarkTarget
internal constructor(
    extension: BenchmarksExtension,
    name: String,
    internal val compilation: KotlinJsIrCompilation
) : BenchmarkTarget(extension, name) {
    var jsBenchmarksExecutor: JsBenchmarksExecutor = JsBenchmarksExecutor.BenchmarkJs
    var buildType: KotlinJsBinaryMode = KotlinJsBinaryMode.PRODUCTION
}

class WasmBenchmarkTarget
internal constructor(
    extension: BenchmarksExtension,
    name: String,
    internal val compilation: KotlinJsIrCompilation
) : BenchmarkTarget(extension, name) {
    var buildType: KotlinJsBinaryMode = KotlinJsBinaryMode.PRODUCTION
}

class NativeBenchmarkTarget
internal constructor(
    extension: BenchmarksExtension,
    name: String,
    internal val compilation: KotlinNativeCompilation
) : BenchmarkTarget(extension, name) {
    var buildType: NativeBuildType = NativeBuildType.RELEASE
}