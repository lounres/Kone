plugins {
    `kotlin-dsl`
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        main {
            dependencies {
                compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.versions.kotlin.asProvider().get()}")
                compileOnly(projects.plugins.benchmarksRunners.gradleWrapper)
                compileOnly("org.openjdk.jmh:jmh-generator-bytecode:${versions.versions.jmh.get()}")
                implementation(projects.gradleLibs.jmhTaskPatch)
                api(versions.kotlinx.serialization.json)
            }
        }
        functionalTest {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
                runtimeOnly(projects.libs.misc.benchmarks)
                runtimeOnly(projects.plugins.benchmarksRunners.gradleWrapper)
            }
        }
    }
}