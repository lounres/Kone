@file:Suppress("SuspiciousCollectionReassignment")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class, KotlinxBenchmarkPluginInternalApi::class)

import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.KotlinJvmBenchmarkTarget
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
//import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.accessors.dm.LibrariesForVersions
import org.gradle.accessors.dm.RootProjectAccessor
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import java.time.LocalDate
import java.time.ZoneId


plugins {
    alias(versions.plugins.kotlin.multiplatform) apply false
    alias(versions.plugins.kotlinx.atomicfu) apply false
    alias(versions.plugins.kotlin.allopen) apply false
    alias(versions.plugins.kotlinx.benchmark) apply false
    alias(versions.plugins.kotest.multiplatform) apply false
    alias(versions.plugins.kotlinx.kover) apply false
    alias(versions.plugins.dokka)
    `version-catalog`
    `maven-publish`
    signing
    alias(versions.plugins.nexus.publish.plugin)
}


val today: LocalDate = LocalDate.now(ZoneId.of("UTC"))
val koneVersion = "0.0.0-experiment-${today.year}.${today.month.value}.${today.dayOfMonth}"
val koneGroup = project.properties["group"] as String
val koneUrl: String by project
val koneBaseUrl: String by project

allprojects {
    version = koneVersion
}

tasks.register("docusaurusGenerateInputData") {
    group = "site"
    outputs.files("site/src/inputData.ts", "site/inputData.js")
    doLast {
        rootDir.resolve("site/src/inputData.ts").writer().use {
            it.write(
                """
                    export const koneGroup = "$koneGroup"
                    export const koneVersion = "$koneVersion"
                    export const koneUrl = "$koneUrl"
                    export const koneBaseUrl = "$koneBaseUrl"
                """.trimIndent()
            )
        }
        rootDir.resolve("site/inputData.js").writer().use {
            it.write(
                """
                    module.exports = {
                        koneGroup: "$koneGroup",
                        koneVersion: "$koneVersion",
                        koneUrl: "$koneUrl",
                        koneBaseUrl: "$koneBaseUrl",
                    }
                """.trimIndent()
            )
        }
    }
}

tasks.register("docusaurusGenerateDevInputData") {
    group = "site"
    outputs.files("site/src/inputData.ts", "site/inputData.js")
    doLast {
        rootDir.resolve("site/src/inputData.ts").writer().use {
            it.write(
                """
                    export const koneGroup = "$koneGroup"
                    export const koneVersion = "$koneVersion"
                    export const koneUrl = "http://localhost:3000"
                    export const koneBaseUrl = "$koneBaseUrl"
                """.trimIndent()
            )
        }
        rootDir.resolve("site/inputData.js").writer().use {
            it.write(
                """
                    module.exports = {
                        koneGroup: "$koneGroup",
                        koneVersion: "$koneVersion",
                        koneUrl: "http://localhost:3000",
                        koneBaseUrl: "$koneBaseUrl",
                    }
                """.trimIndent()
            )
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


val jvmTargetVersion : String by properties
val ignoreManualBugFixes = (properties["ignoreManualBugFixes"] as String) == "true"

val Project.versions: LibrariesForVersions get() = rootProject.extensions.getByName<LibrariesForVersions>("versions")
//val Project.libs: LibrariesForLibs get() = rootProject.extensions.getByName<LibrariesForLibs>("libs")
val Project.projects: RootProjectAccessor get() = rootProject.extensions.getByName<RootProjectAccessor>("projects")
fun PluginAware.apply(pluginDependency: PluginDependency) = apply(plugin = pluginDependency.pluginId)
fun PluginAware.apply(pluginDependency: Provider<PluginDependency>) = apply(plugin = pluginDependency.get().pluginId)
fun PluginManager.withPlugin(pluginDep: PluginDependency, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDep.pluginId, block)
fun PluginManager.withPlugin(pluginDepProvider: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDepProvider.get().pluginId, block)
fun PluginManager.withPlugins(vararg pluginDeps: PluginDependency, block: AppliedPlugin.() -> Unit) = pluginDeps.forEach { withPlugin(it, block) }
fun PluginManager.withPlugins(vararg pluginDeps: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = pluginDeps.forEach { withPlugin(it, block) }
inline fun <T> Iterable<T>.withEach(action: T.() -> Unit) = forEach { it.action() }

val Project.artifact: String get() = "${extra["artifactPrefix"]}${project.name}"
val Project.alias: String get() = "${extra["aliasPrefix"]}${project.name}"

catalog.versionCatalog {
    version("kone", koneVersion)
}

gradle.projectsEvaluated {
    val bundleMainProjects = stal.lookUp.projectsThat { has("versionCatalog bundle main") }
    val bundleMiscProjects = stal.lookUp.projectsThat { has("versionCatalog bundle misc") }
    val bundleUtilProjects = stal.lookUp.projectsThat { has("versionCatalog bundle util") }
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

publishing {
    publications {
        create<MavenPublication>("versionCatalog") {
            artifactId = "kone.versionCatalog"
            from(components["versionCatalog"])
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

stal {
    action {
        "uses libs main core" {
            pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        val main by getting {
                            dependencies {
                                api(projects.libs.main.core)
                            }
                        }
                    }
                }
            }
            pluginManager.withPlugin(versions.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        commonMain {
                            dependencies {
                                api(projects.libs.main.core)
                            }
                        }
                    }
                }
            }
        }
        "kotlin jvm" {
            apply(versions.plugins.kotlin.jvm)
            configure<KotlinJvmProjectExtension> {
                jvmToolchain(jvmTargetVersion.toInt())
                
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
                
                jvmToolchain(jvmTargetVersion.toInt())
                
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
        "atomicfu" {
            apply(versions.plugins.kotlinx.atomicfu)
            configure<AtomicFUPluginExtension> {
                transformJvm = true
                jvmVariant = "VH"
            }
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
                configure<KotlinJvmProjectExtension> {
                
                }
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
        "libs non-core main" {
            val algorithmsSubproject = project("${project.path}:algorithms")
            project("${project.path}:benchmarks") {
                pluginManager.withPlugin(versions.plugins.kotlin.jvm) {
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
                                    implementation(project(algorithmsSubproject.path))
                                }
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
                moduleName = "${project.extra["artifactPrefix"]}${project.name}"
                // DOKKA-3885
                dokkaGeneratorIsolation = ClassLoaderIsolation()
            }

            task<Jar>("dokkaJar") {
                group = "dokka"
                description = "Assembles Kotlin docs with Dokka into a javadoc JAR"
                archiveClassifier = "javadoc"
                afterEvaluate {
                    val dokkaGeneratePublicationHtml by tasks.getting
                    dependsOn(dokkaGeneratePublicationHtml)
                    from(dokkaGeneratePublicationHtml)
                }
            }
        }
        "publication" {
            pluginManager.withPlugin("org.gradle.maven-publish") {
                afterEvaluate {
                    configure<PublishingExtension> {
                        publications.withType<MavenPublication> {
                            artifactId = "${extra["artifactPrefix"]}$artifactId"
                        }
                    }
                }
            }
        }
        "publishing" {
            apply(plugin = "org.gradle.maven-publish")
            apply(plugin = "org.gradle.signing")
            afterEvaluate {
                configure<PublishingExtension> {
                    publications.withType<MavenPublication> {
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
                tasks.withType<AbstractPublishToMaven>().configureEach {
                    val signingTasks = tasks.withType<Sign>()
                    mustRunAfter(signingTasks)
                }
            }
            configure<SigningExtension> {
                sign(the<PublishingExtension>().publications)
            }
        }
        case { hasAllOf("dokka", "publishing") } implies {
            afterEvaluate {
                configure<PublishingExtension> {
                    publications.withType<MavenPublication> {
                        artifact(tasks.named<Jar>("dokkaJar"))
                    }
                }
            }
        }
    }
}