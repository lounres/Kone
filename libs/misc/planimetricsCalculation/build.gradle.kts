kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.algebraic)
                api(projects.libs.main.numberTheory)
                api(projects.libs.main.polynomial)
                api(projects.libs.main.linearAlgebra)
                implementation(projects.libs.util.mapOperations)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.libs.util.kotest)
            }
        }
    }
}