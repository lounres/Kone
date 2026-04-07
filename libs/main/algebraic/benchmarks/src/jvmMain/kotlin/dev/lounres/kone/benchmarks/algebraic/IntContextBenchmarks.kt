/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused", "FunctionName")

package dev.lounres.kone.benchmarks.algebraic

import dev.lounres.kone.algebraic.equality
import dev.lounres.kone.algebraic.order
import dev.lounres.kone.algebraic.reification
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.lt
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
class IntContextEqualityBenchmarks {
    final val boxedIntegers = listOf(0, 1)
    
    @Param("0", "1")
    final var a: Int = 0
    @Param("0", "1")
    final var b: Int = 0
    
    final val equality = Int.equality()
    
    @Benchmark
    fun equality_via_primitives() = a == b
    
    @Benchmark
    fun equality_via_defaultEquality() = equality { a eq b }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntContextEqualityBulkBenchmarks {
    final val boxedIntegers = listOf(0, 1)
    
    final val inputs: Array<IntArray> = Json.decodeFromStream(File("src/jvmMain/resources/defaultComparisonImplementationsArguments.json").inputStream())
    
    final var index: Int = 0
    
    final val equality = Int.equality()
    
    @Benchmark
    fun Blackhole.idle_on_inputs() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }
    
    @Benchmark
    fun Blackhole.equality_via_primitives() {
        val [a, b] = inputs[index]
        consume(a == b)
        index = (index + 1) % inputs.size
    }
    
    @Benchmark
    fun Blackhole.equality_via_defaultEquality() {
        val [a, b] = inputs[index]
        consume(equality { a eq b })
        index = (index + 1) % inputs.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntContextOrderBenchmarks {
    @Param("0", "1")
    final var a: Int = 0
    @Param("0", "1")
    final var b: Int = 0
    
    val order = Int.order()
    
    @Benchmark
    fun comparison_via_primitives() = a < b
    
    @Benchmark
    fun comparison_via_defaultOrder_compareWith_with_boxing() = order { a lt b }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntContextOrderBulkBenchmarks {
    final val inputs: Array<IntArray> = Json.decodeFromStream(File("src/jvmMain/resources/defaultComparisonImplementationsArguments.json").inputStream())
    
    final var index: Int = 0
    
    final val order = Int.order()
    
    @Benchmark
    fun Blackhole.idle_on_inputs() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }
    
    @Benchmark
    fun Blackhole.comparison_via_primitives() {
        val [a, b] = inputs[index]
        consume(a < b)
        index = (index + 1) % inputs.size
    }
    
    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareWith_with_boxing() {
        val [a, b] = inputs[index]
        consume(order { a lt b })
        index = (index + 1) % inputs.size
    }
}

//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class IntOperationsBenchmarks {
//    val a: Int = 1846030199
//    val b: Int = -1469324163
//
//    // region Hashing
//    @Benchmark
//    fun Any_hashCode_for_Int() = a.hashCode()
//
//    @Benchmark
//    fun Any_hashCode_for_generic_Int() = tryAnyHashCode(a)
//
//    @Benchmark
//    fun Hashing_hash_for_Int() = Hashing.primaryFor(Int) { a.hash() }
//
//    @Benchmark
//    fun Hashing_hash_for_generic_Int() = tryHashingHash(a, Hashing.primaryFor(Int))
//    // endregion
//
//    // region Semiring
//    // TODO: Finish benchmarks
//    // endregion
//}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntContextReificationBenchmarks {
    var a: Int = 1846030199

    val reification = Int.reification()

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