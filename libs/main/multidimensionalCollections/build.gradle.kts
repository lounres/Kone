kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(projects.libs.main.comparison)
                implementation(projects.libs.main.algebraic)
            }
        }
    }
}