/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.relations

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State


//enum class ComparisonResult1 {
//    LeftIsGreaterThanRight, LeftIsLessThanRight, Equal;
//}
//
//fun ComparisonResult1.asKotlinComparisonResult1(): Int =
//    when (this) {
//        ComparisonResult1.LeftIsGreaterThanRight -> 1
//        ComparisonResult1.LeftIsLessThanRight -> -1
//        ComparisonResult1.Equal -> 0
//    }
//
//fun Int.asComparisonResult1(): ComparisonResult1 =
//    when {
//        this > 0 -> ComparisonResult1.LeftIsGreaterThanRight
//        this < 0 -> ComparisonResult1.LeftIsLessThanRight
//        else -> ComparisonResult1.Equal
//    }
//
//enum class ComparisonResult2(@JvmField internal val asKotlinComparisonResult: Int) {
//    LeftIsGreaterThanRight(1), LeftIsLessThanRight(-1), Equal(0);
//}
//
//fun ComparisonResult2.asKotlinComparisonResult2(): Int = asKotlinComparisonResult
//
//fun Int.asComparisonResult2(): ComparisonResult2 =
//    when {
//        this > 0 -> ComparisonResult2.LeftIsGreaterThanRight
//        this < 0 -> ComparisonResult2.LeftIsLessThanRight
//        else -> ComparisonResult2.Equal
//    }
//
//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class ComparisonResult1ToIntBenchmarks {
//    @Param("LeftIsGreaterThanRight", "LeftIsLessThanRight", "Equal")
//    lateinit var result: ComparisonResult1
//
//    @Benchmark
//    fun benchmark(blackhole: Blackhole) {
//        blackhole.consume(result.asKotlinComparisonResult1())
//    }
//}
//
//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class IntToComparisonResult1Benchmarks {
//    @Param("0", "1", "-1", "2", "-2", "2147483647", "-2147483647")
//    var result: Int = 0
//
//    @Benchmark
//    fun benchmark(blackhole: Blackhole) {
//        blackhole.consume(result.asComparisonResult1())
//    }
//}
//
//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class ComparisonResult2Benchmarks {
//    @Param("LeftIsGreaterThanRight", "LeftIsLessThanRight", "Equal")
//    lateinit var result: ComparisonResult2
//
//    @Benchmark
//    fun benchmark(blackhole: Blackhole) {
//        blackhole.consume(result.asKotlinComparisonResult2())
//    }
//}
//
//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class IntToComparisonResult2Benchmarks {
//    @Param("0", "1", "-1", "2", "-2", "2147483647", "-2147483647")
//    var result: Int = 0
//
//    @Benchmark
//    fun benchmark(blackhole: Blackhole) {
//        blackhole.consume(result.asComparisonResult2())
//    }
//}

fun factorial(n: Int): Int = if (n == 0) 1 else factorial(n - 1) * n

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class Benchmarks {
    @Param("1"/*, "10", "100", "1000"*/)
    @JvmField
    final var integer: Int = 0
    @JvmField
    final val otherInteger = 179
    
    @Benchmark
    fun empty() {}

    @Benchmark
    fun benchmark1(blackhole: Blackhole) {
        blackhole.consume(integer)
    }

    @Benchmark
    fun benchmark2(blackhole: Blackhole) {
        blackhole.consume(integer > otherInteger)
    }
    
    @Benchmark
    fun foo(blackhole: Blackhole) {
        blackhole.consume(factorial(otherInteger))
    }
    
//    @Benchmark
//    fun benchmark3(blackhole: Blackhole) {
//        val result = integer
//        blackhole.consume(result)
//    }
//
//    @Benchmark
//    fun benchmark4(blackhole: Blackhole) {
//        val result = integer
//    }
//
//    @Benchmark
//    fun benchmark5(blackhole: Blackhole) {
//        val result = integer > otherInteger
//        blackhole.consume(result)
//    }
//
//    @Benchmark
//    fun benchmark6(blackhole: Blackhole) {
//        val result = integer > otherInteger
//    }
}
