kotlin {
    sourceSets {
        all {
            languageSettings {
                languageVersion = "1.9"
                enableLanguageFeature("ContextReceivers")
            }
        }
        commonMain {
            dependencies {
                api(projects.libs.main.algebraic)
                api(projects.libs.main.order)
                api(projects.libs.main.hooks)
            }
        }
    }
}