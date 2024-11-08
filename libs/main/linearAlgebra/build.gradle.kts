kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.comparison)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
                api(projects.libs.main.multidimensionalCollections)
                implementation(projects.libs.main.enumerativeCombinatorics)
            }
        }
        commonTest {
            dependencies {
//                implementation(projects.libs.util.kotest)
            }
        }
    }
}