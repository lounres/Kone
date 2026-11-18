/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.misc.benchmarks.support.kotlinxBenchmark

//import de.infix.testBalloon.framework.core.testSuite
//import dev.lounres.kone.misc.benchmarks.KoneBenchmarkExecutor
//import kotlinx.benchmark.Benchmark
//import kotlinx.benchmark.Blackhole
//import kotlinx.benchmark.Scope
//import kotlinx.benchmark.State
//
//
//@State(Scope.Benchmark)
//class TestBenchmark {
//    @Benchmark
//    fun benchmark(blackhole: Blackhole) {
//        repeat(57) { blackhole.consume(it) }
//    }
//}
//
//val ExecutorJvmTests by testSuite {
//    test("executor is available") {
//        val _ = KoneBenchmarkExecutor.kotlinxBenchmark
//    }
//
//    test("executor doesn't throw") {
//        KoneBenchmarkExecutor.kotlinxBenchmark.execute(
//            configuration = KotlinxBenchmarkConfiguration(
//                name = "test configuration",
//                reportFile = "${System.getProperty("java.io.tmpdir")}/executorNativeTestsResults",
//                traceFormat = "text",
//                reportFormat = "json",
//                params = mapOf(),
//                include = listOf(TestBenchmark::class.qualifiedName!!),
//                exclude = listOf(),
//                iterations = 3,
//                warmups = 3,
//                iterationTime = 1,
//                iterationTimeUnit = SECONDS,
//                advanced = mapOf(),
//                outputTimeUnit = SECONDS,
//                threads = 1,
//                mode = Throughput,
//                compilationMode = "Debug",
//                configurationName = "testConfiguration",
//            ),
//            benchmark = KotlinxBenchmarkBenchmark,
//        )
//    }
//}