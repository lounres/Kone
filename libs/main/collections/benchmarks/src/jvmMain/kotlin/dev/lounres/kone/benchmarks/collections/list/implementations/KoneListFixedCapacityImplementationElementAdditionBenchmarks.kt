/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.benchmarks.collections.list.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.generate
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State


@State(Scope.Benchmark)
class KoneListFixedCapacityImplementationElementAdditionBenchmarks {
    @Param("179:57")
    final var capacityAndSize: String = "179:57"
    
    final var capacity: UInt = 179u
    final var size: UInt = 57u
    
    @Setup
    fun setup() {
        val [capacity, size] = capacityAndSize.split(":")
        this.capacity = capacity.toUInt()
        this.size = size.toUInt()
    }
    
    @Benchmark
    fun KoneArrayFixedCapacityList_instantiate(blackhole: Blackhole) {
        val result = KoneArrayFixedCapacityList.generate(capacity = capacity, size = size) { it }
        blackhole.consume(result)
    }
    
    @Benchmark
    fun KoneArrayFixedCapacityList_fill(blackhole: Blackhole) {
        val result = KoneArrayFixedCapacityList<UInt>(capacity = capacity)
        for (index in 0u ..< size) result.add(index)
        blackhole.consume(result)
    }
}