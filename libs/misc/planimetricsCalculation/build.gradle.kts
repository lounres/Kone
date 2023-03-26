kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.numberTheory)
                api(projects.libs.main.polynomial)
                api(projects.libs.main.linearAlgebra)
                implementation(projects.libs.util.mapOperations)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.util.kotest)
            }
        }
    }
}