/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.invoke
import space.kscience.kmath.structures.Buffer
import kotlin.jvm.JvmName
import kotlin.math.max


public data class NumberedRationalFunction<C>(
    public override val numerator: NumberedPolynomial<C>,
    public override val denominator: NumberedPolynomial<C>
) : RationalFunction<C, NumberedPolynomial<C>> {
    override fun toString(): String = "NumberedRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

public open class NumberedRationalFunctionSpace<C, out A: Ring<C>, out PS: NumberedPolynomialSpace<C, A>> (
    override val polynomialRing : PS
) :
    RationalFunctionSpaceWithPolynomialSpace<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            PS,
            >,
    PolynomialSpaceOfFractions<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            >() {
    
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
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, C>): NumberedPolynomial<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedPolynomial<C>.substitute(argument: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, argument)
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, C>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedRationalFunction<C>.substitute(argument: Map<Int, NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, argument)
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<C>): NumberedPolynomial<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedPolynomial<C>.substitute(argument: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, argument)
    public inline fun NumberedRationalFunction<C>.substitute(argument: Buffer<C>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun NumberedRationalFunction<C>.substitute(arguments: Buffer<NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, arguments)
    @JvmName("substituteRationalFunction")
    public inline fun NumberedRationalFunction<C>.substitute(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, arguments)
    public inline fun NumberedPolynomial<C>.substituteFully(arguments: Buffer<C>): C = substituteFully(polynomialRing.ring, arguments)

    public inline fun NumberedPolynomial<C>.asFunction(): (Buffer<C>) -> C = asFunctionOver(polynomialRing.ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfConstant(): (Buffer<C>) -> C = asFunctionOfConstantOver(polynomialRing.ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedPolynomial<C> = asFunctionOfPolynomialOver(polynomialRing.ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfRationalFunction(): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = asFunctionOfRationalFunctionOver(polynomialRing.ring)
    public inline fun NumberedRationalFunction<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedRationalFunction<C> = asFunctionOfPolynomialOver(polynomialRing.ring)
    public inline fun NumberedRationalFunction<C>.asFunctionOfRationalFunction(): (Buffer<NumberedRationalFunction<C>>) -> NumberedRationalFunction<C> = asFunctionOfRationalFunctionOver(polynomialRing.ring)

    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<C>): C = substituteFully(polynomialRing.ring, arguments)
    @JvmName("invokePolynomial")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> = substitute(polynomialRing.ring, arguments)
    @JvmName("invokeRationalFunction")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, arguments)
    @JvmName("invokePolynomial")
    public inline operator fun NumberedRationalFunction<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, arguments)
    @JvmName("invokeRationalFunction")
    public inline operator fun NumberedRationalFunction<C>.invoke(arguments: Buffer<NumberedRationalFunction<C>>): NumberedRationalFunction<C> = substitute(polynomialRing.ring, arguments)
}

public typealias DefaultNumberedRationalFunctionSpace<C, A> = NumberedRationalFunctionSpace<C, A, NumberedPolynomialSpace<C, A>>

public class NumberedRationalFunctionSpaceOverField<C, out A: Field<C>, out PS: NumberedPolynomialSpaceOverField<C, A>>(
    polynomialRing : PS
) : NumberedRationalFunctionSpace<C, A, PS>(polynomialRing),
    RationalFunctionSpaceWithPolynomialSpace<
            C,
            NumberedPolynomial<C>,
            NumberedRationalFunction<C>,
            PS
            >

public typealias DefaultNumberedRationalFunctionSpaceOverField<C, A> = NumberedRationalFunctionSpaceOverField<C, A, NumberedPolynomialSpaceOverField<C, A>>