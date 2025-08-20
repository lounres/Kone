/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.relations.neq
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.numberTheory.binomial


fun main() {
    Rational.context /* It's another reference to RationalContext */ {
        val a = Rational(1, 2)
        val b = Rational(1, 3)

        // Operations with Rationals
        println(-a)
//      >>> -1/2
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

        // Context equality checkers do not support default Kotlin `equals` operator.
        // Instead, there are `equalsTo`, `notEqualsTo`, `eq`, `neq` infix operations.
        println(a equalsTo b)
//      >>> false
        println(a * 2 eq b * 3)
//      >>> true
        println(a * b neq b * a)
//      >>> false

        // Also, there are other equality checkers and operations defined in Reification, Order, Hashing, Semiring, Ring, and Field interfaces.
        // See API reference for the details.
    }

    // Contexts can also be used to return a result of computation inside them
    fun bernoulliNumber(n: UInt): Rational = Rational.context {
        // Initialise a list for storing the recursively computed Bernoulli numbers
        val bernoulliNumbers = KoneArrayFixedCapacityList<Rational>(n + 1u)
        bernoulliNumbers.add(Rational.context.one)

        // Compute the numbers with recurrent formula
        for (i in 1u..n)
            bernoulliNumbers.add(
                (1u..i).toKoneList().sumOf { k -> binomial(i + 1u, k + 1u) * bernoulliNumbers[i - k] } * -1 / (i + 1u)
            )

        // Return result
        bernoulliNumbers[n]
    }
    println(bernoulliNumber(14u))
//  >>> 7/6
}