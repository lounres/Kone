plugins {
    kotlin("multiplatform")
    id("dev.lounres.kone.plugins.benchmarksRunners.gradleWrapper")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
//            "-Xklib-duplicated-unique-name-strategy=allow-all-with-warning",
//            "-Xvalue-classes",
//            "-Xcontract-syntax-v2",
            "-Xexpect-actual-classes",
            "-Xconsistent-data-class-copy-visibility",
            "-Xcontext-sensitive-resolution",
            "-Xreturn-value-checker=full",
            "-Xlocal-type-aliases",
            "-Xname-based-destructuring=complete",
            "-Xcollection-literals",
            "-Xcompanion-blocks-and-extensions",
//            "-Xfull-value-classes",
            
            // TODO: Waiting for JDK 28
//            "-Xvalhalla-value-classes",
//            "-Xjvm-enable-preview",
            
            // Kone compiler plugins order
            "-Xcompiler-plugin-order=dev.lounres.kone.plugins.suppliedTypes>dev.lounres.kone.plugins.contextsKeys",
        )
    }
    
    jvm {
        koneBenchmarksRunners {
            register("commonRunner") {
                runnerName = "common.doIt"
            }
            register("jvmRunner") {
                runnerName = "jvm.doIt"
            }
        }
    }
    
    js {
        koneBenchmarksRunners {
            register("commonRunner") {
                runnerName = "common.doIt"
            }
            register("webRunner") {
                runnerName = "web.doIt"
            }
            register("jsRunner") {
                runnerName = "js.doIt"
            }
        }
        nodejs()
        binaries.executable()
        binaries.library()
    }
    
    wasmJs {
        koneBenchmarksRunners {
            register("commonRunner") {
                runnerName = "common.doIt"
            }
            register("webRunner") {
                runnerName = "web.doIt"
            }
            register("wasmJsRunner") {
                runnerName = "wasmJs.doIt"
            }
        }
        nodejs()
        nodejs()
        binaries.executable()
        binaries.library()
    }
}