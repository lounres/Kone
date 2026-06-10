kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                compileOnly(versions.kotlin.compiler)
                compileOnly(versions.kotlin.reflect)
            }
        }
    }
}