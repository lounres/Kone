import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.targets
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()
val kotlinVersion: String by project

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
}

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
    kotlin("jvm") apply false
    kotlin("multiplatform") apply false
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
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
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
    }
}