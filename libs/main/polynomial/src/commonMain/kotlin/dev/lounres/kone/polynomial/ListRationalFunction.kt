/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Ring


/**
 * Represents univariate rational function that stores its numerator and denominator as [ListPolynomial]s.
 */
public data class ListRationalFunction<Number>(
    public override val numerator: ListPolynomial<Number>,
    public override val denominator: ListPolynomial<Number>,
) : RationalFunction<ListPolynomial<Number>> {
    override fun toString(): String = "ListRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

/**
 * Arithmetic context for univariate rational functions with numerator and denominator represented as [ListPolynomial]s.
 *
 * @param Number the type of constants. Polynomials have them a coefficients in their terms.
 * @param A type of provided underlying ring of constants. It's [Ring] of [Number].
 */

public open class ListRationalFunctionSpace<Number>(
    override val polynomialSpace: PolynomialSpace<Number, ListPolynomial<Number>>,
) : PolynomialSpaceOfFractions<Number, ListPolynomial<Number>, ListRationalFunction<Number>>() {

    /**
     * Constructor of [ListRationalFunction] from numerator and denominator [ListPolynomial].
     */
    override fun constructRationalFunction(numerator: ListPolynomial<Number>, denominator: ListPolynomial<Number>): ListRationalFunction<Number> =
        ListRationalFunction(numerator, denominator)
}