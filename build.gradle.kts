@file:Suppress("SuspiciousCollectionReassignment")

import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.BenchmarksPlugin
import kotlinx.benchmark.gradle.KotlinJvmBenchmarkTarget
import kotlinx.benchmark.gradle.JsBenchmarkTarget
import kotlinx.benchmark.gradle.NativeBenchmarkTarget
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenGradleSubplugin
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.*
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCompilation
import org.jetbrains.kotlin.gradle.targets.js.yarn.yarn
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(kotlin.multiplatform) apply false
        alias(allopen) apply false
        alias(kotlinx.benchmark) apply false
        alias(kotest.multiplatform) apply false
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
    }
}


val jvmTargetVersion : String by properties
val ignoreManualBugFixes = (properties["ignoreManualBugFixes"] as String) == "true"

fun PluginManager.withPlugin(pluginDep: PluginDependency, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDep.pluginId, block)
fun PluginManager.withPlugin(pluginDepProvider: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDepProvider.get().pluginId, block)
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

stal {
    action {
        on("uses libs main core") {
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm) {
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
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.multiplatform) {
                apply<KotestMultiplatformCompilerGradlePlugin>()
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
        on("kotlin jvm") {
            apply<KotlinPluginWrapper>()
            configure<KotlinJvmProjectExtension> {
                target.compilations.all {
                    kotlinOptions {
                        jvmTarget = jvmTargetVersion
                    }
                    compileTaskProvider.apply {
                        // TODO: Check if really is necessary
                        kotlinOptions {
                            jvmTarget = jvmTargetVersion
                        }
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
        on("kotlin multiplatform") {
            apply<KotlinMultiplatformPluginWrapper>()
            configure<KotlinMultiplatformExtension> {
                jvm {
                    compilations.all {
                        kotlinOptions {
                            jvmTarget = jvmTargetVersion
                            freeCompilerArgs += listOf(
//                                "-Xlambdas=indy"
                            )
                        }
                    }
                    testRuns.all {
                        executionTask {
                            useJUnitPlatform()
                        }
                    }
                }

                js(IR) {
                    browser()
                    nodejs()
                }

                linuxX64()
                mingwX64()
//                macosX64()

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
        on("kotlin common settings") {
            configure<KotlinProjectExtension> {
                sourceSets {
                    all {
                        languageSettings {
                            progressiveMode = true
                            optIn("kotlin.contracts.ExperimentalContracts")
                            optIn("kotlin.ExperimentalStdlibApi")
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
        on("kotlin library settings") {
            configure<KotlinProjectExtension> {
                explicitApi = Warning
            }
        }
        on("kotest") {
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        val test by getting {
                            dependencies {
                                with(rootProject.libs.kotest) {
                                    implementation(framework.engine)
                                    implementation(framework.datatest)
                                    implementation(assertions.core)
                                    implementation(property)
                                    implementation(runner.junit5)
                                }
                            }
                        }
                    }
                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.multiplatform) {
                apply<KotestMultiplatformCompilerGradlePlugin>()
                configure<KotlinMultiplatformExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    sourceSets {
                        val commonTest by getting {
                            dependencies {
                                with(rootProject.libs.kotest) {
                                    implementation(framework.engine)
                                    implementation(framework.datatest)
                                    implementation(assertions.core)
                                    implementation(property)
                                }
                            }
                        }
                        val jvmTest by getting {
                            dependencies {
                                implementation(rootProject.libs.kotest.runner.junit5)
                            }
                        }
                    }
                }
            }
        }
        on("examples") {
            @Suppress("UNUSED_VARIABLE")
            fun NamedDomainObjectContainer<out KotlinCompilation<*>>.configureExamples() {
                val main by getting
                val examples by creating {
                    associateWith(main)
                    defaultSourceSet {
                        kotlin.setSrcDirs(listOf("src/examples/kotlin"))
                        resources.setSrcDirs(listOf("src/examples/resources"))
                        dependencies {
                            implementation(rootProject.projects.libs.util.examples)
                        }
                    }

                    task<JavaExec>("runJvmExample") {
                        group = "examples"
                        description = "Runs the module's examples"
                        classpath = output.classesDirs + compileDependencyFiles + runtimeDependencyFiles!!
                        mainClass = "com.lounres.${project.extra["artifactPrefix"]}${project.name}.examples.MainKt"
                    }
                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    target.compilations.configureExamples()
                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    targets.getByName<KotlinJvmTarget>("jvm").compilations.configureExamples()
                }
            }
        }
        on("benchmark") {
            apply<BenchmarksPlugin>()
            apply<AllOpenGradleSubplugin>()
            the<AllOpenExtension>().annotation("org.openjdk.jmh.annotations.State")

            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm) {
                logger.error("kotlinx.benchmark plugging in and setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.multiplatform) {
                val benchmarksExtension = the<BenchmarksExtension>()
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    val commonMain by sourceSets.getting
                    val commonBenchmarks by sourceSets.creating {
                        dependsOn(commonMain)
                        dependencies {
                            implementation(rootProject.libs.kotlinx.benchmark.runtime)
                        }
                    }
                    targets.filter { it.platformType != KotlinPlatformType.common }.withEach {
                        compilations {
                            val main by getting
                            val benchmarks by creating {
                                associateWith(main)
                                defaultSourceSet {
                                    dependsOn(commonBenchmarks)
                                }
                            }

                            val benchmarksSourceSetName = benchmarks.defaultSourceSet.name

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
                                        compilation = benchmarks as KotlinJvmCompilation
                                    )
                                    benchmarksExtension.targets.add(benchmarkTarget)

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
                                        compilation = benchmarks as KotlinJsCompilation
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
                                        compilation = benchmarks as KotlinNativeCompilation
                                    )
//                                    benchmarksExtension.targets.add(benchmarkTarget)
                                }
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
                        warmups = 20
                        iterations = 10
                        iterationTime = 3
                    }
                    val smoke by creating {
                        warmups = 5
                        iterations = 3
                        iterationTime = 500
                        iterationTimeUnit = "ms"
                    }
                }
            }
        }
        on("dokka") {
            apply<DokkaPlugin>()
            dependencies {
                dokkaPlugin(rootProject.libs.dokka.mathjax)
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
        on("publishing") {
            apply<MavenPublishPlugin>()
            afterEvaluate {
                configure<PublishingExtension> {
                    publications.withType<MavenPublication> {
                        artifactId = "${extra["artifactPrefix"]}$artifactId"
                        artifact(tasks.named<Jar>("dokkaJar"))
                    }
                }
            }
        }
    }
}