import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

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
                api(projects.plugins.benchmarksDeclarative.runtime)
            }
        }
    }
}