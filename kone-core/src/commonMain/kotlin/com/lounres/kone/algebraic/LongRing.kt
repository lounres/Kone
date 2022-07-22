/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic


@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object LongRing: Ring<Long> {
    // region Constants
    override inline val zero: Long get() = 0L
    override inline val one: Long get() = 1
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