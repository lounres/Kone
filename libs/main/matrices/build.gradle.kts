kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.algebraicExtra)
                api(projects.libs.main.collections)
                api(projects.libs.main.multidimensionalCollections)
                api(projects.libs.main.multidimensionalCollectionsAlgebraicExtra)
                api(projects.libs.main.linearAlgebra)
            }
        }
    }
}