/*
 * Copyright © 2022 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic

import com.lounres.kone.algebraic.util.rightAddMultipliedByDoubling
import com.lounres.kone.algebraic.util.rightMultiplyByDoubling


public interface Ring<V>: AlgebraicContext {

    // region Constants
    public val zero: V
    public val one: V
    // endregion

    // region Equality
    public infix fun V.equalsTo(other: V): Boolean = this == other
    public infix fun V.eq(other: V): Boolean = this equalsTo other
    public fun V.isZero(): Boolean = this equalsTo zero
    public fun V.isOne(): Boolean = this equalsTo one
    public fun V.isNotZero(): Boolean = !isZero()
    public fun V.isNotOne(): Boolean = !isOne()
    // endregion

    // region Integers conversion
    public fun valueOf(arg: Int): V = rightMultiplyByDoubling(one, arg, { zero }, { left, right -> left + right }, { c -> -c })
    public fun valueOf(arg: Long): V = rightMultiplyByDoubling(one, arg, { zero }, { left, right -> left + right }, { c -> -c })
    public val Int.value: V get() = valueOf(this)
    public val Long.value: V get() = valueOf(this)
    // endregion

    // region Value-Int operations
    public operator fun V.plus(other: Int): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
    public operator fun V.minus(other: Int): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
    public operator fun V.times(other: Int): V = rightMultiplyByDoubling(this, other, { zero }, { left, right -> left + right }, { c -> -c })
    // endregion

    // region Value-Long operations
    public operator fun V.plus(other: Long): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
    public operator fun V.minus(other: Long): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
    public operator fun V.times(other: Long): V = rightMultiplyByDoubling(this, other, { zero }, { left, right -> left + right }, { c -> -c })
    // endregion

    // region Int-Value operations
    public operator fun Int.plus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
    public operator fun Int.minus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
    public operator fun Int.times(other: V): V = rightMultiplyByDoubling(other, this, { zero }, { left, right -> left + right }, { c -> -c })
    // endregion

    // region Long-Value operations
    public operator fun Long.plus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
    public operator fun Long.minus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
    public operator fun Long.times(other: V): V = rightMultiplyByDoubling(other, this, { zero }, { left, right -> left + right }, { c -> -c })
    // endregion

    // region Value-Value operations
    public operator fun V.unaryPlus(): V = this
    public operator fun V.unaryMinus(): V
    public operator fun V.plus(other: V): V
    public operator fun V.minus(other: V): V
    public operator fun V.times(other: V): V
    public fun power(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, { one }, { left, right -> left * right })
    public fun power(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, { one }, { left, right -> left * right })
    public infix fun V.pow(exponent: UInt): V = power(this, exponent)
    public infix fun V.pow(exponent: ULong): V = power(this, exponent)
    // endregion
}