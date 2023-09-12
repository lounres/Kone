/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.invoke
import com.lounres.kone.polynomial.manipulation.MultivariatePolynomialManipulationSpace
import com.lounres.kone.util.mapOperations.*
import com.lounres.kone.util.option.Option
import com.lounres.kone.util.option.maybe
import space.kscience.kmath.expressions.Symbol
import kotlin.jvm.JvmName


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
                        deg1 > deg2 -> return@Comparator 1
                        deg1 < deg2 -> return@Comparator -1
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
    public override val Pair<Symbol, UInt>.variable: Symbol get() = first
    public override val Pair<Symbol, UInt>.power: UInt get() = second

    // FIXME: KT-39449
//    public override fun signatureOf(vararg variablePowers: Pair<Symbol, UInt>): LabeledMonomialSignature = mapOf(*variablePowers)
    public override fun signatureOf(variablePowers: Collection<Pair<Symbol, UInt>>): LabeledMonomialSignature = variablePowers.toMap()
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    public override val LabeledMonomialSignature.size: Int get() = this.size
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    public override fun LabeledMonomialSignature.isEmpty(): Boolean = isEmpty()
    public override infix fun LabeledMonomialSignature.containsVariable(variable: Symbol): Boolean = variable in this
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    public override operator fun LabeledMonomialSignature.get(variable: Symbol): UInt = getOrDefault(variable, 0u)
    public override fun LabeledMonomialSignature.getOptional(variable: Symbol): Option<UInt> = computeOnOrElse(variable, Option.None) { _, power -> Option.Some(power) }
    public override val LabeledMonomialSignature.variables: Set<Symbol> get() = keys
    public override val LabeledMonomialSignature.powers: Set<Pair<Symbol, UInt>> get() = buildSet { entries.mapTo(this) { Pair(it.key, it.value) } }
    public override operator fun LabeledMonomialSignature.iterator(): Iterator<Pair<Symbol, UInt>> = powers.iterator()

    // FIXME: KT-39449
//    public override fun mutableSignatureOf(vararg variablePowers: Pair<Symbol, UInt>): MutableLabeledMonomialSignature = mutableMapOf(*variablePowers)
    public override fun mutableSignatureOf(variablePowers: Collection<Pair<Symbol, UInt>>): MutableLabeledMonomialSignature = variablePowers.toMap().toMutableMap()
    public override fun MutableLabeledMonomialSignature.getAndSet(variable: Symbol, power: UInt): UInt {
        var result = 0u
        this.putOrChange(variable, { power }) { it -> result = it; power }
        return result
    }
    public override operator fun MutableLabeledMonomialSignature.set(variable: Symbol, power: UInt) { this.put(variable, power) }
    public override fun MutableLabeledMonomialSignature.getAndRemove(variable: Symbol): UInt {
        val result = this.getOrDefault(variable, 0u)
        this.remove(variable)
        return result
    }
    @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
    public override fun MutableLabeledMonomialSignature.remove(variable: Symbol) {
        this.remove(variable)
    }
    public override fun LabeledMonomialSignature.toMutable(): MutableLabeledMonomialSignature = this.toMutableMap()

    public override fun monomial(signature: LabeledMonomialSignature, coefficient: C): Pair<LabeledMonomialSignature, C> = Pair(signature, coefficient)
    public override val Pair<LabeledMonomialSignature, C>.signature: LabeledMonomialSignature get() = first
    public override val Pair<LabeledMonomialSignature, C>.coefficient: C get() = second

    // FIXME: KT-39449
//    public override fun polynomialOf(vararg monomials: Pair<LabeledMonomialSignature, C>): LabeledPolynomial<C> = LabeledPolynomial(monomials.toMap())
    public override fun polynomialOf(monomials: Collection<Pair<LabeledMonomialSignature, C>>): LabeledPolynomial<C> = LabeledPolynomial(monomials.toMap())
    public override val LabeledPolynomial<C>.size: Int get() = this.coefficients.count { it.value != 0u }
    public override fun LabeledPolynomial<C>.isEmpty(): Boolean = this.coefficients.all { it.value == 0u }
    public override infix fun LabeledPolynomial<C>.containsSignature(signature: LabeledMonomialSignature): Boolean = signature in coefficients && constantRing { this@containsSignature[signature].isNotZero() }
    public override operator fun LabeledPolynomial<C>.get(signature: LabeledMonomialSignature): C = coefficients.getOrDefault(signature, constantZero)
    public override fun LabeledPolynomial<C>.getOptional(signature: LabeledMonomialSignature): Option<C> = coefficients.getOption(signature).maybe { constantRing { it.isNotZero() } }
    public override val LabeledPolynomial<C>.signatures: Set<LabeledMonomialSignature> get() = coefficients.keys.filterTo(mutableSetOf()) { constantRing { coefficients[it]!!.isNotZero() } }
    public override val LabeledPolynomial<C>.monomials: Set<Pair<LabeledMonomialSignature, C>> get() = buildSet { coefficients.entries.mapTo(this) { Pair(it.key, it.value) } }
    public override operator fun LabeledPolynomial<C>.iterator(): Iterator<Pair<LabeledMonomialSignature, C>> =
        object: Iterator<Pair<LabeledMonomialSignature, C>> {
            val innerIterator = coefficients.entries.iterator()
            override fun hasNext(): Boolean = innerIterator.hasNext()
            override fun next(): Pair<LabeledMonomialSignature, C> = innerIterator.next().let { Pair(it.key, it.value) }
        }

    // FIXME: KT-39449
//    public override fun mutablePolynomialOf(vararg monomials: Pair<LabeledMonomialSignature, C>): MutableLabeledPolynomial<C> = MutableLabeledPolynomial(mutableMapOf(*monomials))
    public override fun mutablePolynomialOf(monomials: Collection<Pair<LabeledMonomialSignature, C>>): MutableLabeledPolynomial<C> = MutableLabeledPolynomial(monomials.toMap().toMutableMap())
    public override fun MutableLabeledPolynomial<C>.getAndSet(signature: LabeledMonomialSignature, coefficient: C): C {
        var result = constantZero
        this.coefficients.putOrChange(signature, { coefficient }) { it -> result = it; coefficient }
        return result
    }
    public override operator fun MutableLabeledPolynomial<C>.set(signature: LabeledMonomialSignature, coefficient: C) {
        if (constantRing { coefficient.isZero() }) this.remove(signature)
        else this.coefficients[signature] = coefficient
    }
    public override fun MutableLabeledPolynomial<C>.getAndRemove(signature: LabeledMonomialSignature): C {
        val result = this.coefficients.getOrDefault(signature, constantZero)
        this.coefficients.remove(signature)
        return result
    }
    public override fun MutableLabeledPolynomial<C>.remove(signature: LabeledMonomialSignature) {
        this.coefficients.remove(signature)
    }
    public override fun LabeledPolynomial<C>.toMutable(): MutableLabeledPolynomial<C> = MutableLabeledPolynomial(coefficients.toMutableMap())
    // endregion
}

public class LabeledPolynomialSpaceOverField<C, out A : Field<C>>(constantRing: A) : LabeledPolynomialSpace<C, A>(constantRing), MultivariatePolynomialSpaceOverField<C, Symbol, LabeledPolynomial<C>, A> {
    public override fun LabeledPolynomial<C>.div(other: C): LabeledPolynomial<C> =
        LabeledPolynomial(
            coefficients.mapValues { constantRing { it.value / other } }
        )
}