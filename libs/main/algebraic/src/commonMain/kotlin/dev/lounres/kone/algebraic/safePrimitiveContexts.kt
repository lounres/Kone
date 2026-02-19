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

public fun Reification.Companion.safeFor(target: Long.Companion): Reification<Long> = SafeLongContext
public fun Order.Companion.safeFor(target: Long.Companion): Order<Long> = SafeLongContext
public fun Hashing.Companion.safeFor(target: Long.Companion): Hashing<Long> = SafeLongContext
public fun EuclideanRing.Companion.safeFor(target: Long.Companion): EuclideanRing<Long> = SafeLongContext
public fun EuclideanSemiring.Companion.safeFor(target: Long.Companion): EuclideanSemiring<Long> = SafeLongContext
public fun CommutativeRing.Companion.safeFor(target: Long.Companion): CommutativeRing<Long> = SafeLongContext
public fun Ring.Companion.safeFor(target: Long.Companion): Ring<Long> = SafeLongContext
public fun CommutativeSemiring.Companion.safeFor(target: Long.Companion): CommutativeSemiring<Long> = SafeLongContext
public fun Semiring.Companion.safeFor(target: Long.Companion): Semiring<Long> = SafeLongContext
public fun CommutativeGroup.Companion.safeFor(target: Long.Companion): CommutativeGroup<Long> = SafeLongContext
public fun Group.Companion.safeFor(target: Long.Companion): Group<Long> = SafeLongContext
public fun CommutativeMonoid.Companion.safeFor(target: Long.Companion): CommutativeMonoid<Long> = SafeLongContext
public fun Monoid.Companion.safeFor(target: Long.Companion): Monoid<Long> = SafeLongContext
public fun CommutativeSemigroup.Companion.safeFor(target: Long.Companion): CommutativeSemigroup<Long> = SafeLongContext
public fun Semigroup.Companion.safeFor(target: Long.Companion): Semigroup<Long> = SafeLongContext
public fun Equality.Companion.safeFor(target: Long.Companion): Equality<Long> = SafeLongContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setSafeFor(target: Long.Companion) {
    Reification.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setSafeFor(target: Long.Companion) {
    Order.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setSafeFor(target: Long.Companion) {
    Hashing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanRing.Companion.setSafeFor(target: Long.Companion) {
    EuclideanRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setSafeFor(target: Long.Companion) {
    EuclideanSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setSafeFor(target: Long.Companion) {
    CommutativeRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setSafeFor(target: Long.Companion) {
    Ring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setSafeFor(target: Long.Companion) {
    CommutativeSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setSafeFor(target: Long.Companion) {
    Semiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setSafeFor(target: Long.Companion) {
    CommutativeGroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setSafeFor(target: Long.Companion) {
    Group.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setSafeFor(target: Long.Companion) {
    CommutativeMonoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setSafeFor(target: Long.Companion) {
    Monoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setSafeFor(target: Long.Companion) {
    CommutativeSemigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setSafeFor(target: Long.Companion) {
    Semigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setSafeFor(target: Long.Companion) {
    Equality.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo SafeLongContext
}