/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.koneBenchmarks.relations

import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteEquality
import dev.lounres.kone.relations.compareTo
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.defaultHashing
import dev.lounres.kone.relations.defaultOrder
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.lt
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File


@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultImplementationsBenchmarks {
    @Param("0", "1")
    var a: Int = 0
    @Param("0", "1")
    var b: Int = 0
    val structuralEquality = defaultEquality<Int>()
    val referencesEquality = absoluteEquality<Int>()
    val order = defaultOrder<Int>()
    val hashing = defaultHashing<Int>()
    val reification = Reification<Int>()

    @Benchmark
    fun structural_equality_via_primitives() = a == b

    @Benchmark
    fun structural_equality_via_generics() = (a as Any) == (b as Any)

    @Benchmark
    fun structural_equality_via_defaultEquality() = structuralEquality { a eq b }

    @Benchmark
    fun reference_equality_via_generics() = (a as Any) === (b as Any)

    @Benchmark
    fun reference_equality_via_absoluteEquality() = referencesEquality { a eq b }

    @Benchmark
    fun comparison_via_primitives() = a < b

    fun <T: Comparable<T>> compare_via_comparability(a: T, b: T): Boolean = a < b

    @Benchmark
    fun comparison_via_comparability() = compare_via_comparability(a, b)

    context(_: Order<T>)
    fun <T> compare_via_defaultOrder_compareTo(a: T, b: T): Boolean = a < b

    @Benchmark
    fun comparison_via_defaultOrder_compareTo() = order { compare_via_defaultOrder_compareTo(a, b) }

    @Benchmark
    fun comparison_via_defaultOrder_compareWith() = order { a lt b }
    
    @Benchmark
    fun hash_via_primitives() = a.hashCode()
    
    @Benchmark
    fun hash_via_generics() = (a as Any).hashCode()
    
    @Benchmark
    fun hash_via_defaultHashing() = hashing { a.hash() }
    
    @Benchmark
    fun reifibility_successful() = a in reification
    
    @Benchmark
    fun reifibility_unsuccessful() = null in reification
    
    @Benchmark
    fun reification_successful() = runCatching { reification.reify(a) }
    
    @Benchmark
    fun reification_unsuccessful() = runCatching { reification.reify(null) }
    
    @Benchmark
    fun reificationNullable_successful() = reification.reifyOrNull(a)
    
    @Benchmark
    fun reificationNullable_unsuccessful() = reification.reifyOrNull(null)
    
    @Benchmark
    fun reificationMaybe_successful() = reification.reifyMaybe(a)
    
    @Benchmark
    fun reificationMaybe_unsuccessful() = reification.reifyMaybe(null)
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultImplementationsBulkBenchmarks {
    val structuralEquality = defaultEquality<Int>()
    val referencesEquality = absoluteEquality<Int>()
    val order = defaultOrder<Int>()
    val hashing = defaultHashing<Int>()
    val reification = Reification<Int>()

    lateinit var inputs: Array<Array<Int>>

    @Setup
    fun setup() {
        inputs = Json.decodeFromStream(File("src/jvmMain/resources/defaultImplementationsArguments.json").inputStream())
    }

    final var index: Int = 0

    @Benchmark
    fun Blackhole.idle() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.structural_equality_via_primitives() {
        val (a, b) = inputs[index]
        consume(a == b)
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.structural_equality_via_generics() {
        val (a, b) = inputs[index]
        consume((a as Any?) == (b as Any?))
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.structural_equality_via_defaultEquality() {
        val (a, b) = inputs[index]
        consume(structuralEquality { a eq b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reference_equality_via_generics() {
        val (a, b) = inputs[index]
        consume((a as Any?) === (b as Any?))
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reference_equality_via_absoluteEquality() {
        val (a, b) = inputs[index]
        consume(referencesEquality { a eq b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_primitives() {
        val (a, b) = inputs[index]
        consume(a < b)
        index = (index + 1) % inputs.size
    }

    fun <T: Comparable<T>> compare_via_comparability(a: T, b: T): Boolean = a < b

    @Benchmark
    fun Blackhole.comparison_via_comparability() {
        val (a, b) = inputs[index]
        consume(compare_via_comparability(a, b))
        index = (index + 1) % inputs.size
    }

    context(_: Order<T>)
    fun <T> compare_via_defaultOrder_compareTo(a: T, b: T): Boolean = a < b

    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareTo() {
        val (a, b) = inputs[index]
        consume(order { compare_via_defaultOrder_compareTo(a, b) })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareWith() {
        val (a, b) = inputs[index]
        consume(order { a lt b })
        index = (index + 1) % inputs.size
    }
}