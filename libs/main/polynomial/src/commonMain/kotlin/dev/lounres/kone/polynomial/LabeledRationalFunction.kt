/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial


public data class LabeledRationalFunction<Number>(
    public override val numerator: LabeledPolynomial<Number>,
    public override val denominator: LabeledPolynomial<Number>
) : RationalFunction<LabeledPolynomial<Number>> {
    override fun toString(): String = "LabeledRationalFunction${numerator.coefficients}/${denominator.coefficients}"
}

public open class LabeledRationalFunctionSpace<Number>(
    override val polynomialSpace: MultivariatePolynomialSpace<Number, LabeledVariable, LabeledPolynomial<Number>>
) : MultivariatePolynomialSpaceOfFractions<Number, LabeledVariable, LabeledPolynomial<Number>, LabeledRationalFunction<Number>>() {
    override fun constructRationalFunction(numerator: LabeledPolynomial<Number>, denominator: LabeledPolynomial<Number>): LabeledRationalFunction<Number> =
        LabeledRationalFunction<Number>(numerator, denominator)
}