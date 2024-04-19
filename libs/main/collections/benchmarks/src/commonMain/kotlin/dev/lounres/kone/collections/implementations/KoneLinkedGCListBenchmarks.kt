/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("ClassName")

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Equality
import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.Level


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class KoneLinkedGCListBenchmarks_Initialization {
    @Param(
        "1", "2", "4", "8", "16", "32", "64", "128",
        "256", "512", "1024", "2048", "4096", "8192", "16384", "32768",
//        "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304", "8388608",
//        "16777216", "33554432", "67108864", "134217728", "268435456", "536870912", "1073741824"
    )
    var listSize: UInt = 0u

    lateinit var list: KoneLinkedGCList<UInt, Equality<UInt>>

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        list = KoneLinkedGCList(listSize) { 0u }
        blackhole.consume(list)
    }
}

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class KoneLinkedGCListBenchmarks_RemoveFirst {
    @Param(
        "1", "2", "4", "8", "16", "32", "64", "128",
        "256", "512", "1024", "2048", "4096", "8192", "16384", "32768",
//        "65536", "131072", "262144", "524288", "1048576", "2097152", "4194304", "8388608",
//        "16777216", "33554432", "67108864", "134217728", "268435456", "536870912", "1073741824"
    )
    var listSize: UInt = 0u

    lateinit var list: KoneLinkedGCList<UInt, Equality<UInt>>

    @Setup(Level.Invocation)
    fun setup() {
        list = KoneLinkedGCList(listSize) { 0u }
    }

    @Benchmark
    fun benchmark(blackhole: Blackhole) {
        list.removeAt(0u)
        blackhole.consume(list)
    }
}