plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kone.benchmarksRunners)
    alias(libs.plugins.kone.benchmarksRunnersKotlinxBenchmarkSupport)
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
//        koneBenchmarksRunners {
//            register("commonRunner") {
//                runnerName = "common.doIt"
//            }
//            register("jvmRunner") {
//                runnerName = "jvm.doIt"
//            }
//        }
    }
    
    js {
//        koneBenchmarksRunners {
//            register("commonRunner") {
//                runnerName = "common.doIt"
//            }
//            register("webRunner") {
//                runnerName = "web.doIt"
//            }
//            register("jsRunner") {
//                runnerName = "js.doIt"
//            }
//        }
        browser()
        nodejs()
        binaries.executable()
        binaries.library()
    }
    
    wasmJs {
//        koneBenchmarksRunners {
//            register("commonRunner") {
//                runnerName = "common.doIt"
//            }
//            register("webRunner") {
//                runnerName = "web.doIt"
//            }
//            register("wasmJsRunner") {
//                runnerName = "wasmJs.doIt"
//            }
//        }
        browser()
        nodejs()
        d8()
        binaries.executable()
        binaries.library()
    }
}

benchmark {
    targets {
        register("jvm")
    }
    configurations {
        register("custom") {
            warmups = 20
            iterations = 10
            iterationTime = 3
            iterationTimeUnit = "SECONDS"
        }
    }
}