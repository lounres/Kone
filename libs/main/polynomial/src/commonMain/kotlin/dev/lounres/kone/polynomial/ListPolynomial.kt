/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.comparison.equalsTo
import dev.lounres.kone.context
import dev.lounres.kone.scope
import kotlin.math.max
import kotlin.math.min


public data class ListPolynomial<Number>(
    public val coefficients: KoneList<Number>
) {
    override fun toString(): String = "ListPolynomial$coefficients"
}

public open class ListPolynomialSpace<Number>(
    protected open val numberContext: Ring<Number>,
) : PolynomialSpace<Number, ListPolynomial<Number>> {
    final override val numberZero: Number get() = numberContext.zero
    final override val numberOne: Number get() = numberContext.one
    
    final override fun numberValueOf(value: Int): Number = numberContext.valueOf(value)
    final override fun numberValueOf(value: UInt): Number = numberContext.valueOf(value)
    final override fun numberValueOf(value: Long): Number = numberContext.valueOf(value)
    final override fun numberValueOf(value: ULong): Number = numberContext.valueOf(value)
    final override val Int.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    final override val UInt.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    final override val Long.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    final override val ULong.numberValue: Number get() = with(numberContext) { this@numberValue.value }
    
    final override val zero: ListPolynomial<Number> = ListPolynomial(emptyKoneList())
    final override val one: ListPolynomial<Number> by lazy { numberOne.asListPolynomial() }
    public val freeVariable: ListPolynomial<Number> by lazy { ListPolynomial(numberZero, numberOne) }

    final override infix fun ListPolynomial<Number>.equalsTo(other: ListPolynomial<Number>): Boolean = context(numberContext) {
        for (index in 0u .. max(this.coefficients.lastIndex, other.coefficients.lastIndex))
            when (index) {
                !in this.coefficients.indices -> if (other.coefficients[index].isNotZero()) return false
                !in other.coefficients.indices -> if (this.coefficients[index].isNotZero()) return false
                else -> if (!(other.coefficients[index] equalsTo this.coefficients[index])) return false
            }
        return true
    }
    final override fun ListPolynomial<Number>.isZero(): Boolean = context(numberContext) { coefficients.all { it.isZero() } }
    final override fun ListPolynomial<Number>.isOne(): Boolean = context(numberContext) { coefficients.allIndexed { index, it -> index == 0u || it.isZero() } }

    final override fun polynomialValueOf(value: Number): ListPolynomial<Number> = value.asListPolynomial()

    final override operator fun ListPolynomial<Number>.plus(other: Int): ListPolynomial<Number> = context(numberContext) {
        if (other == 0) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } + other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: Int): ListPolynomial<Number> = context(numberContext) {
        if (other == 0) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } - other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: Int): ListPolynomial<Number> = context(numberContext) {
        when (other) {
            0 -> zero
            1 -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }
    
    final override operator fun ListPolynomial<Number>.plus(other: UInt): ListPolynomial<Number> = context(numberContext) {
        if (other == 0u) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } + other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: UInt): ListPolynomial<Number> = context(numberContext) {
        if (other == 0u) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } - other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: UInt): ListPolynomial<Number> = context(numberContext) {
        when (other) {
            0u -> zero
            1u -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }

    final override operator fun ListPolynomial<Number>.plus(other: Long): ListPolynomial<Number> = context(numberContext) {
        if (other == 0L) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } + other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: Long): ListPolynomial<Number> = context(numberContext) {
        if (other == 0L) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } - other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: Long): ListPolynomial<Number> = context(numberContext) {
        when (other) {
            0L -> zero
            1L -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }
    
    final override operator fun ListPolynomial<Number>.plus(other: ULong): ListPolynomial<Number> = context(numberContext) {
        if (other == 0uL) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } + other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: ULong): ListPolynomial<Number> = context(numberContext) {
        if (other == 0uL) this
        else {
            val result = coefficients.getOrElse(0u) { numberOne } - other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: ULong): ListPolynomial<Number> = context(numberContext) {
        when (other) {
            0uL -> zero
            1uL -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }

    final override operator fun Int.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        if (this == 0) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { numberZero }
            ListPolynomial(
                if (other.coefficients.size == 0u) koneListOf(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun Int.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(
            scope {
                if (this == 0) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { numberZero }
                    
                    if (other.coefficients.size == 0u) koneListOf(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun Int.times(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        when (this) {
            0 -> zero
            1 -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }
    
    final override operator fun UInt.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        if (this == 0u) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { numberZero }
            ListPolynomial(
                if (other.coefficients.size == 0u) koneListOf(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun UInt.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(
            scope {
                if (this == 0u) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { numberZero }
                    
                    if (other.coefficients.size == 0u) koneListOf(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun UInt.times(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        when (this) {
            0u -> zero
            1u -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }

    final override operator fun Long.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        if (this == 0L) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { numberZero }
            ListPolynomial(
                if (other.coefficients.size == 0u) koneListOf(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun Long.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(
            scope {
                if (this == 0L) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { numberZero }
                    
                    if (other.coefficients.size == 0u) koneListOf(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun Long.times(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        when (this) {
            0L -> zero
            1L -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }
    
    final override operator fun ULong.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        if (this == 0uL) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { numberZero }
            ListPolynomial(
                if (other.coefficients.size == 0u) koneListOf(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ULong.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(
            scope {
                if (this == 0uL) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { numberZero }
                    
                    if (other.coefficients.size == 0u) koneListOf(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun ULong.times(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        when (this) {
            0uL -> zero
            1uL -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }

    final override operator fun Number.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        if (this.isZero()) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { numberZero }
            ListPolynomial(
                if (other.coefficients.size == 0u) koneListOf(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun Number.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(
            scope {
                if (this.isZero()) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { numberZero }
                    
                    if (other.coefficients.size == 0u) koneListOf(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun Number.times(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        when {
            this.isZero() -> zero
            this.isOne() -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }

    final override operator fun ListPolynomial<Number>.plus(other: Number): ListPolynomial<Number> = context(numberContext) {
        if (other.isZero()) this
        else {
            val result = coefficients.getOrElse(0u) { numberZero } + other
            ListPolynomial(
                if (coefficients.size == 0u) koneListOf(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: Number): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(
            scope {
                if (other.isZero()) { coefficients.map { -it } }
                else {
                    val result = coefficients.getOrElse(0u) { numberZero } - other
                    
                    if (coefficients.size == 0u) koneListOf(result)
                    else coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun ListPolynomial<Number>.times(other: Number): ListPolynomial<Number> = context(numberContext) {
        when {
            other.isZero() -> zero
            other.isOne() -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }

    final override operator fun ListPolynomial<Number>.unaryMinus(): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(coefficients.map { -it })
    }
    final override operator fun ListPolynomial<Number>.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        val thisDegree = degree
        val otherDegree = other.degree
        ListPolynomial(
            KoneList(max(thisDegree, otherDegree) + 1u) {
                when {
                    it > thisDegree -> other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> coefficients[it] + other.coefficients[it]
                }
            }
        )
    }
    final override operator fun ListPolynomial<Number>.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        val thisDegree = degree
        val otherDegree = other.degree
        ListPolynomial(
            KoneList(max(thisDegree, otherDegree) + 1u) {
                when {
                    it > thisDegree -> -other.coefficients[it]
                    it > otherDegree -> coefficients[it]
                    else -> coefficients[it] - other.coefficients[it]
                }
            }
        )
    }
    final override operator fun ListPolynomial<Number>.times(other: ListPolynomial<Number>): ListPolynomial<Number> = context(numberContext) {
        val thisDegree = degree
        val otherDegree = other.degree
        ListPolynomial(
            KoneList(thisDegree + otherDegree + 1u) { d ->
                (max(0u, d - otherDegree)..min(thisDegree, d))
                    .map { coefficients[it] * other.coefficients[d - it] }
                    .reduce { acc, rational -> acc + rational }
            }
        )
    }
    
    // TODO: To optimize boxing
    final override fun power(base: ListPolynomial<Number>, exponent: UInt): ListPolynomial<Number> = super.power(base, exponent)
    
    final override val ListPolynomial<Number>.degree: UInt
        get() = coefficients
            .lastIndexThat { _, it -> context(numberContext) { it.isNotZero() } }
            .let { if (it == UInt.MAX_VALUE) zeroPolynomialDegreeException() else it }
}

public class ListPolynomialSpaceOverField<Number>(
    override val numberContext: Field<Number>,
) : ListPolynomialSpace<Number>(numberContext), PolynomialSpaceOverField<Number, ListPolynomial<Number>> {
    public override fun ListPolynomial<Number>.div(other: Int): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(coefficients.map { it / other })
    }
    public override fun ListPolynomial<Number>.div(other: UInt): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(coefficients.map { it / other })
    }
    public override fun ListPolynomial<Number>.div(other: Long): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(coefficients.map { it / other })
    }
    public override fun ListPolynomial<Number>.div(other: ULong): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(coefficients.map { it / other })
    }
    public override fun ListPolynomial<Number>.div(other: Number): ListPolynomial<Number> = context(numberContext) {
        ListPolynomial(coefficients.map { it / other })
    }
}