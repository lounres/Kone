import dev.lounres.kone.buildSrc.dsl.konePlugins

plugins {
    alias(versions.plugins.kotlin.compose)
    alias(versions.plugins.compose.multiplatform)
}

konePlugins {
    +"suppliedTypes"
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
                api(versions.compose.multiplatform.foundation)
            }
        }
    }
}