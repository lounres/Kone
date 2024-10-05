/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Equality
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State
import org.openjdk.jmh.annotations.Level


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class KoneResizableLinkedArrayListBenchmarks {
    @Param(
        "1", "2", "4", "8", "16", "32", "64", "128",
        "256", "512", "1024", "2048", "4096", "8192", "16384", "32768",
//        "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304", "8388608",
//        "16777216", "33554432", "67108864", "134217728", "268435456", "536870912", "1073741824"
    )
    var listSize: UInt = 0u

    lateinit var list: KoneResizableLinkedArrayList<UInt, Equality<UInt>>

    @Setup(Level.Invocation)
    fun setupListContent() {
        list = KoneResizableLinkedArrayList<UInt>(listSize) { 0u }
    }

    @Benchmark
    fun KoneResizableLinkedArrayList_removeFirst(blackhole: Blackhole) {
        list.removeAt(0u)
        blackhole.consume(list)
    }
}