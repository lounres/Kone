package com.lounres.kone.examples.kone_algebraic

import com.lounres.kone.algebraic.*


fun binomial(n: Int, k: Int): Int {
    val k = if (k <= n / 2) k else n-k
    var acc = 1
    for (i in 1..k) acc = acc * (n-i+1) / i
    return acc
}

fun main() {
    Rational.field /* It's a another reference to RationalField */ {
        val a = Rational(1, 2)
        val b = Rational(1, 3)

        // Operations with Rationals
        println(+a)
//      >>> 1/2
        println(-b)
//      >>> -1/3
        println(a + b)
//      >>> 5/6
        println(a - b)
//      >>> 1/6
        println(a * b)
//      >>> 1/6
        println(a / b)
//      >>> 3/2

        // Operations with Rationals and integers
        println(b - 1)
//      >>> -2/3
        println(34 * a)
//      >>> 17

        // Operations with only integers are not supported! Kotlin built-in operations are used
        println((5 * 7)::class.simpleName)
//      >>> Int

        // Context equality checkers does not support default Kotlin `equals` operator.
        // Instead there are `equalsTo`, `notEqualsTo`, `eq`, `neq` infix operations.
        println(a equalsTo b)
//      >>> false
        println(a * 2 eq b * 3)
//      >>> true
        println(a * b neq b * a)
//      >>> false

        // Also, there are other equality checkers and operations defined in Ring and Field interfaces.
        // See API reference for the details.
    }

    // Contexts can also be used to return result of computation inside them
    fun bernoulliNumber(n: Int): Rational = Rational.field {
        val bernoulliNumbers = Array<Rational?>(n + 1) { null }
        bernoulliNumbers[0] = one
        for (i in 1..n) bernoulliNumbers[i] =
            (1..i)
                .map { k -> binomial(i + 1, k + 1) * bernoulliNumbers[i - k]!! }
                .reduce { acc, r -> acc + r } / -(i + 1)
        bernoulliNumbers[n]!!
    }
    println(bernoulliNumber(14))
//  >>> 7/6
}