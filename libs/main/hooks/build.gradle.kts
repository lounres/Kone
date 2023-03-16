kotlin {
    sourceSets {
        all {
            languageSettings {
                languageVersion = "2.0"
            }
        }
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
            }
        }
    }
}