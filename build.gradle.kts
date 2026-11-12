@file:Suppress("SuspiciousCollectionReassignment")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class, KotlinxBenchmarkPluginInternalApi::class)

//import org.gradle.accessors.dm.LibrariesForLibs
import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.android.build.api.withAndroid
import com.vanniktech.maven.publish.*
import dev.lounres.kone.buildSrc.dsl.apply
import dev.lounres.kone.buildSrc.dsl.withEach
import dev.lounres.kone.buildSrc.dsl.withPlugin
import dev.lounres.kone.buildSrc.dsl.withPlugins
import dev.lounres.kone.buildSrc.koneCompilerOptions
import dev.lounres.kone.buildSrc.koneOptIns
import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.KotlinJvmBenchmarkTarget
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import org.gradle.accessors.dm.LibrariesForVersions
import org.gradle.accessors.dm.RootProjectAccessor
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.compiler.plugin.devkit.*
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import java.io.ByteArrayOutputStream


plugins {
    `kotlin-dsl` apply false
//    alias(versions.plugins.kotlin.multiplatform) apply false
    alias(versions.plugins.android.library) apply false
    pluginDevKit("compiler-plugin") apply false
    alias(versions.plugins.kotlinx.atomicfu) apply false
    alias(versions.plugins.kotlin.compose) apply false
    alias(versions.plugins.compose.multiplatform) apply false
    alias(versions.plugins.kotlin.allopen) apply false
//    alias(versions.plugins.kotlinx.benchmark) apply false
    alias(versions.plugins.testBalloon) apply false
    alias(versions.plugins.kotlinx.kover) apply false
    alias(versions.plugins.grgit)
    alias(versions.plugins.dokka)
    `version-catalog`
    alias(versions.plugins.gradle.maven.publish.plugin)
}

buildscript {
    dependencies {
        classpath(versions.sshj)
    }
}


val koneBranch: String by lazy { grgit.branch.current().name }
val koneVersion = project.extra["koneVersion"] as String
val koneGroup = project.extra["koneGroup"] as String
val koneUrl = project.property("koneUrl") as String
val koneBaseUrl = project.property("koneBaseUrl") as String

val docusaurusGenerateInputData = tasks.register("docusaurusGenerateInputData") {
    group = "site"
    outputs.files("site/inputData.ts")
    doLast {
        val inputDataContent =
            """
                export const koneBranch = "$koneBranch"
                export const koneGroup = "$koneGroup"
                export const koneVersion = "$koneVersion"
                export const koneUrl = "$koneUrl"
                export const koneBaseUrl = "$koneBaseUrl"
            """.trimIndent()
        rootDir.resolve("site/inputData.ts").writer().use { it.write(inputDataContent) }
    }
}

tasks.register("docusaurusGenerateDevInputData") {
    group = "site"
    outputs.files("site/inputData.ts")
    doLast {
        val inputDataContent =
            """
                export const koneBranch = "$koneBranch"
                export const koneGroup = "$koneGroup"
                export const koneVersion = "$koneVersion"
                export const koneUrl = "http://localhost:3000"
                export const koneBaseUrl = "$koneBaseUrl"
            """.trimIndent()
        rootDir.resolve("site/inputData.ts").writer().use { it.write(inputDataContent) }
    }
}

val buildSite = tasks.register<Exec>("buildSite") {
    group = "site"
    description = "Build docs site"
    
    dependsOn(docusaurusGenerateInputData)

    workingDir = rootDir.resolve("site")
    
    standardOutput = ByteArrayOutputStream()
    errorOutput = ByteArrayOutputStream()
    
    commandLine("deno", "run", "build")
}

tasks.register("publishApiToProduction") {
    group = "publishing"
    description = "Publish the API reference to production server"
    
    val docsProject = project(":docs")
    val dokkaGeneratePublicationHtml = docsProject.tasks.getByName("dokkaGeneratePublicationHtml")
    
    dependsOn(dokkaGeneratePublicationHtml)
    
    doLast {
        val hostname = project.property("kone.publishing.hostname") as String
        val port = (project.property("kone.publishing.ssh.port") as String).toInt()
        val username = project.property("kone.publishing.ssh.username") as String
        val password = project.property("kone.publishing.ssh.password") as String
        val destination = project.property("kone.publishing.destination.api") as String
        
        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        ssh.use {
            ssh.connect(hostname, port)
            ssh.authPassword(username, password)
            ssh.use {
                val session = ssh.startSession()
                val command = session.exec("rm -rf $destination/*")
                println(command.inputStream.bufferedReader().use { it.readText() })
                command.join()
                val scpFileTransfer = ssh.newSCPFileTransfer()
                val sources = dokkaGeneratePublicationHtml.outputs.files
                val directory = docsProject.layout.buildDirectory.asFile.get().resolve("dokka/html")
                check(directory in sources.files) { "Irrelevant API HTML directory" }
                directory.listFiles()!!.forEach { file ->
                    scpFileTransfer.upload(file.absolutePath, destination)
                }
            }
        }
    }
}

tasks.register("publishSiteToProduction") {
    group = "publishing"
    description = "Publish the docs site to production server"
    
    dependsOn(buildSite)
    
    doLast {
        val hostname = project.property("kone.publishing.hostname") as String
        val port = (project.property("kone.publishing.ssh.port") as String).toInt()
        val username = project.property("kone.publishing.ssh.username") as String
        val password = project.property("kone.publishing.ssh.password") as String
        val destination = project.property("kone.publishing.destination.site") as String
        
        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        ssh.use {
            ssh.connect(hostname, port)
            ssh.authPassword(username, password)
            ssh.use {
                val session = ssh.startSession()
                val command = session.exec("rm -rf $destination/*")
                println(command.inputStream.bufferedReader().use { it.readText() })
                command.join()
                val scpFileTransfer = ssh.newSCPFileTransfer()
                val directory = rootDir.resolve("site/build")
                directory.listFiles()!!.forEach { file ->
                    scpFileTransfer.upload(file.absolutePath, destination)
                }
            }
        }
    }
}


val ignoreManualBugFixes = (property("ignoreManualBugFixes") as String) == "true"

val Project.versions: LibrariesForVersions get() = rootProject.extensions.getByName<LibrariesForVersions>("versions")
//val Project.libs: LibrariesForLibs get() = rootProject.extensions.getByName<LibrariesForLibs>("libs")
val Project.projects: RootProjectAccessor get() = rootProject.extensions.getByName<RootProjectAccessor>("projects")

val Project.artifact: String get() = extra["artifactId"] as String
val Project.alias: String get() = extra["alias"] as String

catalog.versionCatalog {
    version("kone", koneVersion)
}

gradle.projectsEvaluated {
    val bundleMainProjects = stal.lookUp.projectsThat { has("libs main") }
    val bundleMiscProjects = stal.lookUp.projectsThat { has("libs misc") }
    val bundleUtilProjects = stal.lookUp.projectsThat { has("libs util") }
    val pluginProjects = stal.lookUp.projectsThat { has("kotlin compiler plugin gradle wrapper") }
    val bundleMainAliases = bundleMainProjects.map { it.alias }
    val bundleMiscAliases = bundleMiscProjects.map { it.alias }
    val bundleUtilAliases = bundleUtilProjects.map { it.alias }
    catalog.versionCatalog {
        for (project in bundleMainProjects + bundleMiscProjects + bundleUtilProjects)
            library(project.alias, koneGroup, project.artifact).versionRef("kone")
        for (project in pluginProjects)
            plugin(project.alias, "$koneGroup.${project.artifact}").versionRef("kone")

        bundle("main", bundleMainAliases)
        bundle("misc", bundleMiscAliases)
        bundle("util", bundleUtilAliases)
        bundle("public", bundleMainAliases + bundleMiscAliases)
        bundle("all", bundleMainAliases + bundleMiscAliases + bundleUtilAliases)
    }
    
//    val libsProjects = stal.lookUp.projectsThat { has("libs") }
//    project(":bom") {
////        dependencies {
////            constraints {
////                libsProjects.forEach { add("api", it) }
////            }
////        }
//        configure<KotlinMultiplatformExtension> {
//            sourceSets {
//                commonMain {
//                    dependencies {
//                        libsProjects.forEach { api(it) }
//                    }
//                }
//            }
//        }
//    }
}

stal {
    action {
        "gradle plugin" {
            apply(plugin = "java-gradle-plugin")
            apply(plugin = "org.gradle.kotlin.kotlin-dsl")
            
            configure<GradlePluginDevelopmentExtension> {
                website = rootProject.property("koneGradlePluginsWebsite") as String
                vcsUrl = rootProject.property("koneGradlePluginsVcsUrl") as String
            }
        }
        "kotlin jvm" {
            apply(versions.plugins.kotlin.jvm)
            configure<KotlinJvmProjectExtension> {
                jvmToolchain {
                    languageVersion = JavaLanguageVersion.of(project.extra["jvmTargetVersion"] as Int)
                    vendor = JvmVendorSpec.of(project.extra["jvmVendor"] as String)
                }
                
                compilerOptions {
                    progressiveMode = true
                    freeCompilerArgs.addAll(koneCompilerOptions)
                    optIn.addAll(koneOptIns)
                    verbose = true
                }
            }
            
            tasks.withType<Test> {
                useJUnitPlatform()
                maxHeapSize = "4g"
            }
        }
        "kotlin multiplatform" {
            apply(versions.plugins.kotlin.multiplatform)
            configure<KotlinMultiplatformExtension> {
                jvmToolchain {
                    languageVersion = JavaLanguageVersion.of(project.extra["jvmTargetVersion"] as Int)
                    vendor = JvmVendorSpec.of(project.extra["jvmVendor"] as String)
                }
                
                compilerOptions {
                    progressiveMode = true
                    freeCompilerArgs.addAll(koneCompilerOptions)
                    optIn.addAll(koneOptIns)
                    verbose = true
                }
                
                applyDefaultHierarchyTemplate()

                jvm {
                    testRuns.all {
                        executionTask {
                            useJUnitPlatform()
                        }
                    }
                }

                js {
                    browser()
                    nodejs()
                }

                wasmJs {
                    browser()
                    nodejs()
                    d8()
                }
                
//                @OptIn(ExperimentalWasmDsl::class)
//                wasmWasi {
//                    wasmtime()
//                }

                linuxX64()
                mingwX64()
//                macosX64()

//                iosX64()
//                iosArm64()
//                iosSimulatorArm64()
//                macosArm64()
            }
            
            tasks.withType<Test> {
                useJUnitPlatform()
                maxHeapSize = "4g"
            }
        }
        "kotlin android" {
            apply(versions.plugins.android.library)
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    configure<KotlinMultiplatformAndroidLibraryTarget> {
                        namespace = project.extra["androidNamespace"] as String
                        compileSdk {
                            version = release((rootProject.extra["android.compileSdk"] as String).toInt()) {
                                minorApiLevel = (rootProject.extra["android.compileSdkMinor"] as String).toInt()
                            }
                        }
                        minSdk = (rootProject.extra["android.minSdk"] as String).toInt()

                        withHostTestBuilder { }.configure { }
                        withDeviceTestBuilder {
                            sourceSetTreeName = "test"
                        }
                    }
                }
            }
        }
        "kotlin library settings" {
            pluginManager.withPlugins(versions.plugins.kotlin.jvm, versions.plugins.kotlin.multiplatform) {
                configure<KotlinProjectExtension> {
                    explicitApi = Warning
                }
            }
        }
        "kotlin compiler library" {
            apply<DevKitCompilerLibrary>()
        }
        "kotlin compiler plugin" {
            apply<DevKitCompilerPlugin>()

            configure<DevKitCompilerConfigExtension> {
                pluginPackage = "${extra["koneGroup"]}.${extra["artifactId"]}"
                
                ideaSinceBuild = IdeBuildNumber("263")
                
                componentRegistrar = "${extra["koneGroup"]}.${extra["artifactId"]}.CompilerPluginRegistrar"
                commandLineProcessor = "${extra["koneGroup"]}.${extra["artifactId"]}.CommandLineProcessor"

                companionLibrary(childProjects["runtime"]!!)

                generateTestsClass = "${extra["koneGroup"]}.${extra["artifactId"]}.GenerateTestsKt"
            }

            configure<KotlinMultiplatformExtension> {
                jvmToolchain {
                    languageVersion = JavaLanguageVersion.of(project.extra["jvmTargetVersion"] as Int)
                    vendor = JvmVendorSpec.of(project.extra["jvmVendor"] as String)
                }
                
                compilerOptions {
                    progressiveMode = true
                    freeCompilerArgs.addAll(koneCompilerOptions)
                    optIn.addAll(koneOptIns)
                    verbose = true
                }

                sourceSets {
                    commonMain {
                        dependencies {
                            implementation(projects.plugins.util)
                        }
                    }
                    named("commonTestFixtures") {
                        dependencies {
                            implementation(projects.plugins.testUtil)
                        }
                    }
                }
            }
            
            tasks.withType<Test> {
                useJUnitPlatform()
                maxHeapSize = "4g"
            }
        }
        "kotlin compiler plugin gradle wrapper" {
            apply<DevKitGradlePlugin>()
            
            configure<GradlePluginConfigExtension> {
                compilerPlugin(project.parent!!)
                val runtimeProject = project.parent!!.childProjects["runtime"]!!
                companionLibrary(runtimeProject, group = runtimeProject.extra["koneGroup"], version = runtimeProject.extra["koneVersion"])
            }
            
            configure<GradlePluginDevelopmentExtension> {
                plugins {
                    register("kotlinCompilerPluginGradleWrapper") {
                        id = "${extra["koneGroup"]}.${extra["artifactId"]}"
//                        displayName = ""
//                        description = ""
//                        tags = listOf()
                        implementationClass = "${extra["koneGroup"]}.${extra["artifactId"]}.KoneCompilerPluginGradleWrapper"
                    }
                }
            }
        }
        "atomicfu" {
            apply(versions.plugins.kotlinx.atomicfu)
            configure<AtomicFUPluginExtension> {
                transformJvm = true
                jvmVariant = "VH"
            }
        }
        "compose" {
            apply(versions.plugins.kotlin.compose)
            apply(versions.plugins.compose.multiplatform)
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    applyDefaultHierarchyTemplate {
                        sourceSetTrees(KotlinSourceSetTree.main, KotlinSourceSetTree.test)
                        common {
                            group("compose") {
                                withJvm()
                                group("web")
                                @Suppress("UnstableApiUsage")
                                withAndroid()
                            }
                        }
                    }
                }
            }
        }
        "testBalloon" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                apply(versions.plugins.testBalloon)
                configure<KotlinJvmProjectExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        getByName("test") {
                            dependencies {
                                implementation(versions.testBalloon.framework.core)
                            }
                        }
                    }
                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                apply(versions.plugins.testBalloon)
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        commonTest {
                            dependencies {
                                implementation(versions.testBalloon.framework.core)
                            }
                        }
//                        named("androidHostTest") {
//                            dependencies {
//                                implementation(versions.testBaloon.framework.core)
//                                // TODO
//                            }
//                        }
                    }
                }
            }
        }
        "kover" {
            apply(versions.plugins.kotlinx.kover)
        }
        "libs main algorithms" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                logger.error("algorithm source set setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        commonMain {
                            dependencies {
                                val parentProject = project.parent
                                if (parentProject != null) implementation(project(parentProject.path))
                            }
                        }
                        commonTest {
                            dependencies {
                                val parentProject = project.parent
                                if (parentProject != null) {
                                    val assertionsSibling = parentProject.childProjects["assertions"]
                                    if (assertionsSibling != null) {
                                        implementation(project(assertionsSibling.path))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        "libs main assertions" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                logger.error("assertions source set setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        commonMain {
                            dependencies {
                                api(projects.libs.util.assertions)
                                val parentProject = project.parent
                                if (parentProject != null) api(project(parentProject.path))
                            }
                        }
                    }
                }
            }
            val thisProject = project
            project.parent?.run {
                pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                    logger.error("assertions source set setting is not yet implemented for Kotlin/JVM plug-in")
//                    configure<KotlinJvmProjectExtension> {
//                        // ...
//                    }
                }
                pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                    @Suppress("UNUSED_VARIABLE")
                    configure<KotlinMultiplatformExtension> {
                        sourceSets {
                            commonTest {
                                dependencies {
                                    implementation(project(thisProject.path))
                                }
                            }
                        }
                    }
                }
            }
        }
        "libs main" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                logger.error("libs main source set setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        commonTest {
                            dependencies {
                                implementation(projects.libs.util.assertions)
                            }
                        }
                    }
                }
            }
        }
        "libs main benchmarks" {
            apply(versions.plugins.kotlinx.benchmark)
            apply(versions.plugins.kotlin.allopen)
            the<AllOpenExtension>().annotation("org.openjdk.jmh.annotations.State")

            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                logger.error("kotlinx.benchmark plugging in and setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                val benchmarksExtension = the<BenchmarksExtension>()
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    sourceSets.commonMain {
                        dependencies {
                            implementation(versions.kotlinx.benchmark.runtime)

                            val parentProject = project.parent
                            if (parentProject != null) {
                                implementation(project(parentProject.path))
                                val algorithmsSibling = parentProject.childProjects["algorithms"]
                                if (algorithmsSibling != null) {
                                    implementation(project(algorithmsSibling.path))
                                }
                            }
                        }
                    }
                    targets.filter { it.platformType != KotlinPlatformType.common }.withEach {
                        val main = compilations.getByName("main")

                        val benchmarksSourceSetName = main.defaultSourceSet.name

                        // TODO: For now js target causes problems with tasks initialisation
                        //  Looks similar to
                        //  1. https://github.com/Kotlin/kotlinx-benchmark/issues/101
                        //  2. https://github.com/Kotlin/kotlinx-benchmark/issues/93
                        // TODO: For now native targets work unstable
                        //  May be similar to https://github.com/Kotlin/kotlinx-benchmark/issues/94
                        // Because of all the issues, only JVM targets are registered for now
                        when (platformType) {
                            KotlinPlatformType.common, KotlinPlatformType.androidJvm -> {}
                            KotlinPlatformType.jvm -> {
                                val benchmarkTarget = KotlinJvmBenchmarkTarget(
                                    extension = benchmarksExtension,
                                    name = benchmarksSourceSetName,
                                    compilation = main as KotlinJvmCompilation
                                )
                                benchmarksExtension.targets.add(benchmarkTarget)

                                benchmarkTarget.jmhVersion = versions.versions.jmh.get()

                                // Fix kotlinx-benchmarks bug
                                afterEvaluate {
                                    val jarTaskName = "${benchmarksSourceSetName}BenchmarkJar"
                                    tasks.findByName(jarTaskName).let { it as? org.gradle.jvm.tasks.Jar }?.run {
                                        if (!ignoreManualBugFixes) project.logger.warn("Corrected kotlinx.benchmark task $jarTaskName")
                                        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                                    }
                                }
                            }
                            KotlinPlatformType.js -> {
//                                val benchmarkTarget = JsBenchmarkTarget(
//                                    extension = benchmarksExtension,
//                                    name = benchmarksSourceSetName,
//                                    compilation = main as KotlinJsIrCompilation
//                                )
//                                    benchmarksExtension.targets.add(benchmarkTarget)
                            }
                            KotlinPlatformType.wasm -> {
//                                    val benchmarkTarget = JsBenchmarkTarget(
//                                        extension = benchmarksExtension,
//                                        name = benchmarksSourceSetName,
//                                        compilation = benchmarks as KotlinJsCompilation
//                                    )
//                                    benchmarksExtension.targets.add(benchmarkTarget)
                            }
                            KotlinPlatformType.native -> {
//                                val benchmarkTarget = NativeBenchmarkTarget(
//                                    extension = benchmarksExtension,
//                                    name = benchmarksSourceSetName,
//                                    compilation = main as KotlinNativeCompilation
//                                )
//                                    benchmarksExtension.targets.add(benchmarkTarget)
                            }
                        }
                    }
                }
            }

            @Suppress("UNUSED_VARIABLE")
            configure<BenchmarksExtension> {
                configurations {
                    remove(getByName("main"))
                }
            }
        }
        "libs main examples" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                logger.error("examples are not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//
//                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        commonMain {
                            dependencies {
                                implementation(projects.libs.util.examples)

                                val parentProject = project.parent
                                if (parentProject != null) implementation(project(parentProject.path))
                            }
                        }
                    }
                }
            }
        }
        "dokka" {
            val thisProject = this
            val docsProject = project(":docs")
            
            apply(versions.plugins.dokka)
            dependencies {
                dokkaPlugin(versions.dokka.mathjax)
            }
            
            docsProject.afterEvaluate {
                dependencies {
                    dokka(thisProject)
                }
            }
            
            configure<DokkaExtension> {
                moduleName = project.artifact
                
                dokkaSourceSets.all {
//                    reportUndocumented = true
                    
                    sourceLink {
                        val relativePathToSourceRoot = project.projectDir.toRelativeString(rootDir).replace('\\', '/')
                        remoteUrl("https://github.com/lounres/Kone/tree/experiment/$relativePathToSourceRoot")
                    }
                }
                
                pluginsConfiguration.html {
                    customAssets.from(docsProject.projectDir.resolve("images/logo-icon.svg"), docsProject.projectDir.resolve("images/favicon.svg"))
                    footerMessage = "Copyright © 2025 Gleb Minaev<br>All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE"
                    templatesDir = docsProject.projectDir.resolve("templates")
                }
            }
        }
        "kotlin jvm publication" {
            pluginManager.withPlugin(versions.plugins.gradle.maven.publish.plugin) {
                configure<MavenPublishBaseExtension> {
                    configure(
                        KotlinJvm(
                            javadocJar =
                                if (extra["isDokkaConfigured"] == true) JavadocJar.Dokka("dokkaGeneratePublicationHtml")
                                else JavadocJar.Empty(),
                            sourcesJar = SourcesJar.Sources(),
                        )
                    )
                }
            }
        }
        "kotlin multiplatform publication" {
            pluginManager.withPlugin(versions.plugins.gradle.maven.publish.plugin) {
                configure<MavenPublishBaseExtension> {
                    configure(
                        KotlinMultiplatform(
                            javadocJar =
                                if (extra["isDokkaConfigured"] == true) JavadocJar.Dokka("dokkaGeneratePublicationHtml")
                                else JavadocJar.Empty(),
                            sourcesJar = SourcesJar.Sources(),
                        )
                    )
                }
            }
        }
        "gradle plugin publication" {
            pluginManager.withPlugin(versions.plugins.gradle.maven.publish.plugin) {
                configure<MavenPublishBaseExtension> {
                    configure(
                        GradlePlugin(
                            javadocJar =
                                if (extra["isDokkaConfigured"] == true) JavadocJar.Dokka("dokkaGeneratePublicationHtml")
                                else JavadocJar.Empty(),
                            sourcesJar = SourcesJar.Sources(),
                        )
                    )
                }
            }
        }
        "publishing" {
            apply(versions.plugins.gradle.maven.publish.plugin)
            configure<MavenPublishBaseExtension> {
                publishToMavenCentral()
                
                signAllPublications()
                
                coordinates(groupId = koneGroup, artifactId = project.artifact, version = koneVersion)

                pom {
                    name = "Kone library"
                    description = "Set of libraries for experimental mathematics"
                    url = "https://github.com/lounres/Kone"
    
                    licenses {
                        license {
                            name = "Apache License, Version 2.0"
                            url = "https://opensource.org/license/apache-2-0/"
                        }
                    }
                    developers {
                        developer {
                            id = "lounres"
                            name = "Gleb Minaev"
                            email = "minaevgleb@yandex.ru"
                        }
                    }
                    scm {
                        url = "https://github.com/lounres/Kone"
                    }
                }
            }
        }
    }
}