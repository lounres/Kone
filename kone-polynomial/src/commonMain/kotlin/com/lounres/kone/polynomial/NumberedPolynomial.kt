/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Ring
import com.lounres.kone.mapUtils.*
import space.kscience.kmath.structures.Buffer
import kotlin.jvm.JvmName
import kotlin.math.max


public data class NumberedPolynomial<C>
@PublishedApi
internal constructor(
    public val coefficients: Map<List<UInt>, C>
) : Polynomial<C> {
    override fun toString(): String = "NumberedPolynomial$coefficients"
}

public class NumberedPolynomialSpace<C, out A : Ring<C>>(
    public override val ring: A,
) : PolynomialSpaceOverRing<C, NumberedPolynomial<C>, A> {

    override val zero: NumberedPolynomial<C> = NumberedPolynomialAsIs(emptyMap())
    override val one: NumberedPolynomial<C> by lazy { NumberedPolynomialAsIs(mapOf(emptyList<UInt>() to constantOne)) }

    public override infix fun NumberedPolynomial<C>.equalsTo(other: NumberedPolynomial<C>): Boolean {
        for ((key, value) in this.coefficients) {
            if (key !in other.coefficients && value.isNotZero()) return false
            if (!(other.coefficients.getValue(key) equalsTo value)) return false
        }
        for ((key, value) in other.coefficients) {
            if (key !in other.coefficients && value.isNotZero()) return false
        }
        return true
    }
    public override fun NumberedPolynomial<C>.isZero(): Boolean = coefficients.values.all { it.isZero() }
    public override fun NumberedPolynomial<C>.isOne(): Boolean = coefficients.all { it.key.isEmpty() || it.value.isZero() }

    public override fun valueOf(value: C): NumberedPolynomial<C> = value.asNumberedPolynomial()
    
    public override operator fun NumberedPolynomial<C>.plus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), other.constantValue) { it -> it + other }
        )
    public override operator fun NumberedPolynomial<C>.minus(other: Int): NumberedPolynomial<C> =
        if (other == 0) this
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), (-other).constantValue) { it -> it - other }
        )
    public override operator fun NumberedPolynomial<C>.times(other: Int): NumberedPolynomial<C> =
        when (other) {
            0 -> zero
            1 -> this
            else -> NumberedPolynomialAsIs(
                coefficients.mapValues { it.value * other }
            )
        }

    public override operator fun NumberedPolynomial<C>.plus(other: Long): NumberedPolynomial<C> =
        if (other == 0L) this
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), other.constantValue) { it -> it + other }
        )
    public override operator fun NumberedPolynomial<C>.minus(other: Long): NumberedPolynomial<C> =
        if (other == 0L) this
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), (-other).constantValue) { it -> it - other }
        )
    public override operator fun NumberedPolynomial<C>.times(other: Long): NumberedPolynomial<C> =
        when (other) {
            0L -> zero
            1L -> this
            else -> NumberedPolynomialAsIs(
                coefficients.mapValues { it.value * other }
            )
        }

    public override operator fun Int.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0) other
        else NumberedPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyList(), this@plus.constantValue) { it -> this@plus + it }
        )
    public override operator fun Int.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when {
            this == 0 -> -other
            other.coefficients.isEmpty() -> this.value
            else -> NumberedPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyList(), other.coefficients.computeOnOrElse(emptyList(), { this@minus.constantValue }, { it -> this@minus - it}))
                    other.coefficients.copyMapToBy(this, { _, c -> -c }) { currentC, _ -> currentC }
                }
            )
        }
    public override operator fun Int.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when (this) {
            0 -> zero
            1 -> other
            else -> NumberedPolynomialAsIs(
                other.coefficients.mapValues { this@times * it.value }
            )
        }

    public override operator fun Long.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (this == 0L) other
        else NumberedPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyList(), this@plus.constantValue) { it -> this@plus + it }
        )
    public override operator fun Long.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when {
            this == 0L -> -other
            other.coefficients.isEmpty() -> this.value
            else -> NumberedPolynomialAsIs(
                buildMap(other.coefficients.size + 1) {
                    put(emptyList(), other.coefficients.computeOnOrElse(emptyList(), { this@minus.constantValue }, { it -> this@minus - it}))
                    other.coefficients.copyMapToBy(this, { _, c -> -c }) { currentC, _ -> currentC }
                }
            )
        }
    public override operator fun Long.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        when (this) {
            0L -> zero
            1L -> other
            else -> NumberedPolynomialAsIs(
                other.coefficients.mapValues { this@times * it.value }
            )
        }

    override operator fun NumberedPolynomial<C>.plus(other: C): NumberedPolynomial<C> =
        if (coefficients.isEmpty()) other.value
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), other) { it -> it + other }
        )
    override operator fun NumberedPolynomial<C>.minus(other: C): NumberedPolynomial<C> =
        if (coefficients.isEmpty()) other.value
        else NumberedPolynomialAsIs(
            coefficients.withPutOrChanged(emptyList(), -other) { it -> it - other }
        )
    override operator fun NumberedPolynomial<C>.times(other: C): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            coefficients.mapValues { it.value * other }
        )

    override operator fun C.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (other.coefficients.isEmpty()) this@plus.value
        else NumberedPolynomialAsIs(
            other.coefficients.withPutOrChanged(emptyList(), this@plus) { it -> this@plus + it }
        )
    override operator fun C.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        if (other.coefficients.isEmpty()) this@minus.value
        else NumberedPolynomialAsIs(
            buildMap(other.coefficients.size) {
                put(emptyList(), other.coefficients.computeOnOrElse(emptyList(), this@minus) { it -> this@minus - it })
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, _ -> currentC })
            }
        )
    override operator fun C.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            other.coefficients.mapValues { this@times * it.value }
        )

    override fun NumberedPolynomial<C>.unaryMinus(): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            coefficients.mapValues { -it.value }
        )
    override operator fun NumberedPolynomial<C>.plus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            mergeBy(coefficients, other.coefficients) { _, c1, c2 -> c1 + c2 }
        )
    override operator fun NumberedPolynomial<C>.minus(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            buildMap(coefficients.size + other.coefficients.size) {
                coefficients.copyTo(this)
                other.coefficients.copyMapToBy(this, { _, c -> -c }, { currentC, newC -> currentC - newC })
            }
        )
    override operator fun NumberedPolynomial<C>.times(other: NumberedPolynomial<C>): NumberedPolynomial<C> =
        NumberedPolynomialAsIs(
            buildMap(coefficients.size * other.coefficients.size) {
                for ((degs1, c1) in coefficients) for ((degs2, c2) in other.coefficients) {
                    val degs =
                        (0..max(degs1.lastIndex, degs2.lastIndex))
                            .map { degs1.getOrElse(it) { 0U } + degs2.getOrElse(it) { 0U } }
                    val c = c1 * c2
                    putOrChange(degs, c) { it -> it + c }
                }
            }
        ) // TODO: To optimize boxing
    override fun power(base: NumberedPolynomial<C>, exponent: UInt): NumberedPolynomial<C> = super.power(base, exponent)

    public val NumberedPolynomial<C>.lastVariable: Int
        get() = coefficients.keys.maxOfOrNull { degs -> degs.lastIndex } ?: -1
    override val NumberedPolynomial<C>.degree: Int
        get() = coefficients.keys.maxOfOrNull { degs -> degs.sum().toInt() } ?: -1
    public val NumberedPolynomial<C>.degrees: List<UInt>
        get() =
            MutableList(lastVariable + 1) { 0u }.apply {
                coefficients.keys.forEach { degs ->
                    degs.forEachIndexed { index, deg ->
                        this[index] = max(this[index], deg)
                    }
                }
            }
    public fun NumberedPolynomial<C>.degreeBy(variable: Int): UInt =
        coefficients.keys.maxOfOrNull { degs -> degs.getOrElse(variable) { 0u } } ?: 0u
    public fun NumberedPolynomial<C>.degreeBy(variables: Collection<Int>): UInt =
        coefficients.keys.maxOfOrNull { degs ->
            degs.withIndex().fold(0u) { acc, (index, value) -> if (index in variables) acc + value else acc }
        } ?: 0u
    public val NumberedPolynomial<C>.countOfVariables: Int
        get() =
            MutableList(lastVariable + 1) { false }.apply {
                coefficients.entries.forEach { (degs, _) ->
                    degs.forEachIndexed { index, deg ->
                        if (deg != 0u) this[index] = true
                    }
                }
            }.count { it }

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    public inline fun NumberedPolynomial<C>.substitute(arguments: Map<Int, C>): NumberedPolynomial<C> =
        substitute(ring, arguments)
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(arguments: Map<Int, NumberedPolynomial<C>>) : NumberedPolynomial<C> =
        substitute(ring, arguments)
    public inline fun NumberedPolynomial<C>.substitute(arguments: Buffer<C>): NumberedPolynomial<C> =
        substitute(ring, arguments)
    @JvmName("substitutePolynomial")
    public inline fun NumberedPolynomial<C>.substitute(arguments: Buffer<NumberedPolynomial<C>>) : NumberedPolynomial<C> =
        substitute(ring, arguments)
    public inline fun NumberedPolynomial<C>.substituteFully(arguments: Buffer<C>): C = this.substituteFully(ring, arguments)

    public inline fun NumberedPolynomial<C>.asFunction(): (Buffer<C>) -> C = asFunctionOver(ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfConstant(): (Buffer<C>) -> C = asFunctionOfConstantOver(ring)
    public inline fun NumberedPolynomial<C>.asFunctionOfPolynomial(): (Buffer<NumberedPolynomial<C>>) -> NumberedPolynomial<C> = asFunctionOfPolynomialOver(ring)

    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<C>): C = substituteFully(ring, arguments)
    @JvmName("invokePolynomial")
    public inline operator fun NumberedPolynomial<C>.invoke(arguments: Buffer<NumberedPolynomial<C>>): NumberedPolynomial<C> =
        substitute(ring, arguments)
}