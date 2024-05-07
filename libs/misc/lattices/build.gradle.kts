kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(projects.libs.main.enumerativeCombinatorics)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}