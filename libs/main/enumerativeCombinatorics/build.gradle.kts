kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        all {
            languageSettings {
                enableLanguageFeature("RangeUntilOperator")
            }
        }
        val commonMain by getting {
            dependencies {
                implementation(projects.libs.util.collectionOperations)
            }
        }
    }
}