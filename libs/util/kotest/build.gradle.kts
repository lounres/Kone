kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(versions.kotest.framework.engine)
                api(versions.kotest.framework.datatest)
                api(versions.kotest.assertions.core)
                api(versions.kotest.property)
                api(projects.libs.main.collections)
            }
        }
        commonTest {
            dependencies {

            }
        }
    }
}