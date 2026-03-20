kotlin {
    sourceSets {
        jvmMain {
            dependencies {
                api(versions.kotlin.compiler)
                api(versions.kotlin.reflect)
                api(versions.kotlin.compiler.internal.test.framework)
                
//                api(project.dependencies.platform(versions.junit.bom))
                api(versions.junit.jupiter)
                api(versions.junit.platform.commons)
                api(versions.junit.platform.launcher)
//                api(versions.junit.platform.runner)
                api(versions.junit.platform.suite.api)
            }
        }
    }
}