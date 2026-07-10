import dev.lounres.kone.buildSrc.dsl.konePlugins

konePlugins {
    +"suppliedTypes"
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(project.extra["pluginRuntimeJvmTargetVersion"] as String)
        vendor = JvmVendorSpec.matching(project.extra["pluginRuntimeJvmVendor"] as String)
    }
    
    sourceSets {
        commonMain {
            dependencies {
                api(projects.libs.main.suppliedTypes)
                api(projects.libs.main.typeSafeRegistry)
            }
        }
    }
}