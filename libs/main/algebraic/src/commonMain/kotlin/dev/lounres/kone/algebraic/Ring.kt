/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingMinus
import dev.lounres.kone.algebraic.util.doublingPlus
import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.algebraic.util.squaringPower
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.KoneContext


// TODO: KONE-42
// TODO: Extract equality to separate interface
public interface Ring<N>: Equality<N> {

    // region Constants
    public val zero: N
    public val one: N
    // endregion

    // region Equality
    public fun N.isZero(): Boolean = this equalsTo zero
    public fun N.isOne(): Boolean = this equalsTo one
    // FIXME: KT-5351
    public fun N.isNotZero(): Boolean = !isZero()
    // FIXME: KT-5351
    public fun N.isNotOne(): Boolean = !isOne()
    // endregion

    // region Integers conversion
    public fun valueOf(arg: Int): N = one doublingTimes arg
//    public fun valueOf(arg: UInt): N = one doublingTimes arg
    public fun valueOf(arg: Long): N = one doublingTimes arg
//    public fun valueOf(arg: ULong): N = one doublingTimes arg
    public val Int.value: N get() = valueOf(this)
//    public val UInt.value: N get() = valueOf(this)
    public val Long.value: N get() = valueOf(this)
//    public val ULong.value: N get() = valueOf(this)
    // endregion

    // region Number-Int operations
    public operator fun N.plus(other: Int): N = this doublingPlus other
    public operator fun N.minus(other: Int): N = this doublingMinus other
    public operator fun N.times(other: Int): N = this doublingTimes other
    // endregion

//    // region Number-UInt operations
//    public operator fun N.plus(other: UInt): N = this doublingPlus other
//    public operator fun N.minus(other: UInt): N = this doublingMinus other
//    public operator fun N.times(other: UInt): N = this doublingTimes other
//    // endregion

    // region Number-Long operations
    public operator fun N.plus(other: Long): N = this doublingPlus other
    public operator fun N.minus(other: Long): N = this doublingMinus other
    public operator fun N.times(other: Long): N = this doublingTimes other
    // endregion

//    // region Number-ULong operations
//    public operator fun N.plus(other: ULong): N = this doublingPlus other
//    public operator fun N.minus(other: ULong): N = this doublingMinus other
//    public operator fun N.times(other: ULong): N = this doublingTimes other
//    // endregion

    // region Int-Number operations
    public operator fun Int.plus(other: N): N = this doublingPlus other
    public operator fun Int.minus(other: N): N = this doublingMinus other
    public operator fun Int.times(other: N): N = this doublingTimes other
    // endregion

//    // region UInt-Number operations
//    public operator fun UInt.plus(other: N): N = this doublingPlus other
//    public operator fun UInt.minus(other: N): N = this doublingMinus other
//    public operator fun UInt.times(other: N): N = this doublingTimes other
//    // endregion

    // region Long-Number operations
    public operator fun Long.plus(other: N): N = this doublingPlus other
    public operator fun Long.minus(other: N): N = this doublingMinus other
    public operator fun Long.times(other: N): N = this doublingTimes other
    // endregion

//    // region ULong-Number operations
//    public operator fun ULong.plus(other: N): N = this doublingPlus other
//    public operator fun ULong.minus(other: N): N = this doublingMinus other
//    public operator fun ULong.times(other: N): N = this doublingTimes other
//    // endregion

    // region Number-Number operations
    public operator fun N.unaryPlus(): N = this
    public operator fun N.unaryMinus(): N
    public operator fun N.plus(other: N): N
    public operator fun N.minus(other: N): N
    public operator fun N.times(other: N): N
    public fun power(base: N, exponent: UInt): N = base squaringPower exponent
    public fun power(base: N, exponent: ULong): N = base squaringPower exponent
    public infix fun N.pow(exponent: UInt): N = power(this, exponent)
    public infix fun N.pow(exponent: ULong): N = power(this, exponent)
    // endregion
}