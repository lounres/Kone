kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.libs.main.algebraic)
            }
        }
    }
}

tasks.register<JavaExec>("jvmMainCustomBenchmark") {
    group = "benchmark"
    val jvmMainBenchmarkJar by tasks.getting
    classpath = files(jvmMainBenchmarkJar)
    args("--f=1", "--bm=avgt", "--wi=5", "--i=3", "--r=500ms", "--w=500ms", "--tu=ns", "--prof=gc", /*"--prof=xperfasm",*/ "--prof=stack")
}