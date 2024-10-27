/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.comparison.ComparisonResult
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.asComparisonResult
import kotlin.math.pow as kpow


/**
 * Default ring for [Byte] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object ByteRing: EuclideanRing<Byte>, Order<Byte>, Hashing<Byte> {
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
 * Default ring of the [Byte] type. See [ByteRing] for more.
 */
public val Byte.Companion.ring: ByteRing get() = ByteRing

/**
 * Default ring for [Short] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object ShortRing: EuclideanRing<Short>, Order<Short>, Hashing<Short> {
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
 * Default ring of the [Short] type. See [ShortRing] for more.
 */
public val Short.Companion.ring: ShortRing get() = ShortRing

/**
 * Default ring for [Int] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object IntRing: EuclideanRing<Int>, Order<Int>, Hashing<Int> {
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
 * Default ring of the [Int] type. See [IntRing] for more.
 */
public val Int.Companion.ring: IntRing get() = IntRing

/**
 * Default ring for [Long] type which values are seen as integers and where overflows are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such ring is useless when used as is, but useful when used in generalized algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object LongRing: EuclideanRing<Long>, Order<Long>, Hashing<Long> {
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
 * Default ring of the [Long] type. See [LongRing] for more.
 */
public val Long.Companion.ring: LongRing get() = LongRing

/**
 * Default field for [Double] type which values are seen as real numbers and where precision problems and occurrences of
 * [NaN][Double.NaN], [+INF][Double.POSITIVE_INFINITY], and [-INF][Double.NEGATIVE_INFINITY] are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such field is useless when used as is, but useful when used in generalized algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object DoubleField: Field<Double>, Order<Double>, Hashing<Double> {
    // region Order
    override fun Double.compareWith(other: Double): ComparisonResult = this.compareTo(other).asComparisonResult()
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
 * Default field of the [Double] type. See [DoubleField] for more.
 */
public val Double.Companion.field: DoubleField get() = DoubleField

/**
 * Default field for [Float] type which values are seen as real numbers and where precision problems and occurrences of
 * [NaN][Float.NaN], [+INF][Float.POSITIVE_INFINITY], and [-INF][Float.NEGATIVE_INFINITY] are ignored.
 * It also implements [Order] and [Hashing] interfaces in the default understanding.
 *
 * Such field is useless when used as is, but useful when used in generalized algorithms.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public data object FloatField: Field<Float>, Order<Float>, Hashing<Float> {
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
        else 1/base.kpow(-exponent.toFloat())
    // endregion
}

/**
 * Default field of the [Float] type. See [FloatField] for more.
 */
public val Float.Companion.field: FloatField get() = FloatField