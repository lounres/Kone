package io.heapy.ktc.plugins.dokka

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.amper.plugins.Classpath
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.ModuleSources
import org.jetbrains.amper.plugins.Output
import org.jetbrains.amper.plugins.TaskAction
import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlin.io.path.isRegularFile
import kotlin.io.path.writeText

/** Runs Dokka in its own JVM so its compiler dependencies do not affect plugin execution. */
@OptIn(ExperimentalPathApi::class)
@TaskAction
public fun generateHtml(
    @Input settings: DokkaSettings,
    moduleName: String,
    @Input sources: ModuleSources,
    @Input compileClasspath: Classpath,
    @Input dokkaClasspath: Classpath,
    @Input htmlClasspath: Classpath,
    @Output outputDirectory: Path,
) {
    require(settings.jdkVersion >= 8) { "dokka.jdkVersion must be at least 8" }
    require(settings.documentedVisibilities.isNotEmpty()) { "Choose at least one documented visibility" }
    settings.includes.forEach { require(it.isRegularFile()) { "Dokka include file does not exist: $it" } }
    val cliJar = dokkaClasspath.resolvedFiles.singleOrNull()
        ?: error("Expected the standalone Dokka CLI fat JAR, got ${dokkaClasspath.resolvedFiles}")
    outputDirectory.createDirectories()
    val site = outputDirectory.resolve("html")
    site.deleteRecursively()
    val configuration = buildJsonObject {
        put("moduleName", moduleName)
        put("outputDir", site.toString())
        put("offlineMode", settings.offlineMode)
        put("failOnWarning", settings.failOnWarning)
        put("suppressInheritedMembers", settings.suppressInheritedMembers)
        put("pluginsClasspath", strings(htmlClasspath.resolvedFiles.map(Path::toString)))
        put("sourceSets", buildJsonArray {
            add(buildJsonObject {
                put("sourceSetID", buildJsonObject {
                    put("scopeId", moduleName)
                    put("sourceSetName", "main")
                })
                put("displayName", "JVM")
                put("analysisPlatform", "jvm")
                put("sourceRoots", strings(sources.sourceDirectories.map(Path::toString)))
                put("classpath", strings(compileClasspath.resolvedFiles.map(Path::toString)))
                put("jdkVersion", settings.jdkVersion)
                put("documentedVisibilities", strings(settings.documentedVisibilities.map(Visibility::name)))
                put("reportUndocumented", settings.reportUndocumented)
                put("skipDeprecated", settings.skipDeprecated)
                put("includes", strings(settings.includes.map(Path::toString)))
            })
        })
    }
    val configFile = outputDirectory.resolve("dokka.json")
    configFile.writeText(Json { prettyPrint = true }.encodeToString(JsonElement.serializer(), configuration))
    val java = Path.of(System.getProperty("java.home"), "bin", if (isWindows()) "java.exe" else "java")
    val process = ProcessBuilder(java.toString(), "-jar", cliJar.toString(), configFile.toString())
        .redirectErrorStream(true).start()
    val reader = thread(name = "dokka-output", isDaemon = true) {
        process.inputStream.bufferedReader().useLines { lines -> lines.forEach(::println) }
    }
    try {
        val exit = process.waitFor()
        reader.join()
        check(exit == 0) { "Dokka failed with exit code $exit. See its diagnostics above." }
        check(site.resolve("index.html").isRegularFile()) { "Dokka did not generate $site/index.html" }
        println("Dokka HTML: ${site.resolve("index.html")}")
    } finally {
        if (process.isAlive) process.destroyForcibly()
    }
}

private fun strings(values: List<String>): JsonArray = JsonArray(values.map(::JsonPrimitive))
private fun isWindows(): Boolean = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
