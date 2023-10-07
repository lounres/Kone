/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.*
import dev.lounres.kone.context.KoneContext


// TODO: KONE-42
public interface Ring<V>: KoneContext {

    // region Constants
    public val zero: V
    public val one: V
    // endregion

    // region Equality
    public infix fun V.equalsTo(other: V): Boolean = this == other
    // FIXME: KT-5351
    public infix fun V.notEqualsTo(other: V): Boolean = !(this equalsTo other)
    public infix fun V.eq(other: V): Boolean = this equalsTo other
    // FIXME: KT-5351
    public infix fun V.neq(other: V): Boolean = !(this equalsTo other)
    public fun V.isZero(): Boolean = this equalsTo zero
    public fun V.isOne(): Boolean = this equalsTo one
    // FIXME: KT-5351
    public fun V.isNotZero(): Boolean = !isZero()
    // FIXME: KT-5351
    public fun V.isNotOne(): Boolean = !isOne()
    // endregion

    // region Integers conversion
    public fun valueOf(arg: Int): V = one doublingTimes arg
//    public fun valueOf(arg: UInt): V = one doublingTimes arg
    public fun valueOf(arg: Long): V = one doublingTimes arg
//    public fun valueOf(arg: ULong): V = one doublingTimes arg
    public val Int.value: V get() = valueOf(this)
//    public val UInt.value: V get() = valueOf(this)
    public val Long.value: V get() = valueOf(this)
//    public val ULong.value: V get() = valueOf(this)
    // endregion

    // region Value-Int operations
    public operator fun V.plus(other: Int): V = this doublingPlus other
    public operator fun V.minus(other: Int): V = this doublingMinus other
    public operator fun V.times(other: Int): V = this doublingTimes other
    // endregion

//    // region Value-UInt operations
//    public operator fun V.plus(other: UInt): V = this doublingPlus other
//    public operator fun V.minus(other: UInt): V = this doublingMinus other
//    public operator fun V.times(other: UInt): V = this doublingTimes other
//    // endregion

    // region Value-Long operations
    public operator fun V.plus(other: Long): V = this doublingPlus other
    public operator fun V.minus(other: Long): V = this doublingMinus other
    public operator fun V.times(other: Long): V = this doublingTimes other
    // endregion

//    // region Value-ULong operations
//    public operator fun V.plus(other: ULong): V = this doublingPlus other
//    public operator fun V.minus(other: ULong): V = this doublingMinus other
//    public operator fun V.times(other: ULong): V = this doublingTimes other
//    // endregion

    // region Int-Value operations
    public operator fun Int.plus(other: V): V = this doublingPlus other
    public operator fun Int.minus(other: V): V = this doublingMinus other
    public operator fun Int.times(other: V): V = this doublingTimes other
    // endregion

//    // region UInt-Value operations
//    public operator fun UInt.plus(other: V): V = this doublingPlus other
//    public operator fun UInt.minus(other: V): V = this doublingMinus other
//    public operator fun UInt.times(other: V): V = this doublingTimes other
//    // endregion

    // region Long-Value operations
    public operator fun Long.plus(other: V): V = this doublingPlus other
    public operator fun Long.minus(other: V): V = this doublingMinus other
    public operator fun Long.times(other: V): V = this doublingTimes other
    // endregion

//    // region ULong-Value operations
//    public operator fun ULong.plus(other: V): V = this doublingPlus other
//    public operator fun ULong.minus(other: V): V = this doublingMinus other
//    public operator fun ULong.times(other: V): V = this doublingTimes other
//    // endregion

    // region Value-Value operations
    public operator fun V.unaryPlus(): V = this
    public operator fun V.unaryMinus(): V
    public operator fun V.plus(other: V): V
    public operator fun V.minus(other: V): V
    public operator fun V.times(other: V): V
    public fun power(base: V, exponent: UInt): V = base squaringPower exponent
    public fun power(base: V, exponent: ULong): V = squaringPower(base, exponent)
    public infix fun V.pow(exponent: UInt): V = power(this, exponent)
    public infix fun V.pow(exponent: ULong): V = power(this, exponent)
    // endregion
}