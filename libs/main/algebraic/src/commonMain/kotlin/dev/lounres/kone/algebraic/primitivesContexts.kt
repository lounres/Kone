/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.comparison.Order
import kotlin.math.pow as kpow


@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
public data object ByteField: Ring<Byte>, Order<Byte> {
    // region Order
    override fun Byte.compareTo(other: Byte): Int = this.compareTo(other)
    // endregion

    // region Constants
    override inline val zero: Byte get() = 0
    override inline val one: Byte get() = 1
    // endregion

    // region Conversion
    override inline fun valueOf(arg: Int): Byte = arg.toByte()
    override inline fun valueOf(arg: Long): Byte = arg.toByte()
    // endregion

    // region Byte-Int operations
    public override operator fun Byte.plus(other: Int): Byte = (this + other).toByte()
    public override operator fun Byte.minus(other: Int): Byte = (this - other).toByte()
    public override operator fun Byte.times(other: Int): Byte = (this * other).toByte()
    // endregion

    // region Byte-Long operations
    public override operator fun Byte.plus(other: Long): Byte = (this + other).toByte()
    public override operator fun Byte.minus(other: Long): Byte = (this - other).toByte()
    public override operator fun Byte.times(other: Long): Byte = (this * other).toByte()
    // endregion

    // region Int-Byte operations
    public override operator fun Int.plus(other: Byte): Byte = (this + other).toByte()
    public override operator fun Int.minus(other: Byte): Byte = (this - other).toByte()
    public override operator fun Int.times(other: Byte): Byte = (this * other).toByte()
    // endregion

    // region Long-Byte operations
    public override operator fun Long.plus(other: Byte): Byte = (this + other).toByte()
    public override operator fun Long.minus(other: Byte): Byte = (this - other).toByte()
    public override operator fun Long.times(other: Byte): Byte = (this * other).toByte()
    // endregion

    // region Byte-Byte operations
    override inline operator fun Byte.unaryMinus(): Byte = (-this).toByte()
    override inline operator fun Byte.plus(other: Byte): Byte = (this + other).toByte()
    override inline operator fun Byte.minus(other: Byte): Byte = (this - other).toByte()
    override inline operator fun Byte.times(other: Byte): Byte = (this * other).toByte()
    // endregion
}

public val Byte.Companion.field: ByteField get() = ByteField

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
public data object ShortField: Ring<Short>, Order<Short> {
    // region Order
    override fun Short.compareTo(other: Short): Int = this.compareTo(other)
    // endregion

    // region Constants
    override inline val zero: Short get() = 0
    override inline val one: Short get() = 1
    // endregion

    // region Conversion
    override inline fun valueOf(arg: Int): Short = arg.toShort()
    override inline fun valueOf(arg: Long): Short = arg.toShort()
    // endregion

    // region Short-Int operations
    public override operator fun Short.plus(other: Int): Short = (this + other).toShort()
    public override operator fun Short.minus(other: Int): Short = (this - other).toShort()
    public override operator fun Short.times(other: Int): Short = (this * other).toShort()
    // endregion

    // region Short-Long operations
    public override operator fun Short.plus(other: Long): Short = (this + other).toShort()
    public override operator fun Short.minus(other: Long): Short = (this - other).toShort()
    public override operator fun Short.times(other: Long): Short = (this * other).toShort()
    // endregion

    // region Int-Short operations
    public override operator fun Int.plus(other: Short): Short = (this + other).toShort()
    public override operator fun Int.minus(other: Short): Short = (this - other).toShort()
    public override operator fun Int.times(other: Short): Short = (this * other).toShort()
    // endregion

    // region Long-Short operations
    public override operator fun Long.plus(other: Short): Short = (this + other).toShort()
    public override operator fun Long.minus(other: Short): Short = (this - other).toShort()
    public override operator fun Long.times(other: Short): Short = (this * other).toShort()
    // endregion

    // region Short-Short operations
    override inline operator fun Short.unaryMinus(): Short = (-this).toShort()
    override inline operator fun Short.plus(other: Short): Short = (this + other).toShort()
    override inline operator fun Short.minus(other: Short): Short = (this - other).toShort()
    override inline operator fun Short.times(other: Short): Short = (this * other).toShort()
    // endregion
}

public val Short.Companion.field: ShortField get() = ShortField

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public data object IntRing: Ring<Int>, Order<Int> {
    // region Order
    override fun Int.compareTo(other: Int): Int = this.compareTo(other)
    // endregion

    // region Constants
    override inline val zero: Int get() = 0
    override inline val one: Int get() = 1
    // endregion

    // region Conversion
    override inline fun valueOf(arg: Int): Int = arg
    override inline fun valueOf(arg: Long): Int = arg.toInt()
    // endregion

    // region Int-Int operations
    override inline operator fun Int.unaryMinus(): Int = -this
    override inline operator fun Int.plus(other: Int): Int = this + other
    override inline operator fun Int.minus(other: Int): Int = this - other
    override inline operator fun Int.times(other: Int): Int = this * other
    // endregion

    // region Int-Long operations
    override operator fun Int.plus(other: Long): Int = this + other.toInt()
    override operator fun Int.minus(other: Long): Int = this - other.toInt()
    override operator fun Int.times(other: Long): Int = this * other.toInt()
    // endregion

    // region Long-Int operations
    override operator fun Long.plus(other: Int): Int = this.toInt() + other
    override operator fun Long.minus(other: Int): Int = this.toInt() - other
    override operator fun Long.times(other: Int): Int = this.toInt() * other
    // endregion
}

public val Int.Companion.ring: IntRing get() = IntRing

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public data object LongRing: Ring<Long>, Order<Long> {
    // region Order
    override fun Long.compareTo(other: Long): Int = this.compareTo(other)
    // endregion

    // region Constants
    override inline val zero: Long get() = 0L
    override inline val one: Long get() = 1L
    // endregion

    // region Conversion
    override inline fun valueOf(arg: Int): Long = arg.toLong()
    override inline fun valueOf(arg: Long): Long = arg
    // endregion

    // region Long-Long operations
    override inline operator fun Long.unaryMinus(): Long = -this
    override inline operator fun Long.plus(other: Long): Long = this + other
    override inline operator fun Long.minus(other: Long): Long = this - other
    override inline operator fun Long.times(other: Long): Long = this * other
    // endregion

    // region Long-Int operations
    override inline operator fun Long.plus(other: Int): Long = this + other
    override inline operator fun Long.minus(other: Int): Long = this - other
    override inline operator fun Long.times(other: Int): Long = this * other
    // endregion

    // region Int-Long operations
    override inline operator fun Int.plus(other: Long): Long = this + other
    override inline operator fun Int.minus(other: Long): Long = this - other
    override inline operator fun Int.times(other: Long): Long = this * other
    // endregion
}

public val Long.Companion.ring: LongRing get() = LongRing

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
public data object DoubleField: Field<Double>, Order<Double> {
    // region Order
    override fun Double.compareTo(other: Double): Int = this.compareTo(other)
    // endregion

    // region Constants
    override inline val zero: Double get() = 0.0
    override inline val one: Double get() = 1.0
    // endregion

    // region Conversion
    override inline fun valueOf(arg: Int): Double = arg.toDouble()
    override inline fun valueOf(arg: Long): Double = arg.toDouble()
    // endregion

    // region Double-Int operations
    public override operator fun Double.plus(other: Int): Double = this + other
    public override operator fun Double.minus(other: Int): Double = this - other
    public override operator fun Double.times(other: Int): Double = this * other
    override inline operator fun Double.div(other: Int): Double = this / other
    // endregion

    // region Double-Long operations
    public override operator fun Double.plus(other: Long): Double = this + other
    public override operator fun Double.minus(other: Long): Double = this - other
    public override operator fun Double.times(other: Long): Double = this * other
    override inline operator fun Double.div(other: Long): Double = this / other
    // endregion

    // region Int-Double operations
    public override operator fun Int.plus(other: Double): Double = this + other
    public override operator fun Int.minus(other: Double): Double = this - other
    public override operator fun Int.times(other: Double): Double = this * other
    override inline operator fun Int.div(other: Double): Double = this / other
    // endregion

    // region Long-Double operations
    public override operator fun Long.plus(other: Double): Double = this + other
    public override operator fun Long.minus(other: Double): Double = this - other
    public override operator fun Long.times(other: Double): Double = this * other
    override inline operator fun Long.div(other: Double): Double = this / other
    // endregion
    
    // region Double-Double operations
    override inline operator fun Double.unaryMinus(): Double = -this
    override inline operator fun Double.plus(other: Double): Double = this + other
    override inline operator fun Double.minus(other: Double): Double = this - other
    override inline operator fun Double.times(other: Double): Double = this * other
    override inline operator fun Double.div(other: Double): Double = this / other
    override fun power(base: Double, exponent: UInt): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: ULong): Double = base.kpow(exponent.toDouble())
    override fun power(base: Double, exponent: Int): Double = base.kpow(exponent)
    override fun power(base: Double, exponent: Long): Double =
        if (exponent >= 0) base.kpow(exponent.toDouble())
        else 1/base.kpow(-exponent.toDouble())
    // endregion
}

public val Double.Companion.field: DoubleField get() = DoubleField

@Suppress("OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE", "EXTENSION_SHADOWED_BY_MEMBER")
public data object FloatField: Field<Float>, Order<Float> {
    // region Order
    override fun Float.compareTo(other: Float): Int = this.compareTo(other)
    // endregion

    // region Constants
    override inline val zero: Float get() = 0f
    override inline val one: Float get() = 1f
    // endregion

    // region Conversion
    override inline fun valueOf(arg: Int): Float = arg.toFloat()
    override inline fun valueOf(arg: Long): Float = arg.toFloat()
    // endregion

    // region Float-Int operations
    public override operator fun Float.plus(other: Int): Float = this + other
    public override operator fun Float.minus(other: Int): Float = this - other
    public override operator fun Float.times(other: Int): Float = this * other
    override inline operator fun Float.div(other: Int): Float = this / other
    // endregion

    // region Float-Long operations
    public override operator fun Float.plus(other: Long): Float = this + other
    public override operator fun Float.minus(other: Long): Float = this - other
    public override operator fun Float.times(other: Long): Float = this * other
    override inline operator fun Float.div(other: Long): Float = this / other
    // endregion

    // region Int-Float operations
    public override operator fun Int.plus(other: Float): Float = this + other
    public override operator fun Int.minus(other: Float): Float = this - other
    public override operator fun Int.times(other: Float): Float = this * other
    override inline operator fun Int.div(other: Float): Float = this / other
    // endregion

    // region Long-Float operations
    public override operator fun Long.plus(other: Float): Float = this + other
    public override operator fun Long.minus(other: Float): Float = this - other
    public override operator fun Long.times(other: Float): Float = this * other
    override inline operator fun Long.div(other: Float): Float = this / other
    // endregion

    // region Float-Float operations
    override inline operator fun Float.unaryMinus(): Float = -this
    override inline operator fun Float.plus(other: Float): Float = this + other
    override inline operator fun Float.minus(other: Float): Float = this - other
    override inline operator fun Float.times(other: Float): Float = this * other
    override inline operator fun Float.div(other: Float): Float = this / other
    override fun power(base: Float, exponent: UInt): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: ULong): Float = base.kpow(exponent.toFloat())
    override fun power(base: Float, exponent: Int): Float = base.kpow(exponent)
    override fun power(base: Float, exponent: Long): Float =
        if (exponent >= 0) base.kpow(exponent.toFloat())
        else 1/base.kpow(-exponent.toFloat())
    // endregion
}

public val Float.Companion.field: FloatField get() = FloatField