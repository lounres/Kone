/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused")

package dev.lounres.kone.polynomial.benchmarks

import dev.lounres.kone.algebraic.field
import dev.lounres.kone.context.invoke
import dev.lounres.kone.polynomial.ListPolynomial
import dev.lounres.kone.polynomial.listPolynomialSpace
import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
class ListPolynomialBenchmarkJvm {
    val listPolynomialContext = Double.field.listPolynomialSpace

    val polynomial = ListPolynomial(1.1, 1.1)

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    fun Blackhole.stupidTest() = listPolynomialContext {
        consume(polynomial pow 16u)
    }
}