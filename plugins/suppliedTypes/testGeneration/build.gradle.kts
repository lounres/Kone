dependencies {
    val kotlinVersion = versions.versions.kotlin.asProvider().get()
    
    implementation("org.jetbrains.kotlin:kotlin-compiler:$kotlinVersion")
    
    runtimeOnly("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    runtimeOnly("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    runtimeOnly("org.jetbrains.kotlin:kotlin-annotations-jvm:$kotlinVersion")
    
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-compiler-internal-test-framework:$kotlinVersion")
    implementation("junit:junit:4.13.2")
    
    implementation(platform("org.junit:junit-bom:5.8.0"))
    implementation("org.junit.jupiter:junit-jupiter")
    implementation("org.junit.platform:junit-platform-commons")
    implementation("org.junit.platform:junit-platform-launcher")
    implementation("org.junit.platform:junit-platform-runner")
    implementation("org.junit.platform:junit-platform-suite-api")
    
    implementation(project.parent!!)
}

val testDataPathSourceSetString = "build/generated/paths/main"

sourceSets.main {
    java.srcDirs(testDataPathSourceSetString)
}

val writePaths by tasks.registering {
    val testDataPathSourceSet = projectDir.resolve(testDataPathSourceSetString)
    val testDataPath = project.parent!!.projectDir.resolve("src/test/data").absolutePath.replace("\\", "/")
    doFirst {
        testDataPathSourceSet.also { it.mkdirs() }.resolve("Paths.kt").writeText(
            """
                internal val testDataPath: String = "$testDataPath"
            """.trimIndent()
        )
    }
}

tasks.compileKotlin {
    dependsOn(writePaths)
}

tasks.register("generateTests", JavaExec::class) {
    dependsOn(tasks.compileKotlin)
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.lounres.kone.plugin.suppliedTypes.GenerateTestsKt")
}