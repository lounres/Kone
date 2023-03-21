/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.computationalContext.invoke
import com.lounres.kone.annotations.UnstableKoneAPI
import kotlin.math.max
import kotlin.math.pow


/**
 * Creates a [ListPolynomialSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.listPolynomialSpace: ListPolynomialSpace<C, A>
    get() = ListPolynomialSpace(this)

/**
 * Creates a [ListPolynomialSpaceOverField] over a received field.
 */
public inline val <C, A : Field<C>> A.listPolynomialSpace: ListPolynomialSpaceOverField<C, A>
    get() = ListPolynomialSpaceOverField(this)

/**
 * Creates a [ListRationalFunctionSpace] over a received ring.
 */
public inline val <C, A : Ring<C>> A.listRationalFunctionSpace: DefaultListRationalFunctionSpace<C, A>
    get() = ListRationalFunctionSpace(this.listPolynomialSpace)

/**
 * Creates a [ListRationalFunctionSpaceOverField] over a received field.
 */
public inline val <C, A : Field<C>> A.listRationalFunctionSpace: DefaultListRationalFunctionSpaceOverField<C, A>
    get() = ListRationalFunctionSpaceOverField(this.listPolynomialSpace)


/**
 * Evaluates value of [this] Double polynomial on provided Double argument.
 */
public fun ListPolynomial<Double>.substitute(arg: Double): Double =
    coefficients.reduceIndexedOrNull { index, acc, c ->
        acc + c * arg.pow(index)
    } ?: .0

/**
 * Evaluates value of [this] polynomial on provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: C): C = ring {
    if (coefficients.isEmpty()) return zero
    var result: C = coefficients.last()
    for (j in coefficients.size - 2 downTo 0) {
        result = (arg * result) + coefficients[j]
    }
    return result
}

/**
 * Substitutes provided polynomial [arg] into [this] polynomial.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */ // TODO: To optimize boxing
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: ListPolynomial<C>) : ListPolynomial<C> =
    ring.listPolynomialSpace {
        if (coefficients.isEmpty()) return zero
        var result: ListPolynomial<C> = coefficients.last().value
        for (j in coefficients.size - 2 downTo 0) {
            result = (arg * result) + coefficients[j]
        }
        return result
    }

/**
 * Substitutes provided rational function [arg] into [this] polynomial.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */ // TODO: To optimize boxing
public fun <C> ListPolynomial<C>.substitute(ring: Ring<C>, arg: ListRationalFunction<C>) : ListRationalFunction<C> =
    ring.listRationalFunctionSpace {
        if (coefficients.isEmpty()) return zero
        var result: ListRationalFunction<C> = coefficients.last().value
        for (j in coefficients.size - 2 downTo 0) {
            result = (arg * result) + coefficients[j]
        }
        return result
    }

/**
 * Evaluates value of [this] Double rational function in provided Double argument.
 */
public fun ListRationalFunction<Double>.substitute(arg: Double): Double =
    numerator.substitute(arg) / denominator.substitute(arg)

/**
 * Evaluates value of [this] polynomial for provided argument.
 *
 * It is an implementation of [Horner's method](https://en.wikipedia.org/wiki/Horner%27s_method).
 */
public fun <C> ListRationalFunction<C>.substitute(ring: Field<C>, arg: C): C = ring {
    numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
}

/**
 * Substitutes provided polynomial [arg] into [this] rational function.
 */ // TODO: To optimize boxing
public fun <C> ListRationalFunction<C>.substitute(ring: Ring<C>, arg: ListPolynomial<C>) : ListRationalFunction<C> =
    ring.listRationalFunctionSpace {
        numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
    }

/**
 * Substitutes provided rational function [arg] into [this] rational function.
 */ // TODO: To optimize boxing
public fun <C> ListRationalFunction<C>.substitute(ring: Ring<C>, arg: ListRationalFunction<C>) : ListRationalFunction<C> =
    ring.listRationalFunctionSpace {
        numerator.substitute(ring, arg) / denominator.substitute(ring, arg)
    }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C> ListPolynomial<C>.asFunctionOver(ring: Ring<C>): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C> ListPolynomial<C>.asFunctionOfConstantOver(ring: Ring<C>): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C> ListPolynomial<C>.asFunctionOfPolynomialOver(ring: Ring<C>): (ListPolynomial<C>) -> ListPolynomial<C> = { substitute(ring, it) }

/**
 * Represent [this] polynomial as a regular context-less function.
 */
public fun <C> ListPolynomial<C>.asFunctionOfRationalFunctionOver(ring: Ring<C>): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C> ListRationalFunction<C>.asFunctionOver(ring: Field<C>): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C> ListRationalFunction<C>.asFunctionOfConstantOver(ring: Field<C>): (C) -> C = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C> ListRationalFunction<C>.asFunctionOfPolynomialOver(ring: Ring<C>): (ListPolynomial<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

/**
 * Represent [this] rational function as a regular context-less function.
 */
public fun <C> ListRationalFunction<C>.asFunctionOfRationalFunctionOver(ring: Ring<C>): (ListRationalFunction<C>) -> ListRationalFunction<C> = { substitute(ring, it) }

/**
 * Returns algebraic derivative of received polynomial.
 */
@UnstableKoneAPI
public fun <C, A> ListPolynomial<C>.derivative(
    ring: A,
): ListPolynomial<C> where  A : Ring<C> = ring {
    ListPolynomial(
        buildList(max(0, coefficients.size - 1)) {
            for (deg in 1 .. coefficients.lastIndex) add(deg * coefficients[deg])
        }
    )
}

/**
 * Returns algebraic derivative of received polynomial of specified [order]. The [order] should be non-negative integer.
 */
@UnstableKoneAPI
public fun <C, A> ListPolynomial<C>.nthDerivative(
    ring: A,
    order: Int,
): ListPolynomial<C> where A : Ring<C> = ring {
    require(order >= 0) { "Order of derivative must be non-negative" }
    ListPolynomial(
        buildList(max(0, coefficients.size - order)) {
            for (deg in order.. coefficients.lastIndex)
                add((deg - order + 1 .. deg).fold(coefficients[deg]) { acc, d -> acc * d })
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial.
 */
@UnstableKoneAPI
public fun <C, A> ListPolynomial<C>.antiderivative(
    ring: A,
): ListPolynomial<C> where  A : Field<C> = ring {
    ListPolynomial(
        buildList(coefficients.size + 1) {
            add(zero)
            coefficients.mapIndexedTo(this) { index, t -> t / (index + 1) }
        }
    )
}

/**
 * Returns algebraic antiderivative of received polynomial of specified [order]. The [order] should be non-negative integer.
 */
@UnstableKoneAPI
public fun <C, A> ListPolynomial<C>.nthAntiderivative(
    ring: A,
    order: Int,
): ListPolynomial<C> where  A : Field<C> = ring {
    require(order >= 0) { "Order of antiderivative must be non-negative" }
    ListPolynomial(
        buildList(coefficients.size + order) {
            repeat(order) { add(zero) }
            coefficients.mapIndexedTo(this) { index, c -> (1..order).fold(c) { acc, i -> acc / (index + i) } }
        }
    )
}

/**
 * Computes a definite integral of [this] polynomial in the specified [range].
 */
@UnstableKoneAPI
public fun <C : Comparable<C>> ListPolynomial<C>.integrate(
    ring: Field<C>,
    range: ClosedRange<C>,
): C = ring {
    val antiderivative = antiderivative(ring)
    antiderivative.substitute(ring, range.endInclusive) - antiderivative.substitute(ring, range.start)
}