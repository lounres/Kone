@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
//    alias(versions.plugins.kotlinx.serialization)
//    alias(versions.plugins.kotlin.compose)
//    alias(versions.plugins.compose.multiplatform)
}

//dependencies {
//    add(
//        PLUGIN_CLASSPATH_CONFIGURATION_NAME,
//        projects.plugins.suppliedTypes
//    )
//}

kotlin {
    jvm {
        binaries {
            executable {
                mainClass = "MainKt"
            }.configure {
                jvmArgs("-XX:MaxMetaspaceSize=4G")
            }
        }
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = "Kone-test"
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-debugger-custom-formatters")
        }
        browser {
            commonWebpackConfig {
                outputFileName = "test.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    rootProject.allprojects.forEach {
                        static(it.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    js {
        outputModuleName = "Kone-test"
        compilerOptions {
            freeCompilerArgs.add("-Xwasm-debugger-custom-formatters")
        }
        browser {
            commonWebpackConfig {
                outputFileName = "test.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    rootProject.allprojects.forEach {
                        static(it.projectDir.path)
                    }
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        commonMain {
            dependencies {
//                implementation(projects.libs.util.misc)
//                implementation(projects.libs.main.algebraic)
//                implementation(projects.libs.main.algebraicExtra)
//                implementation(projects.libs.main.multidimensionalCollections)
//                implementation(projects.libs.main.computationalGeometry)
//                implementation(projects.libs.main.enumerativeCombinatorics)
//                implementation(versions.kotlinx.serialization.json)
//                implementation(versions.kotlinx.datetime)
                
//                implementation(compose.runtime)
                
                implementation(projects.libs.main.concurrentCollections)
            }
        }
        jvmMain {
            dependencies {
//                implementation(projects.libs.util.misc)
//                implementation(projects.libs.main.annotations)
//                implementation(projects.libs.main.collections)
//                implementation(projects.libs.main.linearAlgebra)
//                implementation(projects.libs.main.computationalGeometry)
//                implementation(projects.libs.main.algebraicExtra)
//                implementation(projects.libs.misc.planimetricsCalculus)

//                implementation(projects.libs.misc.composeCanvas)
//                implementation(compose.desktop.currentOs)
//                implementation(compose.components.resources)
//                implementation(versions.kotest.assertions.core)
            }
        }
//        wasmJsMain {
//            dependencies {
//                implementation(projects.libs.main.collections)
//
//                api(compose.runtime)
//                api(compose.ui)
//                api(compose.foundation)
//                api(compose.material3)
//                api(compose.components.resources)
//            }
//        }
    }
}

//compose {
//    resources {
//        publicResClass = false
//        packageOfResClass = ""
//        generateResClass = always
//    }
////    desktop {
////        application {
////            mainClass = "MainKt"
////        }
////    }
////    web
//}