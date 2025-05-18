import org.jetbrains.gradle.ext.settings
import org.jetbrains.gradle.ext.taskTriggers

plugins {
    id("org.jetbrains.gradle.plugin.idea-ext")
}

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

val testDataPathSourceSet = "build/generated/testDataPath/main"

sourceSets.main {
    java.srcDirs(testDataPathSourceSet)
}

val writeTestDataPath by tasks.registering {
    doFirst {
        projectDir.resolve(testDataPathSourceSet).also { it.mkdirs() }.resolve("TestDataPath.kt").writeText(
            """
                val testDataPath: String = "${project.parent!!.projectDir.resolve("src/test/data").absolutePath.replace("\\", "/")}"
            """.trimIndent()
        )
    }
}

tasks.compileKotlin {
    dependsOn(writeTestDataPath)
}

rootProject.idea.project.settings {
    taskTriggers {
        afterSync(writeTestDataPath)
    }
}

tasks.register("generateTests", JavaExec::class) {
    dependsOn(tasks.compileKotlin)
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("dev.lounres.kone.plugin.suppliedTypes.GenerateTestsKt")
}