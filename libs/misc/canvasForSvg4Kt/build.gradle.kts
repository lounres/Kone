import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"suppliedTypes"
    +"contexts"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.util.misc)
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
                api(projects.libs.main.collections)
                api(projects.libs.main.computationalGeometry)
                api(projects.libs.misc.canvas)
                api(versions.kotlinx.coroutines.core)
                api(versions.svg4kt)
            }
        }
    }
}