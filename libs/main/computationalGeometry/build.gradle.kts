kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.algebraic)
                api(projects.libs.main.order)
                api(projects.libs.main.multidimensionalCollections)
                api(projects.libs.main.linearAlgebra)
                api(projects.libs.main.hooks)
            }
        }
    }
}