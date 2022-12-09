@file:Suppress("unused")

package com.lounres.kone.polynomial.benchmarks

import com.lounres.kone.algebraic.field
import com.lounres.kone.polynomial.ListPolynomial
import com.lounres.kone.polynomial.listPolynomialSpace
import kotlinx.benchmark.*

@State(Scope.Benchmark)
class ListPolynomialBenchmark {
    @Benchmark
    fun stupidTest(blackhole: Blackhole) = Double.field.listPolynomialSpace {
        val polynomial = ListPolynomial(1.1, 1.1)
        blackhole.consume(polynomial pow 16u)
    }
}

