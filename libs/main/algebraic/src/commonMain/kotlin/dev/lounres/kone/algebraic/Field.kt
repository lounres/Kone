/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic


public interface FieldOperations<N>: RingOperations<N> {
    public operator fun N.div(other: N): N
    public val N.reciprocal: N get() = one / this
    public operator fun N.div(other: Int): N = this / other.value
//    public operator fun N.div(other: UInt): N = this / other.value
    public operator fun N.div(other: Long): N = this / other.value
//    public operator fun N.div(other: ULong): N = this / other.value
    public operator fun Int.div(other: N): N = this.value / other
//    public operator fun UInt.div(other: N): N = this.value / other
    public operator fun Long.div(other: N): N = this.value / other
//    public operator fun ULong.div(other: N): N = this.value / other
    public fun power(base: N, exponent: Int): N =
        if (exponent >= 0) power(base, exponent.toUInt())
        else one / power(base, (-exponent).toUInt())
    public fun power(base: N, exponent: Long): N =
        if (exponent >= 0) power(base, exponent.toULong())
        else one / power(base, (-exponent).toULong())
    public infix fun N.pow(exponent: Int): N = power(this, exponent)
    public infix fun N.pow(exponent: Long): N = power(this, exponent)
}

public interface Field<N>: Ring<N>, FieldOperations<N>