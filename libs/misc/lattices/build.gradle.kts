kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.collections)
                api(projects.libs.main.computations)
                implementation(versions.kotlinx.coroutines.core)
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
    }
}