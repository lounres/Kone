kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.reflect)
                implementation(projects.libs.main.numberTheory)
                implementation(projects.libs.main.order)
                implementation(projects.libs.main.polynomial)
            }
        }
    }
}