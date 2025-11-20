/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package dev.lounres.kone.polynomial

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.utils.*
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.scope


public data class ListPolynomial<Number>(
    public val coefficients: KoneList<Number>
) {
    override fun toString(): String = "ListPolynomial$coefficients"
}

public open class ListPolynomialSpace<Number>(
    protected open val ring: CommutativeRing<Number>,
) : CommutativeAlgebra<Number, ListPolynomial<Number>> {
    final override val zero: ListPolynomial<Number> = ListPolynomial(KoneList.empty())
    final override val one: ListPolynomial<Number> = ring.one.asListPolynomial()
    public val variable: ListPolynomial<Number> = ListPolynomial(ring.zero, ring.one)
    
    final override fun valueOf(arg: Int): ListPolynomial<Number> = ring.valueOf(arg).asListPolynomial()
    final override fun valueOf(arg: Long): ListPolynomial<Number> = ring.valueOf(arg).asListPolynomial()
    final override fun valueOf(arg: UInt): ListPolynomial<Number> = ring.valueOf(arg).asListPolynomial()
    final override fun valueOf(arg: ULong): ListPolynomial<Number> = ring.valueOf(arg).asListPolynomial()
    final override fun valueOf(arg: Number): ListPolynomial<Number> = arg.asListPolynomial()

    final override infix fun ListPolynomial<Number>.equalsTo(other: ListPolynomial<Number>): Boolean = ring {
        for (index in 0u .. maxOf(this.coefficients.lastIndex, other.coefficients.lastIndex))
            when (index) {
                !in this.coefficients.indices -> if (other.coefficients[index].isNotZero()) return false
                !in other.coefficients.indices -> if (this.coefficients[index].isNotZero()) return false
                else -> if (!(other.coefficients[index] equalsTo this.coefficients[index])) return false
            }
        return true
    }
    final override fun ListPolynomial<Number>.isZero(): Boolean = ring { coefficients.all { it.isZero() } }
    final override fun ListPolynomial<Number>.isOne(): Boolean = ring { coefficients.allIndexed { index, it -> index == 0u || it.isZero() } }

    final override operator fun ListPolynomial<Number>.plus(other: Int): ListPolynomial<Number> = ring {
        if (other == 0) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } + other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: Int): ListPolynomial<Number> = ring {
        if (other == 0) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } - other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: Int): ListPolynomial<Number> = ring {
        when (other) {
            0 -> zero
            1 -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }
    
    final override operator fun ListPolynomial<Number>.plus(other: UInt): ListPolynomial<Number> = ring {
        if (other == 0u) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } + other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: UInt): ListPolynomial<Number> = ring {
        if (other == 0u) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } - other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: UInt): ListPolynomial<Number> = ring {
        when (other) {
            0u -> zero
            1u -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }

    final override operator fun ListPolynomial<Number>.plus(other: Long): ListPolynomial<Number> = ring {
        if (other == 0L) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } + other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: Long): ListPolynomial<Number> = ring {
        if (other == 0L) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } - other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: Long): ListPolynomial<Number> = ring {
        when (other) {
            0L -> zero
            1L -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }
    
    final override operator fun ListPolynomial<Number>.plus(other: ULong): ListPolynomial<Number> = ring {
        if (other == 0uL) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } + other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: ULong): ListPolynomial<Number> = ring {
        if (other == 0uL) this
        else {
            val result = coefficients.getOrElse(0u) { ring.one } - other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.times(other: ULong): ListPolynomial<Number> = ring {
        when (other) {
            0uL -> zero
            1uL -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }

    final override operator fun Int.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        if (this == 0) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { ring.zero }
            ListPolynomial(
                if (other.coefficients.size == 0u) KoneList.of(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun Int.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        ListPolynomial(
            scope {
                if (this == 0) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { ring.zero }
                    
                    if (other.coefficients.size == 0u) KoneList.of(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun Int.times(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        when (this) {
            0 -> zero
            1 -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }
    
    final override operator fun UInt.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        if (this == 0u) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { ring.zero }
            ListPolynomial(
                if (other.coefficients.size == 0u) KoneList.of(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun UInt.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        ListPolynomial(
            scope {
                if (this == 0u) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { ring.zero }
                    
                    if (other.coefficients.size == 0u) KoneList.of(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun UInt.times(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        when (this) {
            0u -> zero
            1u -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }

    final override operator fun Long.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        if (this == 0L) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { ring.zero }
            ListPolynomial(
                if (other.coefficients.size == 0u) KoneList.of(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun Long.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        ListPolynomial(
            scope {
                if (this == 0L) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { ring.zero }
                    
                    if (other.coefficients.size == 0u) KoneList.of(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun Long.times(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        when (this) {
            0L -> zero
            1L -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }
    
    final override operator fun ULong.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        if (this == 0uL) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { ring.zero }
            ListPolynomial(
                if (other.coefficients.size == 0u) KoneList.of(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ULong.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        ListPolynomial(
            scope {
                if (this == 0uL) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { ring.zero }
                    
                    if (other.coefficients.size == 0u) KoneList.of(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun ULong.times(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        when (this) {
            0uL -> zero
            1uL -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }

    final override operator fun Number.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        if (this.isZero()) other
        else {
            val result = this@plus + other.coefficients.getOrElse(0u) { ring.zero }
            ListPolynomial(
                if (other.coefficients.size == 0u) KoneList.of(result)
                else other.coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun Number.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        ListPolynomial(
            scope {
                if (this.isZero()) { other.coefficients.map { -it } }
                else {
                    val result = this@minus - other.coefficients.getOrElse(0u) { ring.zero }
                    
                    if (other.coefficients.size == 0u) KoneList.of(result)
                    else other.coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun Number.times(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        when {
            this.isZero() -> zero
            this.isOne() -> other
            else -> ListPolynomial(
                other.coefficients.map { this@times * it }
            )
        }
    }

    final override operator fun ListPolynomial<Number>.plus(other: Number): ListPolynomial<Number> = ring {
        if (other.isZero()) this
        else {
            val result = coefficients.getOrElse(0u) { ring.zero } + other
            ListPolynomial(
                if (coefficients.size == 0u) KoneList.of(result)
                else coefficients.toKoneSettableList().apply { this[0u] = result }
            )
        }
    }
    final override operator fun ListPolynomial<Number>.minus(other: Number): ListPolynomial<Number> = ring {
        ListPolynomial(
            scope {
                if (other.isZero()) { coefficients.map { -it } }
                else {
                    val result = coefficients.getOrElse(0u) { ring.zero } - other
                    
                    if (coefficients.size == 0u) KoneList.of(result)
                    else coefficients.mapIndexed { index, number -> if (index == 0u) result else -number }
                }
            }
        )
    }
    final override operator fun ListPolynomial<Number>.times(other: Number): ListPolynomial<Number> = ring {
        when {
            other.isZero() -> zero
            other.isOne() -> this
            else -> ListPolynomial(
                coefficients.map { it * other }
            )
        }
    }

    final override operator fun ListPolynomial<Number>.unaryMinus(): ListPolynomial<Number> = ring {
        ListPolynomial(coefficients.map { -it })
    }
    final override operator fun ListPolynomial<Number>.plus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        val thisSize = coefficients.lastIndexThat { _, it -> ring { it.isNotZero() } } + 1u
        val otherSize = other.coefficients.lastIndexThat { _, it -> ring { it.isNotZero() } } + 1u
        ListPolynomial(
            KoneList.generate(maxOf(thisSize, otherSize)) {
                when {
                    it >= thisSize -> other.coefficients[it]
                    it >= otherSize -> coefficients[it]
                    else -> coefficients[it] + other.coefficients[it]
                }
            }
        )
    }
    final override operator fun ListPolynomial<Number>.minus(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        val thisSize = coefficients.lastIndexThat { _, it -> ring { it.isNotZero() } } + 1u
        val otherSize = other.coefficients.lastIndexThat { _, it -> ring { it.isNotZero() } } + 1u
        ListPolynomial(
            KoneList.generate(maxOf(thisSize, otherSize)) {
                when {
                    it >= thisSize -> -other.coefficients[it]
                    it >= otherSize -> coefficients[it]
                    else -> coefficients[it] - other.coefficients[it]
                }
            }
        )
    }
    final override operator fun ListPolynomial<Number>.times(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        val thisDegree = coefficients.lastIndexThat { _, it -> ring { it.isNotZero() } }
        val otherDegree = other.coefficients.lastIndexThat { _, it -> ring { it.isNotZero() } }
        if (thisDegree == UInt.MAX_VALUE || otherDegree == UInt.MAX_VALUE) return zero
        ListPolynomial(
            KoneList.generate(thisDegree + otherDegree + 1u) { d ->
                (maxOf(otherDegree, d) - otherDegree .. minOf(thisDegree, d))
                    .map { coefficients[it] * other.coefficients[d - it] }
                    .reduce { acc, rational -> acc + rational }
            }
        )
    }
    
    // TODO: To optimize boxing
    final override fun power(base: ListPolynomial<Number>, exponent: UInt): ListPolynomial<Number> = super.power(base, exponent)
    final override fun power(base: ListPolynomial<Number>, exponent: ULong): ListPolynomial<Number> = super.power(base, exponent)
    
    public val ListPolynomial<Number>.degree: UInt
        get() = coefficients
            .lastIndexThat { _, it -> ring { it.isNotZero() } }
            .let { if (it == UInt.MAX_VALUE) zeroPolynomialDegreeException() else it }
}

public class ListPolynomialSpaceOverField<Number>(
    override val ring: Field<Number>,
) : ListPolynomialSpace<Number>(ring), EuclideanRing<ListPolynomial<Number>> {
    public fun ListPolynomial<Number>.div(other: Int): ListPolynomial<Number> = ring {
        ListPolynomial(coefficients.map { it / other })
    }
    public fun ListPolynomial<Number>.div(other: UInt): ListPolynomial<Number> = ring {
        ListPolynomial(coefficients.map { it / other })
    }
    public fun ListPolynomial<Number>.div(other: Long): ListPolynomial<Number> = ring {
        ListPolynomial(coefficients.map { it / other })
    }
    public fun ListPolynomial<Number>.div(other: ULong): ListPolynomial<Number> = ring {
        ListPolynomial(coefficients.map { it / other })
    }
    public fun ListPolynomial<Number>.div(other: Number): ListPolynomial<Number> = ring {
        ListPolynomial(coefficients.map { it / other })
    }
    
    override fun ListPolynomial<Number>.divrem(other: ListPolynomial<Number>): EuclideanDivisionResult<ListPolynomial<Number>> = ring {
        val dividendDegree = this.coefficients.lastIndexThat { _, element -> element.isNotZero() }
        val divisorDegree = other.coefficients.lastIndexThat { _, element -> element.isNotZero() }
        if (divisorDegree == UInt.MAX_VALUE) divisionByZero()
        if (dividendDegree == UInt.MAX_VALUE) return EuclideanDivisionResult(
            quotient = zero,
            remainder = zero,
        )
        if (divisorDegree > dividendDegree) return EuclideanDivisionResult(
            quotient = ListPolynomial(),
            remainder = this,
        )
        
        val divisorLeadingCoefficient = other.coefficients[divisorDegree]
        val dividendRestCoefficients = KoneSettableList.generate(dividendDegree + 1u) { this.coefficients[it] }
        val quotientRestCoefficients = KoneSettableList.generate(dividendDegree - divisorDegree + 1u) { ring.zero }
        
        for (divisionDegree in dividendDegree - divisorDegree downTo 0u) {
            val quotientCoefficient = dividendRestCoefficients[divisionDegree + divisorDegree] / divisorLeadingCoefficient
            quotientRestCoefficients[divisionDegree] = quotientCoefficient
            for (subtractionDegree in 0u .. divisorDegree)
                dividendRestCoefficients[divisionDegree + subtractionDegree] -= quotientCoefficient * other.coefficients[subtractionDegree]
        }
        
        EuclideanDivisionResult(
            quotient = ListPolynomial(quotientRestCoefficients),
            remainder = ListPolynomial(KoneList.generate(dividendRestCoefficients.lastIndexThat { _, element -> element.isNotZero() } + 1u) { dividendRestCoefficients[it] }),
        )
    }
    
    override fun ListPolynomial<Number>.div(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        val dividendDegree = this.coefficients.lastIndexThat { _, element -> element.isNotZero() }
        val divisorDegree = other.coefficients.lastIndexThat { _, element -> element.isNotZero() }
        if (divisorDegree == UInt.MAX_VALUE) divisionByZero()
        if (dividendDegree == UInt.MAX_VALUE) return zero
        if (divisorDegree > dividendDegree) return zero
        
        val divisorLeadingCoefficient = other.coefficients[divisorDegree]
        val dividendRestCoefficients = KoneSettableList.generate(dividendDegree + 1u) { this.coefficients[it] }
        val quotientRestCoefficients = KoneSettableList.generate(dividendDegree - divisorDegree + 1u) { ring.zero }
        
        for (divisionDegree in dividendDegree - divisorDegree downTo 0u) {
            val quotientCoefficient = dividendRestCoefficients[divisionDegree + divisorDegree] / divisorLeadingCoefficient
            quotientRestCoefficients[divisionDegree] = quotientCoefficient
            for (subtractionDegree in 0u .. divisorDegree)
                dividendRestCoefficients[divisionDegree + subtractionDegree] -= quotientCoefficient * other.coefficients[subtractionDegree]
        }
        
        ListPolynomial(quotientRestCoefficients)
    }
    
    override fun ListPolynomial<Number>.rem(other: ListPolynomial<Number>): ListPolynomial<Number> = ring {
        val dividendDegree = this.coefficients.lastIndexThat { _, element -> element.isNotZero() }
        val divisorDegree = other.coefficients.lastIndexThat { _, element -> element.isNotZero() }
        if (divisorDegree == UInt.MAX_VALUE) divisionByZero()
        if (dividendDegree == UInt.MAX_VALUE) return zero
        if (divisorDegree > dividendDegree) return this
        
        val divisorLeadingCoefficient = other.coefficients[divisorDegree]
        val dividendRestCoefficients = KoneSettableList.generate(dividendDegree + 1u) { this.coefficients[it] }
        
        for (divisionDegree in dividendDegree - divisorDegree downTo 0u) {
            val quotientCoefficient = dividendRestCoefficients[divisionDegree + divisorDegree] / divisorLeadingCoefficient
            for (subtractionDegree in 0u .. divisorDegree)
                dividendRestCoefficients[divisionDegree + subtractionDegree] -= quotientCoefficient * other.coefficients[subtractionDegree]
        }
        
        ListPolynomial(KoneList.generate(dividendRestCoefficients.lastIndexThat { _, element -> element.isNotZero() } + 1u) { dividendRestCoefficients[it] })
    }
}