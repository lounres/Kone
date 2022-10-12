kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.kone.core)
                api(projects.kone.algebraic)
                api(libs.kmath.core)
                implementation(projects.utils.mapOperations)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.kone.numberTheory)
                implementation(projects.utils.kotest)
            }
        }
    }
}