import dev.lounres.kone.buildSrc.dsl.konePlugins

plugins {
    alias(versions.plugins.kotlinx.serialization)
    alias(versions.plugins.kotlin.compose)
    alias(versions.plugins.compose.multiplatform)
}

konePlugins {
    +"contexts"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
                implementation(versions.compose.multiplatform.foundation)
                api(projects.libs.main.contexts)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.linearAlgebra)
                api(projects.libs.main.computationalGeometry)
            }
        }
//        val composeMain by creating {
//            dependsOn(commonMain.get())
//            dependencies {
//                implementation(versions.kotlinx.serialization.json)
//                implementation(versions.compose.multiplatform.foundation)
//                api(projects.libs.main.contexts)
//                api(projects.libs.main.relations)
//                api(projects.libs.main.algebraic)
//                api(projects.libs.main.collections)
//                api(projects.libs.main.linearAlgebra)
//                api(projects.libs.main.computationalGeometry)
//            }
//        }
//        jvmMain { dependsOn(composeMain) }
//        webMain { dependsOn(composeMain) }
    }
}