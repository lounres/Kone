dependencies {
    val kotlinVersion = versions.versions.kotlin.asProvider().get()
    
    "org.jetbrains.kotlin:kotlin-compiler:$kotlinVersion".let {
        compileOnly(it)
        testImplementation(it)
    }
    
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-script-runtime:$kotlinVersion")
    testRuntimeOnly("org.jetbrains.kotlin:kotlin-annotations-jvm:$kotlinVersion")
    
    testImplementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-internal-test-framework:$kotlinVersion")
//    testImplementation("junit:junit:4.13.2")
    
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.platform:junit-platform-commons")
    testImplementation("org.junit.platform:junit-platform-launcher")
//    testImplementation("org.junit.platform:junit-platform-runner")
    testImplementation("org.junit.platform:junit-platform-suite-api")
    
    testImplementation(project.childProjects["testGeneration"]!!)
}

val testGenerationTaskFullName = "${project.path}:testGeneration:generateTests"

tasks.compileTestKotlin {
    dependsOn(testGenerationTaskFullName)
}

val dependencyJarTaskFullName = "${projects.libs.main.suppliedTypes.path}:jvmJar"

tasks.test {
    dependsOn(dependencyJarTaskFullName)
    useJUnitPlatform()
    
    val testRuntimeClasspathFiles by lazy {
        project
            .configurations
            .testRuntimeClasspath.get()
            .files
    }
    
    doFirst {
        fun setLibraryProperty(propName: String, jarName: String) {
            val path = testRuntimeClasspathFiles
                .find { """$jarName-\d.*jar""".toRegex().matches(it.name) }
                ?.absolutePath
                ?: return
            systemProperty(propName, path)
        }
        
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
        setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
    }
}