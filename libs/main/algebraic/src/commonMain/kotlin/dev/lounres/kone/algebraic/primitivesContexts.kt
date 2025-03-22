/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */


package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.relations.ComparisonResult
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.asComparisonResult
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
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
        EuclideanDivisionResult(quotient = (this / other).toByte(), remainder = (this % other).toByte())
    override fun Byte.div(other: Byte): Byte = (this / other).toByte()
    override fun Byte.rem(other: Byte): Byte = (this % other).toByte()
    // endregion
}

/**
 * Default context of the [Byte] type. See [ByteContext] for more.
 */
public val Byte.Companion.context: ByteContext get() = ByteContext

/**
 * Installs default [Byte] context (see [ByteContext])
 * as the following type of contexts with [Byte] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installByteContext() {
    val byteSuppliedType = SuppliedType.Regular<Byte>(
        kClass = Byte::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Equality.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Semiring.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Ring.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[EuclideanSemiring.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[EuclideanRing.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Order.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Hashing.Key(byteSuppliedType)] = ByteContext
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
        EuclideanDivisionResult(quotient = (this / other).toShort(), remainder = (this % other).toShort())
    override fun Short.div(other: Short): Short = (this / other).toShort()
    override fun Short.rem(other: Short): Short = (this % other).toShort()
    // endregion
}

/**
 * Default context of the [Short] type. See [ShortContext] for more.
 */
public val Short.Companion.context: ShortContext get() = ShortContext

/**
 * Installs default [Short] context (see [ShortContext])
 * as the following type of contexts with [Short] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installShortContext() {
    val shortSuppliedType = SuppliedType.Regular<Short>(
        kClass = Short::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Equality.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Semiring.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Ring.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[EuclideanSemiring.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[EuclideanRing.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Order.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Hashing.Key(shortSuppliedType)] = ShortContext
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
        EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Int.div(other: Int): Int = this / other
    override fun Int.rem(other: Int): Int = this % other
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
 * Installs default [Int] context (see [IntContext])
 * as the following type of contexts with [Int] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installIntContext() {
    val intSuppliedType = SuppliedType.Regular<Int>(
        kClass = Int::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(intSuppliedType)] = IntContext
    contextsBuilder[Equality.Key(intSuppliedType)] = IntContext
    contextsBuilder[Semiring.Key(intSuppliedType)] = IntContext
    contextsBuilder[Ring.Key(intSuppliedType)] = IntContext
    contextsBuilder[EuclideanSemiring.Key(intSuppliedType)] = IntContext
    contextsBuilder[EuclideanRing.Key(intSuppliedType)] = IntContext
    contextsBuilder[Order.Key(intSuppliedType)] = IntContext
    contextsBuilder[Hashing.Key(intSuppliedType)] = IntContext
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
        EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Long.div(other: Long): Long = this / other
    override fun Long.rem(other: Long): Long = this % other
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
 * Installs default [Long] context (see [LongContext])
 * as the following type of contexts with [Long] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [EuclideanSemiring],
 * - [EuclideanRing],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installLongContext() {
    val longSuppliedType = SuppliedType.Regular<Long>(
        kClass = Long::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(longSuppliedType)] = LongContext
    contextsBuilder[Equality.Key(longSuppliedType)] = LongContext
    contextsBuilder[Semiring.Key(longSuppliedType)] = LongContext
    contextsBuilder[Ring.Key(longSuppliedType)] = LongContext
    contextsBuilder[EuclideanSemiring.Key(longSuppliedType)] = LongContext
    contextsBuilder[EuclideanRing.Key(longSuppliedType)] = LongContext
    contextsBuilder[Order.Key(longSuppliedType)] = LongContext
    contextsBuilder[Hashing.Key(longSuppliedType)] = LongContext
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
        EuclideanDivisionResult(
            quotient = (this / other).toUByte(),
            remainder = (this % other).toUByte(),
        )
    override fun UByte.div(other: UByte): UByte = (this / other).toUByte()
    override fun UByte.rem(other: UByte): UByte = (this % other).toUByte()
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
 * Installs default [UByte] context (see [UByteContext])
 * as the following type of contexts with [UByte] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installUByteContext() {
    val uByteSuppliedType = SuppliedType.Regular<UByte>(
        kClass = UByte::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Equality.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Semiring.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[EuclideanSemiring.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Order.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Hashing.Key(uByteSuppliedType)] = UByteContext
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
        EuclideanDivisionResult(
            quotient = (this / other).toUShort(),
            remainder = (this % other).toUShort(),
        )
    override fun UShort.div(other: UShort): UShort = (this / other).toUShort()
    override fun UShort.rem(other: UShort): UShort = (this % other).toUShort()
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
 * Installs default [UShort] context (see [UShortContext])
 * as the following type of contexts with [UShort] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installUShortContext() {
    val uShortSuppliedType = SuppliedType.Regular<UShort>(
        kClass = UShort::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Equality.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Semiring.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[EuclideanSemiring.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Order.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Hashing.Key(uShortSuppliedType)] = UShortContext
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
        EuclideanDivisionResult(
            quotient = this / other,
            remainder = this % other,
        )
    override fun UInt.div(other: UInt): UInt = this / other
    override fun UInt.rem(other: UInt): UInt = this % other
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
 * Installs default [UInt] context (see [UIntContext])
 * as the following type of contexts with [UInt] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installUIntContext() {
    val uIntSuppliedType = SuppliedType.Regular<UInt>(
        kClass = UInt::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Equality.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Semiring.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[EuclideanSemiring.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Order.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Hashing.Key(uIntSuppliedType)] = UIntContext
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
    override val zero: ULong get() = 0.toULong()
    override val one: ULong get() = 1.toULong()
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
        EuclideanDivisionResult(
            quotient = this / other,
            remainder = this % other,
        )
    override fun ULong.div(other: ULong): ULong = this / other
    override fun ULong.rem(other: ULong): ULong = this % other
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
 * Installs default [ULong] context (see [ULongContext])
 * as the following type of contexts with [ULong] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [EuclideanSemiring],
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installULongContext() {
    val uLongSuppliedType = SuppliedType.Regular<ULong>(
        kClass = ULong::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Equality.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Semiring.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[EuclideanSemiring.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Order.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Hashing.Key(uLongSuppliedType)] = ULongContext
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
    // TODO: For now `0.0 != -0.0`, and there are other problems...
//    override fun Double.equalsTo(other: Double): Boolean {
//        TODO("Not yet implemented")
//    }
    override fun Double.isZero(): Boolean = this == 0.0 || this == -0.0
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
 * Installs default [Double] context (see [DoubleContext])
 * as the following type of contexts with [Double] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [Field]
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installDoubleContext() {
    val doubleSuppliedType = SuppliedType.Regular<Double>(
        kClass = Double::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Equality.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Semiring.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Ring.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Field.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Order.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Hashing.Key(doubleSuppliedType)] = DoubleContext
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
    override fun Float.isZero(): Boolean = this == 0.0f || this == -0.0f
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
 * Installs default [Float] context (see [FloatContext])
 * as the following type of contexts with [Float] as a type argument:
 * - [Reification],
 * - [Equality],
 * - [Semiring],
 * - [Ring],
 * - [Field]
 * - [Order],
 * - [Hashing].
 */
public fun KoneContextRegistryBuilder.installFloatContext() {
    val floatSuppliedType = SuppliedType.Regular<Float>(
        kClass = Float::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Equality.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Semiring.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Ring.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Field.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Order.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Hashing.Key(floatSuppliedType)] = FloatContext
}