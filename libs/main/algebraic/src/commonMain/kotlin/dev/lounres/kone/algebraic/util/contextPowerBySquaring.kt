/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import dev.lounres.kone.algebraic.Ring


// region Value-Int additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> V.doublingPlus(other: Int): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> V.doublingMinus(other: Int): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> V.doublingTimes(other: Int): V = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })

public fun <V> Ring<V>.doublingPlus(first: V, other: Int): V = rightAddMultipliedByDoubling(first, one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(first: V, other: Int): V = rightAddMultipliedByDoubling(first, -one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(first: V, other: Int): V = rightMultiplyByDoubling(first, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-UInt additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> V.doublingPlus(other: UInt): V = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> V.doublingMinus(other: UInt): V = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> V.doublingTimes(other: UInt): V = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }

public fun <V> Ring<V>.doublingPlus(first: V, other: UInt): V = rightAddMultipliedByDoubling(first, one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(first: V, other: UInt): V = rightAddMultipliedByDoubling(first, -one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(first: V, other: UInt): V = rightMultiplyByDoubling(first, other, ::zero) { left, right -> left + right }
// endregion

// region Value-Long additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> V.doublingPlus(other: Long): V = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> V.doublingMinus(other: Long): V = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> V.doublingTimes(other: Long): V = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })

public fun <V> Ring<V>.doublingPlus(first: V, other: Long): V = rightAddMultipliedByDoubling(first, one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(first: V, other: Long): V = rightAddMultipliedByDoubling(first, -one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(first: V, other: Long): V = rightMultiplyByDoubling(first, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-ULong additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> V.doublingPlus(other: ULong): V = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> V.doublingMinus(other: ULong): V = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> V.doublingTimes(other: ULong): V = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }

public fun <V> Ring<V>.doublingPlus(first: V, other: ULong): V = rightAddMultipliedByDoubling(first, one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(first: V, other: ULong): V = rightAddMultipliedByDoubling(first, -one, other) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(first: V, other: ULong): V = rightMultiplyByDoubling(first, other, ::zero) { left, right -> left + right }
// endregion

// region Int-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> Int.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> Int.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> Int.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })

public fun <V> Ring<V>.doublingPlus(first: Int, other: V): V = rightAddMultipliedByDoubling(other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(first: Int, other: V): V = rightAddMultipliedByDoubling(-other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(first: Int, other: V): V = rightMultiplyByDoubling(other, first, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> UInt.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> UInt.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> UInt.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }

public fun <V> Ring<V>.doublingPlus(first: UInt, other: V): V = rightAddMultipliedByDoubling(other, one, first) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(first: UInt, other: V): V = rightAddMultipliedByDoubling(-other, one, first) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(first: UInt, other: V): V = rightMultiplyByDoubling(other, first, ::zero) { left, right -> left + right }
// endregion

// region Long-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> Long.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> Long.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<V>)
//public fun <V> Long.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })

public fun <V> Ring<V>.doublingPlus(first: Long, other: V): V = rightAddMultipliedByDoubling(other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingMinus(first: Long, other: V): V = rightAddMultipliedByDoubling(-other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <V> Ring<V>.doublingTimes(first: Long, other: V): V = rightMultiplyByDoubling(other, first, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> ULong.doublingPlus(other: V): V = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> ULong.doublingMinus(other: V): V = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
//context(Ring<V>)
//public fun <V> ULong.doublingTimes(other: V): V = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }

public fun <V> Ring<V>.doublingPlus(first: ULong, other: V): V = rightAddMultipliedByDoubling(other, one, first) { left, right -> left + right }
public fun <V> Ring<V>.doublingMinus(first: ULong, other: V): V = rightAddMultipliedByDoubling(-other, one, first) { left, right -> left + right }
public fun <V> Ring<V>.doublingTimes(first: ULong, other: V): V = rightMultiplyByDoubling(other, first, ::zero) { left, right -> left + right }
// endregion

//region Multiplicative operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<V>)
//public fun <V> squaringPower(base: V, exponent: Int): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
//context(Ring<V>)
//public fun <V> squaringPower(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
//context(Ring<V>)
//public fun <V> squaringPower(base: V, exponent: Long): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
//context(Ring<V>)
//public fun <V> squaringPower(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })

public fun <V> Ring<V>.squaringPower(base: V, exponent: Int): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public fun <V> Ring<V>.squaringPower(base: V, exponent: UInt): V = rightMultiplyByDoubling(base, exponent, ::one) { left, right -> left * right }
public fun <V> Ring<V>.squaringPower(base: V, exponent: Long): V = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public fun <V> Ring<V>.squaringPower(base: V, exponent: ULong): V = rightMultiplyByDoubling(base, exponent, ::one) { left, right -> left * right }
//endregion