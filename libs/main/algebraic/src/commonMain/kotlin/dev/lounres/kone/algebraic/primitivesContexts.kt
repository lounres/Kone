/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */


@file:Suppress("unused")

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
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
import dev.lounres.kone.suppliedTypes.suppliedType
import kotlin.math.pow as kpow


@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object ByteContext: Reification<Byte>, EuclideanRing<Byte>, Order<Byte>, Hashing<Byte> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Byte
    override fun reifyMaybe(element: Any?): Maybe<Byte> = if (element is Byte) Some(element) else None
    override fun reifyOrNull(element: Any?): Byte? = element as? Byte
    override fun reify(element: Any?): Byte = element as? Byte ?: reificationException()
    // endregion
    
    // region Order
    override fun Byte.compareWith(other: Byte): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Byte get() = 0
    override val one: Byte get() = 1
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Byte = arg.toByte()
    override fun valueOf(arg: UInt): Byte = arg.toByte()
    override fun valueOf(arg: Long): Byte = arg.toByte()
    override fun valueOf(arg: ULong): Byte = arg.toByte()
    // endregion

    // region Byte-Int operations
    override fun Byte.plus(other: Int): Byte = (this + other).toByte()
    override fun Byte.minus(other: Int): Byte = (this - other).toByte()
    override fun Byte.times(other: Int): Byte = (this * other).toByte()
    // endregion
    
    // region Byte-UInt operations
    override fun Byte.plus(other: UInt): Byte = (this + other.toByte()).toByte()
    override fun Byte.minus(other: UInt): Byte = (this - other.toByte()).toByte()
    override fun Byte.times(other: UInt): Byte = (this * other.toByte()).toByte()
    // endregion

    // region Byte-Long operations
    override fun Byte.plus(other: Long): Byte = (this + other).toByte()
    override fun Byte.minus(other: Long): Byte = (this - other).toByte()
    override fun Byte.times(other: Long): Byte = (this * other).toByte()
    // endregion
    
    // region Byte-ULong operations
    override fun Byte.plus(other: ULong): Byte = (this + other.toByte()).toByte()
    override fun Byte.minus(other: ULong): Byte = (this - other.toByte()).toByte()
    override fun Byte.times(other: ULong): Byte = (this * other.toByte()).toByte()
    // endregion

    // region Int-Byte operations
    override fun Int.plus(other: Byte): Byte = (this + other).toByte()
    override fun Int.minus(other: Byte): Byte = (this - other).toByte()
    override fun Int.times(other: Byte): Byte = (this * other).toByte()
    // endregion
    
    // region UInt-Byte operations
    override fun UInt.plus(other: Byte): Byte = (this.toByte() + other).toByte()
    override fun UInt.minus(other: Byte): Byte = (this.toByte() - other).toByte()
    override fun UInt.times(other: Byte): Byte = (this.toByte() * other).toByte()
    // endregion

    // region Long-Byte operations
    override fun Long.plus(other: Byte): Byte = (this + other).toByte()
    override fun Long.minus(other: Byte): Byte = (this - other).toByte()
    override fun Long.times(other: Byte): Byte = (this * other).toByte()
    // endregion
    
    // region ULong-Byte operations
    override fun ULong.plus(other: Byte): Byte = (this.toByte() + other).toByte()
    override fun ULong.minus(other: Byte): Byte = (this.toByte() - other).toByte()
    override fun ULong.times(other: Byte): Byte = (this.toByte() * other).toByte()
    // endregion

    // region Byte-Byte operations
    override fun Byte.unaryMinus(): Byte = (-this).toByte()
    override fun Byte.plus(other: Byte): Byte = (this + other).toByte()
    override fun Byte.minus(other: Byte): Byte = (this - other).toByte()
    override fun Byte.times(other: Byte): Byte = (this * other).toByte()
    override fun Byte.divrem(other: Byte): EuclideanDivisionResult<Byte> =
        if (other == 0.toByte()) divisionByZero()
        else EuclideanDivisionResult(quotient = (this / other).toByte(), remainder = (this % other).toByte())
    override fun Byte.div(other: Byte): Byte = if (other == 0.toByte()) divisionByZero() else (this / other).toByte()
    override fun Byte.rem(other: Byte): Byte = if (other == 0.toByte()) divisionByZero() else (this % other).toByte()
    // endregion
}

public fun Reification.Companion.primaryFor(target: Byte.Companion): Reification<Byte> = ByteContext
public fun Order.Companion.primaryFor(target: Byte.Companion): Order<Byte> = ByteContext
public fun Hashing.Companion.primaryFor(target: Byte.Companion): Hashing<Byte> = ByteContext
public fun EuclideanRing.Companion.primaryFor(target: Byte.Companion): EuclideanRing<Byte> = ByteContext
public fun EuclideanSemiring.Companion.primaryFor(target: Byte.Companion): EuclideanSemiring<Byte> = ByteContext
public fun CommutativeRing.Companion.primaryFor(target: Byte.Companion): CommutativeRing<Byte> = ByteContext
public fun Ring.Companion.primaryFor(target: Byte.Companion): Ring<Byte> = ByteContext
public fun CommutativeSemiring.Companion.primaryFor(target: Byte.Companion): CommutativeSemiring<Byte> = ByteContext
public fun Semiring.Companion.primaryFor(target: Byte.Companion): Semiring<Byte> = ByteContext
public fun CommutativeGroup.Companion.primaryFor(target: Byte.Companion): CommutativeGroup<Byte> = ByteContext
public fun Group.Companion.primaryFor(target: Byte.Companion): Group<Byte> = ByteContext
public fun CommutativeMonoid.Companion.primaryFor(target: Byte.Companion): CommutativeMonoid<Byte> = ByteContext
public fun Monoid.Companion.primaryFor(target: Byte.Companion): Monoid<Byte> = ByteContext
public fun CommutativeSemigroup.Companion.primaryFor(target: Byte.Companion): CommutativeSemigroup<Byte> = ByteContext
public fun Semigroup.Companion.primaryFor(target: Byte.Companion): Semigroup<Byte> = ByteContext
public fun Equality.Companion.primaryFor(target: Byte.Companion): Equality<Byte> = ByteContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: Byte.Companion) {
    Reification.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: Byte.Companion) {
    Order.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: Byte.Companion) {
    Hashing.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanRing.Companion.setPrimaryFor(target: Byte.Companion) {
    EuclideanRing.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: Byte.Companion) {
    EuclideanSemiring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setPrimaryFor(target: Byte.Companion) {
    CommutativeRing.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setPrimaryFor(target: Byte.Companion) {
    Ring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: Byte.Companion) {
    CommutativeSemiring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: Byte.Companion) {
    Semiring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setPrimaryFor(target: Byte.Companion) {
    CommutativeGroup.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setPrimaryFor(target: Byte.Companion) {
    Group.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: Byte.Companion) {
    CommutativeMonoid.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: Byte.Companion) {
    Monoid.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: Byte.Companion) {
    CommutativeSemigroup.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: Byte.Companion) {
    Semigroup.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: Byte.Companion) {
    Equality.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object ShortContext: Reification<Short>, EuclideanRing<Short>, Order<Short>, Hashing<Short> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Short
    override fun reifyMaybe(element: Any?): Maybe<Short> = if (element is Short) Some(element) else None
    override fun reifyOrNull(element: Any?): Short? = element as? Short
    override fun reify(element: Any?): Short = element as? Short ?: reificationException()
    // endregion
    
    // region Order
    override fun Short.compareWith(other: Short): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Short get() = 0
    override val one: Short get() = 1
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Short = arg.toShort()
    override fun valueOf(arg: UInt): Short = arg.toShort()
    override fun valueOf(arg: Long): Short = arg.toShort()
    override fun valueOf(arg: ULong): Short = arg.toShort()
    // endregion

    // region Short-Int operations
    override fun Short.plus(other: Int): Short = (this + other).toShort()
    override fun Short.minus(other: Int): Short = (this - other).toShort()
    override fun Short.times(other: Int): Short = (this * other).toShort()
    // endregion
    
    // region Short-UInt operations
    override fun Short.plus(other: UInt): Short = (this + other.toShort()).toShort()
    override fun Short.minus(other: UInt): Short = (this - other.toShort()).toShort()
    override fun Short.times(other: UInt): Short = (this * other.toShort()).toShort()
    // endregion

    // region Short-Long operations
    override fun Short.plus(other: Long): Short = (this + other).toShort()
    override fun Short.minus(other: Long): Short = (this - other).toShort()
    override fun Short.times(other: Long): Short = (this * other).toShort()
    // endregion
    
    // region Short-ULong operations
    override fun Short.plus(other: ULong): Short = (this + other.toShort()).toShort()
    override fun Short.minus(other: ULong): Short = (this - other.toShort()).toShort()
    override fun Short.times(other: ULong): Short = (this * other.toShort()).toShort()
    // endregion

    // region Int-Short operations
    override fun Int.plus(other: Short): Short = (this + other).toShort()
    override fun Int.minus(other: Short): Short = (this - other).toShort()
    override fun Int.times(other: Short): Short = (this * other).toShort()
    // endregion
    
    // region Int-Short operations
    override fun UInt.plus(other: Short): Short = (this.toShort() + other).toShort()
    override fun UInt.minus(other: Short): Short = (this.toShort() - other).toShort()
    override fun UInt.times(other: Short): Short = (this.toShort() * other).toShort()
    // endregion

    // region Long-Short operations
    override fun Long.plus(other: Short): Short = (this + other).toShort()
    override fun Long.minus(other: Short): Short = (this - other).toShort()
    override fun Long.times(other: Short): Short = (this * other).toShort()
    // endregion
    
    // region Long-Short operations
    override fun ULong.plus(other: Short): Short = (this.toShort() + other).toShort()
    override fun ULong.minus(other: Short): Short = (this.toShort() - other).toShort()
    override fun ULong.times(other: Short): Short = (this.toShort() * other).toShort()
    // endregion

    // region Short-Short operations
    override fun Short.unaryMinus(): Short = (-this).toShort()
    override fun Short.plus(other: Short): Short = (this + other).toShort()
    override fun Short.minus(other: Short): Short = (this - other).toShort()
    override fun Short.times(other: Short): Short = (this * other).toShort()
    override fun Short.divrem(other: Short): EuclideanDivisionResult<Short> =
        if (other == 0.toShort()) divisionByZero()
        else EuclideanDivisionResult(quotient = (this / other).toShort(), remainder = (this % other).toShort())
    override fun Short.div(other: Short): Short = if (other == 0.toShort()) divisionByZero() else (this / other).toShort()
    override fun Short.rem(other: Short): Short = if (other == 0.toShort()) divisionByZero() else (this % other).toShort()
    // endregion
}

public fun Reification.Companion.primaryFor(target: Short.Companion): Reification<Short> = ShortContext
public fun Order.Companion.primaryFor(target: Short.Companion): Order<Short> = ShortContext
public fun Hashing.Companion.primaryFor(target: Short.Companion): Hashing<Short> = ShortContext
public fun EuclideanRing.Companion.primaryFor(target: Short.Companion): EuclideanRing<Short> = ShortContext
public fun EuclideanSemiring.Companion.primaryFor(target: Short.Companion): EuclideanSemiring<Short> = ShortContext
public fun CommutativeRing.Companion.primaryFor(target: Short.Companion): CommutativeRing<Short> = ShortContext
public fun Ring.Companion.primaryFor(target: Short.Companion): Ring<Short> = ShortContext
public fun CommutativeSemiring.Companion.primaryFor(target: Short.Companion): CommutativeSemiring<Short> = ShortContext
public fun Semiring.Companion.primaryFor(target: Short.Companion): Semiring<Short> = ShortContext
public fun CommutativeGroup.Companion.primaryFor(target: Short.Companion): CommutativeGroup<Short> = ShortContext
public fun Group.Companion.primaryFor(target: Short.Companion): Group<Short> = ShortContext
public fun CommutativeMonoid.Companion.primaryFor(target: Short.Companion): CommutativeMonoid<Short> = ShortContext
public fun Monoid.Companion.primaryFor(target: Short.Companion): Monoid<Short> = ShortContext
public fun CommutativeSemigroup.Companion.primaryFor(target: Short.Companion): CommutativeSemigroup<Short> = ShortContext
public fun Semigroup.Companion.primaryFor(target: Short.Companion): Semigroup<Short> = ShortContext
public fun Equality.Companion.primaryFor(target: Short.Companion): Equality<Short> = ShortContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: Short.Companion) {
    Reification.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: Short.Companion) {
    Order.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: Short.Companion) {
    Hashing.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanRing.Companion.setPrimaryFor(target: Short.Companion) {
    EuclideanRing.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: Short.Companion) {
    EuclideanSemiring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setPrimaryFor(target: Short.Companion) {
    CommutativeRing.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setPrimaryFor(target: Short.Companion) {
    Ring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: Short.Companion) {
    CommutativeSemiring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: Short.Companion) {
    Semiring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setPrimaryFor(target: Short.Companion) {
    CommutativeGroup.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setPrimaryFor(target: Short.Companion) {
    Group.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: Short.Companion) {
    CommutativeMonoid.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: Short.Companion) {
    Monoid.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: Short.Companion) {
    CommutativeSemigroup.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: Short.Companion) {
    Semigroup.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: Short.Companion) {
    Equality.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object IntContext: Reification<Int>, EuclideanRing<Int>, Order<Int>, Hashing<Int> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Int
    override fun reifyMaybe(element: Any?): Maybe<Int> = if (element is Int) Some(element) else None
    override fun reifyOrNull(element: Any?): Int? = element as? Int
    override fun reify(element: Any?): Int = element as? Int ?: reificationException()
    // endregion
    
    // region Order
    override fun Int.compareWith(other: Int): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Int get() = 0
    override val one: Int get() = 1
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Int = arg
    override fun valueOf(arg: UInt): Int = arg.toInt()
    override fun valueOf(arg: Long): Int = arg.toInt()
    override fun valueOf(arg: ULong): Int = arg.toInt()
    // endregion

    // region Int-Int operations
    override fun Int.unaryMinus(): Int = -this
    override fun Int.plus(other: Int): Int = this + other
    override fun Int.minus(other: Int): Int = this - other
    override fun Int.times(other: Int): Int = this * other
    override fun Int.divrem(other: Int): EuclideanDivisionResult<Int> =
        if (other == 0) divisionByZero()
        else EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Int.div(other: Int): Int = if (other == 0) divisionByZero() else this / other
    override fun Int.rem(other: Int): Int = if (other == 0) divisionByZero() else this % other
    // endregion
    
    // region Int-UInt operations
    override fun Int.plus(other: UInt): Int = this + other.toInt()
    override fun Int.minus(other: UInt): Int = this - other.toInt()
    override fun Int.times(other: UInt): Int = this * other.toInt()
    // endregion

    // region Int-Long operations
    override fun Int.plus(other: Long): Int = this + other.toInt()
    override fun Int.minus(other: Long): Int = this - other.toInt()
    override fun Int.times(other: Long): Int = this * other.toInt()
    // endregion
    
    // region Int-ULong operations
    override fun Int.plus(other: ULong): Int = this + other.toInt()
    override fun Int.minus(other: ULong): Int = this - other.toInt()
    override fun Int.times(other: ULong): Int = this * other.toInt()
    // endregion
    
    // region UInt-Int operations
    override fun UInt.plus(other: Int): Int = this.toInt() + other
    override fun UInt.minus(other: Int): Int = this.toInt() - other
    override fun UInt.times(other: Int): Int = this.toInt() * other
    // endregion
    
    // region Long-Int operations
    override fun Long.plus(other: Int): Int = this.toInt() + other
    override fun Long.minus(other: Int): Int = this.toInt() - other
    override fun Long.times(other: Int): Int = this.toInt() * other
    // endregion

    // region ULong-Int operations
    override fun ULong.plus(other: Int): Int = this.toInt() + other
    override fun ULong.minus(other: Int): Int = this.toInt() - other
    override fun ULong.times(other: Int): Int = this.toInt() * other
    // endregion
}

public fun Reification.Companion.primaryFor(target: Int.Companion): Reification<Int> = IntContext
public fun Order.Companion.primaryFor(target: Int.Companion): Order<Int> = IntContext
public fun Hashing.Companion.primaryFor(target: Int.Companion): Hashing<Int> = IntContext
public fun EuclideanRing.Companion.primaryFor(target: Int.Companion): EuclideanRing<Int> = IntContext
public fun EuclideanSemiring.Companion.primaryFor(target: Int.Companion): EuclideanSemiring<Int> = IntContext
public fun CommutativeRing.Companion.primaryFor(target: Int.Companion): CommutativeRing<Int> = IntContext
public fun Ring.Companion.primaryFor(target: Int.Companion): Ring<Int> = IntContext
public fun CommutativeSemiring.Companion.primaryFor(target: Int.Companion): CommutativeSemiring<Int> = IntContext
public fun Semiring.Companion.primaryFor(target: Int.Companion): Semiring<Int> = IntContext
public fun CommutativeGroup.Companion.primaryFor(target: Int.Companion): CommutativeGroup<Int> = IntContext
public fun Group.Companion.primaryFor(target: Int.Companion): Group<Int> = IntContext
public fun CommutativeMonoid.Companion.primaryFor(target: Int.Companion): CommutativeMonoid<Int> = IntContext
public fun Monoid.Companion.primaryFor(target: Int.Companion): Monoid<Int> = IntContext
public fun CommutativeSemigroup.Companion.primaryFor(target: Int.Companion): CommutativeSemigroup<Int> = IntContext
public fun Semigroup.Companion.primaryFor(target: Int.Companion): Semigroup<Int> = IntContext
public fun Equality.Companion.primaryFor(target: Int.Companion): Equality<Int> = IntContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: Int.Companion) {
    Reification.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: Int.Companion) {
    Order.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: Int.Companion) {
    Hashing.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanRing.Companion.setPrimaryFor(target: Int.Companion) {
    EuclideanRing.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: Int.Companion) {
    EuclideanSemiring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setPrimaryFor(target: Int.Companion) {
    CommutativeRing.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setPrimaryFor(target: Int.Companion) {
    Ring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: Int.Companion) {
    CommutativeSemiring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: Int.Companion) {
    Semiring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setPrimaryFor(target: Int.Companion) {
    CommutativeGroup.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setPrimaryFor(target: Int.Companion) {
    Group.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: Int.Companion) {
    CommutativeMonoid.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: Int.Companion) {
    Monoid.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: Int.Companion) {
    CommutativeSemigroup.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: Int.Companion) {
    Semigroup.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: Int.Companion) {
    Equality.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object LongContext: Reification<Long>, EuclideanRing<Long>, Order<Long>, Hashing<Long> {
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
    override fun valueOf(arg: ULong): Long = arg.toLong()
    // endregion

    // region Long-Long operations
    override fun Long.unaryMinus(): Long = -this
    override fun Long.plus(other: Long): Long = this + other
    override fun Long.minus(other: Long): Long = this - other
    override fun Long.times(other: Long): Long = this * other
    override fun Long.divrem(other: Long): EuclideanDivisionResult<Long> =
        if (other == 0L) divisionByZero()
        else EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Long.div(other: Long): Long = if (other == 0L) divisionByZero() else this / other
    override fun Long.rem(other: Long): Long = if (other == 0L) divisionByZero() else this % other
    // endregion
    
    // region Long-Int operations
    override fun Long.plus(other: Int): Long = this + other
    override fun Long.minus(other: Int): Long = this - other
    override fun Long.times(other: Int): Long = this * other
    // endregion
    
    // region Long-UInt operations
    override fun Long.plus(other: UInt): Long = this + other.toLong()
    override fun Long.minus(other: UInt): Long = this - other.toLong()
    override fun Long.times(other: UInt): Long = this * other.toLong()
    // endregion

    // region Long-ULong operations
    override fun Long.plus(other: ULong): Long = this + other.toLong()
    override fun Long.minus(other: ULong): Long = this - other.toLong()
    override fun Long.times(other: ULong): Long = this * other.toLong()
    // endregion
    
    // region Int-Long operations
    override fun Int.plus(other: Long): Long = this + other
    override fun Int.minus(other: Long): Long = this - other
    override fun Int.times(other: Long): Long = this * other
    // endregion

    // region UInt-Long operations
    override fun UInt.plus(other: Long): Long = this.toLong() + other
    override fun UInt.minus(other: Long): Long = this.toLong() - other
    override fun UInt.times(other: Long): Long = this.toLong() * other
    // endregion
    
    // region ULong-Long operations
    override fun ULong.plus(other: Long): Long = this.toLong() + other
    override fun ULong.minus(other: Long): Long = this.toLong() - other
    override fun ULong.times(other: Long): Long = this.toLong() * other
    // endregion
}

public fun Reification.Companion.primaryFor(target: Long.Companion): Reification<Long> = LongContext
public fun Order.Companion.primaryFor(target: Long.Companion): Order<Long> = LongContext
public fun Hashing.Companion.primaryFor(target: Long.Companion): Hashing<Long> = LongContext
public fun EuclideanRing.Companion.primaryFor(target: Long.Companion): EuclideanRing<Long> = LongContext
public fun EuclideanSemiring.Companion.primaryFor(target: Long.Companion): EuclideanSemiring<Long> = LongContext
public fun CommutativeRing.Companion.primaryFor(target: Long.Companion): CommutativeRing<Long> = LongContext
public fun Ring.Companion.primaryFor(target: Long.Companion): Ring<Long> = LongContext
public fun CommutativeSemiring.Companion.primaryFor(target: Long.Companion): CommutativeSemiring<Long> = LongContext
public fun Semiring.Companion.primaryFor(target: Long.Companion): Semiring<Long> = LongContext
public fun CommutativeGroup.Companion.primaryFor(target: Long.Companion): CommutativeGroup<Long> = LongContext
public fun Group.Companion.primaryFor(target: Long.Companion): Group<Long> = LongContext
public fun CommutativeMonoid.Companion.primaryFor(target: Long.Companion): CommutativeMonoid<Long> = LongContext
public fun Monoid.Companion.primaryFor(target: Long.Companion): Monoid<Long> = LongContext
public fun CommutativeSemigroup.Companion.primaryFor(target: Long.Companion): CommutativeSemigroup<Long> = LongContext
public fun Semigroup.Companion.primaryFor(target: Long.Companion): Semigroup<Long> = LongContext
public fun Equality.Companion.primaryFor(target: Long.Companion): Equality<Long> = LongContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: Long.Companion) {
    Reification.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: Long.Companion) {
    Order.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: Long.Companion) {
    Hashing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanRing.Companion.setPrimaryFor(target: Long.Companion) {
    EuclideanRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: Long.Companion) {
    EuclideanSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setPrimaryFor(target: Long.Companion) {
    CommutativeRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setPrimaryFor(target: Long.Companion) {
    Ring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: Long.Companion) {
    CommutativeSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: Long.Companion) {
    Semiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setPrimaryFor(target: Long.Companion) {
    CommutativeGroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setPrimaryFor(target: Long.Companion) {
    Group.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: Long.Companion) {
    CommutativeMonoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: Long.Companion) {
    Monoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: Long.Companion) {
    CommutativeSemigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: Long.Companion) {
    Semigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: Long.Companion) {
    Equality.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object UByteContext: Reification<UByte>, EuclideanSemiring<UByte>, ExtendedSemiring<UByte>, Order<UByte>, Hashing<UByte> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UByte
    override fun reifyMaybe(element: Any?): Maybe<UByte> = if (element is UByte) Some(element) else None
    override fun reifyOrNull(element: Any?): UByte? = element as? UByte
    override fun reify(element: Any?): UByte = element as? UByte ?: reificationException()
    // endregion
    
    // region Order
    override fun UByte.compareWith(other: UByte): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: UByte get() = 0.toUByte()
    override val one: UByte get() = 1.toUByte()
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): UByte = arg.toUByte()
    override fun valueOf(arg: ULong): UByte = arg.toUByte()
    // endregion
    
    // region UByte-UInt operations
    override fun UByte.plus(other: UInt): UByte = (this + other).toUByte()
    override fun UByte.times(other: UInt): UByte = (this * other).toUByte()
    // endregion
    
    // region UByte-ULong operations
    override fun UByte.plus(other: ULong): UByte = (this + other).toUByte()
    override fun UByte.times(other: ULong): UByte = (this * other).toUByte()
    // endregion
    
    // region UInt-UByte operations
    override fun UInt.plus(other: UByte): UByte = (this + other).toUByte()
    override fun UInt.times(other: UByte): UByte = (this * other).toUByte()
    // endregion
    
    // region ULong-UByte operations
    override fun ULong.plus(other: UByte): UByte = (this + other).toUByte()
    override fun ULong.times(other: UByte): UByte = (this * other).toUByte()
    // endregion
    
    // region UByte-UByte operations
    override fun UByte.plus(other: UByte): UByte = (this + other).toUByte()
    override fun UByte.minus(other: UByte): UByte = (this - other).toUByte()
    override fun UByte.times(other: UByte): UByte = (this * other).toUByte()
    override fun UByte.divrem(other: UByte): EuclideanDivisionResult<UByte> =
        if (other == 0.toUByte()) divisionByZero()
        else EuclideanDivisionResult(
            quotient = (this / other).toUByte(),
            remainder = (this % other).toUByte(),
        )
    override fun UByte.div(other: UByte): UByte = if (other == 0.toUByte()) divisionByZero() else (this / other).toUByte()
    override fun UByte.rem(other: UByte): UByte = if (other == 0.toUByte()) divisionByZero() else (this % other).toUByte()
    override fun power(base: UByte, exponent: UInt): UByte = base squaringPower exponent
    override fun power(base: UByte, exponent: ULong): UByte = base squaringPower exponent
    override infix fun UByte.pow(exponent: UInt): UByte = power(this, exponent)
    override infix fun UByte.pow(exponent: ULong): UByte = power(this, exponent)
    // endregion
}

public fun Reification.Companion.primaryFor(target: UByte.Companion): Reification<UByte> = UByteContext
public fun Order.Companion.primaryFor(target: UByte.Companion): Order<UByte> = UByteContext
public fun Hashing.Companion.primaryFor(target: UByte.Companion): Hashing<UByte> = UByteContext
public fun EuclideanSemiring.Companion.primaryFor(target: UByte.Companion): EuclideanSemiring<UByte> = UByteContext
public fun CommutativeSemiring.Companion.primaryFor(target: UByte.Companion): CommutativeSemiring<UByte> = UByteContext
public fun Semiring.Companion.primaryFor(target: UByte.Companion): Semiring<UByte> = UByteContext
public fun CommutativeMonoid.Companion.primaryFor(target: UByte.Companion): CommutativeMonoid<UByte> = UByteContext
public fun Monoid.Companion.primaryFor(target: UByte.Companion): Monoid<UByte> = UByteContext
public fun CommutativeSemigroup.Companion.primaryFor(target: UByte.Companion): CommutativeSemigroup<UByte> = UByteContext
public fun Semigroup.Companion.primaryFor(target: UByte.Companion): Semigroup<UByte> = UByteContext
public fun Equality.Companion.primaryFor(target: UByte.Companion): Equality<UByte> = UByteContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: UByte.Companion) {
    Reification.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: UByte.Companion) {
    Order.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: UByte.Companion) {
    Hashing.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: UByte.Companion) {
    EuclideanSemiring.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: UByte.Companion) {
    CommutativeSemiring.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: UByte.Companion) {
    Semiring.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: UByte.Companion) {
    CommutativeMonoid.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: UByte.Companion) {
    Monoid.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: UByte.Companion) {
    CommutativeSemigroup.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: UByte.Companion) {
    Semigroup.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: UByte.Companion) {
    Equality.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object UShortContext: Reification<UShort>, EuclideanSemiring<UShort>, ExtendedSemiring<UShort>, Order<UShort>, Hashing<UShort> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UShort
    override fun reifyMaybe(element: Any?): Maybe<UShort> = if (element is UShort) Some(element) else None
    override fun reifyOrNull(element: Any?): UShort? = element as? UShort
    override fun reify(element: Any?): UShort = element as? UShort ?: reificationException()
    // endregion
    
    // region Order
    override fun UShort.compareWith(other: UShort): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: UShort get() = 0.toUShort()
    override val one: UShort get() = 1.toUShort()
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): UShort = arg.toUShort()
    override fun valueOf(arg: ULong): UShort = arg.toUShort()
    // endregion
    
    // region UShort-UInt operations
    override fun UShort.plus(other: UInt): UShort = (this + other).toUShort()
    override fun UShort.times(other: UInt): UShort = (this * other).toUShort()
    // endregion
    
    // region UShort-ULong operations
    override fun UShort.plus(other: ULong): UShort = (this + other).toUShort()
    override fun UShort.times(other: ULong): UShort = (this * other).toUShort()
    // endregion
    
    // region UInt-UShort operations
    override fun UInt.plus(other: UShort): UShort = (this + other).toUShort()
    override fun UInt.times(other: UShort): UShort = (this * other).toUShort()
    // endregion
    
    // region ULong-UShort operations
    override fun ULong.plus(other: UShort): UShort = (this + other).toUShort()
    override fun ULong.times(other: UShort): UShort = (this * other).toUShort()
    // endregion
    
    // region UShort-UShort operations
    override fun UShort.plus(other: UShort): UShort = (this + other).toUShort()
    override fun UShort.minus(other: UShort): UShort = (this - other).toUShort()
    override fun UShort.times(other: UShort): UShort = (this * other).toUShort()
    override fun UShort.divrem(other: UShort): EuclideanDivisionResult<UShort> =
        if (other == 0.toUShort()) divisionByZero()
        else EuclideanDivisionResult(
            quotient = (this / other).toUShort(),
            remainder = (this % other).toUShort(),
        )
    override fun UShort.div(other: UShort): UShort = if (other == 0.toUShort()) divisionByZero() else (this / other).toUShort()
    override fun UShort.rem(other: UShort): UShort = if (other == 0.toUShort()) divisionByZero() else (this % other).toUShort()
    override fun power(base: UShort, exponent: UInt): UShort = base squaringPower exponent
    override fun power(base: UShort, exponent: ULong): UShort = base squaringPower exponent
    override infix fun UShort.pow(exponent: UInt): UShort = power(this, exponent)
    override infix fun UShort.pow(exponent: ULong): UShort = power(this, exponent)
    // endregion
}

public fun Reification.Companion.primaryFor(target: UShort.Companion): Reification<UShort> = UShortContext
public fun Order.Companion.primaryFor(target: UShort.Companion): Order<UShort> = UShortContext
public fun Hashing.Companion.primaryFor(target: UShort.Companion): Hashing<UShort> = UShortContext
public fun EuclideanSemiring.Companion.primaryFor(target: UShort.Companion): EuclideanSemiring<UShort> = UShortContext
public fun CommutativeSemiring.Companion.primaryFor(target: UShort.Companion): CommutativeSemiring<UShort> = UShortContext
public fun Semiring.Companion.primaryFor(target: UShort.Companion): Semiring<UShort> = UShortContext
public fun CommutativeMonoid.Companion.primaryFor(target: UShort.Companion): CommutativeMonoid<UShort> = UShortContext
public fun Monoid.Companion.primaryFor(target: UShort.Companion): Monoid<UShort> = UShortContext
public fun CommutativeSemigroup.Companion.primaryFor(target: UShort.Companion): CommutativeSemigroup<UShort> = UShortContext
public fun Semigroup.Companion.primaryFor(target: UShort.Companion): Semigroup<UShort> = UShortContext
public fun Equality.Companion.primaryFor(target: UShort.Companion): Equality<UShort> = UShortContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: UShort.Companion) {
    Reification.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: UShort.Companion) {
    Order.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: UShort.Companion) {
    Hashing.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: UShort.Companion) {
    EuclideanSemiring.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: UShort.Companion) {
    CommutativeSemiring.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: UShort.Companion) {
    Semiring.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: UShort.Companion) {
    CommutativeMonoid.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: UShort.Companion) {
    Monoid.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: UShort.Companion) {
    CommutativeSemigroup.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: UShort.Companion) {
    Semigroup.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: UShort.Companion) {
    Equality.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object UIntContext: Reification<UInt>, EuclideanSemiring<UInt>, ExtendedSemiring<UInt>, Order<UInt>, Hashing<UInt> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UInt
    override fun reifyMaybe(element: Any?): Maybe<UInt> = if (element is UInt) Some(element) else None
    override fun reifyOrNull(element: Any?): UInt? = element as? UInt
    override fun reify(element: Any?): UInt = element as? UInt ?: reificationException()
    // endregion
    
    // region Order
    override fun UInt.compareWith(other: UInt): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: UInt get() = 0u
    override val one: UInt get() = 1u
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): UInt = arg
    override fun valueOf(arg: ULong): UInt = arg.toUInt()
    // endregion
    
    // region UInt-ULong operations
    override fun UInt.plus(other: ULong): UInt = (this + other).toUInt()
    override fun UInt.times(other: ULong): UInt = (this * other).toUInt()
    // endregion
    
    // region ULong-UInt operations
    override fun ULong.plus(other: UInt): UInt = (this + other).toUInt()
    override fun ULong.times(other: UInt): UInt = (this * other).toUInt()
    // endregion
    
    // region UInt-UInt operations
    override fun UInt.plus(other: UInt): UInt = this + other
    override fun UInt.minus(other: UInt): UInt = this - other
    override fun UInt.times(other: UInt): UInt = this * other
    override fun UInt.divrem(other: UInt): EuclideanDivisionResult<UInt> =
        if (other == 0u) divisionByZero()
        else EuclideanDivisionResult(
            quotient = this / other,
            remainder = this % other,
        )
    override fun UInt.div(other: UInt): UInt = if (other == 0u) divisionByZero() else this / other
    override fun UInt.rem(other: UInt): UInt = if (other == 0u) divisionByZero() else this % other
    override fun power(base: UInt, exponent: UInt): UInt = base squaringPower exponent
    override fun power(base: UInt, exponent: ULong): UInt = base squaringPower exponent
    override infix fun UInt.pow(exponent: UInt): UInt = power(this, exponent)
    override infix fun UInt.pow(exponent: ULong): UInt = power(this, exponent)
    // endregion
}

public fun Reification.Companion.primaryFor(target: UInt.Companion): Reification<UInt> = UIntContext
public fun Order.Companion.primaryFor(target: UInt.Companion): Order<UInt> = UIntContext
public fun Hashing.Companion.primaryFor(target: UInt.Companion): Hashing<UInt> = UIntContext
public fun EuclideanSemiring.Companion.primaryFor(target: UInt.Companion): EuclideanSemiring<UInt> = UIntContext
public fun CommutativeSemiring.Companion.primaryFor(target: UInt.Companion): CommutativeSemiring<UInt> = UIntContext
public fun Semiring.Companion.primaryFor(target: UInt.Companion): Semiring<UInt> = UIntContext
public fun CommutativeMonoid.Companion.primaryFor(target: UInt.Companion): CommutativeMonoid<UInt> = UIntContext
public fun Monoid.Companion.primaryFor(target: UInt.Companion): Monoid<UInt> = UIntContext
public fun CommutativeSemigroup.Companion.primaryFor(target: UInt.Companion): CommutativeSemigroup<UInt> = UIntContext
public fun Semigroup.Companion.primaryFor(target: UInt.Companion): Semigroup<UInt> = UIntContext
public fun Equality.Companion.primaryFor(target: UInt.Companion): Equality<UInt> = UIntContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: UInt.Companion) {
    Reification.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: UInt.Companion) {
    Order.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: UInt.Companion) {
    Hashing.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: UInt.Companion) {
    EuclideanSemiring.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: UInt.Companion) {
    CommutativeSemiring.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: UInt.Companion) {
    Semiring.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: UInt.Companion) {
    CommutativeMonoid.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: UInt.Companion) {
    Monoid.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: UInt.Companion) {
    CommutativeSemigroup.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: UInt.Companion) {
    Semigroup.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: UInt.Companion) {
    Equality.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object ULongContext: Reification<ULong>, EuclideanSemiring<ULong>, ExtendedSemiring<ULong>, Order<ULong>, Hashing<ULong> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is ULong
    override fun reifyMaybe(element: Any?): Maybe<ULong> = if (element is ULong) Some(element) else None
    override fun reifyOrNull(element: Any?): ULong? = element as? ULong
    override fun reify(element: Any?): ULong = element as? ULong ?: reificationException()
    // endregion
    
    // region Order
    override fun ULong.compareWith(other: ULong): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
    
    // region Constants
    override val zero: ULong get() = 0uL
    override val one: ULong get() = 1uL
    // endregion
    
    // region Integers conversion
    override fun valueOf(arg: UInt): ULong = arg.toULong()
    override fun valueOf(arg: ULong): ULong = arg
    // endregion
    
    // region ULong-UInt operations
    override fun ULong.plus(other: UInt): ULong = this + other
    override fun ULong.times(other: UInt): ULong = this * other
    // endregion
    
    // region UInt-ULong operations
    override fun UInt.plus(other: ULong): ULong = this + other
    override fun UInt.times(other: ULong): ULong = this * other
    // endregion
    
    // region ULong-ULong operations
    override fun ULong.plus(other: ULong): ULong = this + other
    override fun ULong.minus(other: ULong): ULong = this - other
    override fun ULong.times(other: ULong): ULong = this * other
    override fun ULong.divrem(other: ULong): EuclideanDivisionResult<ULong> =
        if (other == 0uL) divisionByZero()
        else EuclideanDivisionResult(
            quotient = this / other,
            remainder = this % other,
        )
    override fun ULong.div(other: ULong): ULong = if (other == 0uL) divisionByZero() else this / other
    override fun ULong.rem(other: ULong): ULong = if (other == 0uL) divisionByZero() else this % other
    override fun power(base: ULong, exponent: UInt): ULong = base squaringPower exponent
    override fun power(base: ULong, exponent: ULong): ULong = base squaringPower exponent
    override infix fun ULong.pow(exponent: UInt): ULong = power(this, exponent)
    override infix fun ULong.pow(exponent: ULong): ULong = power(this, exponent)
    // endregion
}

public fun Reification.Companion.primaryFor(target: ULong.Companion): Reification<ULong> = ULongContext
public fun Order.Companion.primaryFor(target: ULong.Companion): Order<ULong> = ULongContext
public fun Hashing.Companion.primaryFor(target: ULong.Companion): Hashing<ULong> = ULongContext
public fun EuclideanSemiring.Companion.primaryFor(target: ULong.Companion): EuclideanSemiring<ULong> = ULongContext
public fun CommutativeSemiring.Companion.primaryFor(target: ULong.Companion): CommutativeSemiring<ULong> = ULongContext
public fun Semiring.Companion.primaryFor(target: ULong.Companion): Semiring<ULong> = ULongContext
public fun CommutativeMonoid.Companion.primaryFor(target: ULong.Companion): CommutativeMonoid<ULong> = ULongContext
public fun Monoid.Companion.primaryFor(target: ULong.Companion): Monoid<ULong> = ULongContext
public fun CommutativeSemigroup.Companion.primaryFor(target: ULong.Companion): CommutativeSemigroup<ULong> = ULongContext
public fun Semigroup.Companion.primaryFor(target: ULong.Companion): Semigroup<ULong> = ULongContext
public fun Equality.Companion.primaryFor(target: ULong.Companion): Equality<ULong> = ULongContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: ULong.Companion) {
    Reification.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: ULong.Companion) {
    Order.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: ULong.Companion) {
    Hashing.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun EuclideanSemiring.Companion.setPrimaryFor(target: ULong.Companion) {
    EuclideanSemiring.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: ULong.Companion) {
    CommutativeSemiring.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: ULong.Companion) {
    Semiring.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: ULong.Companion) {
    CommutativeMonoid.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: ULong.Companion) {
    Monoid.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: ULong.Companion) {
    CommutativeSemigroup.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: ULong.Companion) {
    Semigroup.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Equality.Companion.setPrimaryFor(target: ULong.Companion) {
    Equality.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object DoubleContext: Reification<Double>, Field<Double>, Order<Double>, Hashing<Double> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Double
    override fun reifyMaybe(element: Any?): Maybe<Double> = if (element is Double) Some(element) else None
    override fun reifyOrNull(element: Any?): Double? = element as? Double
    override fun reify(element: Any?): Double = element as? Double ?: reificationException()
    // endregion
    
    // region Order
    override fun Double.compareWith(other: Double): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Double get() = 0.0
    override val one: Double get() = 1.0
    // endregion
    
    // region Equality
    override fun Double.equalsTo(other: Double): Boolean = this == other
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Double = arg.toDouble()
    override fun valueOf(arg: UInt): Double = arg.toDouble()
    override fun valueOf(arg: Long): Double = arg.toDouble()
    override fun valueOf(arg: ULong): Double = arg.toDouble()
    // endregion

    // region Double-Int operations
    override fun Double.plus(other: Int): Double = this + other
    override fun Double.minus(other: Int): Double = this - other
    override fun Double.times(other: Int): Double = this * other
    override fun Double.div(other: Int): Double = this / other
    // endregion
    
    // region Double-UInt operations
    override fun Double.plus(other: UInt): Double = this + other.toDouble()
    override fun Double.minus(other: UInt): Double = this - other.toDouble()
    override fun Double.times(other: UInt): Double = this * other.toDouble()
    override fun Double.div(other: UInt): Double = this / other.toDouble()
    // endregion

    // region Double-Long operations
    override fun Double.plus(other: Long): Double = this + other
    override fun Double.minus(other: Long): Double = this - other
    override fun Double.times(other: Long): Double = this * other
    override fun Double.div(other: Long): Double = this / other
    // endregion
    
    // region Double-ULong operations
    override fun Double.plus(other: ULong): Double = this + other.toDouble()
    override fun Double.minus(other: ULong): Double = this - other.toDouble()
    override fun Double.times(other: ULong): Double = this * other.toDouble()
    override fun Double.div(other: ULong): Double = this / other.toDouble()
    // endregion

    // region Int-Double operations
    override fun Int.plus(other: Double): Double = this + other
    override fun Int.minus(other: Double): Double = this - other
    override fun Int.times(other: Double): Double = this * other
    override fun Int.div(other: Double): Double = this / other
    // endregion
    
    // region UInt-Double operations
    override fun UInt.plus(other: Double): Double = this.toDouble() + other
    override fun UInt.minus(other: Double): Double = this.toDouble() - other
    override fun UInt.times(other: Double): Double = this.toDouble() * other
    override fun UInt.div(other: Double): Double = this.toDouble() / other
    // endregion

    // region Long-Double operations
    override fun Long.plus(other: Double): Double = this + other
    override fun Long.minus(other: Double): Double = this - other
    override fun Long.times(other: Double): Double = this * other
    override fun Long.div(other: Double): Double = this / other
    // endregion
    
    // region ULong-Double operations
    override fun ULong.plus(other: Double): Double = this.toDouble() + other
    override fun ULong.minus(other: Double): Double = this.toDouble() - other
    override fun ULong.times(other: Double): Double = this.toDouble() * other
    override fun ULong.div(other: Double): Double = this.toDouble() / other
    // endregion

    // region Double-Double operations
    override fun Double.unaryMinus(): Double = -this
    override fun Double.plus(other: Double): Double = this + other
    override fun Double.minus(other: Double): Double = this - other
    override fun Double.times(other: Double): Double = this * other
    override fun Double.div(other: Double): Double = if (other == 0.0) divisionByZero() else this / other
    override fun power(base: Double, exponent: UInt): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: ULong): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: Int): Double = base.kpow(exponent)
    override fun power(base: Double, exponent: Long): Double =
        if (exponent >= 0) base.kpow(exponent.toDouble())
        else 1/base.kpow(-exponent.toDouble())
    // endregion
}

public fun Reification.Companion.primaryFor(target: Double.Companion): Reification<Double> = DoubleContext
public fun Order.Companion.primaryFor(target: Double.Companion): Order<Double> = DoubleContext
public fun Hashing.Companion.primaryFor(target: Double.Companion): Hashing<Double> = DoubleContext
public fun Field.Companion.primaryFor(target: Double.Companion): Field<Double> = DoubleContext
public fun CommutativeRing.Companion.primaryFor(target: Double.Companion): CommutativeRing<Double> = DoubleContext
public fun Ring.Companion.primaryFor(target: Double.Companion): Ring<Double> = DoubleContext
public fun CommutativeSemiring.Companion.primaryFor(target: Double.Companion): CommutativeSemiring<Double> = DoubleContext
public fun Semiring.Companion.primaryFor(target: Double.Companion): Semiring<Double> = DoubleContext
public fun CommutativeGroup.Companion.primaryFor(target: Double.Companion): CommutativeGroup<Double> = DoubleContext
public fun Group.Companion.primaryFor(target: Double.Companion): Group<Double> = DoubleContext
public fun CommutativeMonoid.Companion.primaryFor(target: Double.Companion): CommutativeMonoid<Double> = DoubleContext
public fun Monoid.Companion.primaryFor(target: Double.Companion): Monoid<Double> = DoubleContext
public fun CommutativeSemigroup.Companion.primaryFor(target: Double.Companion): CommutativeSemigroup<Double> = DoubleContext
public fun Semigroup.Companion.primaryFor(target: Double.Companion): Semigroup<Double> = DoubleContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: Double.Companion) {
    Reification.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: Double.Companion) {
    Order.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: Double.Companion) {
    Hashing.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Field.Companion.setPrimaryFor(target: Double.Companion) {
    Field.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setPrimaryFor(target: Double.Companion) {
    CommutativeRing.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setPrimaryFor(target: Double.Companion) {
    Ring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: Double.Companion) {
    CommutativeSemiring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: Double.Companion) {
    Semiring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setPrimaryFor(target: Double.Companion) {
    CommutativeGroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setPrimaryFor(target: Double.Companion) {
    Group.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: Double.Companion) {
    CommutativeMonoid.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: Double.Companion) {
    Monoid.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: Double.Companion) {
    CommutativeSemigroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: Double.Companion) {
    Semigroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private data object FloatContext: Reification<Float>, Field<Float>, Order<Float>, Hashing<Float> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is Float
    override fun reifyMaybe(element: Any?): Maybe<Float> = if (element is Float) Some(element) else None
    override fun reifyOrNull(element: Any?): Float? = element as? Float
    override fun reify(element: Any?): Float = element as? Float ?: reificationException()
    // endregion
    
    // region Order
    override fun Float.compareWith(other: Float): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion

    // region Constants
    override val zero: Float get() = 0f
    override val one: Float get() = 1f
    // endregion
    
    // region Equality
    override fun Float.equalsTo(other: Float): Boolean = this == other
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Float = arg.toFloat()
    override fun valueOf(arg: UInt): Float = arg.toFloat()
    override fun valueOf(arg: Long): Float = arg.toFloat()
    override fun valueOf(arg: ULong): Float = arg.toFloat()
    // endregion

    // region Float-Int operations
    override fun Float.plus(other: Int): Float = this + other
    override fun Float.minus(other: Int): Float = this - other
    override fun Float.times(other: Int): Float = this * other
    override fun Float.div(other: Int): Float = this / other
    // endregion
    
    // region Float-UInt operations
    override fun Float.plus(other: UInt): Float = this + other.toFloat()
    override fun Float.minus(other: UInt): Float = this - other.toFloat()
    override fun Float.times(other: UInt): Float = this * other.toFloat()
    override fun Float.div(other: UInt): Float = this / other.toFloat()
    // endregion

    // region Float-Long operations
    override fun Float.plus(other: Long): Float = this + other
    override fun Float.minus(other: Long): Float = this - other
    override fun Float.times(other: Long): Float = this * other
    override fun Float.div(other: Long): Float = this / other
    // endregion
    
    // region Float-ULong operations
    override fun Float.plus(other: ULong): Float = this + other.toFloat()
    override fun Float.minus(other: ULong): Float = this - other.toFloat()
    override fun Float.times(other: ULong): Float = this * other.toFloat()
    override fun Float.div(other: ULong): Float = this / other.toFloat()
    // endregion

    // region Int-Float operations
    override fun Int.plus(other: Float): Float = this + other
    override fun Int.minus(other: Float): Float = this - other
    override fun Int.times(other: Float): Float = this * other
    override fun Int.div(other: Float): Float = this / other
    // endregion
    
    // region UInt-Float operations
    override fun UInt.plus(other: Float): Float = this.toFloat() + other
    override fun UInt.minus(other: Float): Float = this.toFloat() - other
    override fun UInt.times(other: Float): Float = this.toFloat() * other
    override fun UInt.div(other: Float): Float = this.toFloat() / other
    // endregion

    // region Long-Float operations
    override fun Long.plus(other: Float): Float = this + other
    override fun Long.minus(other: Float): Float = this - other
    override fun Long.times(other: Float): Float = this * other
    override fun Long.div(other: Float): Float = this / other
    // endregion
    
    // region ULong-Float operations
    override fun ULong.plus(other: Float): Float = this.toFloat() + other
    override fun ULong.minus(other: Float): Float = this.toFloat() - other
    override fun ULong.times(other: Float): Float = this.toFloat() * other
    override fun ULong.div(other: Float): Float = this.toFloat() / other
    // endregion

    // region Float-Float operations
    override fun Float.unaryMinus(): Float = -this
    override fun Float.plus(other: Float): Float = this + other
    override fun Float.minus(other: Float): Float = this - other
    override fun Float.times(other: Float): Float = this * other
    override fun Float.div(other: Float): Float = if (other == 0.0f) divisionByZero() else this / other
    override fun power(base: Float, exponent: UInt): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: ULong): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: Int): Float = base.kpow(exponent)
    override fun power(base: Float, exponent: Long): Float =
        if (exponent >= 0) base.kpow(exponent.toFloat())
        else 1 / base.kpow(-exponent.toFloat())
    // endregion
}

public fun Reification.Companion.primaryFor(target: Float.Companion): Reification<Float> = FloatContext
public fun Order.Companion.primaryFor(target: Float.Companion): Order<Float> = FloatContext
public fun Hashing.Companion.primaryFor(target: Float.Companion): Hashing<Float> = FloatContext
public fun Field.Companion.primaryFor(target: Float.Companion): Field<Float> = FloatContext
public fun CommutativeRing.Companion.primaryFor(target: Float.Companion): CommutativeRing<Float> = FloatContext
public fun Ring.Companion.primaryFor(target: Float.Companion): Ring<Float> = FloatContext
public fun CommutativeSemiring.Companion.primaryFor(target: Float.Companion): CommutativeSemiring<Float> = FloatContext
public fun Semiring.Companion.primaryFor(target: Float.Companion): Semiring<Float> = FloatContext
public fun CommutativeGroup.Companion.primaryFor(target: Float.Companion): CommutativeGroup<Float> = FloatContext
public fun Group.Companion.primaryFor(target: Float.Companion): Group<Float> = FloatContext
public fun CommutativeMonoid.Companion.primaryFor(target: Float.Companion): CommutativeMonoid<Float> = FloatContext
public fun Monoid.Companion.primaryFor(target: Float.Companion): Monoid<Float> = FloatContext
public fun CommutativeSemigroup.Companion.primaryFor(target: Float.Companion): CommutativeSemigroup<Float> = FloatContext
public fun Semigroup.Companion.primaryFor(target: Float.Companion): Semigroup<Float> = FloatContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Reification.Companion.setPrimaryFor(target: Float.Companion) {
    Reification.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Order.Companion.setPrimaryFor(target: Float.Companion) {
    Order.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Hashing.Companion.setPrimaryFor(target: Float.Companion) {
    Hashing.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Field.Companion.setPrimaryFor(target: Float.Companion) {
    Field.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeRing.Companion.setPrimaryFor(target: Float.Companion) {
    CommutativeRing.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Ring.Companion.setPrimaryFor(target: Float.Companion) {
    Ring.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemiring.Companion.setPrimaryFor(target: Float.Companion) {
    CommutativeSemiring.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semiring.Companion.setPrimaryFor(target: Float.Companion) {
    Semiring.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeGroup.Companion.setPrimaryFor(target: Float.Companion) {
    CommutativeGroup.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Group.Companion.setPrimaryFor(target: Float.Companion) {
    Group.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeMonoid.Companion.setPrimaryFor(target: Float.Companion) {
    CommutativeMonoid.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Monoid.Companion.setPrimaryFor(target: Float.Companion) {
    Monoid.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun CommutativeSemigroup.Companion.setPrimaryFor(target: Float.Companion) {
    CommutativeSemigroup.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Semigroup.Companion.setPrimaryFor(target: Float.Companion) {
    Semigroup.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}