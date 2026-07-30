@file:Suppress("SuspiciousCollectionReassignment")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class, KotlinxBenchmarkPluginInternalApi::class)

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import com.vanniktech.maven.publish.GradlePlugin
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SourcesJar
import dev.lounres.kone.buildSrc.dsl.*
import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.KotlinJvmBenchmarkTarget
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
//import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.accessors.dm.LibrariesForVersions
import org.gradle.accessors.dm.RootProjectAccessor
import org.gradle.kotlin.dsl.configure
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
    `kotlin-dsl` apply false
//    alias(versions.plugins.kotlin.multiplatform) apply false
    alias(versions.plugins.android.library) apply false
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
val now: LocalDateTime = LocalDateTime.now(ZoneId.of("UTC"))
// FIXME
val koneVersion = "0.0.0-experiment-${now.year}.${now.month.value}.${now.dayOfMonth}.${now.hour}"
//val koneVersion = "0.0.0-experiment"
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
    
    commandLine("node", "--run", "build")
}

tasks.register("publishApiToProduction") {
    group = "publishing"
    description = "Publish the API reference to production server"
    
    val docsProject = project(":docs")
    val dokkaGeneratePublicationHtml = docsProject.tasks.getByName("dokkaGeneratePublicationHtml")
    
    dependsOn(dokkaGeneratePublicationHtml)
    
    doLast {
        val hostname = project.property("kone.publishing.hostname") as String
        val username = project.property("kone.publishing.ssh.username") as String
        val password = project.property("kone.publishing.ssh.password") as String
        val destination = project.property("kone.publishing.destination.api") as String
        
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
        val hostname = project.property("kone.publishing.hostname") as String
        val username = project.property("kone.publishing.ssh.username") as String
        val password = project.property("kone.publishing.ssh.password") as String
        val destination = project.property("kone.publishing.destination.api") as String
        
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
                compilerOptions {
                    progressiveMode = true
                    freeCompilerArgs.addAll(
//                        "-Xklib-duplicated-unique-name-strategy=allow-all-with-warning",
//                        "-Xvalue-classes",
//                        "-Xcontract-syntax-v2",
                        "-Xexpect-actual-classes",
                        "-Xconsistent-data-class-copy-visibility",
                        "-Xcontext-sensitive-resolution",
                        "-Xreturn-value-checker=full",
                        "-Xlocal-type-aliases",
                        "-Xname-based-destructuring=complete",
                        "-Xcollection-literals",
                        "-Xallow-returns-result-of",
//                        "-Xcompanion-blocks-and-extensions",
                    )
                    optIn.addAll(
                        listOf(
                            "kotlin.experimental.ExperimentalTypeInference",
                            "kotlin.contracts.ExperimentalContracts",
                            "kotlin.ExperimentalStdlibApi",
                            "kotlin.ExperimentalSubclassOptIn",
                            "kotlin.ExperimentalUnsignedTypes",
                            "kotlin.uuid.ExperimentalUuidApi",
                            "kotlin.concurrent.atomics.ExperimentalAtomicApi",
                            "kotlinx.serialization.ExperimentalSerializationApi",
                            "dev.lounres.kone.annotations.UnstableKoneAPI",
                            "dev.lounres.kone.annotations.ExperimentalKoneAPI",
                        )
                    )
                    verbose = true
                }
            }
        }
        "kotlin multiplatform" {
            apply(versions.plugins.kotlin.multiplatform)
            configure<KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()
                
                compilerOptions {
                    progressiveMode = true
                    freeCompilerArgs.addAll(
//                        "-Xklib-duplicated-unique-name-strategy=allow-all-with-warning",
//                        "-Xvalue-classes",
//                        "-Xcontract-syntax-v2",
                        "-Xexpect-actual-classes",
                        "-Xconsistent-data-class-copy-visibility",
                        "-Xcontext-sensitive-resolution",
                        "-Xreturn-value-checker=full",
                        "-Xlocal-type-aliases",
                        "-Xname-based-destructuring=complete",
                        "-Xcollection-literals",
                        "-Xallow-returns-result-of",
//                        "-Xcompanion-blocks-and-extensions",
                    )
                    optIn.set(
                        listOf(
                            "kotlin.experimental.ExperimentalTypeInference",
                            "kotlin.contracts.ExperimentalContracts",
                            "kotlin.ExperimentalStdlibApi",
                            "kotlin.ExperimentalSubclassOptIn",
                            "kotlin.ExperimentalUnsignedTypes",
                            "kotlin.uuid.ExperimentalUuidApi",
                            "kotlin.concurrent.atomics.ExperimentalAtomicApi",
                            "kotlinx.serialization.ExperimentalSerializationApi",
                            "dev.lounres.kone.annotations.UnstableKoneAPI",
                            "dev.lounres.kone.annotations.ExperimentalKoneAPI",
                        )
                    )
                    verbose = true
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

//                iosX64()
//                iosArm64()
//                iosSimulatorArm64()
//                macosArm64()
            }
        }
        "kotlin android" {
            apply(versions.plugins.android.library)
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    configure<KotlinMultiplatformAndroidLibraryTarget> {
                        namespace = project.extra["androidNamespace"] as String
                        compileSdk = (rootProject.extra["android.compileSdk"] as String).toInt()
                        minSdk = (rootProject.extra["android.minSdk"] as String).toInt()

                        withHostTestBuilder { }.configure { }
                        withDeviceTestBuilder {
                            sourceSetTreeName = "test"
                        }
                    }
                }
            }
        }
        "kotlin common settings" {
            pluginManager.withPlugins(versions.plugins.kotlin.jvm, versions.plugins.kotlin.multiplatform) {
                configure<KotlinProjectExtension> {
                    jvmToolchain {
                        languageVersion = JavaLanguageVersion.of(project.extra["jvmTargetVersion"] as String)
                        vendor = JvmVendorSpec.matching(project.extra["jvmVendor"] as String)
                    }
                }
            }
            tasks.withType<Test> {
                useJUnitPlatform()
                maxHeapSize = "4g"
            }
        }
        "kotlin library settings" {
            configure<KotlinProjectExtension> {
                explicitApi = Warning
            }
        }
        val generatedTestsDirectory = "build/generated/kotlinCompilerPluginTestGenerator/test"
        "kotlin compiler plugin" {
            apply(plugin = "org.gradle.java")
            configure<SourceSetContainer> {
                named("test") {
                    java.srcDir(generatedTestsDirectory)
                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    compilerOptions {
                        optIn.add(
                            "org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI"
                        )
                    }
                    sourceSets {
                        named("main") {
                            dependencies {
                                compileOnly(versions.kotlin.compiler)
                                implementation(projects.libs.util.kotlinCompilerUtils)
                            }
                        }
                        named("test") {
                            dependencies {
                                runtimeOnly(versions.kotlin.test)
                                runtimeOnly(versions.kotlin.script.runtime)
                                runtimeOnly(versions.kotlin.annotations.jvm)
                                
                                implementation(versions.kotlin.compiler)
                                implementation(versions.kotlin.reflect)
                                implementation(versions.kotlin.compiler.internal.test.framework)

//                                implementation(project.dependencies.platform(versions.junit.bom))
                                implementation(versions.junit.jupiter)
                                implementation(versions.junit.platform.commons)
                                implementation(versions.junit.platform.launcher)
//                                implementation(versions.junit.platform.runner)
                                implementation(versions.junit.platform.suite.api)
                                
                                implementation(project.childProjects["testGeneration"]!!)
                            }
                        }
                    }
                }
            }
            tasks.named("compileTestKotlin") {
                dependsOn(project.childProjects["testGeneration"]!!.tasks.named("generateTests"))
            }
            tasks.named<Test>("test") {
                useJUnitPlatform()
                
                doFirst {
                    val testRuntimeClasspathFiles = project
                        .configurations
                        .getByName("testRuntimeClasspath")
                        .files
                    
                    fun setLibraryProperty(propName: String, jarName: String) {
                        val path = testRuntimeClasspathFiles
                            .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
                            ?.absolutePath
                            ?: return
                        systemProperty(propName, path)
                    }
                    
                    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
                    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
                    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
                    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
                    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
                    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
                }
            }
        }
        "kotlin compiler plugin test generator" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    sourceSets {
                        named("main") {
                            dependencies {
                                runtimeOnly(versions.kotlin.test)
                                runtimeOnly(versions.kotlin.script.runtime)
                                runtimeOnly(versions.kotlin.annotations.jvm)
                                
                                implementation(versions.kotlin.compiler)
                                implementation(versions.kotlin.reflect)
                                implementation(versions.kotlin.compiler.internal.test.framework)
                                
                                api(project.parent!!)
                                api(projects.libs.util.kotlinCompilerTestUtils)
                            }
                        }
                    }
                }
            }
            
            val testPathsSourceSetDirectory = projectDir.resolve("build/generated/paths/main")
            
            configure<SourceSetContainer> {
                named("main") {
                    java.srcDirs(testPathsSourceSetDirectory)
                }
            }
            
            val compilerPluginRuntimeDependencies = configurations.create("compilerPluginRuntimeDependencies") {
                exclude(group = "org.jetbrains.kotlin")
            }
            
            dependencies {
                compilerPluginRuntimeDependencies(project.parent!!.childProjects["runtime"]!!)
            }
            
            val writePaths = tasks.register("writePaths") {
                dependsOn(compilerPluginRuntimeDependencies)
                doFirst {
                    val testDataPath = project.parent!!.projectDir.resolve("src/test/data").absolutePath.replace("\\", "/")
                    val generatedTestsPath = project.parent!!.projectDir.resolve(generatedTestsDirectory).absolutePath.replace("\\", "/")
                    testPathsSourceSetDirectory.also { it.mkdirs() }.resolve("Paths.kt").writeText(
                        """
                            internal val testDataPath: String = "$testDataPath"
                            internal val generatedTestsPath: String = "$generatedTestsPath"
                            internal val testJvmClasspathRoots: List<String> = listOf(${compilerPluginRuntimeDependencies.resolve().joinToString { "\"${it.absolutePath.replace("\\", "/")}\"" }})
                        """.trimIndent()
                    )
                }
            }
            
            val compileKotlin = tasks.getByName("compileKotlin") {
                dependsOn(writePaths)
            }
            
            tasks.register("generateTests", JavaExec::class) {
                dependsOn(compileKotlin)
                classpath = project.the<SourceSetContainer>().getByName("main").runtimeClasspath
                mainClass.set("dev.lounres.kone.plugin.${project.parent!!.name}.GenerateTestsKt")
            }
        }
        "kotlin compiler plugin gradle wrapper" {
            val constsSourceDirectory = projectDir.resolve("build/generated/konePluginConsts/main")
            
            val writePaths = tasks.register("writePaths") {
                doFirst {
                    val parentProject = project.parent!!
                    val pluginDependency = "$koneGroup:${parentProject.extra["artifactId"] as String}:$koneVersion"
                    val runtimeDependency = "$koneGroup:${parentProject.childProjects["runtime"]!!.extra["artifactId"] as String}:$koneVersion"
                    constsSourceDirectory.also { it.mkdirs() }.resolve("Consts.kt").writeText(
                        """
                            internal val dependencyVersion: String = "$koneVersion"
                            internal val plguinDependencyGroup: String = "$koneGroup"
                            internal val plguinDependencyArtifact: String = "${parentProject.extra["artifactId"] as String}"
                            internal val plguinDependency: String = "$pluginDependency"
                            internal val runtimeDependencyGroup: String = "$koneGroup"
                            internal val runtimeDependencyArtifact: String = "${parentProject.childProjects["runtime"]!!.extra["artifactId"] as String}"
                            internal val runtimeDependency: String = "$runtimeDependency"
                        """.trimIndent()
                    )
                }
            }
            
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    sourceSets {
                        named("main") {
                            kotlin.srcDir(constsSourceDirectory)
                            dependencies {
                                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.versions.kotlin.asProvider().get()}") // TODO: Replace
                            }
                        }
                    }
                }
                
                tasks.named("compileKotlin") {
                    dependsOn(writePaths)
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
        "assertions" {
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