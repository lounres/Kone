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
//    args("--lprof", "--f=1", "--bm=avgt", "--wi=5", "--i=3", "--r=500ms", "--w=500ms", "--tu=ns", "--prof=gc", /*"--prof=",*/ /*"--prof=xperfasm",*/ "--prof=stack")
//}