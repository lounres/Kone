/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("FunctionName", "NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.annotations.ExperimentalKoneAPI
import com.lounres.kone.utils.mapOperations.associateBy
import com.lounres.kone.utils.mapOperations.mapKeys
import com.lounres.kone.utils.mapOperations.putOrChange


internal fun NumberedMonomialSignature.cleanUp() = subList(0, indexOfLast { it != 0U } + 1)

@PublishedApi
internal inline fun <C> NumberedPolynomialAsIs(coefs: NumberedPolynomialCoefficients<C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(coefs)

@PublishedApi
internal inline fun <C> NumberedPolynomialAsIs(pairs: Collection<Pair<NumberedMonomialSignature, C>>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

@PublishedApi
internal inline fun <C> NumberedPolynomialAsIs(vararg pairs: Pair<NumberedMonomialSignature, C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

@DelicatePolynomialAPI
public inline fun <C> NumberedPolynomialWithoutCheck(coefs: NumberedPolynomialCoefficients<C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(coefs)

@DelicatePolynomialAPI
public inline fun <C> NumberedPolynomialWithoutCheck(pairs: Collection<Pair<NumberedMonomialSignature, C>>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

@DelicatePolynomialAPI
public inline fun <C> NumberedPolynomialWithoutCheck(vararg pairs: Pair<NumberedMonomialSignature, C>) : NumberedPolynomial<C> = NumberedPolynomial<C>(pairs.toMap())

public fun <C> NumberedPolynomial(coefs: NumberedPolynomialCoefficients<C>, add: (C, C) -> C) : NumberedPolynomial<C> =
    NumberedPolynomialAsIs(
        coefs.mapKeys({ (key, _) -> key.cleanUp() }, { _, c1, c2 -> add(c1, c2) })
    )

public fun <C> NumberedPolynomial(pairs: Collection<Pair<NumberedMonomialSignature, C>>, add: (C, C) -> C) : NumberedPolynomial<C> =
    NumberedPolynomialAsIs(
        pairs.associateBy({ it.first.cleanUp() }, { it.second }, { _, c1, c2 -> add(c1, c2)})
    )

public fun <C> NumberedPolynomial(vararg pairs: Pair<NumberedMonomialSignature, C>, add: (C, C) -> C) : NumberedPolynomial<C> =
    NumberedPolynomialAsIs(
        pairs.asIterable().associateBy({ it.first.cleanUp() }, { it.second }, { _, c1, c2 -> add(c1, c2)})
    )

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

public inline fun <C, A: Ring<C>> A.NumberedPolynomial(coefs: NumberedPolynomialCoefficients<C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs) { left, right -> left + right }
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(coefs: NumberedPolynomialCoefficients<C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs) { left: C, right: C -> left + right }

public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(coefs: NumberedPolynomialCoefficients<C>) : NumberedPolynomial<C> = NumberedPolynomial(coefs) { left: C, right: C -> left + right }

public inline fun <C, A: Ring<C>> A.NumberedPolynomial(pairs: Collection<Pair<NumberedMonomialSignature, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs) { left, right -> left + right }

public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<NumberedMonomialSignature, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs) { left: C, right: C -> left + right }
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(pairs: Collection<Pair<NumberedMonomialSignature, C>>) : NumberedPolynomial<C> = NumberedPolynomial(pairs) { left: C, right: C -> left + right }

public inline fun <C, A: Ring<C>> A.NumberedPolynomial(vararg pairs: Pair<NumberedMonomialSignature, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs) { left: C, right: C -> left + right }
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<NumberedMonomialSignature, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs) { left: C, right: C -> left + right }
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomial(vararg pairs: Pair<NumberedMonomialSignature, C>) : NumberedPolynomial<C> = NumberedPolynomial(*pairs) { left: C, right: C -> left + right }

public inline fun <C> C.asNumberedPolynomial() : NumberedPolynomial<C> = NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to this))

@DslMarker
@ExperimentalKoneAPI
internal annotation class NumberedPolynomialConstructorDSL1

@ExperimentalKoneAPI
@NumberedPolynomialConstructorDSL1
public class DSL1NumberedPolynomialTermSignatureBuilder {
    private val signature: MutableList<UInt> = ArrayList()

    @PublishedApi
    internal fun build(): NumberedMonomialSignature = signature

    public infix fun Int.inPowerOf(deg: UInt) {
        if (deg == 0u) return
        val index = this
        if (index > signature.lastIndex) {
            signature.addAll(List(index - signature.lastIndex - 1) { 0u })
            signature.add(deg)
        } else {
            signature[index] += deg
        }
    }
    public inline infix fun Int.pow(deg: UInt): Unit = this inPowerOf deg
    public inline infix fun Int.`in`(deg: UInt): Unit = this inPowerOf deg
    public inline infix fun Int.of(deg: UInt): Unit = this inPowerOf deg
}

@ExperimentalKoneAPI
@NumberedPolynomialConstructorDSL1
public class DSL1NumberedPolynomialBuilder<C>(
    private val add: (C, C) -> C,
    initialCapacity: Int? = null
) {
    private val coefficients: MutableMap<NumberedMonomialSignature, C> = if (initialCapacity != null) LinkedHashMap(initialCapacity) else LinkedHashMap()

    @PublishedApi
    internal fun build(): NumberedPolynomial<C> = NumberedPolynomial<C>(coefficients)

    public infix fun C.with(signature: NumberedMonomialSignature) {
        coefficients.putOrChange(signature, this@with) { _, c1, c2 -> add(c1, c2) }
    }
    public inline infix fun C.with(noinline block: DSL1NumberedPolynomialTermSignatureBuilder.() -> Unit): Unit = this.invoke(block)
    public inline operator fun C.invoke(block: DSL1NumberedPolynomialTermSignatureBuilder.() -> Unit): Unit =
        this with DSL1NumberedPolynomialTermSignatureBuilder().apply(block).build()
}

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

///**
// * Creates [NumberedPolynomial] with lambda [block] in context of [this] ring of constants.
// *
// * For example, polynomial \(5 x_0^2 x_2^3 - 6 x_1\) can be described as
// * ```
// * Int.algebra {
// *     val numberedPolynomial : NumberedPolynomial<Int> = NumberedPolynomial {
// *         5 { 0 inPowerOf 2u; 2 inPowerOf 3u } // 5 x_0^2 x_2^3 +
// *         (-6) { 1 inPowerOf 1u }              // (-6) x_1^1
// *     }
// * }
// * ```
// * @usesMathJax
// */
// FIXME: For now this fabric does not let next two fabrics work. (See KT-52803.) Possible feature solutions:
//  1. `LowPriorityInOverloadResolution` becomes public. Then it should be applied to this function.
//  2. Union types are implemented. Then all three functions should be rewritten
//     as one with single union type as a (context) receiver.
//@ExperimentalKoneAPI
//public inline fun <C, A: Ring<C>> A.NumberedPolynomialDSL1(initialCapacity: Int? = null, block: NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = NumberedPolynomialBuilder(::add, initialCapacity).apply(block).build()
@ExperimentalKoneAPI
public inline fun <C, A: Ring<C>> NumberedPolynomialSpace<C, A>.NumberedPolynomialDSL1(initialCapacity: Int? = null, block: DSL1NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = DSL1NumberedPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()
@ExperimentalKoneAPI
public inline fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedPolynomialDSL1(initialCapacity: Int? = null, block: DSL1NumberedPolynomialBuilder<C>.() -> Unit) : NumberedPolynomial<C> = DSL1NumberedPolynomialBuilder({ left: C, right: C -> left + right }, initialCapacity).apply(block).build()

// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

public fun <C, A: Ring<C>> A.NumberedRationalFunction(numeratorCoefficients: NumberedPolynomialCoefficients<C>, denominatorCoefficients: NumberedPolynomialCoefficients<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        NumberedPolynomial(denominatorCoefficients)
    )
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numeratorCoefficients: NumberedPolynomialCoefficients<C>, denominatorCoefficients: NumberedPolynomialCoefficients<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        NumberedPolynomial(denominatorCoefficients)
    )

public fun <C, A: Ring<C>> A.NumberedRationalFunction(numerator: NumberedPolynomial<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(numerator, NumberedPolynomial(mapOf(emptyList<UInt>() to one)))
public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numerator: NumberedPolynomial<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(numerator, polynomialOne)

public fun <C, A: Ring<C>> NumberedRationalFunctionSpace<C, A>.NumberedRationalFunction(numeratorCoefficients: NumberedPolynomialCoefficients<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        polynomialOne
    )
public fun <C, A: Ring<C>> A.NumberedRationalFunction(numeratorCoefficients: NumberedPolynomialCoefficients<C>): NumberedRationalFunction<C> =
    NumberedRationalFunction<C>(
        NumberedPolynomial(numeratorCoefficients),
        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to one))
    )

// Waiting for context receivers :( FIXME: Uncomment when context receivers will be available

///**
// * Converts [this] constant to [NumberedRationalFunction].
// */
//context(A)
//public fun <C, A: Ring<C>> C.asNumberedRationalFunction() : NumberedRationalFunction<C> =
//    NumberedRationalFunction(
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to this)),
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to one))
//    )
///**
// * Converts [this] constant to [NumberedRationalFunction].
// */
//context(NumberedRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> C.asNumberedRationalFunction() : NumberedRationalFunction<C> =
//    NumberedRationalFunction(
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to this)),
//        NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to constantOne))
//    )