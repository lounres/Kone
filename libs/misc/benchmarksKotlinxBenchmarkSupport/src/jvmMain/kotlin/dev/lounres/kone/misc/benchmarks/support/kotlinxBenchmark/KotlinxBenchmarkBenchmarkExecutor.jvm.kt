/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
import kotlinx.benchmark.BenchmarkProgress
import org.openjdk.jmh.infra.BenchmarkParams
import org.openjdk.jmh.infra.IterationParams
import org.openjdk.jmh.results.BenchmarkResult
import org.openjdk.jmh.results.IterationResult
import org.openjdk.jmh.results.RunResult
import org.openjdk.jmh.results.format.ResultFormatFactory
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.IterationType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.format.OutputFormat
import org.openjdk.jmh.runner.options.OptionsBuilder
import org.openjdk.jmh.runner.options.TimeValue
import org.openjdk.jmh.runner.options.VerboseMode
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit


internal class JmhOutputFormat(private val reporter: BenchmarkProgress, private val suiteName: String) :
    PrintOutputFormat(System.out) {
    
    internal var lastBenchmarkStart = ""
    
    override fun startRun() {
        reporter.startSuite(suiteName)
    }
    
    override fun endRun(result: Collection<RunResult>) {
        val summary = ByteArrayOutputStream().apply {
            PrintStream(this, true, "UTF-8").use {
                ResultFormatFactory.getInstance(ResultFormatType.TEXT, it).writeOut(result)
            }
        }.toString("UTF-8")
        reporter.endSuite(suiteName, summary)
    }
    
    override fun startBenchmark(benchParams: BenchmarkParams) {
        val benchmarkId = getBenchmarkId(benchParams)
        reporter.startBenchmark(suiteName, benchmarkId)
        reporter.output(suiteName, benchmarkId,
                        "Threads: ${benchParams.threads} (available CPU cores: ${Runtime.getRuntime().availableProcessors()})"
        )
        lastBenchmarkStart = benchmarkId
    }
    
    override fun endBenchmark(result: BenchmarkResult?) {
        if (result != null) {
            val benchmarkId = getBenchmarkId(result.params)
            val value = result.primaryResult
            val message = value.extendedInfo().trim()
            reporter.endBenchmark(suiteName, benchmarkId, BenchmarkProgress.FinishStatus.Success, message)
        } else {
            reporter.endBenchmarkException(suiteName, lastBenchmarkStart, "<ERROR>", "")
        }
    }
    
    private fun getBenchmarkId(params: BenchmarkParams): String {
        val benchmarkName = params.benchmark
        val paramKeys = params.paramsKeys
        val benchmarkId = if (paramKeys.isEmpty())
            benchmarkName
        else
            benchmarkName + paramKeys.joinToString(prefix = " | ") { "$it=${params.getParam(it)}" }
        return benchmarkId
    }
    
    override fun iteration(benchParams: BenchmarkParams, params: IterationParams, iteration: Int) {}
    
    override fun iterationResult(
        benchParams: BenchmarkParams,
        params: IterationParams,
        iteration: Int,
        data: IterationResult
    ) {
        when (params.type) {
            IterationType.WARMUP -> println("Warm-up $iteration: ${data.primaryResult}")
            IterationType.MEASUREMENT -> println("Iteration $iteration: ${data.primaryResult}")
            null -> throw UnsupportedOperationException("Iteration type not set")
        }
        flush()
    }
    
    override fun println(s: String) {
        if (!s.startsWith("#"))
            reporter.output(suiteName, lastBenchmarkStart, s)
    }
}

/*
private fun Collection<RunResult>.toReportBenchmarkResult(): Collection<ReportBenchmarkResult> = map { result ->
    val benchmarkFQN = result.params.benchmark
    val value = result.primaryResult

    val (min, max) = value.getScoreConfidence()
    val statistics = value.getStatistics()
    val percentiles = listOf(0.0, 0.25, 0.5, 0.75, 0.90, 0.99, 0.999, 0.9999, 1.0).associate {
        (it * 100) to statistics.getPercentile(it)
    }

    val rawData = result.benchmarkResults
        .flatMap { run -> run.iterationResults.map { iteration -> iteration.primaryResult.getScore() } }
        .toDoubleArray()

    ReportBenchmarkResult(benchmarkFQN, value.getScore(), value.getScoreError(), min to max, percentiles, rawData)
}
*/

internal abstract class PrintOutputFormat(private val out: PrintStream, private val verbose: VerboseMode = VerboseMode.NORMAL) :
    OutputFormat {
    
    override fun print(s: String) {
        if (verbose != VerboseMode.SILENT)
            out.print(s)
    }
    
    override fun verbosePrintln(s: String) {
        if (verbose == VerboseMode.EXTRA)
            out.println(s)
    }
    
    override fun write(b: Int) {
        if (verbose != VerboseMode.SILENT)
            out.write(b)
    }
    
    override fun write(b: ByteArray) {
        if (verbose != VerboseMode.SILENT)
            out.write(b)
    }
    
    override fun println(s: String) {
        if (verbose != VerboseMode.SILENT)
            out.println(s)
    }
    
    override fun flush() = out.flush()
    override fun close() = flush()
}

private data object KotlinxBenchmarkBenchmarkExecutor : KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult> {
    override fun execute(configuration: KotlinxBenchmarkConfiguration, benchmark: KotlinxBenchmarkBenchmark) {
        val jmhOptions = OptionsBuilder()
        configuration.iterations?.let { jmhOptions.measurementIterations(it) }
        configuration.warmups?.let { jmhOptions.warmupIterations(it) }
        configuration.iterationTime?.let {
            val timeValue = TimeValue(it, configuration.iterationTimeUnit ?: TimeUnit.SECONDS)
            jmhOptions.warmupTime(timeValue)
            jmhOptions.measurementTime(timeValue)
        }
        configuration.outputTimeUnit?.let {
            jmhOptions.timeUnit(it)
        }
        configuration.mode?.let {
            jmhOptions.mode(it)
        }
        
        configuration.include.forEach {
            jmhOptions.include(it)
        }
        configuration.exclude.forEach {
            jmhOptions.exclude(it)
        }
        
        configuration.params.forEach { (key, value) ->
            jmhOptions.param(key, *value.toTypedArray())
        }
        
        configuration.threads?.let {
            jmhOptions.threads(it)
        }
        
        val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
        val jvmArgs = runtimeMXBean.inputArguments
        if (jvmArgs.any { it.contains("libasyncProfiler") }) {
            jmhOptions.forks(0)
        } else {
            when (val jvmForks = configuration.advanced["jvmForks"]) {
                null -> jmhOptions.forks(1)
                "definedByJmh" -> { /* do not override */ }
                else -> {
                    val forks = jvmForks.toIntOrNull()?.takeIf { it >= 0 }
                        ?: throw IllegalArgumentException("jvmForks: expected a non-negative integer or \"definedByJmh\" string literal")
                    jmhOptions.forks(forks)
                }
            }
        }
        
        val reportFormat = ResultFormatType.valueOf((configuration.reportFormat ?: "json").uppercase())
        val reporter = BenchmarkProgress.create(configuration.traceFormat)
        val output = JmhOutputFormat(reporter, configuration.name)
        try {
            val runner = Runner(jmhOptions.build(), output)
            val results = runner.run()
            val resultFormat = ResultFormatFactory.getInstance(reportFormat, PrintStream(File(configuration.reportFile)))
            resultFormat.writeOut(results)
        } catch (e: Exception) {
            e.printStackTrace()
            reporter.endBenchmark(
                configuration.name,
                output.lastBenchmarkStart,
                BenchmarkProgress.FinishStatus.Failure,
                e.message ?: "<unknown error>"
            )
        }
    }
}

public actual val KoneBenchmarkExecutor.Companion.kotlinxBenchmark: KoneBenchmarkExecutor<KotlinxBenchmarkConfiguration, KotlinxBenchmarkBenchmark, KotlinxBenchmarkResult>
    get() = KotlinxBenchmarkBenchmarkExecutor