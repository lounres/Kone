///*
// * Copyright © 2025 Gleb Minaev
// * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
// */
//
//package dev.lounres.kone.relations
//
//import dev.lounres.kone.contexts.invoke
//import kotlinx.benchmark.Benchmark
//import kotlinx.benchmark.BenchmarkTimeUnit
//import kotlinx.benchmark.Blackhole
//import kotlinx.benchmark.OutputTimeUnit
//import kotlinx.benchmark.Scope
//import kotlinx.benchmark.State
//
//
//@OutputTimeUnit(BenchmarkTimeUnit.NANOSECONDS)
//@State(Scope.Benchmark)
//class IntDefaultImplementationsBenchmarks {
//    val a: Int = 2147483647
//    val b: Int = -2147483648
//    val structuralEquality = defaultEquality<Int>()
//    val referencesEquality = absoluteEquality<Int>()
//    val order = defaultOrder<Int>()
//    val hashing = defaultHashing<Int>()
//    val reification = Reification<Int>()
//
//    @Benchmark
//    fun structural_equality_via_primitives(blackhole: Blackhole) {
//        blackhole.consume(a == b)
//    }
//
//    @Benchmark
//    fun structural_equality_via_generics(blackhole: Blackhole) {
//        blackhole.consume((a as Any?) == (b as Any?))
//    }
//
//    @Benchmark
//    fun structural_equality_via_defaultEquality(blackhole: Blackhole) {
//        blackhole.consume(structuralEquality { a eq b })
//    }
//
//    @Benchmark
//    fun reference_equality_via_generics(blackhole: Blackhole) {
//        blackhole.consume((a as Any?) === (b as Any?))
//    }
//
//    @Benchmark
//    fun reference_equality_via_absoluteEquality(blackhole: Blackhole) {
//        blackhole.consume(referencesEquality { a eq b })
//    }
//
//    @Benchmark
//    fun comparison_via_primitives(blackhole: Blackhole) {
//        blackhole.consume(a < b)
//    }
//
//    fun <T: Comparable<T>> compare_via_comparability(a: T, b: T): Boolean = a < b
//
//    @Benchmark
//    fun comparison_via_comparability(blackhole: Blackhole) {
//        blackhole.consume(compare_via_comparability(a, b))
//    }
//
//    context(_: Order<T>)
//    fun <T> compare_via_defaultOrder_compareTo(a: T, b: T): Boolean = a < b
//
//    @Benchmark
//    fun comparison_via_defaultOrder_compareTo(blackhole: Blackhole) {
//        blackhole.consume(order { compare_via_defaultOrder_compareTo(a, b) })
//    }
//
//    @Benchmark
//    fun comparison_via_defaultOrder_compareWith(blackhole: Blackhole) {
//        blackhole.consume(order { a lt b })
//    }
//}