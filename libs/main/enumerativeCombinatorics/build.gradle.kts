kotlin {
    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("RangeUntilOperator")
            }
        }
        commonMain {
            dependencies {
                implementation(projects.libs.util.collectionOperations)
            }
        }
    }
}