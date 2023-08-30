/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.invoke
import com.lounres.kone.util.mapOperations.*
import space.kscience.kmath.expressions.Symbol
import kotlin.jvm.JvmName
import kotlin.math.max


public data class LabeledPolynomial<C>
@PublishedApi
internal constructor(
    public val coefficients: LabeledPolynomialCoefficients<C>
) : Polynomial<C> {
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

public typealias LabeledMonomialSignature = Map<Symbol, UInt>
public typealias LabeledPolynomialCoefficients<C> = Map<LabeledMonomialSignature, C>

public open class LabeledPolynomialSpace<C, out A : Ring<C>>(override val constantRing: A) : MultivariatePolynomialSpace<C, Symbol, LabeledPolynomial<C>, A> {
    override val zero: LabeledPolynomial<C> = LabeledPolynomialAsIs()
    override val one: LabeledPolynomial<C> by lazy { constantOne.asLabeledPolynomial() }

    public override infix fun LabeledPolynomial<C>.equalsTo(other: LabeledPolynomial<C>): Boolean =
        mergingAll(this.coefficients, other.coefficients, { constantRing { it.value.isZero() } }, { constantRing { it.value.isZero() } }) { _, c1, c2 -> constantRing { c1 equalsTo c2 }}
    public override fun LabeledPolynomial<C>.isZero(): Boolean = coefficients.values.all { constantRing { it.isZero() } }
    public override fun LabeledPolynomial<C>.isOne(): Boolean = coefficients.all { it.key.isEmpty() || constantRing { it.value.isZero() } }

    public override fun polynomialValueOf(value: C): LabeledPolynomial<C> = value.asLabeledPolynomial()

    public override operator fun Symbol.plus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to other.constantValue,
        )
    public override operator fun Symbol.minus(other: Int): LabeledPolynomial<C> =
        if (other == 0) LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to (-other).constantValue,
        )
    public override operator fun Symbol.times(other: Int): LabeledPolynomial<C> =
        if (other == 0) zero
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to other.constantValue,
        )

    public override operator fun Symbol.plus(other: Long): LabeledPolynomial<C> =
        if (other == 0L) LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to other.constantValue,
        )
    public override operator fun Symbol.minus(other: Long): LabeledPolynomial<C> =
        if (other == 0L) LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to (-other).constantValue,
        )
    public override operator fun Symbol.times(other: Long): LabeledPolynomial<C> =
        if (other == 0L) zero
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to other.constantValue,
        )

    public override operator fun Int.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to this@plus.constantValue,
        )
    public override operator fun Int.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantRing { -constantOne },
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantRing { -constantOne },
            emptyMap<Symbol, UInt>() to constantRing { constantOne * this@minus },
        )
    public override operator fun Int.times(other: Symbol): LabeledPolynomial<C> =
        if (this == 0) zero
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to this@times.constantValue,
        )

    public override operator fun Long.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0L) LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to this@plus.constantValue,
        )
    public override operator fun Long.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == 0L) LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantRing { -constantOne },
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantRing { -constantOne },
            emptyMap<Symbol, UInt>() to constantRing { constantOne * this@minus },
        )
    public override operator fun Long.times(other: Symbol): LabeledPolynomial<C> =
        if (this == 0L) zero
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to this@times.constantValue,
        )

    public override operator fun LabeledPolynomial<C>.plus(other: Int): LabeledPolynomial<C> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), { other.constantValue }) { it -> constantRing { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<C>.minus(other: Int): LabeledPolynomial<C> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), { (-other).constantValue }) { it -> constantRing { it - other } }
            )
        }
    public override operator fun LabeledPolynomial<C>.times(other: Int): LabeledPolynomial<C> =
        when(other) {
            0 -> zero
            1 -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValues { (_, value) -> constantRing { value * other } }
            )
        }

    public override operator fun LabeledPolynomial<C>.plus(other: Long): LabeledPolynomial<C> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), { other.constantValue }) { it -> constantRing { it + other } }
            )
        }
    public override operator fun LabeledPolynomial<C>.minus(other: Long): LabeledPolynomial<C> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), { (-other).constantValue }) { it -> constantRing { it - other } }
            )
        }
    public override operator fun LabeledPolynomial<C>.times(other: Long): LabeledPolynomial<C> =
        when(other) {
            0L -> zero
            1L -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValues { (_, value) -> constantRing { value * other } }
            )
        }

    public override operator fun Int.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0 -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withPutOrChanged(emptyMap(), { this@plus.constantValue }) { it -> constantRing { this@plus + it } }
            )
        }
    public override operator fun Int.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyMap(), other.coefficients.computeOnOrElse(emptyMap(), { this@minus.constantValue }) { it -> constantRing { this@minus - it } })
                    other.coefficients.copyMapToBy(this, { (_, c) -> constantRing { -c } }) { _, currentC, _ -> currentC }
                }
            )
        }
    public override operator fun Int.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when(this) {
            0 -> zero
            1 -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValues { (_, value) -> constantRing { this@times * value } }
            )
        }

    public override operator fun Long.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0L -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withPutOrChanged(emptyMap(), this@plus.constantValue) { _, it, _ -> constantRing { this@plus + it } }
            )
        }
    public override operator fun Long.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0L -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyMap(), other.coefficients.computeOnOrElse(emptyMap(), this@minus.constantValue) { _, it -> constantRing { this@minus - it } })
                    other.coefficients.copyMapToBy(this, { (_, c) -> constantRing { -c } }) { _, currentC, _ -> currentC }
                }
            )
        }
    public override operator fun Long.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when(this) {
            0L -> zero
            1L -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValues { (_, value) -> constantRing { this@times * value } }
            )
        }

    public override operator fun Symbol.plus(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this@plus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to other,
        )
    public override operator fun Symbol.minus(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this@minus to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to constantRing { -other },
        )
    public override operator fun Symbol.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this@times to 1U) to other,
        )

    public override operator fun C.plus(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantOne,
            emptyMap<Symbol, UInt>() to this@plus,
        )
    public override operator fun C.minus(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to constantRing { -constantOne },
            emptyMap<Symbol, UInt>() to this@minus,
        )
    public override operator fun C.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to this@times,
        )

    override operator fun LabeledPolynomial<C>.plus(other: C): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(emptyMap(), other) { _, it, _ -> constantRing { it + other } }
        )
    override operator fun LabeledPolynomial<C>.minus(other: C): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(emptyMap(), constantRing { -other }) { _, it, _ -> constantRing { it - other } }
        )
    override operator fun LabeledPolynomial<C>.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { constantRing { it.value * other } }
        )

    override operator fun C.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyMap(), this@plus) { _, it, _ -> constantRing { this@plus + it } }
        )
    override operator fun C.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.polynomialValue
        else LabeledPolynomialAsIs(
            buildMap(other.coefficients.size + 1) {
                put(emptyMap(), this@minus)
                other.coefficients.copyMapToBy(this, { (_, c) -> constantRing { -c } }, { _, currentC, newC -> constantRing { currentC - newC } })
            }
        )
    override operator fun C.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients.mapValues { constantRing { this@times * it.value } }
        )

    public override operator fun Symbol.unaryPlus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
        )
    public override operator fun Symbol.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantRing { -constantOne },
        )
    public override operator fun Symbol.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantRing { constantOne * 2 }
        )
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to constantOne,
        )
    public override operator fun Symbol.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) zero
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to constantRing { -constantOne },
        )
    public override operator fun Symbol.times(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(
            mapOf(this to 2U) to constantOne
        )
        else LabeledPolynomialAsIs(
            mapOf(this to 1U, other to 1U) to constantOne,
        )

    public override operator fun Symbol.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.polynomialValue
        else LabeledPolynomialAsIs(
            other.coefficients.withPutOrChanged(mapOf(this@plus to 1U), constantOne) { _, it, _ -> constantRing { constantOne + it } }
        )
    public override operator fun Symbol.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.polynomialValue
        else LabeledPolynomialAsIs(
            buildMap(other.coefficients.size + 1) {
                put(mapOf(this@minus to 1U), constantOne)
                other.coefficients.copyMapToBy(this, { (_, c) -> constantRing { -c } }, { _, currentC, newC -> constantRing { currentC - newC } })
            }
        )
    public override operator fun Symbol.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients
                .mapKeys { (degs, _) -> degs.withPutOrChanged(this, 1u) { _, it, _ -> it + 1u } }
        )

    public override operator fun LabeledPolynomial<C>.plus(other: Symbol): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.polynomialValue
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(mapOf(other to 1U), constantOne) { _, it, _ -> constantRing { it + constantOne } }
        )
    public override operator fun LabeledPolynomial<C>.minus(other: Symbol): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.polynomialValue
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(mapOf(other to 1U), constantRing { -constantOne }) { _, it, _ -> constantRing { it - constantOne } }
        )
    public override operator fun LabeledPolynomial<C>.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients
                .mapKeys { (degs, _) -> degs.withPutOrChanged(other, 1u) { _, it, _ -> it + 1u } }
        )

    override fun LabeledPolynomial<C>.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { constantRing { -it.value } }
        )
    override operator fun LabeledPolynomial<C>.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mergeBy(coefficients, other.coefficients) { _, c1, c2 -> constantRing { c1 + c2 } }
        )
    override operator fun LabeledPolynomial<C>.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { (_, c) -> constantRing { -c } }, { _, currentC, newC -> constantRing { currentC - newC } })
            }
        )
    override operator fun LabeledPolynomial<C>.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs = mergeBy(degs1, degs2) { _, deg1, deg2 -> deg1 + deg2 }
                    val c = constantRing { c1 * c2 }
                    this.putOrChange(degs, c) { _, it, _ -> constantRing { it + c } }
                }
            }
        )

    override val LabeledPolynomial<C>.degree: Int
        get() = coefficients.entries.maxOfOrNull { (degs, _) -> degs.values.sum().toInt() } ?: -1
    public override val LabeledPolynomial<C>.degrees: LabeledMonomialSignature
        get() =
            buildMap {
                coefficients.keys.forEach { degs ->
                    degs.copyToBy(this) { _, currentDeg, newDeg -> max(currentDeg, newDeg) }
                }
            }
    public override fun LabeledPolynomial<C>.degreeBy(variable: Symbol): UInt =
        coefficients.entries.maxOfOrNull { (degs, _) -> degs.getOrElse(variable) { 0u } } ?: 0u
    public override fun LabeledPolynomial<C>.degreeBy(variables: Collection<Symbol>): UInt =
        coefficients.entries.maxOfOrNull { (degs, _) -> degs.filterKeys { it in variables }.values.sum() } ?: 0u
    public override val LabeledPolynomial<C>.variables: Set<Symbol>
        get() =
            buildSet {
                coefficients.entries.forEach { (degs, _) -> addAll(degs.keys) }
            }
    public override val LabeledPolynomial<C>.countOfVariables: Int get() = variables.size

    // FIXME: When context receivers will be ready move all of these substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, C>): LabeledPolynomial<C> = substitute(constantRing, arguments)
    public inline fun LabeledPolynomial<C>.substitute(vararg arguments: Pair<Symbol, C>): LabeledPolynomial<C> = substitute(constantRing, *arguments)
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> = substitute(constantRing, arguments)
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(vararg arguments: Pair<Symbol, LabeledPolynomial<C>>): LabeledPolynomial<C> = substitute(constantRing, *arguments)
}

public class LabeledPolynomialSpaceOverField<C, out A : Field<C>>(constantRing: A) : LabeledPolynomialSpace<C, A>(constantRing), MultivariatePolynomialSpaceOverField<C, Symbol, LabeledPolynomial<C>, A> {
    public override fun LabeledPolynomial<C>.div(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { constantRing { it.value / other } }
        )
}