kotlin {
    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
        commonMain {
            dependencies {
                implementation(projects.libs.main.enumerativeCombinatorics)
                implementation(libs.kotlinx.coroutines.core)
            }
        }
    }
}