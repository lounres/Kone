/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package com.lounres.kone.polynomial

import com.lounres.kone.algebraic.Field
import com.lounres.kone.algebraic.Ring
import com.lounres.kone.context.invoke
import kotlin.math.max
import kotlin.math.min


public data class ListPolynomial<C>(
    public val coefficients: List<C>
) : Polynomial<C> {
    override fun toString(): String = "ListPolynomial$coefficients"
}

public open class ListPolynomialSpace<C, out A : Ring<C>>(override val constantRing: A) : PolynomialSpace<C, ListPolynomial<C>, A> {
    override val zero: ListPolynomial<C> = ListPolynomial(emptyList())
    override val one: ListPolynomial<C> by lazy { constantOne.asListPolynomial() }
    public val freeVariable: ListPolynomial<C> by lazy { ListPolynomial(constantZero, constantOne) }

    public override infix fun ListPolynomial<C>.equalsTo(other: ListPolynomial<C>): Boolean {
        for (index in 0 .. max(this.coefficients.lastIndex, other.coefficients.lastIndex))
            when (index) {
                !in this.coefficients.indices -> if (constantRing { other.coefficients[index].isNotZero() }) return false
                !in other.coefficients.indices -> if (constantRing { this@equalsTo.coefficients[index].isNotZero() }) return false
                else -> if (constantRing { !(other.coefficients[index] equalsTo this@equalsTo.coefficients[index]) }) return false
            }
        return true
    }
    public override fun ListPolynomial<C>.isZero(): Boolean = coefficients.all { constantRing { it.isZero() } }
    public override fun ListPolynomial<C>.isOne(): Boolean = coefficients.subList(1, coefficients.size).all { constantRing { it.isZero() } }

    public override fun polynomialValueOf(value: C): ListPolynomial<C> = value.asListPolynomial()

    public override operator fun ListPolynomial<C>.plus(other: Int): ListPolynomial<C> =
        if (other == 0) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = constantRing { getOrElse(0) { constantZero } + other }

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
                        val result = constantRing { getOrElse(0) { constantZero } - other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.times(other: Int): ListPolynomial<C> =
        when (other) {
            0 -> zero
            1 -> this
            else -> ListPolynomial(
                coefficients.map { constantRing { it * other } }
            )
        }

    public override operator fun ListPolynomial<C>.plus(other: Long): ListPolynomial<C> =
        if (other == 0L) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = constantRing { getOrElse(0) { constantZero } + other }

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
                        val result = constantRing { getOrElse(0) { constantZero } - other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.times(other: Long): ListPolynomial<C> =
        when (other) {
            0L -> zero
            1L -> this
            else -> ListPolynomial(
                coefficients.map { constantRing { it * other } }
            )
        }

    public override operator fun Int.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = constantRing { this@plus + getOrElse(0) { constantZero } }

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
                        indices.forEach { this[it] = constantRing { -this@apply[it] } }
                    } else {
                        (1..lastIndex).forEach { this[it] = constantRing { -this@apply[it] } }

                        val result = constantRing { this@minus - getOrElse(0) { constantZero } }

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
                other.coefficients.map { constantRing { this@times * it } }
            )
        }

    public override operator fun Long.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0L) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = constantRing { this@plus + getOrElse(0) { constantZero } }

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
                        indices.forEach { this[it] = constantRing { -this@apply[it] } }
                    } else {
                        (1..lastIndex).forEach { this[it] = constantRing { -this@apply[it] } }

                        val result = constantRing { this@minus - getOrElse(0) { constantZero } }

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
                other.coefficients.map { constantRing { this@times * it } }
            )
        }

    public override operator fun C.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@plus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) this@plus else constantRing { this@plus + get(0) }

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
                        (1 .. lastIndex).forEach { this[it] = constantRing { -this@apply[it] } }

                        val result = if (size == 0) this@minus else constantRing { this@minus - get(0) }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun C.times(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients.map { constantRing { this@times * it } }
        )

    public override operator fun ListPolynomial<C>.plus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else constantRing { get(0) + other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun ListPolynomial<C>.minus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(constantRing { -other }))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else constantRing { get(0) - other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun ListPolynomial<C>.times(other: C): ListPolynomial<C> =
        ListPolynomial(
            coefficients.map { constantRing { it * other } }
        )

    public override operator fun ListPolynomial<C>.unaryMinus(): ListPolynomial<C> =
        ListPolynomial(coefficients.map { constantRing { -it } })
    public override operator fun ListPolynomial<C>.plus(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(max(thisDegree, otherDegree) + 1) {
                when {
                    it > thisDegree -> other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> constantRing { coefficients[it] + other.coefficients[it] }
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
                    it > thisDegree -> constantRing { -other.coefficients[it] }
                    it > otherDegree -> coefficients[it]
                    else -> constantRing { coefficients[it] - other.coefficients[it] }
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
                    .map { constantRing { coefficients[it] * other.coefficients[d - it] } }
                    .reduce { acc, rational -> constantRing { acc + rational } }
            }
        )
    } // TODO: To optimize boxing
    override fun power(base: ListPolynomial<C>, exponent: UInt): ListPolynomial<C> = super.power(base, exponent)

    public override val ListPolynomial<C>.degree: Int get() = coefficients.lastIndex
}

public class ListPolynomialSpaceOverField<C, out A : Field<C>>(constantRing: A) : ListPolynomialSpace<C, A>(constantRing), PolynomialSpaceOverField<C, ListPolynomial<C>, A> {
    public override fun ListPolynomial<C>.div(other: C): ListPolynomial<C> =
        ListPolynomial(coefficients.map { constantRing { it / other } })
}