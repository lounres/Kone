/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalKoneAPI::class)

package dev.lounres.kone.algebraic

import dev.lounres.kone.ExperimentalKoneAPI
import dev.lounres.kone.comparison.ComparisonResult
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.asComparisonResult
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.math.pow as kpow


/**
 * Default ring for [Byte] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
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
    override operator fun Byte.plus(other: Int): Byte = (this + other).toByte()
    override operator fun Byte.minus(other: Int): Byte = (this - other).toByte()
    override operator fun Byte.times(other: Int): Byte = (this * other).toByte()
    // endregion
    
    // region Byte-UInt operations
    override operator fun Byte.plus(other: UInt): Byte = (this + other.toByte()).toByte()
    override operator fun Byte.minus(other: UInt): Byte = (this - other.toByte()).toByte()
    override operator fun Byte.times(other: UInt): Byte = (this * other.toByte()).toByte()
    // endregion

    // region Byte-Long operations
    override operator fun Byte.plus(other: Long): Byte = (this + other).toByte()
    override operator fun Byte.minus(other: Long): Byte = (this - other).toByte()
    override operator fun Byte.times(other: Long): Byte = (this * other).toByte()
    // endregion
    
    // region Byte-ULong operations
    override operator fun Byte.plus(other: ULong): Byte = (this + other.toByte()).toByte()
    override operator fun Byte.minus(other: ULong): Byte = (this - other.toByte()).toByte()
    override operator fun Byte.times(other: ULong): Byte = (this * other.toByte()).toByte()
    // endregion

    // region Int-Byte operations
    override operator fun Int.plus(other: Byte): Byte = (this + other).toByte()
    override operator fun Int.minus(other: Byte): Byte = (this - other).toByte()
    override operator fun Int.times(other: Byte): Byte = (this * other).toByte()
    // endregion
    
    // region UInt-Byte operations
    override operator fun UInt.plus(other: Byte): Byte = (this.toByte() + other).toByte()
    override operator fun UInt.minus(other: Byte): Byte = (this.toByte() - other).toByte()
    override operator fun UInt.times(other: Byte): Byte = (this.toByte() * other).toByte()
    // endregion

    // region Long-Byte operations
    override operator fun Long.plus(other: Byte): Byte = (this + other).toByte()
    override operator fun Long.minus(other: Byte): Byte = (this - other).toByte()
    override operator fun Long.times(other: Byte): Byte = (this * other).toByte()
    // endregion
    
    // region ULong-Byte operations
    override operator fun ULong.plus(other: Byte): Byte = (this.toByte() + other).toByte()
    override operator fun ULong.minus(other: Byte): Byte = (this.toByte() - other).toByte()
    override operator fun ULong.times(other: Byte): Byte = (this.toByte() * other).toByte()
    // endregion

    // region Byte-Byte operations
    override operator fun Byte.unaryMinus(): Byte = (-this).toByte()
    override operator fun Byte.plus(other: Byte): Byte = (this + other).toByte()
    override operator fun Byte.minus(other: Byte): Byte = (this - other).toByte()
    override operator fun Byte.times(other: Byte): Byte = (this * other).toByte()
    override fun Byte.divrem(other: Byte): EuclideanDivisionResult<Byte> =
        EuclideanDivisionResult(quotient = (this / other).toByte(), remainder = (this % other).toByte())
    override fun Byte.div(other: Byte): Byte = (this / other).toByte()
    override fun Byte.rem(other: Byte): Byte = (this % other).toByte()
    // endregion
}

/**
 * Default ring of the [Byte] type. See [ByteContext] for more.
 */
public val Byte.Companion.context: ByteContext get() = ByteContext
public fun KoneContextRegistryBuilder.installByteContext() {
    val byteSuppliedType = SuppliedType.Regular<Byte>(
        kClass = Byte::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Equality.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Ring.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[EuclideanRing.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Order.Key(byteSuppliedType)] = ByteContext
    contextsBuilder[Hashing.Key(byteSuppliedType)] = ByteContext
}

/**
 * Default ring for [Short] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
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
    override operator fun Short.plus(other: Int): Short = (this + other).toShort()
    override operator fun Short.minus(other: Int): Short = (this - other).toShort()
    override operator fun Short.times(other: Int): Short = (this * other).toShort()
    // endregion
    
    // region Short-UInt operations
    override operator fun Short.plus(other: UInt): Short = (this + other.toShort()).toShort()
    override operator fun Short.minus(other: UInt): Short = (this - other.toShort()).toShort()
    override operator fun Short.times(other: UInt): Short = (this * other.toShort()).toShort()
    // endregion

    // region Short-Long operations
    override operator fun Short.plus(other: Long): Short = (this + other).toShort()
    override operator fun Short.minus(other: Long): Short = (this - other).toShort()
    override operator fun Short.times(other: Long): Short = (this * other).toShort()
    // endregion
    
    // region Short-ULong operations
    override operator fun Short.plus(other: ULong): Short = (this + other.toShort()).toShort()
    override operator fun Short.minus(other: ULong): Short = (this - other.toShort()).toShort()
    override operator fun Short.times(other: ULong): Short = (this * other.toShort()).toShort()
    // endregion

    // region Int-Short operations
    override operator fun Int.plus(other: Short): Short = (this + other).toShort()
    override operator fun Int.minus(other: Short): Short = (this - other).toShort()
    override operator fun Int.times(other: Short): Short = (this * other).toShort()
    // endregion
    
    // region Int-Short operations
    override operator fun UInt.plus(other: Short): Short = (this.toShort() + other).toShort()
    override operator fun UInt.minus(other: Short): Short = (this.toShort() - other).toShort()
    override operator fun UInt.times(other: Short): Short = (this.toShort() * other).toShort()
    // endregion

    // region Long-Short operations
    override operator fun Long.plus(other: Short): Short = (this + other).toShort()
    override operator fun Long.minus(other: Short): Short = (this - other).toShort()
    override operator fun Long.times(other: Short): Short = (this * other).toShort()
    // endregion
    
    // region Long-Short operations
    override operator fun ULong.plus(other: Short): Short = (this.toShort() + other).toShort()
    override operator fun ULong.minus(other: Short): Short = (this.toShort() - other).toShort()
    override operator fun ULong.times(other: Short): Short = (this.toShort() * other).toShort()
    // endregion

    // region Short-Short operations
    override operator fun Short.unaryMinus(): Short = (-this).toShort()
    override operator fun Short.plus(other: Short): Short = (this + other).toShort()
    override operator fun Short.minus(other: Short): Short = (this - other).toShort()
    override operator fun Short.times(other: Short): Short = (this * other).toShort()
    override fun Short.divrem(other: Short): EuclideanDivisionResult<Short> =
        EuclideanDivisionResult(quotient = (this / other).toShort(), remainder = (this % other).toShort())
    override fun Short.div(other: Short): Short = (this / other).toShort()
    override fun Short.rem(other: Short): Short = (this % other).toShort()
    // endregion
}

/**
 * Default ring of the [Short] type. See [ShortContext] for more.
 */
public val Short.Companion.context: ShortContext get() = ShortContext
public fun KoneContextRegistryBuilder.installShortContext() {
    val shortSuppliedType = SuppliedType.Regular<Short>(
        kClass = Short::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Equality.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Ring.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[EuclideanRing.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Order.Key(shortSuppliedType)] = ShortContext
    contextsBuilder[Hashing.Key(shortSuppliedType)] = ShortContext
}

/**
 * Default ring for [Int] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
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
    override operator fun Int.unaryMinus(): Int = -this
    override operator fun Int.plus(other: Int): Int = this + other
    override operator fun Int.minus(other: Int): Int = this - other
    override operator fun Int.times(other: Int): Int = this * other
    override fun Int.divrem(other: Int): EuclideanDivisionResult<Int> =
        EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Int.div(other: Int): Int = this / other
    override fun Int.rem(other: Int): Int = this % other
    // endregion
    
    // region Int-UInt operations
    override operator fun Int.plus(other: UInt): Int = this + other.toInt()
    override operator fun Int.minus(other: UInt): Int = this - other.toInt()
    override operator fun Int.times(other: UInt): Int = this * other.toInt()
    // endregion

    // region Int-Long operations
    override operator fun Int.plus(other: Long): Int = this + other.toInt()
    override operator fun Int.minus(other: Long): Int = this - other.toInt()
    override operator fun Int.times(other: Long): Int = this * other.toInt()
    // endregion
    
    // region Int-ULong operations
    override operator fun Int.plus(other: ULong): Int = this + other.toInt()
    override operator fun Int.minus(other: ULong): Int = this - other.toInt()
    override operator fun Int.times(other: ULong): Int = this * other.toInt()
    // endregion
    
    // region UInt-Int operations
    override operator fun UInt.plus(other: Int): Int = this.toInt() + other
    override operator fun UInt.minus(other: Int): Int = this.toInt() - other
    override operator fun UInt.times(other: Int): Int = this.toInt() * other
    // endregion
    
    // region Long-Int operations
    override operator fun Long.plus(other: Int): Int = this.toInt() + other
    override operator fun Long.minus(other: Int): Int = this.toInt() - other
    override operator fun Long.times(other: Int): Int = this.toInt() * other
    // endregion

    // region ULong-Int operations
    override operator fun ULong.plus(other: Int): Int = this.toInt() + other
    override operator fun ULong.minus(other: Int): Int = this.toInt() - other
    override operator fun ULong.times(other: Int): Int = this.toInt() * other
    // endregion
}

/**
 * Default ring of the [Int] type. See [IntContext] for more.
 */
public val Int.Companion.context: IntContext get() = IntContext
public fun KoneContextRegistryBuilder.installIntContext() {
    val intSuppliedType = SuppliedType.Regular<Int>(
        kClass = Int::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(intSuppliedType)] = IntContext
    contextsBuilder[Equality.Key(intSuppliedType)] = IntContext
    contextsBuilder[Ring.Key(intSuppliedType)] = IntContext
    contextsBuilder[EuclideanRing.Key(intSuppliedType)] = IntContext
    contextsBuilder[Order.Key(intSuppliedType)] = IntContext
    contextsBuilder[Hashing.Key(intSuppliedType)] = IntContext
}

/**
 * Default ring for [Long] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
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
    override operator fun Long.unaryMinus(): Long = -this
    override operator fun Long.plus(other: Long): Long = this + other
    override operator fun Long.minus(other: Long): Long = this - other
    override operator fun Long.times(other: Long): Long = this * other
    override fun Long.divrem(other: Long): EuclideanDivisionResult<Long> =
        EuclideanDivisionResult(quotient = this / other, remainder = this % other)
    override fun Long.div(other: Long): Long = this / other
    override fun Long.rem(other: Long): Long = this % other
    // endregion
    
    // region Long-Int operations
    override operator fun Long.plus(other: Int): Long = this + other
    override operator fun Long.minus(other: Int): Long = this - other
    override operator fun Long.times(other: Int): Long = this * other
    // endregion
    
    // region Long-UInt operations
    override operator fun Long.plus(other: UInt): Long = this + other.toLong()
    override operator fun Long.minus(other: UInt): Long = this - other.toLong()
    override operator fun Long.times(other: UInt): Long = this * other.toLong()
    // endregion

    // region Long-ULong operations
    override operator fun Long.plus(other: ULong): Long = this + other.toLong()
    override operator fun Long.minus(other: ULong): Long = this - other.toLong()
    override operator fun Long.times(other: ULong): Long = this * other.toLong()
    // endregion
    
    // region Int-Long operations
    override operator fun Int.plus(other: Long): Long = this + other
    override operator fun Int.minus(other: Long): Long = this - other
    override operator fun Int.times(other: Long): Long = this * other
    // endregion

    // region UInt-Long operations
    override operator fun UInt.plus(other: Long): Long = this.toLong() + other
    override operator fun UInt.minus(other: Long): Long = this.toLong() - other
    override operator fun UInt.times(other: Long): Long = this.toLong() * other
    // endregion
    
    // region ULong-Long operations
    override operator fun ULong.plus(other: Long): Long = this.toLong() + other
    override operator fun ULong.minus(other: Long): Long = this.toLong() - other
    override operator fun ULong.times(other: Long): Long = this.toLong() * other
    // endregion
}

/**
 * Default ring of the [Long] type. See [LongContext] for more.
 */
public val Long.Companion.context: LongContext get() = LongContext
public fun KoneContextRegistryBuilder.installLongContext() {
    val longSuppliedType = SuppliedType.Regular<Long>(
        kClass = Long::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(longSuppliedType)] = LongContext
    contextsBuilder[Equality.Key(longSuppliedType)] = LongContext
    contextsBuilder[Ring.Key(longSuppliedType)] = LongContext
    contextsBuilder[EuclideanRing.Key(longSuppliedType)] = LongContext
    contextsBuilder[Order.Key(longSuppliedType)] = LongContext
    contextsBuilder[Hashing.Key(longSuppliedType)] = LongContext
}

// TODO: Make it a semiring
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object UByteContext: Reification<UByte>, Equality<UByte>, Order<UByte>, Hashing<UByte> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UByte
    override fun reifyMaybe(element: Any?): Maybe<UByte> = if (element is UByte) Some(element) else None
    override fun reifyOrNull(element: Any?): UByte? = element as? UByte
    override fun reify(element: Any?): UByte = element as? UByte ?: reificationException()
    // endregion
    
    // region Order
    override fun UByte.compareWith(other: UByte): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
}

public val UByte.Companion.context: UByteContext get() = UByteContext
public fun KoneContextRegistryBuilder.installUByteContext() {
    val uByteSuppliedType = SuppliedType.Regular<UByte>(
        kClass = UByte::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Equality.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Order.Key(uByteSuppliedType)] = UByteContext
    contextsBuilder[Hashing.Key(uByteSuppliedType)] = UByteContext
}

// TODO: Make it a semiring
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object UShortContext: Reification<UShort>, Equality<UShort>, Order<UShort>, Hashing<UShort> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UShort
    override fun reifyMaybe(element: Any?): Maybe<UShort> = if (element is UShort) Some(element) else None
    override fun reifyOrNull(element: Any?): UShort? = element as? UShort
    override fun reify(element: Any?): UShort = element as? UShort ?: reificationException()
    // endregion
    
    // region Order
    override fun UShort.compareWith(other: UShort): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
}

public val UShort.Companion.context: UShortContext get() = UShortContext
public fun KoneContextRegistryBuilder.installUShortContext() {
    val uShortSuppliedType = SuppliedType.Regular<UShort>(
        kClass = UShort::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Equality.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Order.Key(uShortSuppliedType)] = UShortContext
    contextsBuilder[Hashing.Key(uShortSuppliedType)] = UShortContext
}

// TODO: Make it a semiring
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object UIntContext: Reification<UInt>, Equality<UInt>, Order<UInt>, Hashing<UInt> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is UInt
    override fun reifyMaybe(element: Any?): Maybe<UInt> = if (element is UInt) Some(element) else None
    override fun reifyOrNull(element: Any?): UInt? = element as? UInt
    override fun reify(element: Any?): UInt = element as? UInt ?: reificationException()
    // endregion
    
    // region Order
    override fun UInt.compareWith(other: UInt): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
}

public val UInt.Companion.context: UIntContext get() = UIntContext
public fun KoneContextRegistryBuilder.installUIntContext() {
    val uIntSuppliedType = SuppliedType.Regular<UInt>(
        kClass = UInt::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Equality.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Order.Key(uIntSuppliedType)] = UIntContext
    contextsBuilder[Hashing.Key(uIntSuppliedType)] = UIntContext
}

// TODO: Make it a semiring
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object ULongContext: Reification<ULong>, Equality<ULong>, Order<ULong>, Hashing<ULong> {
    // region Reification
    override fun contains(element: Any?): Boolean = element is ULong
    override fun reifyMaybe(element: Any?): Maybe<ULong> = if (element is ULong) Some(element) else None
    override fun reifyOrNull(element: Any?): ULong? = element as? ULong
    override fun reify(element: Any?): ULong = element as? ULong ?: reificationException()
    // endregion
    
    // region Order
    override fun ULong.compareWith(other: ULong): ComparisonResult = this.compareTo(other).asComparisonResult()
    // endregion
}

public val ULong.Companion.context: ULongContext get() = ULongContext
public fun KoneContextRegistryBuilder.installULongContext() {
    val uLongSuppliedType = SuppliedType.Regular<ULong>(
        kClass = ULong::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Equality.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Order.Key(uLongSuppliedType)] = ULongContext
    contextsBuilder[Hashing.Key(uLongSuppliedType)] = ULongContext
}

/**
 * Default field for [Double] type which values are seen as real numbers and where precision problems and occurrences of
 * [NaN][Double.NaN], [+INF][Double.POSITIVE_INFINITY], and [-INF][Double.NEGATIVE_INFINITY] are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such field is useless when used as is, but useful when used in generalized algorithms.
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
    override fun Double.isZero(): Boolean = this == 0.0 || this == -0.0
    // endregion

    // region Conversion
    override fun valueOf(arg: Int): Double = arg.toDouble()
    override fun valueOf(arg: UInt): Double = arg.toDouble()
    override fun valueOf(arg: Long): Double = arg.toDouble()
    override fun valueOf(arg: ULong): Double = arg.toDouble()
    // endregion

    // region Double-Int operations
    override operator fun Double.plus(other: Int): Double = this + other
    override operator fun Double.minus(other: Int): Double = this - other
    override operator fun Double.times(other: Int): Double = this * other
    override operator fun Double.div(other: Int): Double = this / other
    // endregion
    
    // region Double-UInt operations
    override operator fun Double.plus(other: UInt): Double = this + other.toDouble()
    override operator fun Double.minus(other: UInt): Double = this - other.toDouble()
    override operator fun Double.times(other: UInt): Double = this * other.toDouble()
    override operator fun Double.div(other: UInt): Double = this / other.toDouble()
    // endregion

    // region Double-Long operations
    override operator fun Double.plus(other: Long): Double = this + other
    override operator fun Double.minus(other: Long): Double = this - other
    override operator fun Double.times(other: Long): Double = this * other
    override operator fun Double.div(other: Long): Double = this / other
    // endregion
    
    // region Double-ULong operations
    override operator fun Double.plus(other: ULong): Double = this + other.toDouble()
    override operator fun Double.minus(other: ULong): Double = this - other.toDouble()
    override operator fun Double.times(other: ULong): Double = this * other.toDouble()
    override operator fun Double.div(other: ULong): Double = this / other.toDouble()
    // endregion

    // region Int-Double operations
    override operator fun Int.plus(other: Double): Double = this + other
    override operator fun Int.minus(other: Double): Double = this - other
    override operator fun Int.times(other: Double): Double = this * other
    override operator fun Int.div(other: Double): Double = this / other
    // endregion
    
    // region UInt-Double operations
    override operator fun UInt.plus(other: Double): Double = this.toDouble() + other
    override operator fun UInt.minus(other: Double): Double = this.toDouble() - other
    override operator fun UInt.times(other: Double): Double = this.toDouble() * other
    override operator fun UInt.div(other: Double): Double = this.toDouble() / other
    // endregion

    // region Long-Double operations
    override operator fun Long.plus(other: Double): Double = this + other
    override operator fun Long.minus(other: Double): Double = this - other
    override operator fun Long.times(other: Double): Double = this * other
    override operator fun Long.div(other: Double): Double = this / other
    // endregion
    
    // region ULong-Double operations
    override operator fun ULong.plus(other: Double): Double = this.toDouble() + other
    override operator fun ULong.minus(other: Double): Double = this.toDouble() - other
    override operator fun ULong.times(other: Double): Double = this.toDouble() * other
    override operator fun ULong.div(other: Double): Double = this.toDouble() / other
    // endregion

    // region Double-Double operations
    override operator fun Double.unaryMinus(): Double = -this
    override operator fun Double.plus(other: Double): Double = this + other
    override operator fun Double.minus(other: Double): Double = this - other
    override operator fun Double.times(other: Double): Double = this * other
    override operator fun Double.div(other: Double): Double = this / other
    override fun power(base: Double, exponent: UInt): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: ULong): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: Int): Double = base.kpow(exponent)
    override fun power(base: Double, exponent: Long): Double =
        if (exponent >= 0) base.kpow(exponent.toDouble())
        else 1/base.kpow(-exponent.toDouble())
    // endregion
}

/**
 * Default field of the [Double] type. See [DoubleContext] for more.
 */
public val Double.Companion.context: DoubleContext get() = DoubleContext
public fun KoneContextRegistryBuilder.installDoubleContext() {
    val doubleSuppliedType = SuppliedType.Regular<Double>(
        kClass = Double::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(doubleSuppliedType)] = DoubleContext
    contextsBuilder[Equality.Key(doubleSuppliedType)] = DoubleContext
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
 * Such field is useless when used as is, but useful when used in generalized algorithms.
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

    // region Conversion
    override fun valueOf(arg: Int): Float = arg.toFloat()
    override fun valueOf(arg: UInt): Float = arg.toFloat()
    override fun valueOf(arg: Long): Float = arg.toFloat()
    override fun valueOf(arg: ULong): Float = arg.toFloat()
    // endregion

    // region Float-Int operations
    override operator fun Float.plus(other: Int): Float = this + other
    override operator fun Float.minus(other: Int): Float = this - other
    override operator fun Float.times(other: Int): Float = this * other
    override operator fun Float.div(other: Int): Float = this / other
    // endregion
    
    // region Float-UInt operations
    override operator fun Float.plus(other: UInt): Float = this + other.toFloat()
    override operator fun Float.minus(other: UInt): Float = this - other.toFloat()
    override operator fun Float.times(other: UInt): Float = this * other.toFloat()
    override operator fun Float.div(other: UInt): Float = this / other.toFloat()
    // endregion

    // region Float-Long operations
    override operator fun Float.plus(other: Long): Float = this + other
    override operator fun Float.minus(other: Long): Float = this - other
    override operator fun Float.times(other: Long): Float = this * other
    override operator fun Float.div(other: Long): Float = this / other
    // endregion
    
    // region Float-ULong operations
    override operator fun Float.plus(other: ULong): Float = this + other.toFloat()
    override operator fun Float.minus(other: ULong): Float = this - other.toFloat()
    override operator fun Float.times(other: ULong): Float = this * other.toFloat()
    override operator fun Float.div(other: ULong): Float = this / other.toFloat()
    // endregion

    // region Int-Float operations
    override operator fun Int.plus(other: Float): Float = this + other
    override operator fun Int.minus(other: Float): Float = this - other
    override operator fun Int.times(other: Float): Float = this * other
    override operator fun Int.div(other: Float): Float = this / other
    // endregion
    
    // region UInt-Float operations
    override operator fun UInt.plus(other: Float): Float = this.toFloat() + other
    override operator fun UInt.minus(other: Float): Float = this.toFloat() - other
    override operator fun UInt.times(other: Float): Float = this.toFloat() * other
    override operator fun UInt.div(other: Float): Float = this.toFloat() / other
    // endregion

    // region Long-Float operations
    override operator fun Long.plus(other: Float): Float = this + other
    override operator fun Long.minus(other: Float): Float = this - other
    override operator fun Long.times(other: Float): Float = this * other
    override operator fun Long.div(other: Float): Float = this / other
    // endregion
    
    // region ULong-Float operations
    override operator fun ULong.plus(other: Float): Float = this.toFloat() + other
    override operator fun ULong.minus(other: Float): Float = this.toFloat() - other
    override operator fun ULong.times(other: Float): Float = this.toFloat() * other
    override operator fun ULong.div(other: Float): Float = this.toFloat() / other
    // endregion

    // region Float-Float operations
    override operator fun Float.unaryMinus(): Float = -this
    override operator fun Float.plus(other: Float): Float = this + other
    override operator fun Float.minus(other: Float): Float = this - other
    override operator fun Float.times(other: Float): Float = this * other
    override operator fun Float.div(other: Float): Float = this / other
    override fun power(base: Float, exponent: UInt): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: ULong): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: Int): Float = base.kpow(exponent)
    override fun power(base: Float, exponent: Long): Float =
        if (exponent >= 0) base.kpow(exponent.toFloat())
        else 1 / base.kpow(-exponent.toFloat())
    // endregion
}

/**
 * Default field of the [Float] type. See [FloatContext] for more.
 */
public val Float.Companion.context: FloatContext get() = FloatContext
public fun KoneContextRegistryBuilder.installFloatContext() {
    val floatSuppliedType = SuppliedType.Regular<Float>(
        kClass = Float::class,
        typeArguments = emptyList(),
        isNullable = false,
    )
    contextsBuilder[Reification.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Equality.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Ring.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Field.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Order.Key(floatSuppliedType)] = FloatContext
    contextsBuilder[Hashing.Key(floatSuppliedType)] = FloatContext
}