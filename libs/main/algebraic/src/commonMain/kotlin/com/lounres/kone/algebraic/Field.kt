/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic


public interface Field<V>: Ring<V> {
    public val V.reciprocal: V get() = one / this
    public operator fun V.div(other: Int): V = this / (one * other)
    public operator fun V.div(other: Long): V = this / (one * other)
    public operator fun Int.div(other: V): V = (one * this) / other
    public operator fun Long.div(other: V): V = (one * this) / other
    public operator fun V.div(other: V): V
    public fun power(base: V, exponent: Int): V =
        if (exponent >= 0) power(base, exponent.toUInt())
        else one / power(base, (-exponent).toUInt())
    public fun power(base: V, exponent: Long): V =
        if (exponent >= 0) power(base, exponent.toULong())
        else one / power(base, (-exponent).toULong())
    public infix fun V.pow(exponent: Int): V = power(this, exponent)
    public infix fun V.pow(exponent: Long): V = power(this, exponent)
}