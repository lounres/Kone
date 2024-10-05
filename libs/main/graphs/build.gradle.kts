kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.collections)
                api(projects.libs.main.algebraic)
            }
        }
    }
}