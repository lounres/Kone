/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused", "FunctionName")

package dev.lounres.kone.algebraic

import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State


@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntOperationsBenchmarks {
    val a: Int = 1846030199
    val b: Int = -1469324163
    
    // region Reification
    @Benchmark
    fun Reification_contains_for_Int(blackhole: Blackhole) {
        blackhole.consume(a in Int.context)
    }
    
    @Benchmark
    fun Reification_contains_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryReificationContains(a, Int.context))
    }
    
    @Benchmark
    fun Reification_reifyMaybe_for_Int(blackhole: Blackhole) {
        blackhole.consume(Int.context.reifyMaybe(a))
    }
    
    @Benchmark
    fun Reification_reifyMaybe_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryReificationReifyMaybe(a, Int.context))
    }
    
    @Benchmark
    fun Reification_reifyOrNull_for_Int(blackhole: Blackhole) {
        blackhole.consume(Int.context.reifyOrNull(a))
    }
    
    @Benchmark
    fun Reification_reifyOrNull_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryReificationReifyOrNull(a, Int.context))
    }
    
    @Benchmark
    fun Reification_reify_for_Int(blackhole: Blackhole) {
        blackhole.consume(Int.context.reify(a))
    }
    
    @Benchmark
    fun Reification_reify_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryReificationReify(a, Int.context))
    }
    // endregion
    
    // region Equality
    @Benchmark
    fun Any_equals_for_Int(blackhole: Blackhole) {
        blackhole.consume(a == b)
    }
    
    @Benchmark
    fun Any_equals_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryAnyEquals(a, b))
    }
    
    @Benchmark
    fun Equality_equalsTo_for_Int(blackhole: Blackhole) {
        blackhole.consume(Int.context.run { a equalsTo b })
    }
    
    @Benchmark
    fun Equality_equalsTo_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryEqualityEqualsTo(a, b, Int.context))
    }
    // endregion
    
    // region Order
    @Benchmark
    fun Comparable_compareTo_for_Int(blackhole: Blackhole) {
        blackhole.consume(a > b)
    }
    
    @Benchmark
    fun Comparable_compareTo_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryComparableCompareTo(a, b))
    }
    
    @Benchmark
    fun Order_compareTo_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryOrderCompareTo(a, b, Int.context))
    }
    
    @Benchmark
    fun Order_compareWith_for_Int(blackhole: Blackhole) {
        blackhole.consume(Int.context.run { a compareWith b })
    }
    
    @Benchmark
    fun Order_compareWith_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryOrderCompareWith(a, b, Int.context))
    }
    // endregion
    
    // region Hashing
    @Benchmark
    fun Any_hashCode_for_Int(blackhole: Blackhole) {
        blackhole.consume(a.hashCode())
    }
    
    @Benchmark
    fun Any_hashCode_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryAnyHashCode(a))
    }
    
    @Benchmark
    fun Hashing_hash_for_Int(blackhole: Blackhole) {
        blackhole.consume(Int.context.run { a.hash() })
    }
    
    @Benchmark
    fun Hashing_hash_for_generic_Int(blackhole: Blackhole) {
        blackhole.consume(tryHashingHash(a, Int.context))
    }
    // endregion
    
    // region Semiring
    // TODO: Finish benchmarks
    // endregion
}