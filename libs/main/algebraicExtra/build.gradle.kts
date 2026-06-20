import dev.lounres.kone.buildSrc.dsl.konePlugins

plugins {
    alias(versions.plugins.kotlinx.serialization)
}

konePlugins {
    +"suppliedTypes"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
                api(projects.libs.main.annotations)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.linearAlgebra)
                implementation(projects.libs.main.collections)
                implementation(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
            }
        }
    }
}