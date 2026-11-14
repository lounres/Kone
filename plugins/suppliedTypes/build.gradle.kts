kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.mapOperations)
            }
        }
    }
}