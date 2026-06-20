import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +projects.plugins.suppliedTypes
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
            }
        }
    }
}