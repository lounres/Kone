kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.collections)
                implementation(projects.libs.main.algebraic)
                implementation(projects.libs.main.order)
            }
        }
    }
}