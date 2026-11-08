import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

kotlin {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        sourceSetTrees(KotlinSourceSetTree.main, KotlinSourceSetTree.test)
        common {
            group("benchmark") {
                withJvm()
                group("web")
                withWasmWasi()
                group("native")
            }
        }
    }
    
    sourceSets {
        named("benchmarkMain") {
            dependencies {
                implementation(versions.kotlinx.benchmark.runtime)
                api(versions.kotlinx.serialization.core)
                implementation(versions.kotlinx.serialization.json)
            }
        }
        jvmMain {
            dependencies {
                implementation("org.openjdk.jmh:jmh-core:${versions.versions.jmh.get()}")
            }
        }
    }
}