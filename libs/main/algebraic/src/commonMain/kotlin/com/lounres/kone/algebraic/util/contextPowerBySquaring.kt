/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic.util

import com.lounres.kone.algebraic.Ring


// region Value-Int additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public inline fun <V> V.doublingPlus(other: Int): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> V.doublingMinus(other: Int): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> V.doublingTimes(other: Int): V = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })

public inline fun <V> Ring<V>.doublingPlus(first: V, other: Int): V = rightAddMultipliedByDoubling(first, one, other, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingMinus(first: V, other: Int): V = rightAddMultipliedByDoubling(first, one, -other, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingTimes(first: V, other: Int): V = rightMultiplyByDoubling(first, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-Long additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public inline fun <V> V.doublingPlus(other: Long): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> V.doublingMinus(other: Long): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> V.doublingTimes(other: Long): V = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })

public inline fun <V> Ring<V>.doublingPlus(first: V, other: Long): V = rightAddMultipliedByDoubling(first, one, other, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingMinus(first: V, other: Long): V = rightAddMultipliedByDoubling(first, one, -other, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingTimes(first: V, other: Long): V = rightMultiplyByDoubling(first, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Int-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public inline fun <V> Int.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> Int.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> Int.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })

public inline fun <V> Ring<V>.doublingPlus(first: Int, other: V): V = rightAddMultipliedByDoubling(other, one, first, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingMinus(first: Int, other: V): V = rightAddMultipliedByDoubling(-other, one, first, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingTimes(first: Int, other: V): V = rightMultiplyByDoubling(other, first, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Long-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public inline fun <V> Long.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> Long.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public inline fun <V> Long.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })

public inline fun <V> Ring<V>.doublingPlus(first: Long, other: V): V = rightAddMultipliedByDoubling(other, one, first, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingMinus(first: Long, other: V): V = rightAddMultipliedByDoubling(-other, one, first, { left, right -> left + right }, { left, right -> left - right })
public inline fun <V> Ring<V>.doublingTimes(first: Long, other: V): V = rightMultiplyByDoubling(other, first, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

//region Multiplicative operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public inline fun <V> squaringPower(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
//context(Ring<V>)
//public inline fun <V> squaringPower(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
//context(Ring<V>)
//public inline fun <V> squaringPower(base: V, exponent: Int): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
//context(Ring<V>)
//public inline fun <V> squaringPower(base: V, exponent: Long): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })

public inline fun <V> Ring<V>.squaringPower(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, ::one) { left, right -> left * right }
public inline fun <V> Ring<V>.squaringPower(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, ::one) { left, right -> left * right }
public inline fun <V> Ring<V>.squaringPower(base: V, exponent: Int): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public inline fun <V> Ring<V>.squaringPower(base: V, exponent: Long): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
//endregion