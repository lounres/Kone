/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Ring


/**
 * Constructs a [ListPolynomial] instance with provided [coefficients]. The collection of coefficients will be reversed
 * if [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(coefficients: List<C>, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(with(coefficients) { if (reverse) reversed() else this })

/**
 * Constructs a [ListPolynomial] instance with provided [coefficients]. The collection of coefficients will be reversed
 * if [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(vararg coefficients: C, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(with(coefficients) { if (reverse) reversed() else toList() })

/**
 * Represents [this] constant as a [ListPolynomial].
 */
public fun <C> C.asListPolynomial() : ListPolynomial<C> = ListPolynomial(listOf(this))


// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

/**
 * Constructs [ListRationalFunction] instance with numerator and denominator constructed with provided
 * [numeratorCoefficients] and [denominatorCoefficients]. The both collections of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListRationalFunction(numeratorCoefficients: List<C>, denominatorCoefficients: List<C>, reverse: Boolean = false): ListRationalFunction<C> =
    ListRationalFunction<C>(
        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
        ListPolynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } )
    )
/**
 * Constructs [ListRationalFunction] instance with provided [numerator] and unit denominator.
 */
@Suppress("FunctionName")
public fun <C> Ring<C>.ListRationalFunction(numerator: ListPolynomial<C>): ListRationalFunction<C> =
    ListRationalFunction<C>(numerator, ListPolynomial(listOf(one)))
/**
 * Constructs [ListRationalFunction] instance with provided [numerator] and unit denominator.
 */
@Suppress("FunctionName")
public fun <C> ListRationalFunctionSpace<C, *, *>.ListRationalFunction(numerator: ListPolynomial<C>): ListRationalFunction<C> =
    ListRationalFunction<C>(numerator, polynomialOne)
/**
 * Constructs [ListRationalFunction] instance with numerator constructed with provided [numeratorCoefficients] and unit
 * denominator. The collection of numerator coefficients will be reversed if [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> Ring<C>.ListRationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): ListRationalFunction<C> =
    ListRationalFunction<C>(
        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
        ListPolynomial(listOf(one))
    )
/**
 * Constructs [ListRationalFunction] instance with numerator constructed with provided [numeratorCoefficients] and unit
 * denominator. The collection of numerator coefficients will be reversed if [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListRationalFunctionSpace<C, *, *>.ListRationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): ListRationalFunction<C> =
    ListRationalFunction<C>(
        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
        polynomialOne
    )

/**
 * Represents [this] constant as a rational function.
 */ // FIXME: When context receivers will be ready, delete this function and uncomment the following two
public fun <C> C.asListRationalFunction(ring: Ring<C>) : ListRationalFunction<C> = ring.ListRationalFunction(asListPolynomial())
///**
// * Represents [this] constant as a rational function.
// */
//context(Ring<C>)
//public fun <C> C.asListRationalFunction() : ListRationalFunction<C> = ListRationalFunction(asListPolynomial())
///**
// * Represents [this] constant as a rational function.
// */
//context(ListRationalFunctionSpace<C, *>)
//public fun <C> C.asListRationalFunction() : ListRationalFunction<C> = ListRationalFunction(asListPolynomial())