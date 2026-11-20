import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        sourceSetTrees(KotlinSourceSetTree.main, KotlinSourceSetTree.test)
        common {
            group("kotlinxBenchmark") {
                withJvm()
                group("web")
                withWasmWasi()
                group("native")
            }
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.misc.benchmarksKotlinxBenchmarkSupport)
                api(projects.plugins.benchmarksRunners.runtime)
            }
        }
    }
}