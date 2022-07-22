/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic


@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE", "NOTHING_TO_INLINE")
public object IntRing: Ring<Int> {
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