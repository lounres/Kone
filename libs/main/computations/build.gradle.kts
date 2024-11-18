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