plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(project.extra["pluginRuntimeJvmTargetVersion"] as String)
        vendor = JvmVendorSpec.matching(project.extra["pluginRuntimeJvmVendor"] as String)
    }
    
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.maybe)
                
                api(versions.kotlinx.serialization.core)
            }
        }
    }
}