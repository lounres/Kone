/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import space.kscience.kmath.expressions.Symbol
import com.lounres.kone.algebraic.Ring
import kotlin.jvm.JvmName


public data class LabeledRationalFunction<C>(
    public override val numerator: LabeledPolynomial<C>,
    public override val denominator: LabeledPolynomial<C>
) : RationalFunction<C, LabeledPolynomial<C>> {
    override fun toString(): String = "LabeledRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

public class LabeledRationalFunctionSpace<C, A: Ring<C>>(
    public val ring: A,
) :
    MultivariateRationalFunctionSpaceOverMultivariatePolynomialSpace<
            C,
            Symbol,
            LabeledPolynomial<C>,
            LabeledRationalFunction<C>,
            LabeledPolynomialSpace<C, A>,
            >,
    MultivariatePolynomialSpaceOfFractions<
            C,
            Symbol,
            LabeledPolynomial<C>,
            LabeledRationalFunction<C>,
            >() {

    override val polynomialRing : LabeledPolynomialSpace<C, A> = LabeledPolynomialSpace(ring)
    override fun constructRationalFunction(
        numerator: LabeledPolynomial<C>,
        denominator: LabeledPolynomial<C>
    ): LabeledRationalFunction<C> =
        LabeledRationalFunction<C>(numerator, denominator)

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Symbol, C>): LabeledPolynomial<C> = substitute(ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Symbol, LabeledPolynomial<C>>): LabeledPolynomial<C> = substitute(ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun LabeledPolynomial<C>.substitute(argument: Map<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> = substitute(ring, argument)
    public inline fun LabeledRationalFunction<C>.substitute(argument: Map<Symbol, C>): LabeledRationalFunction<C> = substitute(ring, argument)
    @JvmName("substitutePolynomial")
    public inline fun LabeledRationalFunction<C>.substitute(argument: Map<Symbol, LabeledPolynomial<C>>): LabeledRationalFunction<C> = substitute(ring, argument)
    @JvmName("substituteRationalFunction")
    public inline fun LabeledRationalFunction<C>.substitute(argument: Map<Symbol, LabeledRationalFunction<C>>): LabeledRationalFunction<C> = substitute(ring, argument)
}