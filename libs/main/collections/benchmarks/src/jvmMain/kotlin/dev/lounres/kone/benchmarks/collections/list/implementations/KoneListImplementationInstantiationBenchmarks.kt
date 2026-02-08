/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks.collections.list.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.generate
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.State


@State(Scope.Benchmark)
class KoneListImplementationInstantiationBenchmarks {
    @Param("0")
    final var size: UInt = 0u
    
    @Benchmark
    fun KoneArrayFixedCapacityList_null(blackhole: Blackhole) {
        blackhole.consume(KoneArrayFixedCapacityList.generate(size) { null })
    }
    
    @Benchmark
    fun KoneArrayFixedCapacityList_uint(blackhole: Blackhole) {
        blackhole.consume(KoneArrayFixedCapacityList.generate(size) { it })
    }
    
    @Benchmark
    fun KoneArrayFixedCapacityList_uint_empty(blackhole: Blackhole) {
        blackhole.consume(KoneArrayFixedCapacityList<UInt>(size))
    }
    
    @Benchmark
    fun KoneArraySettableList_null(blackhole: Blackhole) {
        blackhole.consume(KoneArraySettableList.generate(size) { null })
    }
    
    @Benchmark
    fun KoneArraySettableList_uint(blackhole: Blackhole) {
        blackhole.consume(KoneArraySettableList.generate(size) { it })
    }
}