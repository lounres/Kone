import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"suppliedTypes"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.relations)
                api(projects.libs.main.collections)
                api(projects.libs.main.algebraic)
            }
        }
    }
}