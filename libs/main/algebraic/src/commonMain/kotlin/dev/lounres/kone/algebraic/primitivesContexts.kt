/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

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

public fun Byte.Companion.reification(): Reification<Byte> = ByteContext
public fun Byte.Companion.order(): Order<Byte> = ByteContext
public fun Byte.Companion.hashing(): Hashing<Byte> = ByteContext
public fun Byte.Companion.euclideanRing(): EuclideanRing<Byte> = ByteContext
public fun Byte.Companion.euclideanSemiring(): EuclideanSemiring<Byte> = ByteContext
public fun Byte.Companion.commutativeRing(): CommutativeRing<Byte> = ByteContext
public fun Byte.Companion.ring(): Ring<Byte> = ByteContext
public fun Byte.Companion.commutativeSemiring(): CommutativeSemiring<Byte> = ByteContext
public fun Byte.Companion.semiring(): Semiring<Byte> = ByteContext
public fun Byte.Companion.commutativeGroup(): CommutativeGroup<Byte> = ByteContext
public fun Byte.Companion.group(): Group<Byte> = ByteContext
public fun Byte.Companion.commutativeMonoid(): CommutativeMonoid<Byte> = ByteContext
public fun Byte.Companion.monoid(): Monoid<Byte> = ByteContext
public fun Byte.Companion.commutativeSemigroup(): CommutativeSemigroup<Byte> = ByteContext
public fun Byte.Companion.semigroup(): Semigroup<Byte> = ByteContext
public fun Byte.Companion.equality(): Equality<Byte> = ByteContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setReification() {
    Reification.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setOrder() {
    Order.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setHashing() {
    Hashing.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setEuclideanRing() {
    EuclideanRing.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeRing() {
    CommutativeRing.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setRing() {
    Ring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setSemiring() {
    Semiring.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setGroup() {
    Group.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setMonoid() {
    Monoid.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setSemigroup() {
    Semigroup.Key<Byte>(Byte.suppliedType).withImpliedUsingFirst correspondsTo ByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Byte.Companion.setEquality() {
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

public fun Short.Companion.reification(): Reification<Short> = ShortContext
public fun Short.Companion.order(): Order<Short> = ShortContext
public fun Short.Companion.hashing(): Hashing<Short> = ShortContext
public fun Short.Companion.euclideanRing(): EuclideanRing<Short> = ShortContext
public fun Short.Companion.euclideanSemiring(): EuclideanSemiring<Short> = ShortContext
public fun Short.Companion.commutativeRing(): CommutativeRing<Short> = ShortContext
public fun Short.Companion.ring(): Ring<Short> = ShortContext
public fun Short.Companion.commutativeSemiring(): CommutativeSemiring<Short> = ShortContext
public fun Short.Companion.semiring(): Semiring<Short> = ShortContext
public fun Short.Companion.commutativeGroup(): CommutativeGroup<Short> = ShortContext
public fun Short.Companion.group(): Group<Short> = ShortContext
public fun Short.Companion.commutativeMonoid(): CommutativeMonoid<Short> = ShortContext
public fun Short.Companion.monoid(): Monoid<Short> = ShortContext
public fun Short.Companion.commutativeSemigroup(): CommutativeSemigroup<Short> = ShortContext
public fun Short.Companion.semigroup(): Semigroup<Short> = ShortContext
public fun Short.Companion.equality(): Equality<Short> = ShortContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setReification() {
    Reification.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setOrder() {
    Order.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setHashing() {
    Hashing.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setEuclideanRing() {
    EuclideanRing.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeRing() {
    CommutativeRing.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setRing() {
    Ring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setSemiring() {
    Semiring.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setGroup() {
    Group.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setMonoid() {
    Monoid.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setSemigroup() {
    Semigroup.Key<Short>(Short.suppliedType).withImpliedUsingFirst correspondsTo ShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Short.Companion.setEquality() {
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

public fun Int.Companion.reification(): Reification<Int> = IntContext
public fun Int.Companion.order(): Order<Int> = IntContext
public fun Int.Companion.hashing(): Hashing<Int> = IntContext
public fun Int.Companion.euclideanRing(): EuclideanRing<Int> = IntContext
public fun Int.Companion.euclideanSemiring(): EuclideanSemiring<Int> = IntContext
public fun Int.Companion.commutativeRing(): CommutativeRing<Int> = IntContext
public fun Int.Companion.ring(): Ring<Int> = IntContext
public fun Int.Companion.commutativeSemiring(): CommutativeSemiring<Int> = IntContext
public fun Int.Companion.semiring(): Semiring<Int> = IntContext
public fun Int.Companion.commutativeGroup(): CommutativeGroup<Int> = IntContext
public fun Int.Companion.group(): Group<Int> = IntContext
public fun Int.Companion.commutativeMonoid(): CommutativeMonoid<Int> = IntContext
public fun Int.Companion.monoid(): Monoid<Int> = IntContext
public fun Int.Companion.commutativeSemigroup(): CommutativeSemigroup<Int> = IntContext
public fun Int.Companion.semigroup(): Semigroup<Int> = IntContext
public fun Int.Companion.equality(): Equality<Int> = IntContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setReification() {
    Reification.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setOrder() {
    Order.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setHashing() {
    Hashing.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setEuclideanRing() {
    EuclideanRing.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeRing() {
    CommutativeRing.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setRing() {
    Ring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setSemiring() {
    Semiring.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setGroup() {
    Group.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setMonoid() {
    Monoid.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setSemigroup() {
    Semigroup.Key<Int>(Int.suppliedType).withImpliedUsingFirst correspondsTo IntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Int.Companion.setEquality() {
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

public fun Long.Companion.reification(): Reification<Long> = LongContext
public fun Long.Companion.order(): Order<Long> = LongContext
public fun Long.Companion.hashing(): Hashing<Long> = LongContext
public fun Long.Companion.euclideanRing(): EuclideanRing<Long> = LongContext
public fun Long.Companion.euclideanSemiring(): EuclideanSemiring<Long> = LongContext
public fun Long.Companion.commutativeRing(): CommutativeRing<Long> = LongContext
public fun Long.Companion.ring(): Ring<Long> = LongContext
public fun Long.Companion.commutativeSemiring(): CommutativeSemiring<Long> = LongContext
public fun Long.Companion.semiring(): Semiring<Long> = LongContext
public fun Long.Companion.commutativeGroup(): CommutativeGroup<Long> = LongContext
public fun Long.Companion.group(): Group<Long> = LongContext
public fun Long.Companion.commutativeMonoid(): CommutativeMonoid<Long> = LongContext
public fun Long.Companion.monoid(): Monoid<Long> = LongContext
public fun Long.Companion.commutativeSemigroup(): CommutativeSemigroup<Long> = LongContext
public fun Long.Companion.semigroup(): Semigroup<Long> = LongContext
public fun Long.Companion.equality(): Equality<Long> = LongContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setReification() {
    Reification.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setOrder() {
    Order.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setHashing() {
    Hashing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setEuclideanRing() {
    EuclideanRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeRing() {
    CommutativeRing.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setRing() {
    Ring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSemiring() {
    Semiring.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setGroup() {
    Group.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setMonoid() {
    Monoid.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setSemigroup() {
    Semigroup.Key<Long>(Long.suppliedType).withImpliedUsingFirst correspondsTo LongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Long.Companion.setEquality() {
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

public fun UByte.Companion.reification(): Reification<UByte> = UByteContext
public fun UByte.Companion.order(): Order<UByte> = UByteContext
public fun UByte.Companion.hashing(): Hashing<UByte> = UByteContext
public fun UByte.Companion.euclideanSemiring(): EuclideanSemiring<UByte> = UByteContext
public fun UByte.Companion.commutativeSemiring(): CommutativeSemiring<UByte> = UByteContext
public fun UByte.Companion.semiring(): Semiring<UByte> = UByteContext
public fun UByte.Companion.commutativeMonoid(): CommutativeMonoid<UByte> = UByteContext
public fun UByte.Companion.monoid(): Monoid<UByte> = UByteContext
public fun UByte.Companion.commutativeSemigroup(): CommutativeSemigroup<UByte> = UByteContext
public fun UByte.Companion.semigroup(): Semigroup<UByte> = UByteContext
public fun UByte.Companion.equality(): Equality<UByte> = UByteContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setReification() {
    Reification.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setOrder() {
    Order.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setHashing() {
    Hashing.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setSemiring() {
    Semiring.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setMonoid() {
    Monoid.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setSemigroup() {
    Semigroup.Key<UByte>(UByte.suppliedType).withImpliedUsingFirst correspondsTo UByteContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UByte.Companion.setEquality() {
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

public fun UShort.Companion.reification(): Reification<UShort> = UShortContext
public fun UShort.Companion.order(): Order<UShort> = UShortContext
public fun UShort.Companion.hashing(): Hashing<UShort> = UShortContext
public fun UShort.Companion.euclideanSemiring(): EuclideanSemiring<UShort> = UShortContext
public fun UShort.Companion.commutativeSemiring(): CommutativeSemiring<UShort> = UShortContext
public fun UShort.Companion.semiring(): Semiring<UShort> = UShortContext
public fun UShort.Companion.commutativeMonoid(): CommutativeMonoid<UShort> = UShortContext
public fun UShort.Companion.monoid(): Monoid<UShort> = UShortContext
public fun UShort.Companion.commutativeSemigroup(): CommutativeSemigroup<UShort> = UShortContext
public fun UShort.Companion.semigroup(): Semigroup<UShort> = UShortContext
public fun UShort.Companion.equality(): Equality<UShort> = UShortContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setReification() {
    Reification.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setOrder() {
    Order.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setHashing() {
    Hashing.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setSemiring() {
    Semiring.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setMonoid() {
    Monoid.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setSemigroup() {
    Semigroup.Key<UShort>(UShort.suppliedType).withImpliedUsingFirst correspondsTo UShortContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UShort.Companion.setEquality() {
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

public fun UInt.Companion.reification(): Reification<UInt> = UIntContext
public fun UInt.Companion.order(): Order<UInt> = UIntContext
public fun UInt.Companion.hashing(): Hashing<UInt> = UIntContext
public fun UInt.Companion.euclideanSemiring(): EuclideanSemiring<UInt> = UIntContext
public fun UInt.Companion.commutativeSemiring(): CommutativeSemiring<UInt> = UIntContext
public fun UInt.Companion.semiring(): Semiring<UInt> = UIntContext
public fun UInt.Companion.commutativeMonoid(): CommutativeMonoid<UInt> = UIntContext
public fun UInt.Companion.monoid(): Monoid<UInt> = UIntContext
public fun UInt.Companion.commutativeSemigroup(): CommutativeSemigroup<UInt> = UIntContext
public fun UInt.Companion.semigroup(): Semigroup<UInt> = UIntContext
public fun UInt.Companion.equality(): Equality<UInt> = UIntContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setReification() {
    Reification.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setOrder() {
    Order.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setHashing() {
    Hashing.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setSemiring() {
    Semiring.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setMonoid() {
    Monoid.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setSemigroup() {
    Semigroup.Key<UInt>(UInt.suppliedType).withImpliedUsingFirst correspondsTo UIntContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun UInt.Companion.setEquality() {
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

public fun ULong.Companion.reification(): Reification<ULong> = ULongContext
public fun ULong.Companion.order(): Order<ULong> = ULongContext
public fun ULong.Companion.hashing(): Hashing<ULong> = ULongContext
public fun ULong.Companion.euclideanSemiring(): EuclideanSemiring<ULong> = ULongContext
public fun ULong.Companion.commutativeSemiring(): CommutativeSemiring<ULong> = ULongContext
public fun ULong.Companion.semiring(): Semiring<ULong> = ULongContext
public fun ULong.Companion.commutativeMonoid(): CommutativeMonoid<ULong> = ULongContext
public fun ULong.Companion.monoid(): Monoid<ULong> = ULongContext
public fun ULong.Companion.commutativeSemigroup(): CommutativeSemigroup<ULong> = ULongContext
public fun ULong.Companion.semigroup(): Semigroup<ULong> = ULongContext
public fun ULong.Companion.equality(): Equality<ULong> = ULongContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setReification() {
    Reification.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setOrder() {
    Order.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setHashing() {
    Hashing.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setEuclideanSemiring() {
    EuclideanSemiring.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setSemiring() {
    Semiring.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setMonoid() {
    Monoid.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setSemigroup() {
    Semigroup.Key<ULong>(ULong.suppliedType).withImpliedUsingFirst correspondsTo ULongContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun ULong.Companion.setEquality() {
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
    override fun Double.div(other: Double): Double = this / other
    override fun power(base: Double, exponent: UInt): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: ULong): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: Int): Double = base.kpow(exponent)
    override fun power(base: Double, exponent: Long): Double = base.kpow(exponent.toDouble())
    // endregion
}

public fun Double.Companion.reification(): Reification<Double> = DoubleContext
public fun Double.Companion.order(): Order<Double> = DoubleContext
public fun Double.Companion.hashing(): Hashing<Double> = DoubleContext
public fun Double.Companion.field(): Field<Double> = DoubleContext
public fun Double.Companion.commutativeRing(): CommutativeRing<Double> = DoubleContext
public fun Double.Companion.ring(): Ring<Double> = DoubleContext
public fun Double.Companion.commutativeSemiring(): CommutativeSemiring<Double> = DoubleContext
public fun Double.Companion.semiring(): Semiring<Double> = DoubleContext
public fun Double.Companion.commutativeGroup(): CommutativeGroup<Double> = DoubleContext
public fun Double.Companion.group(): Group<Double> = DoubleContext
public fun Double.Companion.commutativeMonoid(): CommutativeMonoid<Double> = DoubleContext
public fun Double.Companion.monoid(): Monoid<Double> = DoubleContext
public fun Double.Companion.commutativeSemigroup(): CommutativeSemigroup<Double> = DoubleContext
public fun Double.Companion.semigroup(): Semigroup<Double> = DoubleContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setReification() {
    Reification.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setOrder() {
    Order.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setHashing() {
    Hashing.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setField() {
    Field.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeRing() {
    CommutativeRing.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setRing() {
    Ring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSemiring() {
    Semiring.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setGroup() {
    Group.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setMonoid() {
    Monoid.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Double>(Double.suppliedType).withImpliedUsingFirst correspondsTo DoubleContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Double.Companion.setSemigroup() {
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
    override fun Float.div(other: Float): Float = this / other
    override fun power(base: Float, exponent: UInt): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: ULong): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: Int): Float = base.kpow(exponent)
    override fun power(base: Float, exponent: Long): Float = base.kpow(exponent.toFloat())
    // endregion
}

public fun Float.Companion.reification(): Reification<Float> = FloatContext
public fun Float.Companion.order(): Order<Float> = FloatContext
public fun Float.Companion.hashing(): Hashing<Float> = FloatContext
public fun Float.Companion.field(): Field<Float> = FloatContext
public fun Float.Companion.commutativeRing(): CommutativeRing<Float> = FloatContext
public fun Float.Companion.ring(): Ring<Float> = FloatContext
public fun Float.Companion.commutativeSemiring(): CommutativeSemiring<Float> = FloatContext
public fun Float.Companion.semiring(): Semiring<Float> = FloatContext
public fun Float.Companion.commutativeGroup(): CommutativeGroup<Float> = FloatContext
public fun Float.Companion.group(): Group<Float> = FloatContext
public fun Float.Companion.commutativeMonoid(): CommutativeMonoid<Float> = FloatContext
public fun Float.Companion.monoid(): Monoid<Float> = FloatContext
public fun Float.Companion.commutativeSemigroup(): CommutativeSemigroup<Float> = FloatContext
public fun Float.Companion.semigroup(): Semigroup<Float> = FloatContext

context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setReification() {
    Reification.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setOrder() {
    Order.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setHashing() {
    Hashing.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setField() {
    Field.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeRing() {
    CommutativeRing.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setRing() {
    Ring.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeSemiring() {
    CommutativeSemiring.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setSemiring() {
    Semiring.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeGroup() {
    CommutativeGroup.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setGroup() {
    Group.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeMonoid() {
    CommutativeMonoid.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setMonoid() {
    Monoid.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setCommutativeSemigroup() {
    CommutativeSemigroup.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public fun Float.Companion.setSemigroup() {
    Semigroup.Key<Float>(Float.suppliedType).withImpliedUsingFirst correspondsTo FloatContext
}