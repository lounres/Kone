kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.mapOperations)
                implementation(projects.libs.util.misc)
            }
        }
    }
}