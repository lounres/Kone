plugins {
    alias(libs.plugins.kotlinx.atomicfu)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.collections)
                api(projects.libs.main.computations)
                implementation(libs.kotlinx.coroutines.core)
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
    }
}