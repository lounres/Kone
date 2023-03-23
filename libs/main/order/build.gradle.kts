kotlin {
    sourceSets {
        all {
            languageSettings {
//                languageVersion = "1.9"
                enableLanguageFeature("ContextReceivers")
            }
        }
        commonMain {
            dependencies {
                api(projects.libs.main.computationalContext)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}