@file:Suppress("SuspiciousCollectionReassignment")

import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(kotlin.multiplatform) apply false
        alias(kotest.multiplatform) apply false
        alias(kover)
        alias(dokka)
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.kotlin.link")
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    if (name.startsWith("kone-", ignoreCase = true) || name in listOf("mapUtil")) {
        apply<DokkaPlugin>()
        dependencies {
            dokkaPlugin(rootProject.libs.dokka.mathjax)
        }

        afterEvaluate {
            tasks.withType<DokkaTask> {
                // TODO
            }
            task<Jar>("dokkaJar") {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles Kotlin docs with Dokka"
                archiveClassifier.set("javadoc")
                val dokkaHtml by tasks.getting
                dependsOn(dokkaHtml)
                from(dokkaHtml)
            }
        }
    }

    group = "com.lounres.kone.experimental.1-7-20-Beta"
    version = "0.0.0-dev-1"
}

subprojects {
    if (name.startsWith("kone-", ignoreCase = true) || name in listOf("testUtil")) {
        apply(plugin = rootProject.libs.plugins.kotlin.multiplatform.get().pluginId)
        apply(plugin = rootProject.libs.plugins.kotest.multiplatform.get().pluginId)
        configure<KotlinMultiplatformExtension> {
            explicitApi = Warning

            jvm {
                compilations.all {
                    kotlinOptions {
                        jvmTarget = properties["jvmTarget"] as String
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
                all {
                    languageSettings {
                        progressiveMode = true
                        optIn("kotlin.contracts.ExperimentalContracts")
                    }
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
                val commonTest by getting {
                    dependencies {
                        implementation(kotlin("test"))
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
    if (name.startsWith("kone-", ignoreCase = true) || name in listOf("mapUtil")) {
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