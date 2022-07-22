import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.targets
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget


plugins {
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
    id("org.jetbrains.kotlinx.kover")
    id("org.jetbrains.dokka")
}

val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()
val kotlinVersion: String by project
val dokkaVersion: String by project
val kotestVersion: String by project

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
                if (this is KotlinJvmOptions) jvmTarget = "11"
            }
        }
        if (this is KotlinJvmTarget)
            testRuns["test"].executionTask.configure {
                useJUnitPlatform()
            }
    }

    sourceSets.withEach {
        if (name.endsWith("test")) {
            dependencies {
                implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
                implementation("io.kotest:kotest-framework-engine:$kotestVersion")
                implementation("io.kotest:kotest-assertions-core:$kotestVersion")
                implementation("io.kotest:kotest-property:$kotestVersion")
            }
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
//        maven("https://repo.kotlin.link")
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    if (name.startsWith("kone-", ignoreCase = true)) {
        if (contextReceiversSupportCrunch) {
            apply(plugin = "org.jetbrains.kotlin.jvm")
            configure<KotlinJvmProjectExtension> {
                configureBase()

                sourceSets {
                    val main by getting {
                        kotlin.setSrcDirs(listOf("src/commonMain/kotlin"))
                        resources.setSrcDirs(listOf("src/commonMain/resources"))
                    }
                    val test by getting {
                        kotlin.setSrcDirs(listOf("src/commonTest/kotlin"))
                        resources.setSrcDirs(listOf("src/commonTest/resources"))
                    }
                }
            }
        } else {
            apply<KotlinMultiplatformPlugin>()
            configure<KotlinMultiplatformExtension> {

                jvm ()

//                js(IR) {
//                    browser()
//                    nodejs()
//                }
//
//                mingwX64()
            }
        }

        apply<DokkaPlugin>()
        tasks.withType<DokkaTask> {
            // TODO
        }
        dependencies {
            dokkaPlugin("org.jetbrains.dokka:mathjax-plugin:$dokkaVersion")
        }
    }
}