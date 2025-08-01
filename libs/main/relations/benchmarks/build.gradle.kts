plugins {
    alias(versions.plugins.kotlinx.serialization)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(versions.kotlinx.serialization.json)
            }
        }
    }
}

//tasks.register<JavaExec>("jvmMainCustomBenchmark") {
//    group = "benchmark"
//    val jvmMainBenchmarkJar by tasks.getting
//    classpath = files(jvmMainBenchmarkJar)
//    val reportsDirectory =
//    args(
////        "--lprof",
//        "--f=1",
//        "--bm=avgt",
//        "--wi=5",
//        "--i=3",
//        "--r=500ms",
//        "--w=500ms",
//        "--tu=ns",
////        "--prof=async:dir=${project.layout.buildDirectory.asFile.get().resolve("reports")}",
////        "--prof=cl",
////        "--prof=comp",
////        "--prof=gc",
////        "--prof=jfr",
////        "--prof=mempool",
////        "--prof=pauses",
////        "--prof=perf",
////        "--prof=perfasm",
////        "--prof=perfc2c",
////        "--prof=perfnorm",
////        "--prof=safepoints",
////        "--prof=stack"
//    )
//}