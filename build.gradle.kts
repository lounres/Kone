@file:Suppress("SuspiciousCollectionReassignment")

import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(kotlin.multiplatform) apply false
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
}

val jvmTargetApi = properties["jvmTarget"] as String

fun PluginManager.withPlugin(pluginDep: PluginDependency, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDep.pluginId, block)
fun PluginManager.withPlugin(pluginDepProvider: Provider<PluginDependency>, block: AppliedPlugin.() -> Unit) = withPlugin(pluginDepProvider.get().pluginId, block)

catalog {
    versionCatalog {
        val koneLibraries = project(":kone").subprojects - project(":kone:misc")
        val miscLibraries = project(":kone:misc").subprojects
        val utilsLibraries = project(":utils").subprojects

        koneLibraries.forEach { library(it.name, "com.lounres.kone:${it.name}:$projectVersion") }
        miscLibraries.forEach { library("misc-${it.name}", "com.lounres.kone.misc:${it.name}:$projectVersion") }
        utilsLibraries.forEach { library("utils-${it.name}", "com.lounres.kone.utils:${it.name}:$projectVersion") }
    }
}

publishing {
    publications {
        create<MavenPublication>("versionCatalog") {
            groupId = "com.lounres.kone"
            artifactId = "versionCatalog"
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
                    compileKotlinTask.apply {
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
//                explicitApi = Warning

                jvm {
                    compilations.all {
                        kotlinOptions {
                            jvmTarget = jvmTargetApi
                        }
                        compileKotlinTask.apply {
                            kotlinOptions {
                                jvmTarget = jvmTargetApi
                            }
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
                        all {
                            if (name.endsWith("test", ignoreCase = true)) {
                                dependencies {
                                    with(rootProject.libs.kotest) {
                                        implementation(framework.engine)
                                        implementation(framework.datatest)
                                        implementation(assertions.core)
                                        implementation(property)
                                    }
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
                tasks.withType<DokkaTask> {
                    // TODO
                }
            }
        }
        on("publishing") {
            apply<MavenPublishPlugin>()
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