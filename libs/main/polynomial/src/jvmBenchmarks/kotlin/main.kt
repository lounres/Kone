@file:Suppress("unused")

package com.lounres.kone.polynomial.benchmarks

import com.lounres.kone.algebraic.invoke
import com.lounres.kone.algebraic.field
import com.lounres.kone.polynomial.ListPolynomial
import com.lounres.kone.polynomial.listPolynomialSpace
import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
class ListPolynomialBenchmark {
    val listPolynomialContext = Double.field.listPolynomialSpace

    val polynomial = ListPolynomial(1.1, 1.1)

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    fun Blackhole.stupidTest() = listPolynomialContext {
        consume(polynomial pow 16u)
    }
}