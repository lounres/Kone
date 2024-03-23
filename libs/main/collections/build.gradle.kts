kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.comparison)
                implementation(projects.libs.main.algebraic)
            }
        }
    }
}