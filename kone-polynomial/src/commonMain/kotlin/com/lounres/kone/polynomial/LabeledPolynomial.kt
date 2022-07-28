/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import space.kscience.kmath.expressions.Symbol
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.mapUtils.*
import kotlin.jvm.JvmName
import kotlin.math.max


public data class LabeledPolynomial<C>
@PublishedApi
internal constructor(
    public val coefficients: Map<Map<Symbol, UInt>, C>
) : Polynomial<C> {
    override fun toString(): String = "LabeledPolynomial$coefficients"
}

public class LabeledPolynomialSpace<C, out A : Ring<C>>(
    public override val ring: A,
) : MultivariatePolynomialSpace<C, Symbol, LabeledPolynomial<C>>, PolynomialSpaceOverRing<C, LabeledPolynomial<C>, A> {
    override val zero: LabeledPolynomial<C> = LabeledPolynomialAsIs()
    override val one: LabeledPolynomial<C> = constantOne.asLabeledPolynomial()

    public override infix fun LabeledPolynomial<C>.equalsTo(other: LabeledPolynomial<C>): Boolean {
        for ((key, value) in this.coefficients) {
            if (key !in other.coefficients && value.isNotZero()) return false
            if (!(other.coefficients.getValue(key) equalsTo value)) return false
        }
        for ((key, value) in other.coefficients) {
            if (key !in other.coefficients && value.isNotZero()) return false
        }
        return true
    }
    public override fun LabeledPolynomial<C>.isZero(): Boolean = coefficients.values.all { it.isZero() }
    public override fun LabeledPolynomial<C>.isOne(): Boolean = coefficients.all { it.key.isEmpty() || it.value.isZero() }

//    @JvmName("valueOfConstant")
    public override fun valueOf(value: C): LabeledPolynomial<C> = value.asLabeledPolynomial()

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
            mapOf(other to 1U) to -constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to constantOne * this@minus,
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
            mapOf(other to 1U) to -constantOne,
        )
        else LabeledPolynomialAsIs(
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to constantOne * this@minus,
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
                coefficients.withPutOrChanged(emptyMap(), other.constantValue) { it -> it + other }
            )
        }
    public override operator fun LabeledPolynomial<C>.minus(other: Int): LabeledPolynomial<C> =
        when {
            other == 0 -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), (-other).constantValue) { it -> it - other }
            )
        }
    public override operator fun LabeledPolynomial<C>.times(other: Int): LabeledPolynomial<C> =
        when(other) {
            0 -> zero
            1 -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValues { (_, value) -> value * other }
            )
        }

    public override operator fun LabeledPolynomial<C>.plus(other: Long): LabeledPolynomial<C> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), other.constantValue) { it -> it + other }
            )
        }
    public override operator fun LabeledPolynomial<C>.minus(other: Long): LabeledPolynomial<C> =
        when {
            other == 0L -> this
            coefficients.isEmpty() -> other.value
            else -> LabeledPolynomialAsIs(
                coefficients.withPutOrChanged(emptyMap(), (-other).constantValue) { it -> it - other }
            )
        }
    public override operator fun LabeledPolynomial<C>.times(other: Long): LabeledPolynomial<C> =
        when(other) {
            0L -> zero
            1L -> this
            else -> LabeledPolynomialAsIs(
                coefficients.mapValues { (_, value) -> value * other }
            )
        }

    public override operator fun Int.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0 -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withPutOrChanged(emptyMap(), this@plus.constantValue) { it -> this@plus + it }
            )
        }
    public override operator fun Int.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyMap(), constantValue)
                    other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
                }
            )
        }
    public override operator fun Int.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when(this) {
            0 -> zero
            1 -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValues { (_, value) -> this@times * value }
            )
        }

    public override operator fun Long.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0L -> other
            other.coefficients.isEmpty() -> this@plus.value
            else -> LabeledPolynomialAsIs(
                other.coefficients.withPutOrChanged(emptyMap(), this@plus.constantValue) { it -> this@plus + it }
            )
        }
    public override operator fun Long.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when {
            this == 0L -> -other
            other.coefficients.isEmpty() -> this@minus.value
            else -> LabeledPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyMap(), constantValue)
                    other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
                }
            )
        }
    public override operator fun Long.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        when(this) {
            0L -> zero
            1L -> other
            else -> LabeledPolynomialAsIs(
                other.coefficients.mapValues { (_, value) -> this@times * value }
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
            emptyMap<Symbol, UInt>() to -other,
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
            mapOf(other to 1U) to -constantOne,
            emptyMap<Symbol, UInt>() to this@minus,
        )
    public override operator fun C.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(other to 1U) to this@times,
        )

    override operator fun C.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyMap(), this@plus) { it -> this@plus + it }
        )
    override operator fun C.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.value
        else LabeledPolynomialAsIs(
            buildMap(other.coefficients.size + 1) {
                put(emptyMap(), this@minus)
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
            }
        )
    override operator fun C.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients.mapValues { this@times * it.value }
        )

    override operator fun LabeledPolynomial<C>.plus(other: C): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(emptyMap(), other) { it -> it + other }
        )
    override operator fun LabeledPolynomial<C>.minus(other: C): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.asLabeledPolynomial()
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(emptyMap(), -other) { it -> it - other }
        )
    override operator fun LabeledPolynomial<C>.times(other: C): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { it.value * other }
        )

    public override operator fun Symbol.unaryPlus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
        )
    public override operator fun Symbol.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mapOf(this to 1U) to -constantOne,
        )
    public override operator fun Symbol.plus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne * 2
        )
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to constantOne,
        )
    public override operator fun Symbol.minus(other: Symbol): LabeledPolynomial<C> =
        if (this == other) zero
        else LabeledPolynomialAsIs(
            mapOf(this to 1U) to constantOne,
            mapOf(other to 1U) to -constantOne,
        )
    public override operator fun Symbol.times(other: Symbol): LabeledPolynomial<C> =
        if (this == other) LabeledPolynomialAsIs(
            mapOf(this to 2U) to constantOne
        )
        else LabeledPolynomialAsIs(
            mapOf(this to 1U, other to 1U) to constantOne,
        )

    public override operator fun Symbol.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.value
        else LabeledPolynomialAsIs(
            other.coefficients.withPutOrChanged(mapOf(this@plus to 1U), constantOne) { it -> constantOne + it }
        )
    public override operator fun Symbol.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.value
        else LabeledPolynomialAsIs(
            buildMap(other.coefficients.size + 1) {
                put(mapOf(this@minus to 1U), constantOne)
                other.coefficients.copyMapToBy(this, { _, c -> -c }) { currentC, newC -> currentC - newC }
            }
        )
    public override operator fun Symbol.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            other.coefficients
                .mapKeys { (degs, _) -> degs.withPutOrChanged(this, 1u) { it -> it + 1u } }
        )

    public override operator fun LabeledPolynomial<C>.plus(other: Symbol): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.value
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(mapOf(other to 1U), constantOne) { it -> it + constantOne }
        )
    public override operator fun LabeledPolynomial<C>.minus(other: Symbol): LabeledPolynomial<C> =
        if (coefficients.isEmpty()) other.value
        else LabeledPolynomialAsIs(
            coefficients.withPutOrChanged(mapOf(other to 1U), -constantOne) { it -> it - constantOne }
        )
    public override operator fun LabeledPolynomial<C>.times(other: Symbol): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients
                .mapKeys { (degs, _) -> degs.withPutOrChanged(other, 1u) { it -> it + 1u } }
        )

    override fun LabeledPolynomial<C>.unaryMinus(): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            coefficients.mapValues { -it.value }
        )
    override operator fun LabeledPolynomial<C>.plus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            mergeBy(coefficients, other.coefficients) { c1, c2 -> c1 + c2 }
        )
    override operator fun LabeledPolynomial<C>.minus(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
            }
        )
    override operator fun LabeledPolynomial<C>.times(other: LabeledPolynomial<C>): LabeledPolynomial<C> =
        LabeledPolynomialAsIs(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs = mergeBy(degs1, degs2) { deg1, deg2 -> deg1 + deg2 }
                    val c = c1 * c2
                    this.putOrChange(degs, c) { it -> it + c }
                }
            }
        )

    override val LabeledPolynomial<C>.degree: Int
        get() = coefficients.entries.maxOfOrNull { (degs, _) -> degs.values.sum().toInt() } ?: -1
    public override val LabeledPolynomial<C>.degrees: Map<Symbol, UInt>
        get() =
            buildMap {
                coefficients.keys.forEach { degs ->
                    degs.copyToBy(this, ::max)
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

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, C>): LabeledPolynomial<C> = substitute(ring, arguments)
    @JvmName("substitutePolynomial")
    public inline fun LabeledPolynomial<C>.substitute(arguments: Map<Symbol, LabeledPolynomial<C>>) : LabeledPolynomial<C> = substitute(ring, arguments)
}