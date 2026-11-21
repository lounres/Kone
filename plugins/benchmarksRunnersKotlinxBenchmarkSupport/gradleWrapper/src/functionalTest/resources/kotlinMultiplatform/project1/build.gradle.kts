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
    
    jvm()
    
    js {
        browser()
        nodejs()
        binaries.executable()
        binaries.library()
    }
    
    wasmJs {
        browser()
        nodejs()
        d8()
        binaries.executable()
        binaries.library()
    }
    
    linuxX64()
    mingwX64()
//    macosX64()
//    macosArm64()
}

benchmark {
    targets {
        register("jvm")
        register("js")
        register("wasmJs")
        register("linuxX64")
        register("mingwX64")
//        register("macosX64")
//        register("macosArm64")
    }
    configurations {
        register("custom") {
            warmups = 3
            iterations = 3
            iterationTime = 1
            iterationTimeUnit = "SECONDS"
        }
    }
}