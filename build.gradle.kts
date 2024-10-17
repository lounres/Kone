@file:Suppress("SuspiciousCollectionReassignment")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class, KotlinxBenchmarkPluginInternalApi::class)

import kotlinx.atomicfu.plugin.gradle.AtomicFUPluginExtension
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.KotlinJvmBenchmarkTarget
import kotlinx.benchmark.gradle.JsBenchmarkTarget
import kotlinx.benchmark.gradle.NativeBenchmarkTarget
import kotlinx.benchmark.gradle.internal.KotlinxBenchmarkPluginInternalApi
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.accessors.dm.RootProjectAccessor
import org.gradle.kotlin.dsl.libs
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrCompilation
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn


plugins {
    with(libs.plugins) {
        alias(kotlin.multiplatform) apply false
        alias(kotlinx.atomicfu) apply false
        alias(allopen) apply false
        alias(kotlinx.benchmark) apply false
//        alias(kotest.multiplatform) apply false
        alias(dokka)
    }
    `version-catalog`
    `maven-publish`
}

val koneVersion = project.properties["version"] as String
val koneGroup = project.properties["group"] as String
val koneUrl: String by project
val koneBaseUrl: String by project

tasks.register<Copy>("docusaurusProcessResources") {
    group = "documentation"
    dependsOn("dokkaHtmlMultiModule")
    from("build/dokka/htmlMultiModule")
    into("docs/static/api")
    outputs.files("docs/src/inputData.ts", "docs/inputData.js")
    doLast {
        rootDir.resolve("docs/src/inputData.ts").writer().use {
            it.write(
                """
                    export const koneGroup = "$koneGroup"
                    export const koneVersion = "$koneVersion"
                    export const koneUrl = "$koneUrl"
                    export const koneBaseUrl = "$koneBaseUrl"
                """.trimIndent()
            )
        }
        rootDir.resolve("docs/inputData.js").writer().use {
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

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.kotlin.link")
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
        mavenLocal()
    }
}


val jvmTargetVersion : String by properties
val ignoreManualBugFixes = (properties["ignoreManualBugFixes"] as String) == "true"

val Project.libs: LibrariesForLibs get() = rootProject.extensions.getByName<LibrariesForLibs>("libs")
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
    val bundleMainProjects = stal.lookUp.projectsThat { hasAllOf("publishing", "libs main") }
    val bundleMiscProjects = stal.lookUp.projectsThat { hasAllOf("publishing", "libs misc") }
    val bundleUtilProjects = stal.lookUp.projectsThat { hasAllOf("publishing", "libs util") }
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

tasks.dokkaHtmlMultiModule {

}

allprojects {
    pluginManager.withPlugin(libs.plugins.kotlinx.atomicfu) {
        configure<AtomicFUPluginExtension> {
            transformJvm = true
            jvmVariant = "VH"
            transformJs = true
        }
    }
}

stal {
    action {
        "uses libs main core" {
            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
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
            pluginManager.withPlugin(libs.plugins.kotlin.multiplatform) {
//                apply(libs.plugins.kotest.multiplatform)
                configure<KotlinMultiplatformExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        val commonMain by getting {
                            dependencies {
                                api(projects.libs.main.core)
                            }
                        }
                    }
                }
            }
        }
        "kotlin jvm" {
            apply(libs.plugins.kotlin.jvm)
            configure<KotlinJvmProjectExtension> {
                target {
                    compilerOptions {
                        jvmTarget = JvmTarget.fromTarget(jvmTargetVersion)
                        freeCompilerArgs = freeCompilerArgs.get() + listOf(
                            "-Xlambdas=indy",
                            "-Xexpect-actual-classes",
                            "-Xconsistent-data-class-copy-visibility",
                        )
                    }
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
            apply(libs.plugins.kotlin.multiplatform)
            configure<KotlinMultiplatformExtension> {
                applyDefaultHierarchyTemplate()
                
                compilerOptions {
                    freeCompilerArgs = freeCompilerArgs.get() + listOf(
                        "-Xlambdas=indy",
                        "-Xexpect-actual-classes",
                        "-Xconsistent-data-class-copy-visibility",
                    )
                }

                jvm {
                    compilerOptions {
                        jvmTarget = JvmTarget.fromTarget(jvmTargetVersion)
                    }
                    testRuns.all {
                        executionTask {
                            useJUnitPlatform()
                        }
                    }
                }

//                js(IR) {
//                    browser()
//                    nodejs()
//                }

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
                    val commonTest by getting {
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
            pluginManager.withPlugins(libs.plugins.kotlin.jvm, libs.plugins.kotlin.multiplatform) {
                configure<KotlinProjectExtension> {
                    sourceSets {
                        all {
                            languageSettings {
                                progressiveMode = true
                                enableLanguageFeature("ContextReceivers")
                                enableLanguageFeature("ValueClasses")
                                enableLanguageFeature("ContractSyntaxV2")
                                optIn("kotlin.contracts.ExperimentalContracts")
                                optIn("kotlin.ExperimentalStdlibApi")
                                optIn("kotlin.ExperimentalUnsignedTypes")
                            }
                        }
                    }
                }
            }
            pluginManager.withPlugin("org.gradle.java") {
                configure<JavaPluginExtension> {
                    targetCompatibility = JavaVersion.toVersion(jvmTargetVersion)
                }
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
//        "kotest" {
//            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
//                configure<KotlinJvmProjectExtension> {
//                    @Suppress("UNUSED_VARIABLE")
//                    sourceSets {
//                        val test by getting {
//                            dependencies {
//                                with(libs.kotest) {
//                                    implementation(framework.engine)
//                                    implementation(framework.datatest)
//                                    implementation(assertions.core)
//                                    implementation(property)
//                                    implementation(runner.junit5)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            pluginManager.withPlugin(libs.plugins.kotlin.multiplatform) {
//                apply(libs.plugins.kotest.multiplatform)
//                configure<KotlinMultiplatformExtension> {
//                    @Suppress("UNUSED_VARIABLE")
//                    sourceSets {
//                        val commonTest by getting {
//                            dependencies {
//                                with(libs.kotest) {
//                                    implementation(framework.engine)
//                                    implementation(framework.datatest)
//                                    implementation(assertions.core)
//                                    implementation(property)
//                                }
//                            }
//                        }
//                        val jvmTest by getting {
//                            dependencies {
//                                implementation(libs.kotest.runner.junit5)
//                            }
//                        }
//                    }
//                }
//            }
//        }
        "algorithms" {
            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
                logger.error("algorithm source set setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(libs.plugins.kotlin.multiplatform) {
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        val commonMain by getting {
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
            apply(libs.plugins.kotlinx.benchmark)
            apply(libs.plugins.allopen)
            the<AllOpenExtension>().annotation("org.openjdk.jmh.annotations.State")

            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
                logger.error("kotlinx.benchmark plugging in and setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(libs.plugins.kotlin.multiplatform) {
                val benchmarksExtension = the<BenchmarksExtension>()
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    val commonMain by sourceSets.getting {
                        dependencies {
                            implementation(libs.kotlinx.benchmark.runtime)

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

                                benchmarkTarget.jmhVersion = libs.versions.jmh.get()

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
                                val benchmarkTarget = JsBenchmarkTarget(
                                    extension = benchmarksExtension,
                                    name = benchmarksSourceSetName,
                                    compilation = main as KotlinJsIrCompilation
                                )
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
                                val benchmarkTarget = NativeBenchmarkTarget(
                                    extension = benchmarksExtension,
                                    name = benchmarksSourceSetName,
                                    compilation = main as KotlinNativeCompilation
                                )
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
            pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {

                }
            }
            pluginManager.withPlugin(libs.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    sourceSets {
                        val commonMain by getting {
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
                pluginManager.withPlugin(libs.plugins.kotlin.jvm) {
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
                }
                pluginManager.withPlugin(libs.plugins.kotlin.multiplatform) {
                    @Suppress("UNUSED_VARIABLE")
                    configure<KotlinMultiplatformExtension> {
                        sourceSets {
                            val commonMain by getting {
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
            apply(libs.plugins.dokka)
            dependencies {
                dokkaPlugin(libs.dokka.mathjax)
            }

            task<Jar>("dokkaJar") {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles Kotlin docs with Dokka"
                archiveClassifier = "javadoc"
                afterEvaluate {
                    val dokkaHtml by tasks.getting
                    dependsOn(dokkaHtml)
                    from(dokkaHtml)
                }
            }

            afterEvaluate {
                tasks.withType<AbstractDokkaLeafTask> {
                    moduleName = "${project.extra["artifactPrefix"]}${project.name}"
                    // TODO
                }
            }
        }
        "publishing" {
            apply(plugin = "org.gradle.maven-publish")
            afterEvaluate {
                configure<PublishingExtension> {
                    publications.withType<MavenPublication> {
                        artifactId = "${extra["artifactPrefix"]}$artifactId"
                    }
                }
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