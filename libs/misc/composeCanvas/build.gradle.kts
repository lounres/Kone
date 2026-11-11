import dev.lounres.kone.buildSrc.dsl.konePlugins

plugins {
    alias(versions.plugins.kotlinx.serialization)
}

konePlugins {
    +"contexts"
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.linearAlgebra)
                api(projects.libs.main.computationalGeometry)
            }
        }
        composeMain {
            dependencies {
                implementation(versions.compose.multiplatform.foundation)
            }
        }
    }
}