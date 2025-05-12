kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(versions.kotlinx.coroutines.core)
            }
        }
    }
}