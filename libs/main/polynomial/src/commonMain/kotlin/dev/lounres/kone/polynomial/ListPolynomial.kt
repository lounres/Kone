/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.context.invoke
import kotlin.math.max
import kotlin.math.min


public data class ListPolynomial<C>(
    public val coefficients: List<C>
) {
    override fun toString(): String = "ListPolynomial$coefficients"
}

public open class ListPolynomialSpace<C, out A : Ring<C>>(override val numericalRing: A) : PolynomialSpace<C, ListPolynomial<C>, A> {
    override val zero: ListPolynomial<C> = ListPolynomial(emptyList())
    override val one: ListPolynomial<C> by lazy { numericalOne.asListPolynomial() }
    public val freeVariable: ListPolynomial<C> by lazy { ListPolynomial(numericalZero, numericalOne) }

    public override infix fun ListPolynomial<C>.equalsTo(other: ListPolynomial<C>): Boolean {
        for (index in 0 .. max(this.coefficients.lastIndex, other.coefficients.lastIndex))
            when (index) {
                !in this.coefficients.indices -> if (numericalRing { other.coefficients[index].isNotZero() }) return false
                !in other.coefficients.indices -> if (numericalRing { this.coefficients[index].isNotZero() }) return false
                else -> if (!numericalRing { other.coefficients[index] equalsTo this.coefficients[index] }) return false
            }
        return true
    }
    public override fun ListPolynomial<C>.isZero(): Boolean = coefficients.all { numericalRing { it.isZero() } }
    public override fun ListPolynomial<C>.isOne(): Boolean = coefficients.subList(1, coefficients.size).all { numericalRing { it.isZero() } }

    public override fun polynomialValueOf(value: C): ListPolynomial<C> = value.asListPolynomial()

    public override operator fun ListPolynomial<C>.plus(other: Int): ListPolynomial<C> =
        if (other == 0) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = numericalRing { getOrElse(0) { numericalZero } + other }

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
                        val result = numericalRing { getOrElse(0) { numericalZero } - other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.times(other: Int): ListPolynomial<C> =
        when (other) {
            0 -> zero
            1 -> this
            else -> ListPolynomial(
                coefficients.map { numericalRing { it * other } }
            )
        }

    public override operator fun ListPolynomial<C>.plus(other: Long): ListPolynomial<C> =
        if (other == 0L) this
        else
            ListPolynomial(
                coefficients
                    .toMutableList()
                    .apply {
                        val result = numericalRing { getOrElse(0) { numericalZero } + other }

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
                        val result = numericalRing { getOrElse(0) { numericalZero } - other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
    public override operator fun ListPolynomial<C>.times(other: Long): ListPolynomial<C> =
        when (other) {
            0L -> zero
            1L -> this
            else -> ListPolynomial(
                coefficients.map { numericalRing { it * other } }
            )
        }

    public override operator fun Int.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = numericalRing { this@plus + getOrElse(0) { numericalZero } }

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
                        indices.forEach { this[it] = numericalRing { -this[it] } }
                    } else {
                        (1..lastIndex).forEach { this[it] = numericalRing { -this[it] } }

                        val result = numericalRing { this@minus - getOrElse(0) { numericalZero } }

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
                other.coefficients.map { numericalRing { this@times * it } }
            )
        }

    public override operator fun Long.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        if (this == 0L) other
        else
            ListPolynomial(
                other.coefficients
                    .toMutableList()
                    .apply {
                        val result = numericalRing { this@plus + getOrElse(0) { numericalZero } }

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
                        indices.forEach { this[it] = numericalRing { -this[it] } }
                    } else {
                        (1..lastIndex).forEach { this[it] = numericalRing { -this[it] } }

                        val result = numericalRing { this@minus - getOrElse(0) { numericalZero } }

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
                other.coefficients.map { numericalRing { this@times * it } }
            )
        }

    public override operator fun C.plus(other: ListPolynomial<C>): ListPolynomial<C> =
        with(other.coefficients) {
            if (isEmpty()) ListPolynomial(listOf(this@plus))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) this@plus else numericalRing { this@plus + get(0) }

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
                        (1 .. lastIndex).forEach { this[it] = numericalRing { -this[it] } }

                        val result = if (size == 0) this@minus else numericalRing { this@minus - get(0) }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun C.times(other: ListPolynomial<C>): ListPolynomial<C> =
        ListPolynomial(
            other.coefficients.map { numericalRing { this@times * it } }
        )

    public override operator fun ListPolynomial<C>.plus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(other))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else numericalRing { get(0) + other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun ListPolynomial<C>.minus(other: C): ListPolynomial<C> =
        with(coefficients) {
            if (isEmpty()) ListPolynomial(listOf(numericalRing { -other }))
            else ListPolynomial(
                toMutableList()
                    .apply {
                        val result = if (size == 0) other else numericalRing { get(0) - other }

                        if(size == 0) add(result)
                        else this[0] = result
                    }
            )
        }
    public override operator fun ListPolynomial<C>.times(other: C): ListPolynomial<C> =
        ListPolynomial(
            coefficients.map { numericalRing { it * other } }
        )

    public override operator fun ListPolynomial<C>.unaryMinus(): ListPolynomial<C> =
        ListPolynomial(coefficients.map { numericalRing { -it } })
    public override operator fun ListPolynomial<C>.plus(other: ListPolynomial<C>): ListPolynomial<C> {
        val thisDegree = degree
        val otherDegree = other.degree
        return ListPolynomial(
            List(max(thisDegree, otherDegree) + 1) {
                when {
                    it > thisDegree -> other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> numericalRing { coefficients[it] + other.coefficients[it] }
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
                    it > thisDegree -> numericalRing { -other.coefficients[it] }
                    it > otherDegree -> coefficients[it]
                    else -> numericalRing { coefficients[it] - other.coefficients[it] }
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
                    .map { numericalRing { coefficients[it] * other.coefficients[d - it] } }
                    .reduce { acc, rational -> numericalRing { acc + rational } }
            }
        )
    } // TODO: To optimize boxing
    override fun power(base: ListPolynomial<C>, exponent: UInt): ListPolynomial<C> = super.power(base, exponent)

    public override val ListPolynomial<C>.degree: Int get() = coefficients.lastIndex
}

public class ListPolynomialSpaceOverField<C, out A : Field<C>>(numericalRing: A) : ListPolynomialSpace<C, A>(numericalRing), PolynomialSpaceOverField<C, ListPolynomial<C>, A> {
    public override fun ListPolynomial<C>.div(other: C): ListPolynomial<C> =
        ListPolynomial(coefficients.map { numericalRing { it / other } })
}