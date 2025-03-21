kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("dev.lounres.kone.polynomial.DelicatePolynomialAPI")
            }
        }
        commonMain {
            dependencies {
                implementation(projects.libs.util.misc)
                api(projects.libs.main.contexts)
                implementation(projects.libs.main.annotations)
                api(projects.libs.main.relations) // TODO: Что-то транзитивность не сработала...
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