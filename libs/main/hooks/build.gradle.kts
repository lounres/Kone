kotlin {
    sourceSets {
        all {
            languageSettings {
//                languageVersion = "1.9"
            }
        }
        commonMain {
            dependencies {
                api(projects.libs.main.collections)
            }
        }
    }
}