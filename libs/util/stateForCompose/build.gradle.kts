kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
    
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.state)
                api(versions.compose.multiplatform.runtime)
            }
        }
    }
}