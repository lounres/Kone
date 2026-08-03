import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"suppliedTypes"
    +"contexts"
    +"contextsKeys"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.collectionsAlgebraicExtra)
                api(projects.libs.main.multidimensionalCollections)
            }
        }
    }
}