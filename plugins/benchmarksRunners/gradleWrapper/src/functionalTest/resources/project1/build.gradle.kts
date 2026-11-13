plugins {
    kotlin("multiplatform")
    id("dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper")
}

kotlin {
    jvm {
        koneBenchmarksRunners {
            register("testRunner") {
                runnerName = "foo.bar.doIt"
            }
        }
    }
}