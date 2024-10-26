/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State
import org.openjdk.jmh.annotations.Level
import org.openjdk.jmh.annotations.Setup
import kotlin.random.Random


enum class BenchmarkComparisonResult {
    Greater, Less, Equal;
}

data class DummyClass(val value: Int)

operator fun DummyClass.compareTo(other: DummyClass): Int = this.value.compareTo(other.value)
fun DummyClass.compareWith1(other: DummyClass): BenchmarkComparisonResult =
    when {
        this.value > other.value -> BenchmarkComparisonResult.Greater
        this.value < other.value -> BenchmarkComparisonResult.Less
        else -> BenchmarkComparisonResult.Equal
    }
fun DummyClass.compareWith2(other: DummyClass): BenchmarkComparisonResult {
    val comparison = this.value.compareTo(other.value)
    return when {
        comparison > 0 -> BenchmarkComparisonResult.Greater
        comparison < 0 -> BenchmarkComparisonResult.Less
        else -> BenchmarkComparisonResult.Equal
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ComparisonViaIntVSEnum {
    lateinit var dummyObject1: DummyClass
    lateinit var dummyObject2: DummyClass
    
    @Setup(Level.Iteration)
    fun setup() {
        dummyObject1 = DummyClass(Random.nextInt())
        dummyObject2 = DummyClass(Random.nextInt())
    }
    
    @Benchmark
    fun compareToBenchmark(blackhole: Blackhole) {
        blackhole.consume(dummyObject1.compareTo(dummyObject2))
    }
    
    @Benchmark
    fun compareWith1Benchmark(blackhole: Blackhole) {
        blackhole.consume(dummyObject1.compareWith1(dummyObject2))
    }
    
    @Benchmark
    fun compareWith2Benchmark(blackhole: Blackhole) {
        blackhole.consume(dummyObject1.compareWith2(dummyObject2))
    }
}

fun Blackhole.useInt(result: Int) {
    when {
        result > 0 -> this.consume(DummyClass(57))
        result < 0 -> this.consume(DummyClass(179))
        else -> this.consume(DummyClass(2))
    }
}
fun Blackhole.useEnum(result: BenchmarkComparisonResult) {
    when(result) {
        BenchmarkComparisonResult.Greater -> this.consume(DummyClass(57))
        BenchmarkComparisonResult.Less -> this.consume(DummyClass(179))
        BenchmarkComparisonResult.Equal -> this.consume(DummyClass(2))
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class ComparisonUsageViaIntVSEnum {
    var resultInt: Int = 0
    lateinit var resultEnum: BenchmarkComparisonResult
    
    @Setup(Level.Iteration)
    fun setup() {
        resultInt = Random.nextInt()
        resultEnum = BenchmarkComparisonResult.entries.random(Random)
    }
    
    @Benchmark
    fun useIntBenchmark(blackhole: Blackhole) {
        blackhole.useInt(resultInt)
    }
    
    @Benchmark
    fun useEnumBenchmark(blackhole: Blackhole) {
        blackhole.useEnum(resultEnum)
    }
}