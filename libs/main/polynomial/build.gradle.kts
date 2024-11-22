kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.algebraic)
                api(versions.kmath.core)
                implementation(projects.libs.util.mapOperations)
                implementation(projects.libs.main.comparison) // TODO: Что-то транзитивность не сработала...
            }
        }
        commonTest {
            dependencies {
                implementation(projects.libs.main.numberTheory)
//                implementation(projects.libs.util.kotest)
            }
        }
    }
}