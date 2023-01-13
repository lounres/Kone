package com.lounres.kone.polynomial.examples

import com.lounres.kone.algebraic.field
import com.lounres.kone.algebraic.invoke
import com.lounres.kone.examples.execute
import com.lounres.kone.polynomial.*
import space.kscience.kmath.expressions.symbol


fun main() = execute(
    ::listPolynomialsExample,
    ::numberedPolynomialsExample,
    ::labeledPolynomialsExample,
)

fun listPolynomialsExample() = Double.field.listPolynomialSpace {
    val x = freeVariable
    val xChoose3 = x * (x - 1) * (x - 2) / 6
    println(xChoose3)
    println()
    for (p in listOf(0.0, 1.0, 2.0, 3.0)) {
        println(
            """
                Value: $p
                Polynomial substitution: ${xChoose3.substitute(p)}
                Expanded computation: ${(p * p * p * .16666666666666666 - p * p * .5 + p * .3333333333333333)}
                Straightforward computation: ${p * (p - 1) * (p - 2) / 6}
                
            """.trimIndent()
        )
    }
}

fun numberedPolynomialsExample() = Double.field.numberedPolynomialSpace {
    val x = NumberedPolynomial(listOf(1u) to 1.0)
    val xChoose3 = x * (x - 1) * (x - 2) / 6
    println(xChoose3)
    println()
    for (p in listOf(0.0, 1.0, 2.0, 3.0)) {
        println(
            """
                Value: $p
                Polynomial substitution: ${xChoose3.substitute(0 to p)}
                Expanded computation: ${(p * p * p * .16666666666666666 - p * p * .5 + p * .3333333333333333)}
                Straightforward computation: ${p * (p - 1) * (p - 2) / 6}
                
            """.trimIndent()
        )
    }
}

fun labeledPolynomialsExample() = Double.field.labeledPolynomialSpace {
    val x by symbol
    val xChoose3 = x * (x - 1) * (x - 2) / 6
    println(xChoose3)
    println()
    for (p in listOf(0.0, 1.0, 2.0, 3.0)) {
        println(
            """
                Value: $p
                Polynomial substitution: ${xChoose3.substitute(x to p)}
                Expanded computation: ${(p * p * p * .16666666666666666 - p * p * .5 + p * .3333333333333333)}
                Straightforward computation: ${p * (p - 1) * (p - 2) / 6}
                
            """.trimIndent()
        )
    }
}