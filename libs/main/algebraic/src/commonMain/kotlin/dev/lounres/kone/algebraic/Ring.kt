/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingMinus
import dev.lounres.kone.algebraic.util.doublingPlus
import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.context.KoneContext


// TODO: KONE-42
// TODO: Extract equality to separate interface
public interface Ring<N>: KoneContext {

    // region Constants
    public val zero: N
    public val one: N
    // endregion

    // region Equality
    public infix fun N.equalsTo(other: N): Boolean = this == other
    // FIXME: KT-5351
    public infix fun N.notEqualsTo(other: N): Boolean = !(this equalsTo other)
    public infix fun N.eq(other: N): Boolean = this equalsTo other
    // FIXME: KT-5351
    public infix fun N.neq(other: N): Boolean = !(this equalsTo other)
    public fun N.isZero(): Boolean = this equalsTo zero
    public fun N.isOne(): Boolean = this equalsTo one
    // FIXME: KT-5351
    public fun N.isNotZero(): Boolean = !isZero()
    // FIXME: KT-5351
    public fun N.isNotOne(): Boolean = !isOne()
    // endregion

    // region Integers conversion
    public fun valueOf(arg: Int): N = doublingTimes(one, arg)
//    public fun valueOf(arg: UInt): N = doublingTimes(one, arg)
    public fun valueOf(arg: Long): N = doublingTimes(one, arg)
//    public fun valueOf(arg: ULong): N = doublingTimes(one, arg)
    public val Int.value: N get() = valueOf(this)
//    public val UInt.value: N get() = valueOf(this)
    public val Long.value: N get() = valueOf(this)
//    public val ULong.value: N get() = valueOf(this)
    // endregion

    // region Number-Int operations
    public operator fun N.plus(other: Int): N = doublingPlus(this, other)
    public operator fun N.minus(other: Int): N = doublingMinus(this, other)
    public operator fun N.times(other: Int): N = doublingTimes(this, other)
    // endregion

//    // region Number-UInt operations
//    public operator fun N.plus(other: UInt): N = doublingPlus(this, other)
//    public operator fun N.minus(other: UInt): N = doublingMinus(this, other)
//    public operator fun N.times(other: UInt): N = doublingTimes(this, other)
//    // endregion

    // region Number-Long operations
    public operator fun N.plus(other: Long): N = doublingPlus(this, other)
    public operator fun N.minus(other: Long): N = doublingMinus(this, other)
    public operator fun N.times(other: Long): N = doublingTimes(this, other)
    // endregion

//    // region Number-ULong operations
//    public operator fun N.plus(other: ULong): N = doublingPlus(this, other)
//    public operator fun N.minus(other: ULong): N = doublingMinus(this, other)
//    public operator fun N.times(other: ULong): N = doublingTimes(this, other)
//    // endregion

    // region Int-Number operations
    public operator fun Int.plus(other: N): N = doublingPlus(this, other)
    public operator fun Int.minus(other: N): N = doublingMinus(this, other)
    public operator fun Int.times(other: N): N = doublingTimes(this, other)
    // endregion

//    // region UInt-Number operations
//    public operator fun UInt.plus(other: N): N = doublingPlus(this, other)
//    public operator fun UInt.minus(other: N): N = doublingMinus(this, other)
//    public operator fun UInt.times(other: N): N = doublingTimes(this, other)
//    // endregion

    // region Long-Number operations
    public operator fun Long.plus(other: N): N = doublingPlus(this, other)
    public operator fun Long.minus(other: N): N = doublingMinus(this, other)
    public operator fun Long.times(other: N): N = doublingTimes(this, other)
    // endregion

//    // region ULong-Number operations
//    public operator fun ULong.plus(other: N): N = doublingPlus(this, other)
//    public operator fun ULong.minus(other: N): N = doublingMinus(this, other)
//    public operator fun ULong.times(other: N): N = doublingTimes(this, other)
//    // endregion

    // region Number-Number operations
    public operator fun N.unaryPlus(): N = this
    public operator fun N.unaryMinus(): N
    public operator fun N.plus(other: N): N
    public operator fun N.minus(other: N): N
    public operator fun N.times(other: N): N
    public fun power(base: N, exponent: UInt): N = squaringPower(base, exponent)
    public fun power(base: N, exponent: ULong): N = squaringPower(base, exponent)
    public infix fun N.pow(exponent: UInt): N = power(this, exponent)
    public infix fun N.pow(exponent: ULong): N = power(this, exponent)
    // endregion
}