import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(kotlin.multiplatform)
        alias(kotest.multiplatform)
    }
}

kotlin {
    explicitApi = Warning

    jvm {
        compilations.all {
            kotlinOptions {
                jvmTarget = properties["jvmTarget"] as String
            }
        }

        testRuns["test"].executionTask {
            useJUnitPlatform()
        }
    }

    js(IR) {
        browser()
        nodejs()
    }

    linuxX64()
    mingwX64()
    macosX64()

    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        all {
            languageSettings {
                progressiveMode = true
                optIn("kotlin.contracts.ExperimentalContracts")
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotest.framework.engine)
                implementation(libs.kotest.framework.datatest)
                implementation(libs.kotest.assertions.core)
                implementation(libs.kotest.property)
            }
        }

        val jvmTest by getting {
            dependencies {
                implementation(libs.kotest.runner.junit5)
            }
        }
    }
}