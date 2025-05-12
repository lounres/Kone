/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused", "FunctionName")

package dev.lounres.kone.benchmarks.algebraic

import dev.lounres.kone.algebraic.context
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State


@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntContextReificationBenchmarks {
    var a: Int = 1846030199

    val reification = Int.context

    @Benchmark
    fun reifibility_successful() = a in reification

    @Benchmark
    fun reifibility_unsuccessful() = null in reification

    @Benchmark
    fun idle_runCatching(): Any? = runCatching {}

    @Benchmark
    fun reification_successful(): Any? = runCatching { reification.reify(a) }

    @Benchmark
    fun reification_unsuccessful(): Any? = runCatching { reification.reify(null) }

    @Benchmark
    fun reificationMaybe_successful() = reification.reifyMaybe(a)

    @Benchmark
    fun reificationMaybe_unsuccessful() = reification.reifyMaybe(null)

    @Benchmark
    fun reificationNullable_successful() = reification.reifyOrNull(a)

    @Benchmark
    fun reificationNullable_unsuccessful() = reification.reifyOrNull(null)
}

//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class IntOperationsBenchmarks {
//    val a: Int = 1846030199
//    val b: Int = -1469324163
//
//    // region Equality
//    @Benchmark
//    fun Any_equals_for_Int() = a == b
//
//    @Benchmark
//    fun Any_equals_for_generic_Int() = tryAnyEquals(a, b)
//
//    @Benchmark
//    fun Equality_equalsTo_for_Int() = Int.context { a equalsTo b }
//
//    @Benchmark
//    fun Equality_equalsTo_for_generic_Int() = tryEqualityEqualsTo(a, b, Int.context)
//    // endregion
//
//    // region Order
//    @Benchmark
//    fun Comparable_compareTo_for_Int() = a > b
//
//    @Benchmark
//    fun Comparable_compareTo_for_generic_Int() = tryComparableCompareTo(a, b)
//
//    @Benchmark
//    fun Order_compareTo_for_generic_Int() = tryOrderCompareTo(a, b, Int.context)
//
//    @Benchmark
//    fun Order_compareWith_for_Int() = Int.context { a compareWith b }
//
//    @Benchmark
//    fun Order_compareWith_for_generic_Int() = tryOrderCompareWith(a, b, Int.context)
//    // endregion
//
//    // region Hashing
//    @Benchmark
//    fun Any_hashCode_for_Int() = a.hashCode()
//
//    @Benchmark
//    fun Any_hashCode_for_generic_Int() = tryAnyHashCode(a)
//
//    @Benchmark
//    fun Hashing_hash_for_Int() = Int.context { a.hash() }
//
//    @Benchmark
//    fun Hashing_hash_for_generic_Int() = tryHashingHash(a, Int.context)
//    // endregion
//
//    // region Semiring
//    // TODO: Finish benchmarks
//    // endregion
//}