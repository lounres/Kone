/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
import kotlinx.benchmark.BenchmarkDescriptorWithBlackholeParameter
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.SuiteDescriptor


val ExecutorNativeTests by testSuite {
    test("executor is available") {
        val _ = KoneBenchmarkExecutor.kotlinxBenchmark
    }
    
    test("executor doesn't throw") {
        class Test
        
        KoneBenchmarkExecutor.kotlinxBenchmark.execute(
            configuration = KotlinxBenchmarkConfiguration(
                name = "test configuration",
                reportFile = "$tempDirectory/executorNativeTestsResults",
                traceFormat = "text",
                reportFormat = "json",
                params = mapOf(),
                include = listOf(),
                exclude = listOf(),
                iterations = 3,
                warmups = 3,
                iterationTime = 1,
                iterationTimeUnit = BenchmarkTimeUnit.SECONDS,
                advanced = mapOf(),
                outputTimeUnit = BenchmarkTimeUnit.SECONDS,
                threads = 1,
                mode = Mode.Throughput,
                compilationMode = "Debug",
                configurationName = "testConfiguration",
            ),
            benchmark = KotlinxBenchmarkBenchmark(
                listOf(
                    SuiteDescriptor(
                        name = "testSuite",
                        factory = { Test() },
                        parametrize = { _, _ -> },
                        setup = {},
                        teardown = {},
                        parameters = listOf(),
                        defaultParameters = mapOf(),
                    ).apply {
                        add(
                            BenchmarkDescriptorWithBlackholeParameter(
                                name = "testBenchmark",
                                suite = this,
                                blackhole = Blackhole(),
                                function = { blackhole ->
                                    repeat(57) { blackhole.consume(it) }
                                }
                            )
                        )
                    }
                )
            ),
        )
    }
}