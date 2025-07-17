kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                implementation(projects.libs.main.automata)
                api(projects.libs.main.collections)
                api(versions.kotlinx.coroutines.core)
            }
        }
    }
}