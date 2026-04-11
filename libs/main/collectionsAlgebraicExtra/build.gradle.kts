kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
            }
        }
    }
}