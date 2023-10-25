/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import dev.lounres.kone.algebraic.Ring


// region Value-Int additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> N.doublingPlus(other: Int): N = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> N.doublingMinus(other: Int): N = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> N.doublingTimes(other: Int): N = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })

public fun <N> Ring<N>.doublingPlus(first: N, other: Int): N = rightAddMultipliedByDoubling(first, one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingMinus(first: N, other: Int): N = rightAddMultipliedByDoubling(first, -one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingTimes(first: N, other: Int): N = rightMultiplyByDoubling(first, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-UInt additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> N.doublingPlus(other: UInt): N = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> N.doublingMinus(other: UInt): N = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> N.doublingTimes(other: UInt): N = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }

public fun <N> Ring<N>.doublingPlus(first: N, other: UInt): N = rightAddMultipliedByDoubling(first, one, other) { left, right -> left + right }
public fun <N> Ring<N>.doublingMinus(first: N, other: UInt): N = rightAddMultipliedByDoubling(first, -one, other) { left, right -> left + right }
public fun <N> Ring<N>.doublingTimes(first: N, other: UInt): N = rightMultiplyByDoubling(first, other, ::zero) { left, right -> left + right }
// endregion

// region Value-Long additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> N.doublingPlus(other: Long): N = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> N.doublingMinus(other: Long): N = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> N.doublingTimes(other: Long): N = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })

public fun <N> Ring<N>.doublingPlus(first: N, other: Long): N = rightAddMultipliedByDoubling(first, one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingMinus(first: N, other: Long): N = rightAddMultipliedByDoubling(first, -one, other, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingTimes(first: N, other: Long): N = rightMultiplyByDoubling(first, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Value-ULong additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> N.doublingPlus(other: ULong): N = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> N.doublingMinus(other: ULong): N = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> N.doublingTimes(other: ULong): N = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }

public fun <N> Ring<N>.doublingPlus(first: N, other: ULong): N = rightAddMultipliedByDoubling(first, one, other) { left, right -> left + right }
public fun <N> Ring<N>.doublingMinus(first: N, other: ULong): N = rightAddMultipliedByDoubling(first, -one, other) { left, right -> left + right }
public fun <N> Ring<N>.doublingTimes(first: N, other: ULong): N = rightMultiplyByDoubling(first, other, ::zero) { left, right -> left + right }
// endregion

// region Int-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> Int.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> Int.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> Int.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })

public fun <N> Ring<N>.doublingPlus(first: Int, other: N): N = rightAddMultipliedByDoubling(other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingMinus(first: Int, other: N): N = rightAddMultipliedByDoubling(-other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingTimes(first: Int, other: N): N = rightMultiplyByDoubling(other, first, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> UInt.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> UInt.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> UInt.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }

public fun <N> Ring<N>.doublingPlus(first: UInt, other: N): N = rightAddMultipliedByDoubling(other, one, first) { left, right -> left + right }
public fun <N> Ring<N>.doublingMinus(first: UInt, other: N): N = rightAddMultipliedByDoubling(-other, one, first) { left, right -> left + right }
public fun <N> Ring<N>.doublingTimes(first: UInt, other: N): N = rightMultiplyByDoubling(other, first, ::zero) { left, right -> left + right }
// endregion

// region Long-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> Long.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> Long.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
//context(Ring<N>)
//public fun <N> Long.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })

public fun <N> Ring<N>.doublingPlus(first: Long, other: N): N = rightAddMultipliedByDoubling(other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingMinus(first: Long, other: N): N = rightAddMultipliedByDoubling(-other, one, first, { left, right -> left + right }, { left, right -> left - right })
public fun <N> Ring<N>.doublingTimes(first: Long, other: N): N = rightMultiplyByDoubling(other, first, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Value additive operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> ULong.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> ULong.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
//context(Ring<N>)
//public fun <N> ULong.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }

public fun <N> Ring<N>.doublingPlus(first: ULong, other: N): N = rightAddMultipliedByDoubling(other, one, first) { left, right -> left + right }
public fun <N> Ring<N>.doublingMinus(first: ULong, other: N): N = rightAddMultipliedByDoubling(-other, one, first) { left, right -> left + right }
public fun <N> Ring<N>.doublingTimes(first: ULong, other: N): N = rightMultiplyByDoubling(other, first, ::zero) { left, right -> left + right }
// endregion

//region Multiplicative operations
// Waiting for context receivers :( FIXME: Uncomment and delete replacements when context receivers will be available
//context(Ring<N>)
//public fun <N> squaringPower(base: N, exponent: Int): N = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
//context(Ring<N>)
//public fun <N> squaringPower(base: N, exponent: UInt): N = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })
//context(Ring<N>)
//public fun <N> squaringPower(base: N, exponent: Long): N = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
//context(Ring<N>)
//public fun <N> squaringPower(base: N, exponent: ULong): N = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right })

public fun <N> Ring<N>.squaringPower(base: N, exponent: Int): N = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public fun <N> Ring<N>.squaringPower(base: N, exponent: UInt): N = rightMultiplyByDoubling(base, exponent, ::one) { left, right -> left * right }
public fun <N> Ring<N>.squaringPower(base: N, exponent: Long): N = rightMultiplyByDoubling(base, exponent, ::one, { left, right -> left * right }, { v -> -v })
public fun <N> Ring<N>.squaringPower(base: N, exponent: ULong): N = rightMultiplyByDoubling(base, exponent, ::one) { left, right -> left * right }
//endregion