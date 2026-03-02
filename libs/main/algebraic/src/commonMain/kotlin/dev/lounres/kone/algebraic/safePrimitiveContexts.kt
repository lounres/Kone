/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("unused")

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.asComparisonResult
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.math.pow as kpow


// TODO: Add other safe contexts

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object SafeLongContext: Reification<Long>, EuclideanRing<Long>, Order<Long>, Hashing<Long> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Long
    override fun reifyMaybe(element: Any?): Maybe<Long> = if (element is Long) Some(element) else None
    override fun reifyOrNull(element: Any?): Long? = element as? Long
    override fun reify(element: Any?): Long = element as? Long ?: reificationException()
    // endregion

    // region Order
    override fun Long.compareWith(other: Long): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Long get() = 0L
    override val one: Long get() = 1L
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Long = arg.toLong()
    override fun valueOf(arg: UInt): Long = arg.toLong()
    override fun valueOf(arg: Long): Long = arg
    override fun valueOf(arg: ULong): Long {
        if (arg and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        return arg.toLong()
    }
    // endregion

    // region Long-Long operations
    override operator fun Long.unaryMinus(): Long {
        if (this == Long.MIN_VALUE) overflow()
        return -this
    }
    override operator fun Long.plus(other: Long): Long {
        val result = this + other
        if (other > 0L != result > this) overflow()
        return result
    }
    override operator fun Long.minus(other: Long): Long {
        val result = this - other
        if (other < 0L != result > this) overflow()
        return result
    }
    override operator fun Long.times(other: Long): Long {
        if (other == 0L) return 0L
        val result = this * other
        if (result / other != this) overflow()
        return result
    }
    override fun Long.divrem(other: Long): EuclideanDivisionResult<Long> =
        if (other == 0L) divisionByZero()
        else EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Long.div(other: Long): Long = if (other == 0L) divisionByZero() else this / other
    override fun Long.rem(other: Long): Long = if (other == 0L) divisionByZero() else this % other
    // endregion

    // region Long-Int operations
    override operator fun Long.plus(other: Int): Long {
        val result = this + other
        if (other > 0L != result > this) overflow()
        return result
    }
    override operator fun Long.minus(other: Int): Long {
        val result = this - other
        if (other < 0L != result > this) overflow()
        return result
    }
    override operator fun Long.times(other: Int): Long {
        if (other == 0) return 0L
        val result = this * other
        if (result / other != this) overflow()
        return result
    }
    // endregion

    // region Long-UInt operations
    override operator fun Long.plus(other: UInt): Long {
        val result = this + other.toLong()
        if (result < this) overflow()
        return result
    }
    override operator fun Long.minus(other: UInt): Long {
        val result = this - other.toLong()
        if (result > this) overflow()
        return result
    }
    override operator fun Long.times(other: UInt): Long {
        if (other == 0u) return 0L
        val other = other.toLong()
        val result = this * other
        if (result / other != this) overflow()
        return result
    }
    // endregion

    // region Long-ULong operations
    override operator fun Long.plus(other: ULong): Long {
        val result = this + other.toLong()
        if (result < this) overflow()
        return result
    }
    override operator fun Long.minus(other: ULong): Long {
        val result = this - other.toLong()
        if (result > this) overflow()
        return result
    }
    override operator fun Long.times(other: ULong): Long {
        if (this == 0L) return 0L
        if (other and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val other = other.toLong()
        val result = this * other
        if (result / this != other) overflow()
        return result
    }
    // endregion

    // region Int-Long operations
    override operator fun Int.plus(other: Long): Long {
        val result = this + other
        if (other > 0L != result > this) overflow()
        return result
    }
    override operator fun Int.minus(other: Long): Long {
        val result = this - other
        if (other < 0L != result > this) overflow()
        return result
    }
    override operator fun Int.times(other: Long): Long {
        if (other == 0L) return 0L
        val result = this * other
        if (result / other != this.toLong()) overflow()
        return result
    }
    // endregion

    // region UInt-Long operations
    override operator fun UInt.plus(other: Long): Long {
        val result = this.toLong() + other
        if (result < other) overflow()
        return result
    }
    override operator fun UInt.minus(other: Long): Long {
        val that = this.toLong()
        val result = that - other
        if (other < 0L != result > that) overflow()
        return result
    }
    override operator fun UInt.times(other: Long): Long {
        if (other == 0L) return 0L
        val result = this.toLong() * other
        if (result / other != this.toLong()) overflow()
        return result
    }
    // endregion

    // region ULong-Long operations
    override operator fun ULong.plus(other: Long): Long {
        val result = this.toLong() + other
        if (result < other) overflow()
        return result
    }
    override operator fun ULong.minus(other: Long): Long {
        TODO()
    }
    override operator fun ULong.times(other: Long): Long {
        if (other == 0L) return 0L
        if (this and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val that = this.toLong()
        val result = that * other
        if (result / other != that) overflow()
        return result
    }
    // endregion
}

public fun Long.Companion.safeReification(): Reification<Long> = SafeLongContext
public fun Long.Companion.safeOrder(): Order<Long> = SafeLongContext
public fun Long.Companion.safeHashing(): Hashing<Long> = SafeLongContext
public fun Long.Companion.safeEuclideanRing(): EuclideanRing<Long> = SafeLongContext
public fun Long.Companion.safeEuclideanSemiring(): EuclideanSemiring<Long> = SafeLongContext
public fun Long.Companion.safeCommutativeRing(): CommutativeRing<Long> = SafeLongContext
public fun Long.Companion.safeRing(): Ring<Long> = SafeLongContext
public fun Long.Companion.safeCommutativeSemiring(): CommutativeSemiring<Long> = SafeLongContext
public fun Long.Companion.safeSemiring(): Semiring<Long> = SafeLongContext
public fun Long.Companion.safeCommutativeGroup(): CommutativeGroup<Long> = SafeLongContext
public fun Long.Companion.safeGroup(): Group<Long> = SafeLongContext
public fun Long.Companion.safeCommutativeMonoid(): CommutativeMonoid<Long> = SafeLongContext
public fun Long.Companion.safeMonoid(): Monoid<Long> = SafeLongContext
public fun Long.Companion.safeCommutativeSemigroup(): CommutativeSemigroup<Long> = SafeLongContext
public fun Long.Companion.safeSemigroup(): Semigroup<Long> = SafeLongContext
public fun Long.Companion.safeEquality(): Equality<Long> = SafeLongContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeReification() {
    Reification.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeOrder() {
    Order.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeHashing() {
    Hashing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEuclideanRing() {
    EuclideanRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEuclideanSemiring() {
    EuclideanSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeRing() {
    CommutativeRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeRing() {
    Ring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeSemiring() {
    CommutativeSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeSemiring() {
    Semiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeGroup() {
    CommutativeGroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeGroup() {
    Group.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeMonoid() {
    CommutativeMonoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeMonoid() {
    Monoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeSemigroup() {
    CommutativeSemigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeSemigroup() {
    Semigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEquality() {
    Equality.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object SafeDoubleContext: Reification<Double>, Field<Double>, Order<Double>, Hashing<Double> {
    private fun Double.validate() {
        when {
            this.isNaN() -> throw IllegalArgumentException("NaN encountered!")
            this.isInfinite() -> throw IllegalArgumentException("Infinity encountered!")
        }
    }
    
    // region Reification
    override fun contains(element: Any?): Boolean =
        if (element is Double) {
            element.validate()
            true
        } else false
    override fun reifyMaybe(element: Any?): Maybe<Double> =
        if (element is Double) {
            element.validate()
            Some(element)
        } else None
    override fun reifyOrNull(element: Any?): Double? =
        if (element is Double) {
            element.validate()
            element
        } else null
    override fun reify(element: Any?): Double =
        if (element is Double) {
            element.validate()
            element
        } else reificationException()
    // endregion
    
    // region Order
    override fun Double.compareWith(other: Double): ComparisonResult {
        this.validate()
        other.validate()
        return this.compareTo(other).asComparisonResult()
    }
    // endregion
    
    // region Constants
    override val zero: Double get() = 0.0
    override val one: Double get() = 1.0
    // endregion
    
    // region Equality
    override fun Double.equalsTo(other: Double): Boolean {
        this.validate()
        other.validate()
        return this == other
    }
    // endregion
    
    // region Conversion
    override fun valueOf(arg: Int): Double = arg.toDouble()
    override fun valueOf(arg: UInt): Double = arg.toDouble()
    override fun valueOf(arg: Long): Double = arg.toDouble()
    override fun valueOf(arg: ULong): Double = arg.toDouble()
    // endregion
    
    // region Double-Int operations
    override fun Double.plus(other: Int): Double {
        this.validate()
        return this + other
    }
    override fun Double.minus(other: Int): Double {
        this.validate()
        return this - other
    }
    override fun Double.times(other: Int): Double {
        this.validate()
        return this * other
    }
    override fun Double.div(other: Int): Double {
        this.validate()
        if (other == 0) divisionByZero()
        return this / other
    }
    // endregion
    
    // region Double-UInt operations
    override fun Double.plus(other: UInt): Double {
        this.validate()
        return this + other.toDouble()
    }
    override fun Double.minus(other: UInt): Double {
        this.validate()
        return this - other.toDouble()
    }
    override fun Double.times(other: UInt): Double {
        this.validate()
        return this * other.toDouble()
    }
    override fun Double.div(other: UInt): Double {
        this.validate()
        if (other == 0u) divisionByZero()
        return this / other.toDouble()
    }
    // endregion
    
    // region Double-Long operations
    override fun Double.plus(other: Long): Double {
        this.validate()
        return this + other
    }
    override fun Double.minus(other: Long): Double {
        this.validate()
        return this - other
    }
    override fun Double.times(other: Long): Double {
        this.validate()
        return this * other
    }
    override fun Double.div(other: Long): Double {
        this.validate()
        if (other == 0L) divisionByZero()
        return this / other
    }
    // endregion
    
    // region Double-ULong operations
    override fun Double.plus(other: ULong): Double {
        this.validate()
        return this + other.toDouble()
    }
    override fun Double.minus(other: ULong): Double {
        this.validate()
        return this - other.toDouble()
    }
    override fun Double.times(other: ULong): Double {
        this.validate()
        return this * other.toDouble()
    }
    override fun Double.div(other: ULong): Double {
        this.validate()
        if (other == 0uL) divisionByZero()
        return this / other.toDouble()
    }
    // endregion
    
    // region Int-Double operations
    override fun Int.plus(other: Double): Double {
        other.validate()
        return this + other
    }
    override fun Int.minus(other: Double): Double {
        other.validate()
        return this - other
    }
    override fun Int.times(other: Double): Double {
        other.validate()
        return this * other
    }
    override fun Int.div(other: Double): Double {
        other.validate()
        if (other == 0.0) divisionByZero()
        return this / other
    }
    // endregion
    
    // region UInt-Double operations
    override fun UInt.plus(other: Double): Double {
        other.validate()
        return this.toDouble() + other
    }
    override fun UInt.minus(other: Double): Double {
        other.validate()
        return this.toDouble() - other
    }
    override fun UInt.times(other: Double): Double {
        other.validate()
        return this.toDouble() * other
    }
    override fun UInt.div(other: Double): Double {
        other.validate()
        if (other == 0.0) divisionByZero()
        return this.toDouble() / other
    }
    // endregion
    
    // region Long-Double operations
    override fun Long.plus(other: Double): Double {
        other.validate()
        return this + other
    }
    override fun Long.minus(other: Double): Double {
        other.validate()
        return this - other
    }
    override fun Long.times(other: Double): Double {
        other.validate()
        return this * other
    }
    override fun Long.div(other: Double): Double {
        other.validate()
        if (other == 0.0) divisionByZero()
        return this / other
    }
    // endregion
    
    // region ULong-Double operations
    override fun ULong.plus(other: Double): Double {
        other.validate()
        return this.toDouble() + other
    }
    override fun ULong.minus(other: Double): Double {
        other.validate()
        return this.toDouble() - other
    }
    override fun ULong.times(other: Double): Double {
        other.validate()
        return this.toDouble() * other
    }
    override fun ULong.div(other: Double): Double {
        other.validate()
        if (other == 0.0) divisionByZero()
        return this.toDouble() / other
    }
    // endregion
    
    // region Double-Double operations
    override fun Double.unaryMinus(): Double {
        this.validate()
        return -this
    }
    override fun Double.plus(other: Double): Double {
        this.validate()
        other.validate()
        return this + other
    }
    override fun Double.minus(other: Double): Double {
        this.validate()
        other.validate()
        return this - other
    }
    override fun Double.times(other: Double): Double {
        this.validate()
        other.validate()
        return this * other
    }
    override fun Double.div(other: Double): Double {
        this.validate()
        other.validate()
        if (other == 0.0) divisionByZero()
        return this / other
    }
    override fun power(base: Double, exponent: UInt): Double {
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        return base.kpow(exponent.toDouble())
    }
    override fun power(base: Double, exponent: ULong): Double {
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        return base.kpow(exponent.toDouble())
    }
    override fun power(base: Double, exponent: Int): Double {
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        return base.kpow(exponent)
    }
    override fun power(base: Double, exponent: Long): Double {
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        return base.kpow(exponent.toDouble())
    }
    // endregion
}

public fun Double.Companion.safeReification(): Reification<Double> = SafeDoubleContext
public fun Double.Companion.safeOrder(): Order<Double> = SafeDoubleContext
public fun Double.Companion.safeHashing(): Hashing<Double> = SafeDoubleContext
public fun Double.Companion.safeField(): Field<Double> = SafeDoubleContext
public fun Double.Companion.safeCommutativeRing(): CommutativeRing<Double> = SafeDoubleContext
public fun Double.Companion.safeRing(): Ring<Double> = SafeDoubleContext
public fun Double.Companion.safeCommutativeSemiring(): CommutativeSemiring<Double> = SafeDoubleContext
public fun Double.Companion.safeSemiring(): Semiring<Double> = SafeDoubleContext
public fun Double.Companion.safeCommutativeGroup(): CommutativeGroup<Double> = SafeDoubleContext
public fun Double.Companion.safeGroup(): Group<Double> = SafeDoubleContext
public fun Double.Companion.safeCommutativeMonoid(): CommutativeMonoid<Double> = SafeDoubleContext
public fun Double.Companion.safeMonoid(): Monoid<Double> = SafeDoubleContext
public fun Double.Companion.safeCommutativeSemigroup(): CommutativeSemigroup<Double> = SafeDoubleContext
public fun Double.Companion.safeSemigroup(): Semigroup<Double> = SafeDoubleContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeReification() {
    Reification.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeOrder() {
    Order.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeHashing() {
    Hashing.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeField() {
    Field.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeRing() {
    CommutativeRing.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeRing() {
    Ring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeSemiring() {
    CommutativeSemiring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeSemiring() {
    Semiring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeGroup() {
    CommutativeGroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeGroup() {
    Group.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeMonoid() {
    CommutativeMonoid.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeMonoid() {
    Monoid.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeSemigroup() {
    CommutativeSemigroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeSemigroup() {
    Semigroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo SafeDoubleContext
}