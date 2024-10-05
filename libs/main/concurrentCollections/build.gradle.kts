plugins {
    alias(libs.plugins.kotlinx.atomicfu)
}

atomicfu {
    transformJvm = true
    jvmVariant = "VH"
    transformJs = true
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        jvmTest {
            dependencies {
                implementation(libs.kotlinx.lincheck)
            }
        }
    }
}