import dev.lounres.kone.buildSrc.dsl.konePlugins

plugins {
    alias(versions.plugins.kotlinx.serialization)
}

konePlugins {
    +"suppliedTypes"
    +"contexts"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
                api(projects.libs.main.contexts)
                api(projects.libs.main.maybe)
                implementation(projects.libs.main.annotations)
                api(projects.libs.main.relations)
                implementation(versions.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}