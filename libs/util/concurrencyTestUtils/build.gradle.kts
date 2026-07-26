kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(versions.testBalloon.framework.core)
                api(versions.kotlinx.lincheck)
                api(versions.fray.junit)
            }
        }
    }
}