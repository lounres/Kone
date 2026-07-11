/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

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
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImpliedUsingFirst
import dev.lounres.kone.relations.Equality
import kotlin.math.pow as kpow


// TODO: Add other safe contexts

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object SafeLongContext: Reification<Long>, Equality<Long>, Order<Long>, Hashing<Long>, EuclideanRing<Long> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Long
    override fun reifyMaybe(element: Any?): Maybe<Long> = if (element is Long) Some(element) else None
    override fun reifyOrNull(element: Any?): Long? = element as? Long
    override fun reify(element: Any?): Long = element as? Long ?: reificationException()
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<Long> = IsZero { this == 0L }
    override val numberIsOne: IsOne<Long> = IsOne { this == 1L }
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
    override val numberUnaryMinus: UnaryMinus<Long, Long> = UnaryMinus {
        if (this == Long.MIN_VALUE) overflow()
        -this
    }
    override val numberPlusNumber: Plus<Long, Long, Long> = Plus { other ->
        val result = this + other
        if (other > 0L != result > this) overflow()
        result
    }
    override val numberMinusNumber: Minus<Long, Long, Long> = Minus { other ->
        val result = this - other
        if (other < 0L != result > this) overflow()
        result
    }
    override val numberTimesNumber: Times<Long, Long, Long> = Times { other ->
        if (other == 0L) return@Times 0L
        val result = this * other
        if (result / other != this) overflow()
        result
    }
    override val numberDivideRemainderNumber: DivideRemainder<Long, Long, EuclideanDivisionResult<Long>> = DivideRemainder { other ->
        if (other == 0L) divisionByZero()
        else EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    }
    override val numberDivideNumber: Divide<Long, Long, Long> = Divide { other -> if (other == 0L) divisionByZero() else this / other }
    override val numberRemainderNumber: Remainder<Long, Long, Long> = Remainder { other -> if (other == 0L) divisionByZero() else this % other }
    // endregion
    
    // region Long-Int operations
    override val numberPlusInt: Plus<Long, Int, Long> = Plus { other ->
        val result = this + other
        if (other > 0L != result > this) overflow()
        result
    }
    override val numberMinusInt: Minus<Long, Int, Long> = Minus { other ->
        val result = this - other
        if (other < 0L != result > this) overflow()
        result
    }
    override val numberTimesInt: Times<Long, Int, Long> = Times { other ->
        if (other == 0) return@Times 0L
        val result = this * other
        if (result / other != this) overflow()
        result
    }
    // endregion

    // region Long-UInt operations
    override val numberPlusUInt: Plus<Long, UInt, Long> = Plus { other ->
        val result = this + other.toLong()
        if (result < this) overflow()
        result
    }
    override val numberMinusUInt: Minus<Long, UInt, Long> = Minus { other ->
        val result = this - other.toLong()
        if (result < this) overflow()
        result
    }
    override val numberTimesUInt: Times<Long, UInt, Long> = Times { other ->
        if (other == 0u) return@Times 0L
        val other = other.toLong()
        val result = this * other
        if (result / other != this) overflow()
        result
    }
    // endregion
    
    // region Long-Long operations
    override val numberPlusLong: Plus<Long, Long, Long> = Plus { other ->
        val result = this + other
        if (other > 0L != result > this) overflow()
        result
    }
    override val numberMinusLong: Minus<Long, Long, Long> = Minus { other ->
        val result = this - other
        if (other < 0L != result > this) overflow()
        result
    }
    override val numberTimesLong: Times<Long, Long, Long> = Times { other ->
        if (other == 0L) return@Times 0L
        val result = this * other
        if (result / other != this) overflow()
        result
    }
    // endregion

    // region Long-ULong operations
    override val numberPlusULong: Plus<Long, ULong, Long> = Plus { other ->
        val result = this + other.toLong()
        if (result < this) overflow()
        result
    }
    override val numberMinusULong: Minus<Long, ULong, Long> = Minus { other ->
        val result = this - other.toLong()
        if (result > this) overflow()
        result
    }
    override val numberTimesULong: Times<Long, ULong, Long> = Times { other ->
        if (this == 0L) return@Times 0L
        if (other and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val other = other.toLong()
        val result = this * other
        if (result / this != other) overflow()
        result
    }
    // endregion
    
    // region Int-Long operations
    override val intPlusNumber: Plus<Int, Long, Long> = Plus { other ->
        val result = this + other
        if (other > 0L != result > this) overflow()
        result
    }
    override val intMinusNumber: Minus<Int, Long, Long> = Minus { other ->
        val result = this - other
        if (other < 0L != result > this) overflow()
        result
    }
    override val intTimesNumber: Times<Int, Long, Long> = Times { other ->
        if (other == 0L) return@Times 0L
        val result = this * other
        if (result / other != this.toLong()) overflow()
        result
    }
    // endregion

    // region UInt-Long operations
    override val uIntPlusNumber: Plus<UInt, Long, Long> = Plus { other ->
        val result = this.toLong() + other
        if (result < other) overflow()
        result
    }
    override val uIntMinusNumber: Minus<UInt, Long, Long> = Minus { other ->
        val that = this.toLong()
        val result = that - other
        if (other < 0L != result > that) overflow()
        result
    }
    override val uIntTimesNumber: Times<UInt, Long, Long> = Times { other ->
        if (other == 0L) return@Times 0L
        val result = this.toLong() * other
        if (result / other != this.toLong()) overflow()
        result
    }
    // endregion
    
    // region Long-Long operations
    override val longPlusNumber: Plus<Long, Long, Long> = Plus { other ->
        val result = this + other
        if (other > 0L != result > this) overflow()
        result
    }
    override val longMinusNumber: Minus<Long, Long, Long> = Minus { other ->
        val result = this - other
        if (other < 0L != result > this) overflow()
        result
    }
    override val longTimesNumber: Times<Long, Long, Long> = Times { other ->
        if (other == 0L) return@Times 0L
        val result = this * other
        if (result / other != this) overflow()
        result
    }
    // endregion

    // region ULong-Long operations
    override val uLongPlusNumber: Plus<ULong, Long, Long> = Plus { other ->
        val result = this.toLong() + other
        if (result < other) overflow()
        result
    }
    override val uLongMinusNumber: Minus<ULong, Long, Long> = Minus { other ->
        val that = this.toLong()
        val result = that - other
        if (other < 0L != result > that) overflow()
        result
    }
    override val uLongTimesNumber: Times<ULong, Long, Long> = Times { other ->
        if (other == 0L) return@Times 0L
        if (this and 0b1000000000000000000000000000000000000000000000000000000000000000uL != 0uL) overflow()
        val that = this.toLong()
        val result = that * other
        if (result / other != that) overflow()
        result
    }
    // endregion
}

public fun Long.Companion.safeReification(): Reification<Long> = SafeLongContext
public fun Long.Companion.safeEquality(): Equality<Long> = SafeLongContext
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

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeReification() {
    Reification.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEquality() {
    Equality.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeOrder() {
    Order.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeHashing() {
    Hashing.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEuclideanRing() {
    EuclideanRing.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeEuclideanSemiring() {
    EuclideanSemiring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeRing() {
    CommutativeRing.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeRing() {
    Ring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeSemiring() {
    CommutativeSemiring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeSemiring() {
    Semiring.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeGroup() {
    CommutativeGroup.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeGroup() {
    Group.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeMonoid() {
    CommutativeMonoid.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeMonoid() {
    Monoid.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeCommutativeSemigroup() {
    CommutativeSemigroup.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Long.Companion.setSafeSemigroup() {
    Semigroup.Key<Long>().withImpliedUsingFirst correspondsTo SafeLongContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object SafeDoubleContext: Reification<Double>, Equality<Double>, Order<Double>, Hashing<Double>, Field<Double> {
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
    
    // region Equality
    override fun Double.equalsTo(other: Double): Boolean {
        this.validate()
        other.validate()
        return this == other
    }
    override val numberIsZero: IsZero<Double> = IsZero {
        this.validate()
        this == 0.0
    }
    override val numberIsOne: IsOne<Double> = IsOne {
        this.validate()
        this == 1.0
    }
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
    
    // region Conversion
    override fun valueOf(arg: Int): Double = arg.toDouble()
    override fun valueOf(arg: UInt): Double = arg.toDouble()
    override fun valueOf(arg: Long): Double = arg.toDouble()
    override fun valueOf(arg: ULong): Double = arg.toDouble()
    // endregion
    
    // region Double-Int operations
    override val numberPlusInt: Plus<Double, Int, Double> = Plus { other ->
        this.validate()
        this + other
    }
    override val numberMinusInt: Minus<Double, Int, Double> = Minus { other ->
        this.validate()
        this - other
    }
    override val numberTimesInt: Times<Double, Int, Double> = Times { other ->
        this.validate()
        this * other
    }
    override val numberDivideInt: Divide<Double, Int, Double> = Divide { other ->
        this.validate()
        if (other == 0) divisionByZero()
        this / other
    }
    // endregion
    
    // region Double-UInt operations
    override val numberPlusUInt: Plus<Double, UInt, Double> = Plus { other ->
        this.validate()
        this + other.toDouble()
    }
    override val numberMinusUInt: Minus<Double, UInt, Double> = Minus { other ->
        this.validate()
        this - other.toDouble()
    }
    override val numberTimesUInt: Times<Double, UInt, Double> = Times { other ->
        this.validate()
        this * other.toDouble()
    }
    override val numberDivideUInt: Divide<Double, UInt, Double> = Divide { other ->
        this.validate()
        if (other == 0u) divisionByZero()
        this / other.toDouble()
    }
    // endregion
    
    // region Double-Long operations
    override val numberPlusLong: Plus<Double, Long, Double> = Plus { other ->
        this.validate()
        this + other
    }
    override val numberMinusLong: Minus<Double, Long, Double> = Minus { other ->
        this.validate()
        this - other
    }
    override val numberTimesLong: Times<Double, Long, Double> = Times { other ->
        this.validate()
        this * other
    }
    override val numberDivideLong: Divide<Double, Long, Double> = Divide { other ->
        this.validate()
        if (other == 0L) divisionByZero()
        this / other
    }
    // endregion
    
    // region Double-ULong operations
    override val numberPlusULong: Plus<Double, ULong, Double> = Plus { other ->
        this.validate()
        this + other.toDouble()
    }
    override val numberMinusULong: Minus<Double, ULong, Double> = Minus { other ->
        this.validate()
        this - other.toDouble()
    }
    override val numberTimesULong: Times<Double, ULong, Double> = Times { other ->
        this.validate()
        this * other.toDouble()
    }
    override val numberDivideULong: Divide<Double, ULong, Double> = Divide { other ->
        this.validate()
        if (other == 0uL) divisionByZero()
        this / other.toDouble()
    }
    // endregion
    
    // region Int-Double operations
    override val intPlusNumber: Plus<Int, Double, Double> = Plus { other ->
        other.validate()
        this + other
    }
    override val intMinusNumber: Minus<Int, Double, Double> = Minus { other ->
        other.validate()
        this - other
    }
    override val intTimesNumber: Times<Int, Double, Double> = Times { other ->
        other.validate()
        this * other
    }
    override val intDivideNumber: Divide<Int, Double, Double> = Divide { other ->
        other.validate()
        if (other == 0.0) divisionByZero()
        this / other
    }
    // endregion
    
    // region UInt-Double operations
    override val uIntPlusNumber: Plus<UInt, Double, Double> = Plus { other ->
        other.validate()
        this.toDouble() + other
    }
    override val uIntMinusNumber: Minus<UInt, Double, Double> = Minus { other ->
        other.validate()
        this.toDouble() - other
    }
    override val uIntTimesNumber: Times<UInt, Double, Double> = Times { other ->
        other.validate()
        this.toDouble() * other
    }
    override val uIntDivideNumber: Divide<UInt, Double, Double> = Divide { other ->
        other.validate()
        if (other == 0.0) divisionByZero()
        this.toDouble() / other
    }
    // endregion
    
    // region Long-Double operations
    override val longPlusNumber: Plus<Long, Double, Double> = Plus { other ->
        other.validate()
        this + other
    }
    override val longMinusNumber: Minus<Long, Double, Double> = Minus { other ->
        other.validate()
        this - other
    }
    override val longTimesNumber: Times<Long, Double, Double> = Times { other ->
        other.validate()
        this * other
    }
    override val longDivideNumber: Divide<Long, Double, Double> = Divide { other ->
        other.validate()
        if (other == 0.0) divisionByZero()
        this / other
    }
    // endregion
    
    // region ULong-Double operations
    override val uLongPlusNumber: Plus<ULong, Double, Double> = Plus { other ->
        other.validate()
        this.toDouble() + other
    }
    override val uLongMinusNumber: Minus<ULong, Double, Double> = Minus { other ->
        other.validate()
        this.toDouble() - other
    }
    override val uLongTimesNumber: Times<ULong, Double, Double> = Times { other ->
        other.validate()
        this.toDouble() * other
    }
    override val uLongDivideNumber: Divide<ULong, Double, Double> = Divide { other ->
        other.validate()
        if (other == 0.0) divisionByZero()
        this.toDouble() / other
    }
    // endregion
    
    // region Double-Double operations
    override val numberUnaryMinus: UnaryMinus<Double, Double> = UnaryMinus {
        this.validate()
        -this
    }
    override val numberPlusNumber: Plus<Double, Double, Double> = Plus { other ->
        this.validate()
        other.validate()
        this + other
    }
    override val numberMinusNumber: Minus<Double, Double, Double> = Minus { other ->
        this.validate()
        other.validate()
        this - other
    }
    override val numberTimesNumber: Times<Double, Double, Double> = Times { other ->
        this.validate()
        other.validate()
        this * other
    }
    override val numberDivideNumber: Divide<Double, Double, Double> = Divide { other ->
        this.validate()
        other.validate()
        if (other == 0.0) divisionByZero()
        this / other
    }
    override val powerNumberUInt: Power<Double, UInt, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent.toDouble())
    }
    override val powerNumberULong: Power<Double, ULong, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent.toDouble())
    }
    override val powerNumberInt: Power<Double, Int, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent)
    }
    override val powerNumberLong: Power<Double, Long, Double> = Power { base, exponent ->
        base.validate()
        if (base <= 0.0) throw IllegalArgumentException("Cannot take power of non-positive number $base")
        base.kpow(exponent.toDouble())
    }
    // endregion
}

public fun Double.Companion.safeReification(): Reification<Double> = SafeDoubleContext
public fun Double.Companion.safeEquality(): Equality<Double> = SafeDoubleContext
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

context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeReification() {
    Reification.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeEquality() {
    Equality.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeOrder() {
    Order.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeHashing() {
    Hashing.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeField() {
    Field.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeRing() {
    CommutativeRing.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeRing() {
    Ring.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeSemiring() {
    CommutativeSemiring.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeSemiring() {
    Semiring.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeGroup() {
    CommutativeGroup.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeGroup() {
    Group.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeMonoid() {
    CommutativeMonoid.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeMonoid() {
    Monoid.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeCommutativeSemigroup() {
    CommutativeSemigroup.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun Double.Companion.setSafeSemigroup() {
    Semigroup.Key<Double>().withImpliedUsingFirst correspondsTo SafeDoubleContext
}