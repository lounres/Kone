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
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.withImplied
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.math.pow as kpow


/**
 * Default Euclidean ring for [Byte] type which values are seen as integers and where overflows are ignored.
 * It also implements [Reification], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object ByteContext: Reification<Byte>, EuclideanRing<Byte>, Order<Byte>, Hashing<Byte> {
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

/**
 * Default context of the [Byte] type. See [ByteContext] for more.
 */
public val Byte.Companion.context: ByteContext get() = ByteContext

/**
 * Sets default [Byte] context (see [ByteContext])
 * as the following type of contexts with [Byte] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Group],
 * - [CommutativeGroup],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [Ring],
 * - [CommutativeRing],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun ByteContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val byteSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Byte",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in ByteContext>>(
        Reification.Key(byteSuppliedType),
        EuclideanRing.Key(byteSuppliedType),
        Order.Key(byteSuppliedType),
        Hashing.Key(byteSuppliedType),
    ).forEach {
        it.withImplied correspondsTo ByteContext
    }
}

/**
 * Default Euclidean ring for [Short] type which values are seen as integers and where overflows are ignored.
 * It also implements [Reification], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object ShortContext: Reification<Short>, EuclideanRing<Short>, Order<Short>, Hashing<Short> {
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

/**
 * Default context of the [Short] type. See [ShortContext] for more.
 */
public val Short.Companion.context: ShortContext get() = ShortContext

/**
 * Sets default [Short] context (see [ShortContext])
 * as the following type of contexts with [Short] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Group],
 * - [CommutativeGroup],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [Ring],
 * - [CommutativeRing],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun ShortContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val shortSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Short",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in ShortContext>>(
        Reification.Key(shortSuppliedType),
        EuclideanRing.Key(shortSuppliedType),
        Order.Key(shortSuppliedType),
        Hashing.Key(shortSuppliedType),
    ).forEach {
        it.withImplied correspondsTo ShortContext
    }
}

/**
 * Default Euclidean ring for [Int] type which values are seen as integers and where overflows are ignored.
 * It also implements [Reification], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object IntContext: Reification<Int>, EuclideanRing<Int>, Order<Int>, Hashing<Int> {
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

/**
 * Default context of the [Int] type. See [IntContext] for more.
 */
public val Int.Companion.context: IntContext get() = IntContext

/**
 * Sets default [Int] context (see [IntContext])
 * as the following type of contexts with [Int] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Group],
 * - [CommutativeGroup],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [Ring],
 * - [CommutativeRing],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun IntContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val intSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Int",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in IntContext>>(
        Reification.Key(intSuppliedType),
        EuclideanRing.Key(intSuppliedType),
        Order.Key(intSuppliedType),
        Hashing.Key(intSuppliedType),
    ).forEach {
        it.withImplied correspondsTo IntContext
    }
}

/**
 * Default Euclidean ring for [Long] type which values are seen as integers and where overflows are ignored.
 * It also implements [Reification], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object LongContext: Reification<Long>, EuclideanRing<Long>, Order<Long>, Hashing<Long> {
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

/**
 * Default context of the [Long] type. See [LongContext] for more.
 */
public val Long.Companion.context: LongContext get() = LongContext

/**
 * Sets default [Long] context (see [LongContext])
 * as the following type of contexts with [Long] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Group],
 * - [CommutativeGroup],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [Ring],
 * - [CommutativeRing],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun LongContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val longSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Long",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in LongContext>>(
        Reification.Key(longSuppliedType),
        EuclideanRing.Key(longSuppliedType),
        Order.Key(longSuppliedType),
        Hashing.Key(longSuppliedType),
    ).forEach {
        it.withImplied correspondsTo LongContext
    }
}

/**
 * Default Euclidean semiring for [UByte] type which values are seen as non-negative integers and where overflows are ignored.
 * It also implements [Reification], [ExtendedSemiring], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object UByteContext: Reification<UByte>, EuclideanSemiring<UByte>, ExtendedSemiring<UByte>, Order<UByte>, Hashing<UByte> {
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

/**
 * Default context of the [UByte] type. See [UByteContext] for more.
 */
public val UByte.Companion.context: UByteContext get() = UByteContext

/**
 * Sets default [UByte] context (see [UByteContext])
 * as the following type of contexts with [UByte] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun UByteContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val uByteSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.UByte",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in UByteContext>>(
        Reification.Key(uByteSuppliedType),
        EuclideanSemiring.Key(uByteSuppliedType),
        Order.Key(uByteSuppliedType),
        Hashing.Key(uByteSuppliedType),
    ).forEach {
        it.withImplied correspondsTo UByteContext
    }
}

/**
 * Default Euclidean semiring for [UShort] type which values are seen as non-negative integers and where overflows are ignored.
 * It also implements [Reification], [ExtendedSemiring], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object UShortContext: Reification<UShort>, EuclideanSemiring<UShort>, ExtendedSemiring<UShort>, Order<UShort>, Hashing<UShort> {
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

/**
 * Default context of the [UShort] type. See [UShortContext] for more.
 */
public val UShort.Companion.context: UShortContext get() = UShortContext

/**
 * Sets default [UShort] context (see [UShortContext])
 * as the following type of contexts with [UShort] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun UShortContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val uShortSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.UShort",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in UShortContext>>(
        Reification.Key(uShortSuppliedType),
        EuclideanSemiring.Key(uShortSuppliedType),
        Order.Key(uShortSuppliedType),
        Hashing.Key(uShortSuppliedType),
    ).forEach {
        it.withImplied correspondsTo UShortContext
    }
}

/**
 * Default Euclidean semiring for [UInt] type which values are seen as non-negative integers and where overflows are ignored.
 * It also implements [Reification], [ExtendedSemiring], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object UIntContext: Reification<UInt>, EuclideanSemiring<UInt>, ExtendedSemiring<UInt>, Order<UInt>, Hashing<UInt> {
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

/**
 * Default context of the [UInt] type. See [UIntContext] for more.
 */
public val UInt.Companion.context: UIntContext get() = UIntContext

/**
 * Sets default [UInt] context (see [UIntContext])
 * as the following type of contexts with [UInt] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun UIntContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val uIntSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.UInt",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in UIntContext>>(
        Reification.Key(uIntSuppliedType),
        EuclideanSemiring.Key(uIntSuppliedType),
        Order.Key(uIntSuppliedType),
        Hashing.Key(uIntSuppliedType),
    ).forEach {
        it.withImplied correspondsTo UIntContext
    }
}

/**
 * Default Euclidean semiring for [ULong] type which values are seen as non-negative integers and where overflows are ignored.
 * It also implements [Reification], [ExtendedSemiring], [Order], and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object ULongContext: Reification<ULong>, EuclideanSemiring<ULong>, ExtendedSemiring<ULong>, Order<ULong>, Hashing<ULong> {
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

/**
 * Default context of the [ULong] type. See [ULongContext] for more.
 */
public val ULong.Companion.context: ULongContext get() = ULongContext

/**
 * Sets default [ULong] context (see [ULongContext])
 * as the following type of contexts with [ULong] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun ULongContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val uLongSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.ULong",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in ULongContext>>(
        Reification.Key(uLongSuppliedType),
        EuclideanSemiring.Key(uLongSuppliedType),
        Order.Key(uLongSuppliedType),
        Hashing.Key(uLongSuppliedType),
    ).forEach {
        it.withImplied correspondsTo ULongContext
    }
}

/**
 * Default field for [Double] type which values are seen as real numbers and where precision problems and occurrences of
 * [NaN][Double.NaN], [+INF][Double.POSITIVE_INFINITY], and [-INF][Double.NEGATIVE_INFINITY] are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object DoubleContext: Reification<Double>, Field<Double>, Order<Double>, Hashing<Double> {
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

/**
 * Default context of the [Double] type. See [DoubleContext] for more.
 */
public val Double.Companion.context: DoubleContext get() = DoubleContext

/**
 * Sets default [Double] context (see [DoubleContext])
 * as the following type of contexts with [Double] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Group],
 * - [CommutativeGroup],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [Ring],
 * - [CommutativeRing],
 * - [Field]
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun DoubleContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val doubleSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Double",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in DoubleContext>>(
        Reification.Key(doubleSuppliedType),
        Field.Key(doubleSuppliedType),
        Order.Key(doubleSuppliedType),
        Hashing.Key(doubleSuppliedType),
    ).forEach {
        it.withImplied correspondsTo DoubleContext
    }
}

/**
 * Default field for [Float] type which values are seen as real numbers and where precision problems and occurrences of
 * [NaN][Float.NaN], [+INF][Float.POSITIVE_INFINITY], and [-INF][Float.NEGATIVE_INFINITY] are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such context is useless when used as is, but useful when used in generalised algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object FloatContext: Reification<Float>, Field<Float>, Order<Float>, Hashing<Float> {
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

/**
 * Default context of the [Float] type. See [FloatContext] for more.
 */
public val Float.Companion.context: FloatContext get() = FloatContext

/**
 * Sets default [Float] context (see [FloatContext])
 * as the following type of contexts with [Float] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semigroup],
 * - [CommutativeSemigroup],
 * - [Monoid],
 * - [CommutativeMonoid],
 * - [Group],
 * - [CommutativeGroup],
 * - [Semiring],
 * - [CommutativeSemiring],
 * - [Ring],
 * - [CommutativeRing],
 * - [Field]
 * - [Order],
 * - [Hashing].
 */
context(koneContextRegistryBuilder: OwnedRegistryBuilder<KoneContextRegistry>)
public fun FloatContext.set() {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    val floatSuppliedType = SuppliedType.Regular(
        fullyQualifiedName = "kotlin.Float",
        typeArguments = emptyList(),
        isNullable = false,
    )
    listOf<RegistryKey<in FloatContext>>(
        Reification.Key(floatSuppliedType),
        Field.Key(floatSuppliedType),
        Order.Key(floatSuppliedType),
        Hashing.Key(floatSuppliedType),
    ).forEach {
        it.withImplied correspondsTo FloatContext
    }
}