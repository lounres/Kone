/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic


public interface Field<V>: Ring<V> {
    public operator fun V.div(other: V): V
    public val V.reciprocal: V get() = one / this
    public operator fun V.div(other: Int): V = this / other.value
    public operator fun V.div(other: Long): V = this / other.value
    public operator fun Int.div(other: V): V = this.value / other
    public operator fun Long.div(other: V): V = this.value / other
    public fun power(base: V, exponent: Int): V =
        if (exponent >= 0) power(base, exponent.toUInt())
        else one / power(base, (-exponent).toUInt())
    public fun power(base: V, exponent: Long): V =
        if (exponent >= 0) power(base, exponent.toULong())
        else one / power(base, (-exponent).toULong())
    public infix fun V.pow(exponent: Int): V = power(this, exponent)
    public infix fun V.pow(exponent: Long): V = power(this, exponent)
}