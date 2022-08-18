@file:Suppress("SuspiciousCollectionReassignment")

import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinWithJavaTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.targets
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(kotlin.jvm) apply false
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
            dokkaPlugin("org.jetbrains.dokka:mathjax-plugin:${rootProject.libs.versions.dokka.get()}")
        }
        tasks.withType<DokkaTask> {
            // TODO
        }

        afterEvaluate {
            task<Jar>("dokkaJar") {
                group = JavaBasePlugin.DOCUMENTATION_GROUP
                description = "Assembles Kotlin docs with Dokka"
                archiveClassifier.set("javadoc")
                val dokkaHtmlPartial by tasks.getting // FIXME: Не понятно почему, но dokkaHtml ничего не генерит
                dependsOn(dokkaHtmlPartial)
                from(dokkaHtmlPartial)
            }
        }
    }

    group = "com.lounres.kone"
    version = "0.1.0-pre-1"
}

val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()
val onlyJvmTarget: Boolean = (properties["onlyJvmTarget"] as String).toBoolean()

fun <T> Iterable<T>.withEach(action: T.() -> Unit) {
    for (element in this) element.apply(action)
}
fun KotlinProjectExtension.configureBase() {
    explicitApi = Warning

    targets.withEach {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs += listOf(
                    "-Xcontext-receivers",
//                    "-Xuse-k2",
                )
                if (this is KotlinJvmOptions) jvmTarget = properties["jvmTarget"] as String
            }
        }
        if (this is KotlinJvmTarget) {
            testRuns["test"].executionTask {
                useJUnitPlatform()
            }
            sourceSets.configureEach {
                if (name.endsWith("test", ignoreCase = true)) {
                    dependencies {
                        implementation(libs.kotest.runner.junit5)
                    }
                }
            }
        }
        if (this is KotlinWithJavaTarget<*>) {
            sourceSets.configureEach {
                if (name.endsWith("test", ignoreCase = true)) {
                    dependencies {
                        implementation(libs.kotest.runner.junit5)
                    }
                }
            }
        }

        sourceSets {
            all {
                languageSettings {
                    progressiveMode = true
                    optIn("kotlin.contracts.ExperimentalContracts")
                    enableLanguageFeature("RangeUntilOperator")
                }
            }
        }
    }

    sourceSets.configureEach {
        if (name.endsWith("test", ignoreCase = true)) {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.framework.datatest)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
            }
        }
    }
}

subprojects {
    if (name.startsWith("kone-", ignoreCase = true) || name in listOf("testUtil")) {
        if (contextReceiversSupportCrunch) {
            apply(plugin = "org.jetbrains.kotlin.jvm")
            configure<KotlinJvmProjectExtension> {
                configureBase()

                @Suppress("UNUSED_VARIABLE")
                sourceSets {
                    val main by getting {
                        kotlin.setSrcDirs(listOf("src/commonMain/kotlin"))
                        resources.setSrcDirs(listOf("src/commonMain/resources"))
                    }
                    val test by getting {
                        kotlin.setSrcDirs(listOf("src/commonTest/kotlin"))
                        resources.setSrcDirs(listOf("src/commonTest/resources"))
                        dependencies {
                            implementation(kotlin("test"))
                        }
                    }
                }
            }

            configure<JavaPluginExtension> {
                withSourcesJar()
            }

            tasks.withType<Test> {
                useJUnitPlatform()
            }
        } else {
            apply<KotestMultiplatformCompilerGradlePlugin>()
            apply<KotlinMultiplatformPlugin>()
            configure<KotlinMultiplatformExtension> {
                configureBase()

                jvm ()

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
                    val commonTest by getting {
                        dependencies {
                            implementation(kotlin("test"))
                        }
                    }
                }
            }
        }
    }
    if (name.startsWith("kone-", ignoreCase = true) || name in listOf("mapUtil")) {
        apply<MavenPublishPlugin>()

        afterEvaluate {
            if (name.startsWith("kone-", ignoreCase = true))
                if (contextReceiversSupportCrunch) {
                    configure<PublishingExtension> {
                        publications {
                            create<MavenPublication>("jvm") {
                                artifactId = "${project.name}-jvm"
                                from(components["java"])
                            }
                        }
                    }
                }

            configure<PublishingExtension> {
                publications.withType<MavenPublication> {
                    artifact(tasks.named<Jar>("dokkaJar"))
                }
            }
        }
    }
}