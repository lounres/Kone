kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(versions.kotlinx.coroutines.core)
//                api(projects.libs.main.suppliedTypes)
//                api(projects.libs.main.typeSafeRegistry)
//                api(projects.libs.main.contexts)
//                api(projects.libs.main.maybe)
//                api(projects.libs.main.relations)
//                implementation(projects.libs.main.algebraic)
//                api(projects.libs.main.collections)
            }
        }
        commonTest {
            dependencies {
//                implementation(projects.libs.main.enumerativeCombinatorics)
                implementation(projects.libs.main.concurrentCollections)
            }
        }
        jvmTest {
            dependencies {
                implementation(versions.kotlinx.lincheck)
            }
        }
    }
}