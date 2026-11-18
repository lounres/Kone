/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
import kotlinx.benchmark.native.runBenchmarks
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKString
import platform.posix.EOF
import platform.posix.S_IFREG
import platform.posix.closedir
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.posix.fputs
import platform.posix.opendir
import platform.posix.readdir
import platform.posix.stat
import kotlin.uuid.Uuid


@OptIn(ExperimentalForeignApi::class)
internal fun String.writeFile(text: String) {
    // Preserve line endings: Windows text mode would turn CSV's CRLF into CRCRLF.
    val file = fopen(this, "wb")
    try {
        if (fputs(text, file) == EOF) throw Error("File '$this' write error")
    } finally {
        fclose(file)
    }
}

@OptIn(ExperimentalForeignApi::class)
internal fun String.readFile(): String = buildString {
    val file = fopen(this@readFile, "rb")
    try {
        memScoped {
            while (true) {
                val bufferLength = 64 * 1024
                val buffer = allocArray<ByteVar>(bufferLength)
                val line = fgets(buffer, bufferLength, file)?.toKString() // newline symbol is included
                if (line.isNullOrEmpty()) break
                append(line)
            }
        }
    } finally {
        fclose(file)
    }
}

@OptIn(ExperimentalForeignApi::class, UnsafeNumber::class)
internal fun String.listFiles(): List<String> = buildList {
    val directoryPath = this@listFiles
    val directory = opendir(directoryPath)
    
    while (true) {
        val entry = readdir(directory) ?: break
        val entryName = entry.pointed.d_name.toKString()
        memScoped {
            val sb = alloc<stat>()
            stat("$directoryPath/$entryName", sb.ptr)
            if (sb.st_mode.toInt() and S_IFREG == S_IFREG) add("$directoryPath/$entryName")
        }
    }
    
    closedir(directory)
}

internal expect fun String.makeDir()

internal expect val tempDirectory: String

internal expect val targetName: String

private data object KotlinxBenchmarkBenchmarkExecutor : KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult> {
    override fun execute(configuration: KotlinxBenchmarkConfiguration, benchmark: KotlinxBenchmarkBenchmark) {
        validateConfig(configuration)
        
        val nativeFork = configuration.advanced["nativeFork"]
        val benchmarkDirectory = "$tempDirectory/bench${Uuid.generateV4().toHexString()}"
        benchmarkDirectory.makeDir()
        var tempFileCounter = 0u
        fun tempFile() = "$benchmarkDirectory/${tempFileCounter++}"
        
        val benchProgressPath = tempFile()
        val configFile = tempFile()
        val benchsDescriptionDir = tempFile()
        benchsDescriptionDir.makeDir()
        configFile.writeFile(
            buildString {
                appendLine("name:${configuration.name}")
                appendLine("traceFormat:${configuration.traceFormat}")
                configuration.reportFormat?.let { appendLine("reportFormat:$it") }
                configuration.iterations?.let { appendLine("iterations:$it") }
                configuration.warmups?.let { appendLine("warmups:$it") }
                configuration.iterationTime?.let { appendLine("iterationTime:$it") }
                configuration.iterationTimeUnit?.let { appendLine("iterationTimeUnit:$it") }
                configuration.outputTimeUnit?.let { appendLine("outputTimeUnit:$it") }
                configuration.mode?.let { appendLine("mode:$it") }
                configuration.threads?.let { appendLine("threads:$it") }
                configuration.compilationMode?.let { appendLine("compilationMode:$it") }
                appendLine("configurationName:${configuration.name}")
                
                configuration.include.forEach {
                    appendLine("include:$it")
                }
                configuration.exclude.forEach {
                    appendLine("exclude:$it")
                }
                configuration.params.forEach { [param, values] ->
                    values.forEach { value -> appendLine("param:$param=$value") }
                }
                configuration.advanced.forEach { [param, value] ->
                    appendLine("advanced:$param=$value")
                }
                appendLine("reportFile:${configuration.reportFile}")
            }
        )
        
        fun execute(args: Array<out String>) {
            runBenchmarks(
                name = targetName,
                args = args,
            ) {
                for (suite in benchmark.suites) it.suite(suite)
                it.run()
            }
        }
        
        execute(arrayOf(configFile, "--list", benchProgressPath, benchsDescriptionDir))
        val detailedConfigFiles = benchsDescriptionDir.listFiles()
        val runResults = mutableMapOf<String, String>()
        
        val forkPerBenchmark = nativeFork.let { it == null || it == "perBenchmark" }
        
        for (runConfigPath in detailedConfigFiles) {
            val lines = runConfigPath.readFile().lines()
            require(lines.size > 1) { "Wrong detailed configuration format" }
            val currentConfigDescription = lines[1]
            
            // Execute benchmark
            if (forkPerBenchmark) {
                val suiteResultsFile = tempFile()
                execute(arrayOf(configFile, "--benchmark", benchProgressPath, runConfigPath, suiteResultsFile))
                val suiteResults = suiteResultsFile.readFile()
                if (suiteResults.isNotEmpty()) runResults[runConfigPath] = suiteResults
            } else {
                val iterations = currentConfigDescription.substringAfter("iterations=").substringBefore(',').toInt()
                val warmups = currentConfigDescription.substringAfter("warmups=").substringBefore(',').toInt()
                // Warm up
                var exceptionDuringExecution = false
                var textResult: String? = null
                for (i in 0 until warmups) {
                    textResult = tempFile()
                    execute(arrayOf(configFile, "--warmup", benchProgressPath, runConfigPath, i.toString(), textResult))
                    val result = textResult.readFile().lines().getOrNull(0)
                    if (result == "null") {
                        exceptionDuringExecution = true
                        break
                    }
                }
                // Get cycles number
                val cycles = if (!exceptionDuringExecution && textResult != null) textResult.readFile() else "1"
                // Execution
                val iterationResults = mutableListOf<Double>()
                var iteration = 0
                while (!exceptionDuringExecution && iteration in 0 until iterations) {
                    textResult = tempFile()
                    execute(arrayOf(configFile, "--iteration", benchProgressPath, runConfigPath, iteration.toString(), cycles, textResult))
                    val result = textResult.readFile().lines()[0]
                    if (result == "null") exceptionDuringExecution = true
                    iterationResults.add(result.toDouble())
                    iteration++
                }
                // Store results
                if (iterationResults.size == iterations) {
                    val iterationsResultsFile = tempFile()
                    iterationsResultsFile.writeFile(iterationResults.joinToString { it.toString() })
                    execute(arrayOf(configFile, "--end-run", benchProgressPath, runConfigPath, iterationsResultsFile))
                    runResults[runConfigPath] = iterationResults.joinToString()
                }
            }
        }
        // Merge results
        val samplesFile = tempFile()
        samplesFile.writeFile(runResults.toList().joinToString("\n") { "${it.first}: ${it.second}"})
        execute(arrayOf(configFile, "--store-results", benchProgressPath, samplesFile))
    }
}

public actual val KoneBenchmarkExecutor.Companion.kotlinxBenchmark: KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult>
    get() = KotlinxBenchmarkBenchmarkExecutor