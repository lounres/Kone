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

afterEvaluate {
    tasks.named("publishMavenPublicationToFunctionalTestRepository") {
        dependsOn(tasks.named("signPluginMavenPublication"))
    }
    
    tasks.named("publishPluginMavenPublicationToFunctionalTestRepository") {
        dependsOn(tasks.named("signMavenPublication"))
    }
}