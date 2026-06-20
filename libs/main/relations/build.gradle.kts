import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"suppliedTypes"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
            }
        }
    }
}