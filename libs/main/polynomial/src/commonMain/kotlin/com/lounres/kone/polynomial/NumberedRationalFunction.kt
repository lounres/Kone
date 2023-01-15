/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.algebraic.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.jvm.JvmName
import kotlin.math.max


public data class NumberedRationalFunction<C>(
    public override val numerator: NumberedPolynomial<C>,
    public override val denominator: NumberedPolynomial<C>
) : RationalFunction<C, NumberedPolynomial<C>> {
    override fun toString(): String = "NumberedRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

public class NumberedRationalFunctionSpace<C, A: Ring<C>> (
    public val ring: A,
) :
    RationalFunctionSpaceOverPolynomialSpace<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            NumberedPolynomialSpace<C, A>,
            >,
    PolynomialSpaceOfFractions<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            >() {

    public override val polynomialRing : NumberedPolynomialSpace<C, A> = NumberedPolynomialSpace(ring)
    protected override fun constructRationalFunction(
        numerator: NumberedPolynomial<C>,
        denominator: NumberedPolynomial<C>
    ): NumberedRationalFunction<C> =
        NumberedRationalFunction(numerator, denominator)

    public val NumberedPolynomial<C>.lastVariable: Int get() = polynomialRing { lastVariable }
    public val NumberedPolynomial<C>.degrees: NumberedMonomialSignature get() = polynomialRing { degrees }
    public fun NumberedPolynomial<C>.degreeBy(variable: Int): UInt = polynomialRing { degreeBy(variable) }
    public fun NumberedPolynomial<C>.degreeBy(variables: Collection<Int>): UInt = polynomialRing { degreeBy(variables) }
    public val NumberedPolynomial<C>.countOfVariables: Int get() = polynomialRing { countOfVariables }

    public val NumberedRationalFunction<C>.lastVariable: Int
        get() = polynomialRing { max(numerator.lastVariable, denominator.lastVariable) }
    public val NumberedRationalFunction<C>.countOfVariables: Int
        get() =
            MutableList(lastVariable + 1) { false }.apply {
                numerator.coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
                denominator.coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
            }.count { it }

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, C>): NumberedPolynomial<C> = substitute(ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, C>): NumberedRationalFunction<C> = substitute(ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<C>): NumberedPolynomial<C> = substitute(ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, argument)
    public inline fun NumberedRationalFunction<C>.substitute(argument: Buffer<C>): NumberedRationalFunction<C> = substitute(ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedRationalFunction<C>.substitute(arguments: Buffer<NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedRationalFunction<C>.substitute(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    public inline fun NumberedPolynomial<C>.substituteFully(arguments: Buffer<C>): C = substituteFully(ring, arguments)

    public inline fun NumberedPolynomial<C>.asFunction(): (Buffer<C>) -> C = asFunctionOver(ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfConstant(): (Buffer<C>) -> C = asFunctionOfConstantOver(ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedPolynomial<C> = asFunctionOfPolynomialOver(ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfRationalFunction(): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = asFunctionOfRationalFunctionOver(ring)
    public inline fun NumberedRationalFunction<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedRationalFunction<C> = asFunctionOfPolynomialOver(ring)
    public inline fun NumberedRationalFunction<C>.asFunctionOfRationalFunction(): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = asFunctionOfRationalFunctionOver(ring)

    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<C>): C = substituteFully(ring, arguments)
    @JvmName("invokePolynomial")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(ring, arguments)
    @JvmName("invokeRationalFunction")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    @JvmName("invokePolynomial")
    public inline operator fun NumberedRationalFunction<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
    @JvmName("invokeRationalFunction")
    public inline operator fun NumberedRationalFunction<C>.invoke(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(ring, arguments)
}