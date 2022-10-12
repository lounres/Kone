kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kone.core)
                api(projects.kone.algebraic)
                api(projects.kone.numberTheory)
                api(projects.kone.polynomial)
                api(projects.kone.linearAlgebra)
                implementation(projects.utils.mapOperations)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.utils.kotest)
            }
        }
    }
}