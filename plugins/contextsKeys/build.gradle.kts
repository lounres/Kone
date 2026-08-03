kotlin {
    sourceSets {
        main {
            dependencies {
                api(projects.plugins.suppliedTypes)
                implementation(projects.libs.util.mapOperations)
                implementation(projects.libs.util.misc)
            }
        }
    }
}