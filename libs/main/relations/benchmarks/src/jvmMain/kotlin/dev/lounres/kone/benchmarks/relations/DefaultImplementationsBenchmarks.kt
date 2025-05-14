/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused", "FunctionName")

package dev.lounres.kone.benchmarks.relations

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
class IntDefaultEqualityImplementationsBenchmarks {
    final val boxedIntegers = listOf(0, 1)

    @Param("0", "1")
    final var a: Int = 0
    @Param("0", "1")
    final var b: Int = 0

    final lateinit var aBoxed: Any
    final lateinit var bBoxed: Any

    @Setup
    fun setup() {
        aBoxed = boxedIntegers[a]
        bBoxed = boxedIntegers[b]
    }

    final val structuralEquality = defaultEquality<Any>()
    final val referencesEquality = absoluteEquality<Any>()

    @Benchmark
    fun structural_equality_via_primitives() = a == b

    @Benchmark
    fun structural_equality_via_generics() = aBoxed == bBoxed

    @Benchmark
    fun structural_equality_via_defaultEquality() = structuralEquality { aBoxed eq bBoxed }

    @Benchmark
    fun structural_equality_via_defaultEquality_with_boxing() = structuralEquality { a eq b }

    @Benchmark
    fun reference_equality_via_generics() = aBoxed === bBoxed

    @Benchmark
    fun reference_equality_via_absoluteEquality() = referencesEquality { aBoxed eq bBoxed }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultEqualityImplementationsBulkBenchmarks {
    final val boxedIntegers = listOf(0, 1)

    final val inputs: Array<IntArray> = Json.decodeFromStream(File("src/jvmMain/resources/defaultComparisonImplementationsArguments.json").inputStream())
    final val inputsBoxed: Array<Array<Any>> = Array(inputs.size) { inputIndex ->
        val case = inputs[inputIndex]
        Array(case.size) { boxedIntegers[case[it]] }
    }

    final var index: Int = 0

    final val structuralEquality = defaultEquality<Any>()
    final val referencesEquality = absoluteEquality<Any>()

    @Benchmark
    fun Blackhole.idle_on_inputs() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.idle_on_inputsBoxed() {
        consume(inputsBoxed[index])
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
        val (a, b) = inputsBoxed[index]
        consume(a == b)
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.structural_equality_via_defaultEquality() {
        val (a, b) = inputsBoxed[index]
        consume(structuralEquality { a eq b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.structural_equality_via_defaultEquality_with_boxing() {
        val (a, b) = inputs[index]
        consume(structuralEquality { a eq b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reference_equality_via_generics() {
        val (a, b) = inputsBoxed[index]
        consume(a === b)
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reference_equality_via_absoluteEquality() {
        val (a, b) = inputsBoxed[index]
        consume(referencesEquality { a eq b })
        index = (index + 1) % inputs.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultOrderImplementationsBenchmarks {
    final val boxedIntegers = listOf(0, 1)

    @Param("0", "1")
    final var a: Int = 0
    @Param("0", "1")
    final var b: Int = 0

    final lateinit var aBoxed: Any
    final lateinit var aComparable: Comparable<Any>
    final lateinit var bBoxed: Any

    @Setup
    @Suppress("UNCHECKED_CAST")
    fun setup() {
        aBoxed = boxedIntegers[a]
        aComparable = boxedIntegers[a] as Comparable<Any>
        bBoxed = boxedIntegers[b]
    }

    @Suppress("UNCHECKED_CAST")
    val order = defaultOrder<Nothing>() as Order<Any>

    @Benchmark
    fun comparison_via_primitives() = a < b

    @Benchmark
    fun comparison_via_comparability() = aComparable < bBoxed

    @Benchmark
    fun comparison_via_defaultOrder_compareTo() = order { aBoxed < bBoxed }

    @Benchmark
    fun comparison_via_defaultOrder_compareTo_with_boxing() = order { (a as Any) < (b as Any) }

    @Benchmark
    fun comparison_via_defaultOrder_compareWith() = order { aBoxed lt bBoxed }

    @Benchmark
    fun comparison_via_defaultOrder_compareWith_with_boxing() = order { a lt b }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultOrderImplementationsBulkBenchmarks {
    final val boxedIntegers = listOf(0, 1)

    final val inputs: Array<IntArray> = Json.decodeFromStream(File("src/jvmMain/resources/defaultComparisonImplementationsArguments.json").inputStream())
    @Suppress("UNCHECKED_CAST")
    final val inputsBoxed: Array<Array<Comparable<Any>>> = Array(inputs.size) { inputIndex ->
        val case = inputs[inputIndex]
        Array(case.size) { boxedIntegers[case[it]] as Comparable<Any> }
    }

    final var index: Int = 0

    @Suppress("UNCHECKED_CAST")
    final val order = defaultOrder<Nothing>() as Order<Any>

    @Benchmark
    fun Blackhole.idle_on_inputs() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.idle_on_inputsBoxed() {
        consume(inputsBoxed[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_primitives() {
        val (a, b) = inputs[index]
        consume(a < b)
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_comparability() {
        val (a, b) = inputsBoxed[index]
        consume(a < b)
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareTo() {
        val (a: Any, b: Any) = inputsBoxed[index]
        consume(order { a < b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareTo_with_boxing() {
        val (a: Any, b: Any) = inputs[index]
        consume(order { a < b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareWith() {
        val (a: Any, b: Any) = inputsBoxed[index]
        consume(order { a lt b })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.comparison_via_defaultOrder_compareWith_with_boxing() {
        val (a: Any, b: Any) = inputs[index]
        consume(order { a lt b })
        index = (index + 1) % inputs.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultHashingImplementationsBenchmarks {
    @Param("0", "1")
    final var a: Int = 0

    final lateinit var aBoxed: Any

    @Setup
    fun setup() {
        aBoxed = a
    }

    val hashing = defaultHashing<Any>()

    @Benchmark
    fun hash_via_primitives() = a.hashCode()

    @Benchmark
    fun hash_via_generics() = aBoxed.hashCode()

    @Benchmark
    fun hash_via_defaultHashing() = hashing { aBoxed.hash() }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultHashingImplementationsBulkBenchmarks {
    final val inputs: IntArray = Json.decodeFromStream(File("src/jvmMain/resources/defaultHashingImplementationsArguments.json").inputStream())
    final val inputsBoxed: Array<Any> = Array(inputs.size) { inputs[it] }

    final var index: Int = 0

    final val hashing = defaultHashing<Any>()

    @Benchmark
    fun Blackhole.idle_on_inputs() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.idle_on_inputsBoxed() {
        consume(inputsBoxed[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.hash_via_primitives() {
        consume(inputs[index].hashCode())
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.hash_via_generics() {
        consume(inputsBoxed[index].hashCode())
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.hash_via_defaultHashing() {
        consume(hashing { inputsBoxed[index].hash() })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.hash_via_defaultHashing_with_boxing() {
        consume(hashing { inputs[index].hash() })
        index = (index + 1) % inputs.size
    }
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultReificationImplementationsBenchmarks {
    final val a: Int = 57
    final val aBoxed: Any = 57

    final val reification = Reification<Int>()

    @Benchmark
    fun reifibility_successful() = aBoxed in reification

    @Benchmark
    fun reifibility_successful_with_boxing() = a in reification

    @Benchmark
    fun reifibility_unsuccessful() = null in reification

    @Benchmark
    fun idle_runCatching(): Any? = runCatching {}

    @Benchmark
    fun reification_successful(): Any? = runCatching { reification.reify(aBoxed) }

    @Benchmark
    fun reification_successful_with_boxing(): Any? = runCatching { reification.reify(a) }

    @Benchmark
    fun reification_unsuccessful(): Any? = runCatching { reification.reify(null) }

    @Benchmark
    fun reificationNullable_successful() = reification.reifyOrNull(aBoxed)

    @Benchmark
    fun reificationNullable_successful_with_boxing() = reification.reifyOrNull(a)

    @Benchmark
    fun reificationNullable_unsuccessful() = reification.reifyOrNull(null)

    @Benchmark
    fun reificationMaybe_successful() = reification.reifyMaybe(aBoxed)

    @Benchmark
    fun reificationMaybe_successful_with_boxing() = reification.reifyMaybe(a)

    @Benchmark
    fun reificationMaybe_unsuccessful() = reification.reifyMaybe(null)
}

@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
class IntDefaultReificationImplementationsBulkBenchmarks {
    final val inputs: Array<Any?>

    init {
        val integer: Any = 57
        val string = "57"
        val data = Json.decodeFromStream<IntArray>(File("src/jvmMain/resources/defaultReificationImplementationsArguments.json").inputStream())
        inputs = Array(data.size) {
            when (data[it]) {
                0 -> integer
                1 -> string
                else -> error("Unexpected input data element.")
            }
        }
    }

    final var index: Int = 0

    final val reification = Reification<Int>()

    @Benchmark
    fun Blackhole.idle_on_inputs() {
        consume(inputs[index])
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reifibility() {
        consume(inputs[index] in reification)
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.idle_runCatching() {
        consume(runCatching { inputs[index] })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reification() {
        consume(runCatching { reification.reify(inputs[index]) })
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reificationNullable() {
        consume(reification.reifyOrNull(inputs[index]))
        index = (index + 1) % inputs.size
    }

    @Benchmark
    fun Blackhole.reificationMaybe() {
        consume(reification.reifyMaybe(inputs[index]))
        index = (index + 1) % inputs.size
    }
}