kotlin {
    sourceSets {
        main {
            dependencies {
                compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.versions.kotlin.asProvider().get()}")
                compileOnly("org.openjdk.jmh:jmh-generator-bytecode:${versions.versions.jmh.get()}")
            }
        }
    }
}