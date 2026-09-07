package dev.lounres.kone.buildLogic.kotlinCompilerPluginTestSetup

import org.jetbrains.amper.plugins.Classpath
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.TaskAction
import kotlin.io.path.absolute
import kotlin.io.path.name


@TaskAction
fun setupKotlinCompilerPluginTestSystemProperties(
    @Input classpath: Classpath,
) {
    fun setLibraryProperty(propertyName: String, jarName: String) {
        val jarRegex = """$jarName-\d.*jar""".toRegex()
        val path = classpath.resolvedFiles
            .find { jarRegex matches it.name }
            ?.absolute()
            ?: return
//        TODO: Add system property registration
//        systemProperty(propName, path)
    }
    
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib", "kotlin-stdlib")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-stdlib-jdk8", "kotlin-stdlib-jdk8")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-reflect", "kotlin-reflect")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-test", "kotlin-test")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-script-runtime", "kotlin-script-runtime")
    setLibraryProperty("org.jetbrains.kotlin.test.kotlin-annotations-jvm", "kotlin-annotations-jvm")
}