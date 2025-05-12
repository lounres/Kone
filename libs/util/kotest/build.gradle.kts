kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotest.framework.engine)
                implementation(versions.kotest.framework.datatest)
                implementation(versions.kotest.assertions.core)
                implementation(versions.kotest.property)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}