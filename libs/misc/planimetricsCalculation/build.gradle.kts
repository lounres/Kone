kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.core)
                api(projects.libs.main.relations)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.numberTheory)
                api(projects.libs.main.collections)
                api(projects.libs.main.polynomial)
                api(projects.libs.main.linearAlgebra)
            }
        }
        commonTest {
            dependencies {
//                implementation(projects.libs.util.kotest)
            }
        }
    }
}