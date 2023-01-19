/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import kotlin.math.max
import kotlin.math.min


public data class ListPolynomial<out C>(
    public val coefficients: List<C>
) : Polynomial<C> {
    override fun toString(): String = "ListPolynomial$coefficients"
}

public open class ListPolynomialSpace<C, out A : Ring<C>>(
    public override val ring: A,
) : PolynomialSpaceWithRing<C, ListPolynomial<C>, A> {
    override val zero: ListPolynomial<C> = ListPolynomial(emptyList())
    override val one: ListPolynomial<C> by lazy { constantOne.asListPolynomial() }
    public val freeVariable: ListPolynomial<C> by lazy { ListPolynomial(constantZero, constantOne) }

    public override infix fun ListPolynomial<C>.equalsTo(other: ListPolynomial<C>): Boolean {
        for (index in 0 .. max(this.coefficients.lastIndex, other.coefficients.lastIndex))
            when (index) {
                !in this.coefficients.indices -> if (other.coefficients[index].isNotZero()) return false
                !in other.coefficients.indices -> if (this.coefficients[index].isNotZero()) return false
                else -> if (!(other.coefficients[index] equalsTo this.coefficients[index])) return false
            }
        return true
    }
    public override fun ListPolynomial<C>.isZero(): Boolean = coefficients.all { it.isZero() }
    public override fun ListPolynomial<C>.isOne(): Boolean = coefficients.subList(1, coefficients.size).all { it.isZero() }

    public override fun valueOf(value: C): ListPolynomial<C> = value.asListPolynomial()

    public override operator fun ListPolynomial<C>.plus(other: Int): ListPolynomial<C> =
        if (other == 0) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = getOrElse(0) { constantZero } + other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.minus(other: Int): ListPolynomial<C> =
        if (other == 0) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = getOrElse(0) { constantZero } - other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.times(other: Int): ListPolynomial<C> =
        when (other) {
            0 -> zero
            1 -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }

    public override operator fun ListPolynomial<C>.plus(other: Long): ListPolynomial<C> =
        if (other == 0L) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = getOrElse(0) { constantZero } + other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.minus(other: Long): ListPolynomial<C> =
        if (other == 0L) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = getOrElse(0) { constantZero } - other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.times(other: Long): ListPolynomial<C> =
        when (other) {
            0L -> zero
            1L -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }

    public override operator fun Int.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = this@plus + getOrElse(0) { constantZero }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun Int.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients
                .toMutableList()
                .apply {
                    if (this@minus == 0) {
                        indices.forEach { this[it] = -this[it] }
                    } else {
                        (1..lastIndex).forEach { this[it] = -this[it] }

                        val result = this@minus - getOrElse(0) { constantZero }

                        if (size == 0) add(result)
                        else this[0] = result
                    }
                }
        )
    public override operator fun Int.times(other: ListPolynomial<C>): ListPolynomial<C> =
        when (this) {
            0 -> zero
            1 -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }

    public override operator fun Long.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0L) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = this@plus + getOrElse(0) { constantZero }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun Long.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients
                .toMutableList()
                .apply {
                    if (this@minus == 0L) {
                        indices.forEach { this[it] = -this[it] }
                    } else {
                        (1..lastIndex).forEach { this[it] = -this[it] }

                        val result = this@minus - getOrElse(0) { constantZero }

                        if (size == 0) add(result)
                        else this[0] = result
                    }
                }
        )
    public override operator fun Long.times(other: ListPolynomial<C>): ListPolynomial<C> =
        when (this) {
            0L -> zero
            1L -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }

    public override operator fun C.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@plus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) this@plus else this@plus + get(0)

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun C.minus(other: ListPolynomial<C>): ListPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@minus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        (1 .. lastIndex).forEach { this[it] = -this[it] }

                        val result = if (size == 0) this@minus else this@minus - get(0)

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun C.times(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients.map { this@times * it }
        )

    public override operator fun ListPolynomial<C>.plus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else get(0) + other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun ListPolynomial<C>.minus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(-other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else get(0) - other

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun ListPolynomial<C>.times(other: C): ListPolynomial<C> =
        ListPolynomial(
            coefficients.map { it * other }
        )

    public override operator fun ListPolynomial<C>.unaryMinus(): ListPolynomial<C> =
        ListPolynomial(coefficients.map { -it })
    public override operator fun ListPolynomial<C>.plus(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(max(thisDegree, otherDegree) + 1) {
                when {
                    it > thisDegree -> other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> coefficients[it] + other.coefficients[it]
                }
            }
        )
    }
    public override operator fun ListPolynomial<C>.minus(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(max(thisDegree, otherDegree) + 1) {
                when {
                    it > thisDegree -> -other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> coefficients[it] - other.coefficients[it]
                }
            }
        )
    }
    public override operator fun ListPolynomial<C>.times(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(thisDegree + otherDegree + 1) { d ->
                (max(0, d - otherDegree)..min(thisDegree, d))
                    .map { coefficients[it] * other.coefficients[d - it] }
                    .reduce { acc, rational -> acc + rational }
            }
        )
    } // TODO: To optimize boxing
    override fun power(base: ListPolynomial<C>, exponent: UInt): ListPolynomial<C> = super.power(base, exponent)

    public override val ListPolynomial<C>.degree: Int get() = coefficients.lastIndex

    // TODO: When context receivers will be ready move all of this substitutions and invocations to utilities with
    //  [ListPolynomialSpace] as a context receiver
    public inline fun ListPolynomial<C>.substitute(argument: C): C = substitute(ring, argument)
    public inline fun ListPolynomial<C>.substitute(argument: ListPolynomial<C>): ListPolynomial<C> = substitute(ring, argument)

    public inline fun ListPolynomial<C>.asFunction(): (C) -> C = asFunctionOver(ring)
    public inline fun ListPolynomial<C>.asFunctionOfConstant(): (C) -> C = asFunctionOfConstantOver(ring)
    public inline fun ListPolynomial<C>.asFunctionOfPolynomial(): (ListPolynomial<C>) -> ListPolynomial<C> = asFunctionOfPolynomialOver(ring)

    public inline operator fun ListPolynomial<C>.invoke(argument: C): C = substitute(ring, argument)
    public inline operator fun ListPolynomial<C>.invoke(argument: ListPolynomial<C>): ListPolynomial<C> = substitute(ring, argument)
}

public class ListPolynomialSpaceOverField<C, out A : Field<C>>(
    ring: A,
) : ListPolynomialSpace<C, A>(ring), PolynomialSpaceWithField<C, ListPolynomial<C>, A> {

    // region Polynomial-Int operations
    public override operator fun ListPolynomial<C>.div(other: Int): ListPolynomial<C> =
        when(other) {
            0 -> throw IllegalArgumentException("/ by zero")
            1 -> this
            else -> {
                val rec = other.constantValue.reciprocal
                ListPolynomial(coefficients.map { it * rec })
            }
        }
    // endregion

    // region Polynomial-Long operations
    public override operator fun ListPolynomial<C>.div(other: Long): ListPolynomial<C> =
        when(other) {
            0L -> throw IllegalArgumentException("/ by zero")
            1L -> this
            else -> {
                val rec = other.constantValue.reciprocal
                ListPolynomial(coefficients.map { it * rec })
            }
        }
    // endregion

    // region Polynomial-Constant operations
    public override fun ListPolynomial<C>.div(other: C): ListPolynomial<C> {
        val rec = other.reciprocal
        return ListPolynomial(coefficients.map { it * rec } )
    }
    // endregion
}