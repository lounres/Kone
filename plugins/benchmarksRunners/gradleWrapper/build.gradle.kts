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
                implementation(versions.testBalloon.framework.core)
            }
        }
    }
}

//dependencies {
//    companionLibrariesClasspath(projects.libs.misc.benchmarks) {
//        isTransitive = true
//    }
//    companionLibrariesClasspath(versions.kotlinx.serialization.json) {
//        isTransitive = true
//    }
//}

println(configurations.names)