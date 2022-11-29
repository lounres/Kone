kotlin {
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.libs.main.core)
                api(projects.libs.main.algebraic)
                api(libs.kmath.core)
                implementation(projects.libs.util.mapOperations)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(projects.libs.main.numberTheory)
                implementation(projects.libs.util.kotest)
            }
        }
    }
}

// TODO: Uncomment to let jvmTest run and get errors
//val mingwX64Test: Task by tasks.getting {
//    enabled = false
//}