kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.algebraic)
                api(libs.kmath.core)
                implementation(projects.libs.util.mapOperations)
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.main.numberTheory)
                implementation(projects.libs.util.kotest)
            }
        }
    }
}