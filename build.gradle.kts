@file:Suppress("SuspiciousCollectionReassignment")

import io.kotest.framework.multiplatform.gradle.KotestMultiplatformCompilerGradlePlugin
import org.jetbrains.kotlin.gradle.dsl.*
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Warning
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper


@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    with(libs.plugins) {
        alias(kotlin.multiplatform) apply false
        alias(kotest.multiplatform) apply false
    }
}

val projectVersion = "0.0.0-dev-1"

allprojects {
    repositories {
        mavenCentral()
        maven("https://repo.kotlin.link")
//        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    version = projectVersion
    group = "com.lounres"

    val defaultProperties = mapOf(
        "artifactPrefix" to "",
        "aliasPrefix" to "",
    )
    for ((prop, value) in defaultProperties)
        if (!extra.has(prop)) extra[prop] = value
}

project(":libs:main").subprojects {
    extra["artifactPrefix"] = "kone."
    extra["aliasPrefix"] = ""
}
project(":libs:util").subprojects {
    extra["artifactPrefix"] = "kone.util."
    extra["aliasPrefix"] = "util-"
}

val jvmTargetApi = properties["jvmTarget"] as String

project(":libs").subprojects.flatMap { it.subprojects }.forEach {
    with(it) {
        apply<KotlinMultiplatformPluginWrapper>()
        apply<KotestMultiplatformCompilerGradlePlugin>()
        configure<KotlinMultiplatformExtension> {
            explicitApi = Warning

            jvm {
                compilations.all {
                    kotlinOptions {
                        jvmTarget = jvmTargetApi
                    }
                    compileKotlinTask.apply {
                        kotlinOptions {
                            jvmTarget = jvmTargetApi
                        }
                    }
                }
                testRuns.all {
                    executionTask {
                        useJUnitPlatform()
                    }
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
                    if (name.endsWith("test", ignoreCase = true)) {
                        dependencies {
                            with(rootProject.libs.kotest) {
                                implementation(framework.engine)
                                implementation(framework.datatest)
                                implementation(assertions.core)
                                implementation(property)
                            }
                        }
                    }
                }
                val commonTest by getting {
                    dependencies {
                        implementation(kotlin("test"))
                    }
                }
                val jvmTest by getting {
                    dependencies {
                        implementation(rootProject.libs.kotest.runner.junit5)
                    }
                }
            }
        }
        pluginManager.withPlugin("org.gradle.java") {
            configure<JavaPluginExtension> {
                targetCompatibility = JavaVersion.toVersion(jvmTargetApi)
            }
            tasks.withType<Test> {
                useJUnitPlatform()
            }
        }
    }
}