@file:Suppress("SuspiciousCollectionReassignment")

import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin
import kotlinx.benchmark.gradle.BenchmarksExtension
import kotlinx.benchmark.gradle.BenchmarksPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.AbstractDokkaLeafTask
import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.allopen.gradle.AllOpenGradleSubplugin
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinCompilationToRunnableFiles
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

val projectVersion = "0.0.0-dev-1"

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.kotlin.link")
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    version = projectVersion
    group = "com.lounres"

    val defaultProperties = mapOf(
        "artifactPrefix" to "",
        "aliasPrefix" to "",
    )
    for ((prop, value) in defaultProperties)
        if (!extra.has(prop)) extra[prop] = value
}

val jvmTargetApi = properties["jvmTarget"] as String

fun PluginManager.withPlugin(pluginDep: PluginDependency, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDep.pluginId, block)
fun PluginManager.withPlugin(pluginDepProvider: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDepProvider.get().pluginId, block)
inline fun <T> Iterable<T>.withEach(action: T.() -> Unit) = forEach { it.action() }


publishing {
    publications {
        create<MavenPublication>("versionCatalog") {
            artifactId = "kone.versionCatalog"
            from(components["versionCatalog"])
        }
    }
}

featuresManagement {
    features {
        on("kotlin jvm") {
            apply<KotlinPluginWrapper>()
            configure<KotlinJvmProjectExtension> {
                target.compilations.all {
                    kotlinOptions {
                        jvmTarget = jvmTargetApi
                    }
                    compileTaskProvider.apply {
                        // TODO: Check if really is necessary
                        kotlinOptions {
                            jvmTarget = jvmTargetApi
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
                            jvmTarget = jvmTargetApi
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
                macosX64()

                @Suppress("UNUSED_VARIABLE")
                sourceSets {
                    val commonTest by getting {
                        dependencies {
                            implementation(kotlin("test"))
                        }
                    }
                }
            }
        }
        on("kotlin common settings") {
            configure<KotlinProjectExtension> {
                explicitApi = Warning

                sourceSets {
                    all {
                        languageSettings {
                            progressiveMode = true
                            optIn("kotlin.contracts.ExperimentalContracts")
                        }
                    }
                }
            }
            pluginManager.withPlugin("org.gradle.java") {
                configure<JavaPluginExtension> {
                    targetCompatibility = JavaVersion.toVersion(jvmTargetApi)
                }
                tasks.withType<Test> {
                    useJUnitPlatform()
                }
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
            fun NamedDomainObjectContainer<out AbstractKotlinCompilationToRunnableFiles<*>>.configureExamples() {
                val main by getting
                val examples by creating {
                    defaultSourceSet {
                        dependsOn(main.defaultSourceSet)
                        kotlin.setSrcDirs(listOf("src/examples/kotlin"))
                        resources.setSrcDirs(listOf("src/examples/resources"))
                    }

                    task<JavaExec>("runJvmExample") {
                        group = "examples"
                        classpath = output.classesDirs + compileDependencyFiles + runtimeDependencyFiles
                        mainClass.set("com.lounres.${project.extra["artifactPrefix"]}${project.name}.examples.MainKt")
                    }
                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm) {
                configure<KotlinJvmProjectExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    target.compilations.configureExamples()
                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.multiplatform) {
                configure<KotlinMultiplatformExtension> {
                    @Suppress("UNUSED_VARIABLE")
                    targets.getByName<KotlinJvmTarget>("jvm").compilations.configureExamples()
                }
            }
        }
        on("benchmark") {
            apply<AllOpenGradleSubplugin>()
            apply<BenchmarksPlugin>()
            the<AllOpenExtension>().annotation("org.openjdk.jmh.annotations.State")

            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.jvm) {
                logger.error("kotlinx.benchmark plugging in and setting is not yet implemented for Kotlin/JVM plug-in")
//                configure<KotlinJvmProjectExtension> {
//                    // ...
//                }
            }
            pluginManager.withPlugin(rootProject.libs.plugins.kotlin.multiplatform) {
                val kotlinxBenchmarkDebug = (property("kotlinx.benchmark.debug") as String?).toBoolean()
                val benchmarksExtension = the<BenchmarksExtension>()
                @Suppress("UNUSED_VARIABLE")
                configure<KotlinMultiplatformExtension> {
                    val commonBenchmarks by sourceSets.creating {
                        dependencies {
                            implementation(rootProject.libs.kotlinx.benchmark.runtime)
                        }
                    }
                    targets.filter { it.platformType != KotlinPlatformType.common }.withEach {
                        compilations {
                            val main by getting
                            val benchmarks by creating {
                                defaultSourceSet {
                                    dependsOn(main.defaultSourceSet)
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
                            if (platformType == KotlinPlatformType.jvm) {
                                benchmarksExtension.targets.register(benchmarksSourceSetName)
                                // Fix kotlinx-benchmarks bug
                                if (platformType == KotlinPlatformType.jvm) afterEvaluate {
                                    val jarTaskName = "${benchmarksSourceSetName}BenchmarkJar"
                                    tasks.findByName(jarTaskName).let { if (it is org.gradle.jvm.tasks.Jar) it else null }?.apply {
                                        if (kotlinxBenchmarkDebug) logger.warn("Corrected kotlinx.benchmark task $jarTaskName")
                                        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
                                    }
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
                archiveClassifier.set("javadoc")
                afterEvaluate {
                    val dokkaHtml by tasks.getting
                    dependsOn(dokkaHtml)
                    from(dokkaHtml)
                }
            }

            afterEvaluate {
                tasks.withType<AbstractDokkaLeafTask> {
                    moduleName.set("${project.extra["artifactPrefix"]}${project.name}")
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
                rootProject.catalog {
                    versionCatalog {
                        library("${extra["aliasPrefix"]}${project.name}", "$group:${extra["artifactPrefix"]}${project.name}:$version")
                    }
                }
            }
        }
    }
}