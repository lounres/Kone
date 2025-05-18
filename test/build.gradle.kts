@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.PLUGIN_CLASSPATH_CONFIGURATION_NAME
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(versions.plugins.kotlinx.serialization)
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
                    static = (static ?: mutableListOf()).apply {
                        rootProject.allprojects.forEach {
                            add(it.projectDir.path)
                        }
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
                    static = (static ?: mutableListOf()).apply {
                        rootProject.allprojects.forEach {
                            add(it.projectDir.path)
                        }
                    }
                }
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.serialization.core)
            }
        }
//        jvmMain {
//            dependencies {
//                implementation(versions.kotlinx.serialization.json)
//
//                implementation(projects.libs.util.misc)
//                implementation(projects.libs.main.annotations)
//                implementation(projects.libs.main.collections)
//                implementation(projects.libs.main.enumerativeCombinatorics)
//                implementation(projects.libs.main.linearAlgebra)
////                implementation(projects.libs.main.multidimensionalCollections)
//                implementation(projects.libs.main.computationalGeometry)
//                implementation(projects.libs.main.algebraicExtra)
//                implementation(projects.libs.misc.planimetricsCalculus)
//
//                implementation(projects.libs.misc.composeCanvas)
//                implementation(compose.desktop.currentOs)
//                implementation(compose.components.resources)
//            }
//        }
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
//    desktop {
//        application {
//            mainClass = "MainKt"
//        }
//    }
//    web
//}