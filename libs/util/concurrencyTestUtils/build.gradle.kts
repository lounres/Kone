kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(versions.testBaloon.framework.core)
                api(versions.kotlinx.lincheck)
                api(versions.fray.junit)
            }
        }
    }
}