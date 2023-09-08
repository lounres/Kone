/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.polynomial.manipulation.MultivariatePolynomialManipulationSpace
import space.kscience.kmath.expressions.Symbol


public open class LabeledPolynomial<C>
@PublishedApi
internal constructor(
    public open val coefficients: LabeledPolynomialCoefficients<C>
) {
    override fun toString(): String = "LabeledPolynomial$coefficients"

    public object signatureComparator {
        public fun lexBy(variableComparator: Comparator<Symbol>): Comparator<LabeledMonomialSignature> =
            Comparator { o1: Map<Symbol, UInt>, o2: Map<Symbol, UInt> ->
                if (o1 === o2) return@Comparator 0

                val commonVariables = (o1.keys union o2.keys).sortedWith(variableComparator)

                for (variable in commonVariables) {
                    val deg1 = o1.getOrElse(variable) { 0u }
                    val deg2 = o2.getOrElse(variable) { 0u }
                    when {
                        deg1 > deg2 -> return@Comparator -1
                        deg1 < deg2 -> return@Comparator 1
                    }
                }

                return@Comparator 0
            }
        public val lex: Comparator<LabeledMonomialSignature> = lexBy { o1: Symbol, o2: Symbol -> o1.identity.compareTo(o2.identity) }

        public fun deglexBy(variableComparator: Comparator<Symbol>): Comparator<LabeledMonomialSignature> =
            Comparator { o1: Map<Symbol, UInt>, o2: Map<Symbol, UInt> -> o1.values.sum().compareTo(o2.values.sum()) } then lexBy(variableComparator)
        public val deglex: Comparator<LabeledMonomialSignature> = deglexBy { o1: Symbol, o2: Symbol -> o1.identity.compareTo(o2.identity) }

        public fun degrevlexBy(variableComparator: Comparator<Symbol>): Comparator<LabeledMonomialSignature> =
            Comparator { o1: Map<Symbol, UInt>, o2: Map<Symbol, UInt> -> o1.values.sum().compareTo(o2.values.sum()) } then lexBy(variableComparator).reversed()
        public val degrevlex: Comparator<LabeledMonomialSignature> = degrevlexBy { o1: Symbol, o2: Symbol -> o1.identity.compareTo(o2.identity) }

    }
}

public data class MutableLabeledPolynomial<C>
@PublishedApi
internal constructor(
    public override val coefficients: MutableLabeledPolynomialCoefficients<C>
) : LabeledPolynomial<C>(coefficients) {
    override fun toString(): String = "MutableLabeledPolynomial$coefficients"
}

public typealias LabeledMonomialSignature = Map<Symbol, UInt>
public typealias MutableLabeledMonomialSignature = MutableMap<Symbol, UInt>
public typealias LabeledPolynomialCoefficients<C> = Map<LabeledMonomialSignature, C>
public typealias MutableLabeledPolynomialCoefficients<C> = MutableMap<LabeledMonomialSignature, C>

public open class LabeledPolynomialSpace<C, out A : Ring<C>>(override val constantRing: A) :
    MultivariatePolynomialManipulationSpace<C, Symbol, Pair<Symbol, UInt>, LabeledMonomialSignature, MutableLabeledMonomialSignature, Pair<LabeledMonomialSignature, C>, LabeledPolynomial<C>, MutableLabeledPolynomial<C>, A>
{
    // region Manipulation
    public override fun variablePower(variable: Symbol, power: UInt): Pair<Symbol, UInt> = Pair(variable, power)

    public override fun signatureOf(vararg variablePowers: Pair<Symbol, UInt>): LabeledMonomialSignature = mapOf(*variablePowers)

    public override fun monomial(signature: LabeledMonomialSignature, coefficient: C): Pair<LabeledMonomialSignature, C> = Pair(signature, coefficient)

    public override fun polynomialOf(vararg monomials: Pair<LabeledMonomialSignature, C>): LabeledPolynomial<C> = LabeledPolynomial(monomials.toMap())
    // endregion
}