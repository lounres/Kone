kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("dev.lounres.kone.polynomial.DelicatePolynomialAPI")
            }
        }
        commonMain {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.comparison) // TODO: Что-то транзитивность не сработала...
                api(projects.libs.main.algebraic)
                api(projects.libs.main.collections)
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