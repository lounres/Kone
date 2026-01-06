kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.collections)
                api(versions.kotlinx.coroutines.core)
            }
        }
    }
}