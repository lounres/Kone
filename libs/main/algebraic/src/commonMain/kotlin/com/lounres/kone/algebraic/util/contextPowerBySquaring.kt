/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic.util

import com.lounres.kone.algebraic.Ring
import kotlin.jvm.JvmName


// region Value-Int additive operations
public fun <V> Ring<V>.doublingPlus(that: V, other: Int): V = rightAddMultipliedByDoubling(that, one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(that: V, other: Int): V = rightAddMultipliedByDoubling(that, one, -other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(that: V, other: Int): V = rightMultiplyByDoubling(that, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-UInt additive operations
public fun <V> Ring<V>.doublingPlus(that: V, other: UInt): V = rightAddMultipliedByDoubling(that, one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(that: V, other: UInt): V = rightAddMultipliedByDoubling(that, -one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(that: V, other: UInt): V = rightMultiplyByDoubling(that, other, ::zero) { left, right -> left + right }
// endregion

// region Value-Long additive operations
public fun <V> Ring<V>.doublingPlus(that: V, other: Long): V = rightAddMultipliedByDoubling(that, one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(that: V, other: Long): V = rightAddMultipliedByDoubling(that, one, -other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(that: V, other: Long): V = rightMultiplyByDoubling(that, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-ULong additive operations
public fun <V> Ring<V>.doublingPlus(that: V, other: ULong): V = rightAddMultipliedByDoubling(that, one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(that: V, other: ULong): V = rightAddMultipliedByDoubling(that, -one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(that: V, other: ULong): V = rightMultiplyByDoubling(that, other, ::zero) { left, right -> left + right }
// endregion

// region Int-Value additive operations
public fun <V> Ring<V>.doublingPlus(that: Int, other: V): V = rightAddMultipliedByDoubling(other, one, that, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(that: Int, other: V): V = rightAddMultipliedByDoubling(-other, one, that, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(that: Int, other: V): V = rightMultiplyByDoubling(other, that, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Value additive operations
public fun <V> Ring<V>.doublingPlus(that: UInt, other: V): V = rightAddMultipliedByDoubling(other, one, that) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(that: UInt, other: V): V = rightAddMultipliedByDoubling(-other, one, that) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(that: UInt, other: V): V = rightMultiplyByDoubling(other, that, ::zero) { left, right -> left + right }
// endregion

// region Long-Value additive operations
public fun <V> Ring<V>.doublingPlus(that: Long, other: V): V = rightAddMultipliedByDoubling(other, one, that, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(that: Long, other: V): V = rightAddMultipliedByDoubling(-other, one, that, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(that: Long, other: V): V = rightMultiplyByDoubling(other, that, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Value additive operations
public fun <V> Ring<V>.doublingPlus(that: ULong, other: V): V = rightAddMultipliedByDoubling(other, one, that) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(that: ULong, other: V): V = rightAddMultipliedByDoubling(-other, one, that) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(that: ULong, other: V): V = rightMultiplyByDoubling(other, that, ::zero) { left, right -> left + right }
// endregion

//region Multiplicative operations
public fun <V> Ring<V>.squaringPower(base: V, exponent: Int): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public fun <V> Ring<V>.squaringPower(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
public fun <V> Ring<V>.squaringPower(base: V, exponent: Long): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public fun <V> Ring<V>.squaringPower(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
//endregion