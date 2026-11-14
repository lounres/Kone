kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.plugins.suppliedTypes)
                implementation(projects.libs.util.mapOperations)
                implementation(projects.libs.util.misc)
            }
        }
    }
}