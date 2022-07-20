import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.*

val contextReceiversSupportCrunch: Boolean = (properties["contextReceiversSupportCrunch"] as String).toBoolean()
val kotlinVersion: String by project
val kotlinCompilerArgs: List<String> = listOf(
    "-Xcontext-receivers",
//    "-Xuse-k2",
)
val explicitApiMode = Warning
val jvmTargetVersion = "11"

plugins {
    id("org.jetbrains.kotlinx.kover") version "0.5.0"
    `kotlin-dsl`
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
                explicitApi = explicitApiMode

                target.compilations.all {
                    kotlinOptions {
                        freeCompilerArgs += kotlinCompilerArgs
                        jvmTarget = jvmTargetVersion
                    }
                }

                sourceSets {
                    main {
                        kotlin.setSrcDirs(listOf("src/commonMain/kotlin"))
                        resources.setSrcDirs(listOf("src/commonMain/resources"))
                    }
                    test {
                        kotlin.setSrcDirs(listOf("src/commonTest/kotlin"))
                        resources.setSrcDirs(listOf("src/commonTest/resources"))
                    }
                }
            }
        } else {
            apply(plugin = "org.jetbrains.kotlin.multiplatform")
            configure<KotlinMultiplatformExtension> {
                explicitApi = explicitApiMode

                jvm {
                    compilations.all {
                        kotlinOptions {
                            jvmTarget = jvmTargetVersion
                        }
                    }

                    testRuns["test"].executionTask.configure {
                        useJUnitPlatform()
                    }
                }

//                js(IR) {
//                    browser()
//                    nodejs()
//                }
//
//                mingwX64()

                targets.all {
                    compilations.all {
                        kotlinOptions {
                            freeCompilerArgs += kotlinCompilerArgs
                        }
                    }
                }
            }
        }
    }
}