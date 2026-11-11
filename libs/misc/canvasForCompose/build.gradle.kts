import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"suppliedTypes"
    +"contexts"
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
    
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
            }
        }
        composeMain {
            dependencies {
                api(versions.compose.multiplatform.foundation)
            }
        }
    }
}