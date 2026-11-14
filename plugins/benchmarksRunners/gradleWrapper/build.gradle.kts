plugins {
    `kotlin-dsl`
}

kotlin {
    sourceSets {
        main {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.versions.kotlin.asProvider().get()}")
                api(versions.kotlinx.serialization.json)
            }
        }
        functionalTest {
            dependencies {
                runtimeOnly(projects.libs.misc.benchmarks)
            }
        }
    }
}