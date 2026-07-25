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
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
                api(projects.libs.main.relations)
                api(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
    }
}