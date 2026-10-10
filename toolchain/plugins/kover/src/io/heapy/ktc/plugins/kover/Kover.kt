package io.heapy.ktc.plugins.kover

import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import javax.xml.XMLConstants
import javax.xml.parsers.DocumentBuilderFactory
import kotlinx.kover.features.jvm.ClassFilters
import kotlinx.kover.features.jvm.KoverLegacyFeatures
import org.jetbrains.amper.plugins.Classpath
import org.jetbrains.amper.plugins.CompilationArtifact
import org.jetbrains.amper.plugins.ExecutionAvoidance
import org.jetbrains.amper.plugins.Input
import org.jetbrains.amper.plugins.ModuleSources
import org.jetbrains.amper.plugins.Output
import org.jetbrains.amper.plugins.TaskAction
import org.w3c.dom.Element

/** Always performs a fresh instrumented test run before producing reports. */
@TaskAction(executionAvoidance = ExecutionAvoidance.Disabled)
fun koverReport(
    @Input(inferTaskDependency = false) projectDir: Path,
    moduleName: String,
    @Input classes: CompilationArtifact,
    @Input sources: ModuleSources,
    @Input agentClasspath: Classpath,
    @Output outputDir: Path,
    settings: KoverSettings,
) {
    require(!System.getProperty("os.name").startsWith("Windows")) {
        "ktc-kover currently supports macOS and Linux hosts."
    }
    require(settings.minimumLineCoverage in 0..100) { "minimumLineCoverage must be between 0 and 100" }
    require(settings.testTimeoutSeconds > 0) { "testTimeoutSeconds must be positive" }
    val wrapper = projectDir.resolve("kotlin")
    require(Files.isRegularFile(wrapper)) { "Coverage requires the project's pinned ./kotlin wrapper: $wrapper" }
    val agent = agentClasspath.resolvedFiles.singleOrNull { it.fileName.toString() == "kover-jvm-agent-0.9.11.jar" }
        ?: error("Expected the resolved kover-jvm-agent-0.9.11.jar")
    Files.createDirectories(outputDir)
    val binary = outputDir.resolve("coverage.ic")
    val xml = outputDir.resolve("coverage.xml")
    val html = outputDir.resolve("html")
    // Never let a failed or empty test run reuse coverage from a previous invocation.
    Files.deleteIfExists(binary)
    Files.deleteIfExists(xml)
    removeTree(html)
    val arguments = outputDir.resolve("agent.args")
    Files.writeString(arguments, agentArguments(binary, settings.includes, settings.excludes))
    val childBuild = outputDir.resolve("test-build")
    removeTree(childBuild.resolve("reports"))
    val command = mutableListOf(
        "sh", wrapper.toString(), "test", "--project-dir", projectDir.toString(),
        "--build-dir", childBuild.toString(), "-m", moduleName, "--platform", "jvm",
        "--jvm-args", quoteJvmArgument("-javaagent:$agent=file:$arguments"),
    )
    settings.includeTags.forEach { command += listOf("--include-tag", it) }
    settings.excludeTags.forEach { command += listOf("--exclude-tag", it) }
    println("Running instrumented JVM tests for $moduleName")
    runTests(command, projectDir, settings.testTimeoutSeconds)
    require(hasExecutedTests(childBuild.resolve("reports"))) {
        "No JVM tests executed. Check the selected module and tag filters."
    }
    require(Files.isRegularFile(binary) && Files.size(binary) > 0) {
        "No coverage was collected. The selected module must have JVM tests; check tag filters."
    }
    val filters = ClassFilters(
        settings.includes.toSet(), settings.excludes.toSet(), emptySet(), emptySet(), emptySet(), emptySet(),
    )
    val classDirs = listOf(classes.artifact.toFile())
    val sourceDirs = sources.sourceDirectories.filter(Files::isDirectory).map(Path::toFile)
    // Kover's report engine uses mutable global state. Serialize both formats in this classloader.
    synchronized(KoverLegacyFeatures) {
        KoverLegacyFeatures.generateXmlReport(xml.toFile(), listOf(binary.toFile()), classDirs, sourceDirs, moduleName, filters)
        KoverLegacyFeatures.generateHtmlReport(html.toFile(), "UTF-8", listOf(binary.toFile()), classDirs, sourceDirs, moduleName, filters)
    }
    val coverage = readLineCoverage(xml)
    require(coverage.total > 0) { "No executable lines remain in the coverage report; check class filters." }
    println("Kover reports: $xml and ${html.resolve("index.html")}")
}

@TaskAction
fun koverCheck(@Input reportDir: Path, minimumLineCoverage: Int) {
    require(minimumLineCoverage in 0..100) { "minimumLineCoverage must be between 0 and 100" }
    val coverage = readLineCoverage(reportDir.resolve("coverage.xml"))
    require(coverage.total > 0) { "No executable lines in coverage report" }
    check(coverage.meets(minimumLineCoverage)) {
        "Line coverage ${coverage.covered}/${coverage.total} is below $minimumLineCoverage%. See ${reportDir.resolve("html/index.html")}"
    }
    println("Line coverage ${coverage.covered}/${coverage.total} meets $minimumLineCoverage%")
}

internal fun agentArguments(report: Path, includes: List<String>, excludes: List<String>): String = buildString {
    val reportPath = report.toAbsolutePath().toString()
    require('\n' !in reportPath && '\r' !in reportPath) { "Coverage paths cannot contain line breaks" }
    appendLine("report.file=$reportPath")
    appendLine("report.append=false")
    for ((key, patterns) in listOf("include" to includes, "exclude" to excludes)) {
        patterns.forEach { pattern ->
            require(pattern.isNotBlank() && '\n' !in pattern && '\r' !in pattern) { "Invalid coverage class pattern" }
            appendLine("$key=$pattern")
        }
    }
}

internal fun quoteJvmArgument(value: String): String {
    require('\n' !in value && '\r' !in value) {
        "Coverage agent paths cannot contain line breaks"
    }
    return "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
}

internal data class LineCoverage(val covered: Long, val missed: Long) {
    val total: Long get() = covered + missed
    fun meets(minimum: Int): Boolean = total > 0 && covered.toBigInteger() * 100.toBigInteger() >= total.toBigInteger() * minimum.toBigInteger()
}

internal fun readLineCoverage(xml: Path): LineCoverage {
    val root = readXml(xml)
    require(root.tagName == "report") { "Invalid Kover XML report" }
    val counters = (0 until root.childNodes.length).mapNotNull { root.childNodes.item(it) as? Element }
        .filter { it.tagName == "counter" && it.getAttribute("type") == "LINE" }
    require(counters.size == 1) { "Expected one aggregate LINE counter in Kover XML report" }
    val counter = counters.single()
    val covered = counter.getAttribute("covered").toLong()
    val missed = counter.getAttribute("missed").toLong()
    require(covered >= 0 && missed >= 0 && covered <= Long.MAX_VALUE - missed) { "Invalid line coverage counts" }
    return LineCoverage(covered, missed)
}

internal fun hasExecutedTests(reports: Path): Boolean {
    if (!Files.isDirectory(reports)) return false
    return Files.walk(reports).use { paths ->
        paths.filter { Files.isRegularFile(it) && it.fileName.toString().startsWith("TEST-") && it.toString().endsWith(".xml") }
            .anyMatch { path ->
                val cases = readXml(path).getElementsByTagName("testcase")
                (0 until cases.length).any { (cases.item(it) as Element).getElementsByTagName("skipped").length == 0 }
            }
    }
}

private fun readXml(xml: Path): Element {
    val factory = DocumentBuilderFactory.newInstance().apply {
        setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true)
        setFeature("http://xml.org/sax/features/external-general-entities", false)
        setFeature("http://xml.org/sax/features/external-parameter-entities", false)
        setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "")
        setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "")
        isXIncludeAware = false
        isExpandEntityReferences = false
    }
    return Files.newInputStream(xml).use { factory.newDocumentBuilder().parse(it).documentElement }
}

private fun runTests(command: List<String>, projectDir: Path, timeoutSeconds: Int) {
    val process = ProcessBuilder(command).directory(projectDir.toFile()).inheritIO().start()
    try {
        check(process.waitFor(timeoutSeconds.toLong(), TimeUnit.SECONDS)) { "Instrumented tests timed out after $timeoutSeconds seconds" }
        check(process.exitValue() == 0) { "Instrumented tests failed (exit ${process.exitValue()}); no coverage report generated" }
    } finally {
        if (process.isAlive) {
            process.descendants().use { children -> children.forEach { it.destroyForcibly() } }
            process.destroyForcibly()
        }
    }
}

private fun removeTree(directory: Path) {
    if (!Files.exists(directory)) return
    Files.walk(directory).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach(Files::delete) }
}
