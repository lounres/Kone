/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import kotlin.jvm.JvmName


// region Number-Int additive operations
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: Int): N = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: Int): N = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: Int): N = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Number-UInt additive operations
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: UInt): N = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: UInt): N = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: UInt): N = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }
// endregion

// region Number-Long additive operations
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: Long): N = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: Long): N = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: Long): N = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Number-ULong additive operations
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: ULong): N = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: ULong): N = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: ULong): N = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }
// endregion

// region Int-Number additive operations
context(Ring<N>)
public infix fun <N> Int.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> Int.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> Int.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Number additive operations
context(Ring<N>)
public infix fun <N> UInt.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> UInt.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> UInt.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }
// endregion

// region Long-Number additive operations
context(Ring<N>)
public infix fun <N> Long.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> Long.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<N>)
public infix fun <N> Long.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Number additive operations
context(Ring<N>)
public infix fun <N> ULong.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> ULong.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
context(Ring<N>)
public infix fun <N> ULong.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }
// endregion

//region Multiplicative operations
context(Field<N>)
@JvmName("squaringPowerReceiver")
public infix fun <N> N.squaringPower(exponent: Int): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right }, { v -> v.reciprocal })
context(Ring<N>)
@JvmName("squaringPowerUReceiver")
public infix fun <N> N.squaringPower(exponent: UInt): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right })
context(Field<N>)
@JvmName("squaringPowerReceiver")
public infix fun <N> N.squaringPower(exponent: Long): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right }, { v -> v.reciprocal })
context(Ring<N>)
@JvmName("squaringPowerUReceiver")
public infix fun <N> N.squaringPower(exponent: ULong): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right })
//endregion