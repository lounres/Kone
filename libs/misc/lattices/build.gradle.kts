kotlin {
    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("ContextReceivers")
            }
        }
        commonMain {
            dependencies {
                api(projects.libs.main.core)
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
    }
}