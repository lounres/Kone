/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Ring


public data class PolynomialSpaceScope<
    Number,
    Polynomial,
    out NumberContext: Ring<Number>,
    out PolynomialContext: PolynomialSpace<Number, Polynomial>,
>(
    public val numberContext: NumberContext,
    public val polynomialSpace: PolynomialContext,
)

public inline operator fun <
    Number,
    Polynomial,
    NumberContext: Ring<Number>,
    PolynomialContext: PolynomialSpace<Number, Polynomial>,
    Result
> PolynomialSpaceScope<Number, Polynomial, NumberContext, PolynomialContext>.invoke(block: context(NumberContext, PolynomialContext) () -> Result): Result {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(numberContext, polynomialSpace)
}