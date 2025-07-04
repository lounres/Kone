kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.automata)
                api(versions.kone.collections)
                api(versions.kotlinx.coroutines.core)
            }
        }
    }
}