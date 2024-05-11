plugins {
    id("kotlinx-atomicfu")
}

val kotlinxAtomicfuVersion: String by properties

atomicfu {
    dependenciesVersion = kotlinxAtomicfuVersion
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
    }
}