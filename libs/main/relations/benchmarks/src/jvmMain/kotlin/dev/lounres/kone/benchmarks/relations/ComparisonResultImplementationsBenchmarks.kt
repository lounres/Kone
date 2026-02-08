/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused")

package dev.lounres.kone.benchmarks.relations

import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.asComparisonResult
import dev.lounres.kone.relations.asKotlinComparisonResult
import dev.lounres.kone.algorithms.relations.ComparisonResult2
import dev.lounres.kone.algorithms.relations.asComparisonResult2
import dev.lounres.kone.algorithms.relations.asKotlinComparisonResult2
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File


@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ComparisonResultToIntBenchmarks {
    @Param("LeftIsGreaterThanRight", "LeftIsLessThanRight", "Equal")
    lateinit var result: ComparisonResult
    
    @Benchmark
    fun benchmark() = result.asKotlinComparisonResult()
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ComparisonResultToIntBulkBenchmarks {
    final val results: Array<ComparisonResult> = Json.decodeFromStream(File("src/jvmMain/resources/comparisonResults.json").inputStream())
    
    final var index: Int = 0
    
    @Benchmark
    fun Blackhole.idle() {
        consume(results[index])
        index = (index + 1) % results.size
    }
    
    @Benchmark
    fun Blackhole.benchmark() {
        consume(results[index].asKotlinComparisonResult())
        index = (index + 1) % results.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntToComparisonResultBenchmarks {
    @Param("0", "1", "-1", "2", "-2", "2147483647", "-2147483647")
    var result: Int = 0
    
    @Benchmark
    fun benchmark() = result.asComparisonResult()
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntToComparisonResultBulkBenchmarks {
    final val results: IntArray
    
    init {
        val data = Json.decodeFromStream<Array<ComparisonResult>>(File("src/jvmMain/resources/comparisonResults.json").inputStream())
        results = IntArray(data.size) { data[it].asKotlinComparisonResult() }
    }
    
    final var index: Int = 0
    
    @Benchmark
    fun Blackhole.idle() {
        consume(results[index])
        index = (index + 1) % results.size
    }
    
    @Benchmark
    fun Blackhole.benchmark() {
        consume(results[index].asComparisonResult())
        index = (index + 1) % results.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ComparisonResult2ToIntBenchmarks {
    @Param("LeftIsGreaterThanRight", "LeftIsLessThanRight", "Equal")
    lateinit var result: ComparisonResult2

    @Benchmark
    fun benchmark() = result.asKotlinComparisonResult2()
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ComparisonResult2ToIntBulkBenchmarks {
    final val results: Array<ComparisonResult2> = Json.decodeFromStream(File("src/jvmMain/resources/comparisonResults.json").inputStream())
    
    final var index: Int = 0
    
    @Benchmark
    fun Blackhole.idle() {
        consume(results[index])
        index = (index + 1) % results.size
    }
    
    @Benchmark
    fun Blackhole.benchmark() {
        consume(results[index].asKotlinComparisonResult2())
        index = (index + 1) % results.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntToComparisonResult2Benchmarks {
    @Param("0", "1", "-1", "2", "-2", "2147483647", "-2147483647")
    var result: Int = 0

    @Benchmark
    fun benchmark() = result.asComparisonResult2()
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntToComparisonResult2BulkBenchmarks {
    final val results: IntArray
    
    init {
        val data = Json.decodeFromStream<Array<ComparisonResult2>>(File("src/jvmMain/resources/comparisonResults.json").inputStream())
        results = IntArray(data.size) { data[it].asKotlinComparisonResult2() }
    }
    
    final var index: Int = 0
    
    @Benchmark
    fun Blackhole.idle() {
        consume(results[index])
        index = (index + 1) % results.size
    }
    
    @Benchmark
    fun Blackhole.benchmark() {
        consume(results[index].asComparisonResult2())
        index = (index + 1) % results.size
    }
}
