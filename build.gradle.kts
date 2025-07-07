@file:Suppress("SuspiciousCollectionReassignment")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class, KotlinxBenchmarkPluginInternalApi::class)

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.KotlinJvmBenchmarkTarget
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
//import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.accessors.dm.LibrariesForVersions
import org.gradle.accessors.dm.RootProjectAccessor
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.text.replace


plugins {
    alias(versions.plugins.kotlin.multiplatform) apply false
    alias(versions.plugins.kotlinx.atomicfu) apply false
    alias(versions.plugins.kotlin.compose) apply false
    alias(versions.plugins.compose.multiplatform) apply false
    alias(versions.plugins.kotlin.allopen) apply false
    alias(versions.plugins.kotlinx.benchmark) apply false
    alias(versions.plugins.kotest.multiplatform) apply false
    alias(versions.plugins.kotlinx.kover) apply false
    id("org.ajoberstar.grgit") version "5.3.0"
    alias(versions.plugins.dokka)
    `version-catalog`
    alias(versions.plugins.gradle.maven.publish.plugin)
}

buildscript {
    dependencies {
        classpath("com.hierynomus:sshj:0.40.0")
    }
}


val koneBranch: String = grgit.branch.current().name
val now: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
val koneVersion = "0.0.0-experiment-${now.year}.${now.month.value}.${now.dayOfMonth}.${now.hour}"
//val koneVersion = "0.0.0-experiment"
val koneGroup = project.properties["group"] as String
val koneUrl: String by project
val koneBaseUrl: String by project

allprojects {
    version = koneVersion
}

val docusaurusGenerateInputData by tasks.registering {
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

val buildSite by tasks.registering(Exec::class) {
    group = "site"
    description = "Build docs site"
    
    dependsOn(docusaurusGenerateInputData)

    workingDir = rootDir.resolve("site")
    
    standardOutput = ByteArrayOutputStream()
    errorOutput = ByteArrayOutputStream()
    
    commandLine("node", "--run", "build")
}

tasks.register("publishApiToProduction") {
    group = "publishing"
    description = "Publish the API reference to production server"
    
    val docsProject = project(":docs")
    val dokkaGeneratePublicationHtml by docsProject.tasks
    
    dependsOn(dokkaGeneratePublicationHtml)
    
    doLast {
        val hostname = project.properties["kone.publishing.hostname"] as String
        val username = project.properties["kone.publishing.ssh.username"] as String
        val password = project.properties["kone.publishing.ssh.password"] as String
        val destination = project.properties["kone.publishing.destination.api"] as String
        
        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        ssh.use {
            ssh.connect(hostname)
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
        val hostname = project.properties["kone.publishing.hostname"] as String
        val username = project.properties["kone.publishing.ssh.username"] as String
        val password = project.properties["kone.publishing.ssh.password"] as String
        val destination = project.properties["kone.publishing.destination.site"] as String
        
        val ssh = SSHClient()
        ssh.addHostKeyVerifier(PromiscuousVerifier())
        ssh.use {
            ssh.connect(hostname)
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

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://repo.kotlin.link")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        mavenLocal()
    }
}


val ignoreManualBugFixes = (properties["ignoreManualBugFixes"] as String) == "true"

val Project.versions: LibrariesForVersions get() = rootProject.extensions.getByName<LibrariesForVersions>("versions")
//val Project.libs: LibrariesForLibs get() = rootProject.extensions.getByName<LibrariesForLibs>("libs")
val Project.projects: RootProjectAccessor get() = rootProject.extensions.getByName<RootProjectAccessor>("projects")
fun PluginAware.apply(pluginDependency: PluginDependency) = apply(plugin = pluginDependency.pluginId)
fun PluginAware.apply(pluginDependency: Provider<PluginDependency>) = apply(plugin = pluginDependency.get().pluginId)
fun PluginAware.apply(pluginDependency: ProviderConvertible<PluginDependency>) = apply(plugin = pluginDependency.asProvider().get().pluginId)
fun PluginManager.withPlugin(pluginDep: PluginDependency, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDep.pluginId, block)
fun PluginManager.withPlugin(pluginDepProvider: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDepProvider.get().pluginId, block)
fun PluginManager.withPlugins(vararg pluginDeps: PluginDependency, block: AppliedPlugin.() -> Unit) = pluginDeps.forEach { withPlugin(it, block) }
fun PluginManager.withPlugins(vararg pluginDeps: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = pluginDeps.forEach { withPlugin(it, block) }
inline fun <T> Iterable<T>.withEach(action: T.() -> Unit) = forEach { it.action() }

val Project.artifact: String get() = extra["artifactId"] as String
val Project.alias: String get() = extra["alias"] as String

catalog.versionCatalog {
    version("kone", koneVersion)
}

gradle.projectsEvaluated {
    val bundleMainProjects = stal.lookUp.projectsThat { has("libs main") }
    val bundleMiscProjects = stal.lookUp.projectsThat { has("libs misc") }
    val bundleUtilProjects = stal.lookUp.projectsThat { has("libs util") }
    val bundleProjects = bundleMainProjects + bundleMiscProjects + bundleUtilProjects
    val bundleMainAliases = bundleMainProjects.map { it.alias }
    val bundleMiscAliases = bundleMiscProjects.map { it.alias }
    val bundleUtilAliases = bundleUtilProjects.map { it.alias }
    catalog.versionCatalog {
        for (p in bundleProjects)
            library(p.alias, koneGroup, p.artifact).versionRef("kone")

        bundle("main", bundleMainAliases)
        bundle("misc", bundleMiscAliases)
        bundle("util", bundleUtilAliases)
        bundle("public", bundleMainAliases + bundleMiscAliases)
        bundle("all", bundleMainAliases + bundleMiscAliases + bundleUtilAliases)
    }
}

stal {
    action {
        "kotlin jvm" {
            apply(versions.plugins.kotlin.jvm)
            configure<KotlinJvmProjectExtension> {
                compilerOptions {
                    freeCompilerArgs = freeCompilerArgs.get() + listOf(
                        "-Xklib-duplicated-unique-name-strategy=allow-all-with-warning",
                        "-Xexpect-actual-classes",
                        "-Xconsistent-data-class-copy-visibility",
                    )
                }

                @Suppress("UNUSED_VARIABLE")
                sourceSets {
                    val test by getting {
                        dependencies {
                            implementation(kotlin("test"))
                        }
                    }
                }
            }
        }
        "kotlin multiplatform" {
            apply(versions.plugins.kotlin.multiplatform)
            configure<KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()
                
                compilerOptions {
                    freeCompilerArgs = freeCompilerArgs.get() + listOf(
                        "-Xklib-duplicated-unique-name-strategy=allow-all-with-warning",
                        "-Xexpect-actual-classes",
                        "-Xconsistent-data-class-copy-visibility",
                    )
                }

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

                @OptIn(ExperimentalWasmDsl::class)
                wasmJs {
                    browser()
                    nodejs()
                    d8()
                }

//                linuxX64()
//                mingwX64()
//                macosX64()

//                androidTarget()
//                iosX64()
//                iosArm64()
//                iosSimulatorArm64()
//                macosArm64()

                @Suppress("UNUSED_VARIABLE")
                sourceSets {
                    commonTest {
                        dependencies {
                            implementation(kotlin("test"))
                        }
                    }
                }
            }
            afterEvaluate {
                yarn.lockFileDirectory = rootDir.resolve("gradle")
            }
        }
        "kotlin common settings" {
            pluginManager.withPlugins(versions.plugins.kotlin.jvm, versions.plugins.kotlin.multiplatform) {
                configure<KotlinProjectExtension> {
                    jvmToolchain {
                        languageVersion = JavaLanguageVersion.of(project.extra["jvmTargetVersion"] as String)
                        vendor = JvmVendorSpec.matching(project.extra["jvmVendor"] as String)
                    }
                    
                    sourceSets {
                        all {
                            languageSettings {
                                progressiveMode = true
                                enableLanguageFeature("ContextParameters")
                                enableLanguageFeature("ValueClasses")
                                enableLanguageFeature("ContractSyntaxV2")
                                enableLanguageFeature("ExplicitBackingFields")
                                optIn("kotlin.contracts.ExperimentalContracts")
                                optIn("kotlin.ExperimentalStdlibApi")
                                optIn("kotlin.ExperimentalSubclassOptIn")
                                optIn("kotlin.ExperimentalUnsignedTypes")
                                optIn("kotlin.uuid.ExperimentalUuidApi")
                                optIn("kotlin.concurrent.atomics.ExperimentalAtomicApi")
                                optIn("kotlinx.serialization.ExperimentalSerializationApi")
                                optIn("dev.lounres.kone.annotations.UnstableKoneAPI")
                                optIn("dev.lounres.kone.annotations.ExperimentalKoneAPI")
                            }
                        }
                    }
                }
            }
            pluginManager.withPlugin("org.gradle.java") {
                tasks.withType<Test> {
                    useJUnitPlatform()
                }
            }
        }
        "kotlin library settings" {
            configure<KotlinProjectExtension> {
                explicitApi = Warning
            }
        }
        "kotlin compiler plugin" {
            apply(plugin = "org.gradle.java")
            configure<SourceSetContainer> {
                named("test") {
                    java.setSrcDirs(listOf("src/test/java", "build/generated/kotlinCompilerPluginTestGenerator/test"))
                }
            }
            configure<KotlinJvmProjectExtension> {
                sourceSets {
                
                }
            }
        }
        "kotlin compiler plugin test generator" {
        
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
        }
        "kotest" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        all {
                            languageSettings {
                                optIn("io.kotest.common.ExperimentalKotest")
                            }
                        }
                        val test by getting {
                            dependencies {
                                with(versions.kotest) {
                                    implementation(framework.datatest)
                                    implementation(assertions.core)
                                    implementation(property)
                                    implementation(runner.junit5)
                                    implementation(projects.libs.util.kotest)
                                }
                            }
                        }
                    }
                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                apply(versions.plugins.kotest.multiplatform)
                configure<KotlinMultiplatformExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        all {
                            languageSettings {
                                optIn("io.kotest.common.ExperimentalKotest")
                            }
                        }
                        commonTest {
                            dependencies {
                                with(versions.kotest) {
                                    implementation(framework.engine)
                                    implementation(framework.datatest)
                                    implementation(assertions.core)
                                    implementation(property)
                                    implementation(projects.libs.util.kotest)
                                }
                            }
                        }
                        jvmTest {
                            dependencies {
                                implementation(versions.kotest.runner.junit5)
                            }
                        }
                    }
                }
            }
        }
        "kover" {
            apply(versions.plugins.kotlinx.kover)
        }
        "algorithms" {
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
                    }
                }
            }
        }
        "benchmarks" {
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
                        val main by compilations.getting

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
                    // TODO: Create my own configurations
                    val main by getting {
                        mode = "AverageTime"
                        warmups = 20
                        iterations = 10
                        iterationTime = 3
                        iterationTimeUnit = "s"
                    }
                    val smoke by creating {
                        mode = "AverageTime"
                        warmups = 5
                        iterations = 3
                        iterationTime = 500
                        iterationTimeUnit = "ms"
                    }
                }
            }
        }
        "examples" {
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
                                // TODO: Investigate why it creates tasks cycle.
//                                implementation(projects.libs.util.examples)

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
                // DOKKA-3885
                dokkaGeneratorIsolation = ClassLoaderIsolation()
                
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
            pluginManager.withPlugin("com.vanniktech.maven.publish") {
                configure<MavenPublishBaseExtension> {
                    configure(
                        KotlinJvm(
                            javadocJar = JavadocJar.Empty(),
                            sourcesJar = true,
                        )
                    )
                }
            }
        }
        "kotlin multiplatform publication" {
            pluginManager.withPlugin("com.vanniktech.maven.publish") {
                configure<MavenPublishBaseExtension> {
                    configure(
                        KotlinMultiplatform(
                            javadocJar =
                                if (extra["isDokkaConfigured"] == true) JavadocJar.Dokka("dokkaGeneratePublicationHtml")
                                else JavadocJar.Empty(),
                            sourcesJar = true,
                        )
                    )
                }
            }
        }
        "publishing" {
            apply(plugin = "com.vanniktech.maven.publish")
            configure<MavenPublishBaseExtension> {
                publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
                
                signAllPublications()
                
                coordinates(groupId = project.group as String, artifactId = project.artifact, version = project.version as String)

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