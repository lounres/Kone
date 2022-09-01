@file:Suppress("SuspiciousCollectionReassignment")

import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper


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

    group = "com.lounres.kone.experimental.context-receivers"
    version = "0.1.0-pre-1"
}

val onlyJvmTarget: Boolean = (properties["onlyJvmTarget"] as String).toBoolean()

subprojects {
    if (name.startsWith("kone-", ignoreCase = true) || name in listOf("testUtil")) {
        apply<KotlinMultiplatformPluginWrapper>()
        apply<KotestMultiplatformCompilerGradlePlugin>()
        configure<KotlinMultiplatformExtension> {
            explicitApi = Warning

            jvm {
                compilations.all {
                    kotlinOptions {
//                        freeCompilerArgs += listOf(
//                            "-X",
//                        )
                        jvmTarget = properties["jvmTarget"] as String
                    }
                }
                testRuns["test"].executionTask {
                    useJUnitPlatform()
                }
            }

            if (!onlyJvmTarget) {
                js(IR) {
                    browser()
                    nodejs()
                }

                linuxX64()
                mingwX64()
                macosX64()
            }

            @Suppress("UNUSED_VARIABLE")
            sourceSets {
                all {
                    languageSettings {
                        progressiveMode = true
                        optIn("kotlin.contracts.ExperimentalContracts")
                        enableLanguageFeature("ContextReceivers")
                    }
                    dependencies {
                        compileOnly(kotlin("stdlib:${rootProject.libs.versions.kotlin.get()}"))
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