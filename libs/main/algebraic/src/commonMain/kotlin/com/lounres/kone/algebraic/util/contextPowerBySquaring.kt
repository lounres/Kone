/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic.util

import com.lounres.kone.algebraic.Ring
import kotlin.jvm.JvmName


// region Value-Int additive operations
context(Ring<V>)
public infix fun <V> V.doublingPlus(other: Int): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> V.doublingMinus(other: Int): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> V.doublingTimes(other: Int): V = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-UInt additive operations
context(Ring<V>)
public infix fun <V> V.doublingPlus(other: UInt): V = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> V.doublingMinus(other: UInt): V = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> V.doublingTimes(other: UInt): V = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }
// endregion

// region Value-Long additive operations
context(Ring<V>)
public infix fun <V> V.doublingPlus(other: Long): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> V.doublingMinus(other: Long): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> V.doublingTimes(other: Long): V = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-ULong additive operations
context(Ring<V>)
public infix fun <V> V.doublingPlus(other: ULong): V = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> V.doublingMinus(other: ULong): V = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> V.doublingTimes(other: ULong): V = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }
// endregion

// region Int-Value additive operations
context(Ring<V>)
public infix fun <V> Int.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> Int.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> Int.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Value additive operations
context(Ring<V>)
public infix fun <V> UInt.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> UInt.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> UInt.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }
// endregion

// region Long-Value additive operations
context(Ring<V>)
public infix fun <V> Long.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> Long.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
context(Ring<V>)
public infix fun <V> Long.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Value additive operations
context(Ring<V>)
public infix fun <V> ULong.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> ULong.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
context(Ring<V>)
public infix fun <V> ULong.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }
// endregion

//region Multiplicative operations
context(Ring<V>)
public fun <V> squaringPower(base: V, exponent: Int): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
context(Ring<V>)
public fun <V> squaringPower(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
context(Ring<V>)
public fun <V> squaringPower(base: V, exponent: Long): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
context(Ring<V>)
public fun <V> squaringPower(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })

context(Ring<V>)
@JvmName("squaringPowerReceiver")
public inline infix fun <V> V.squaringPower(exponent: Int): V = squaringPower(this, exponent)
context(Ring<V>)
@JvmName("squaringPowerUReceiver")
public inline infix fun <V> V.squaringPower(exponent: UInt): V = squaringPower(this, exponent)
context(Ring<V>)
@JvmName("squaringPowerReceiver")
public inline infix fun <V> V.squaringPower(exponent: Long): V = squaringPower(this, exponent)
context(Ring<V>)
@JvmName("squaringPowerUReceiver")
public inline infix fun <V> V.squaringPower(exponent: ULong): V = squaringPower(this, exponent)
//endregion