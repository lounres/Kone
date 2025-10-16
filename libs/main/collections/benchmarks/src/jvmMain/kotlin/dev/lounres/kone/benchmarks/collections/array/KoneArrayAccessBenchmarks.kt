/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks.collections.array

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.array.serializers.serializer
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.serializer
import java.io.File


@State(Scope.Benchmark)
class KoneArrayAccessBenchmarks {
    @Param("0")
    final var size: UInt = 0u

    class KoneArrayHolder(val array: KoneArray<Int>)
    final var arrayHolder: KoneArrayHolder = KoneArrayHolder(KoneArray.of())

    final var middleIndex: UInt = 0u
    final var lastIndex: UInt = 0u

    @Setup
    fun setup() {
        arrayHolder = KoneArrayHolder(Json.decodeFromStream(KoneArray.serializer<Int, Int>(serializer()), File("src/jvmMain/resources/array/$size.json").inputStream()))
        check(arrayHolder.array.size == size)
        middleIndex = size / 2u
        lastIndex = size - 1u
    }

    @Benchmark
    fun firstElement(blackhole: Blackhole) {
        blackhole.consume(arrayHolder.array[0u])
    }

    @Benchmark
    fun middleElement(blackhole: Blackhole) {
        blackhole.consume(arrayHolder.array[middleIndex])
    }

    @Benchmark
    fun lastElement(blackhole: Blackhole) {
        blackhole.consume(arrayHolder.array[lastIndex])
    }
}