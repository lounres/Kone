/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.numberTheory

import kotlin.jvm.JvmInline
import kotlin.math.*


/**
 * Container for [Bézout coefficients](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity) and
 * [GCD](https://en.wikipedia.org/wiki/Greatest_common_divisor).
 */
@JvmInline
public value class BezoutCoefficientsWithGCD<T>(public val first: T, public val second: T, public val gcd: T)

// region Int

/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 *
 * It's computed by [Euclidean algorithm](https://en.wikipedia.org/wiki/Greatest_common_divisor#Euclidean_algorithm).
 * Hence, its time complexity is \(O(\log(a+b))\) (see [Wolfram MathWorld](https://mathworld.wolfram.com/EuclideanAlgorithm.html)).
 *
 * @usesMathJax
 */
public tailrec fun gcd(a: Int, b: Int): Int = if (a == 0) abs(b) else gcd(b % a, a)

/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of the [values].
 */
public fun gcd(vararg values: Int): Int = with(values) { abs(if (isEmpty()) 0 else reduce(::gcd)) }
/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of the [values].
 */
public fun gcd(values: Iterable<Int>): Int = abs(values.reduceOrNull(::gcd) ?: 0)

/**
 * Computes "the smallest" [Bézout coefficients](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity) and
 * [GCD](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 */
public fun bezoutIdentityWithGCD(a: Int, b: Int): BezoutCoefficientsWithGCD<Int> =
    when {
        a < 0 && b < 0 -> with(bezoutIdentityWithGCDInternalLogic(-a, -b, 1, 0, 0, 1)) { BezoutCoefficientsWithGCD(-first, -second, gcd) }
        a < 0 -> with(bezoutIdentityWithGCDInternalLogic(-a, b, 1, 0, 0, 1)) { BezoutCoefficientsWithGCD(-first, second, gcd) }
        b < 0 -> with(bezoutIdentityWithGCDInternalLogic(a, -b, 1, 0, 0, 1)) { BezoutCoefficientsWithGCD(first, -second, gcd) }
        else -> bezoutIdentityWithGCDInternalLogic(a, b, 1, 0, 0, 1)
    }

/**
 * Computes "the smallest" [Bézout coefficients](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity) and
 * [GCD](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 *
 * Also assumes that [a] and [b] are non-negative. TODO: Docs
 */
internal tailrec fun bezoutIdentityWithGCDInternalLogic(a: Int, b: Int, m1: Int, m2: Int, m3: Int, m4: Int): BezoutCoefficientsWithGCD<Int> =
    if (b == 0) BezoutCoefficientsWithGCD(m1, m3, a)
    else {
        val quotient = a / b
        val reminder = a % b
        bezoutIdentityWithGCDInternalLogic(b, reminder, m2, m1 - quotient * m2, m4, m3 - quotient * m4)
    }

// endregion

// region Long

/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 *
 * It's computed by [Euclidean algorithm](https://en.wikipedia.org/wiki/Greatest_common_divisor#Euclidean_algorithm).
 * Hence, its time complexity is \(O(\log(a+b))\) (see [Wolfram MathWorld](https://mathworld.wolfram.com/EuclideanAlgorithm.html)).
 *
 * @usesMathJax
 */
public tailrec fun gcd(a: Long, b: Long): Long = if (a == 0L) abs(b) else gcd(b % a, a)

/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of the [values].
 */
public fun gcd(vararg values: Long): Long = with(values) { abs(if (isEmpty()) 0 else reduce(::gcd)) }
/**
 * Computes [Greatest Common Divisor](https://en.wikipedia.org/wiki/Greatest_common_divisor) of the [values].
 */
public fun gcd(values: Iterable<Long>): Long = abs(values.reduceOrNull(::gcd) ?: 0)

/**
 * Computes "the smallest" [Bézout coefficients](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity) and
 * [GCD](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 */
public fun bezoutIdentityWithGCD(a: Long, b: Long): BezoutCoefficientsWithGCD<Long> =
    when {
        a < 0 && b < 0 -> with(bezoutIdentityWithGCDInternalLogic(-a, -b, 1, 0, 0, 1)) { BezoutCoefficientsWithGCD(-first, -second, gcd) }
        a < 0 -> with(bezoutIdentityWithGCDInternalLogic(-a, b, 1, 0, 0, 1)) { BezoutCoefficientsWithGCD(-first, second, gcd) }
        b < 0 -> with(bezoutIdentityWithGCDInternalLogic(a, -b, 1, 0, 0, 1)) { BezoutCoefficientsWithGCD(first, -second, gcd) }
        else -> bezoutIdentityWithGCDInternalLogic(a, b, 1, 0, 0, 1)
    }

/**
 * Computes "the smallest" [Bézout coefficients](https://en.wikipedia.org/wiki/B%C3%A9zout%27s_identity) and
 * [GCD](https://en.wikipedia.org/wiki/Greatest_common_divisor) of [a] and [b].
 *
 * Also assumes that [a] and [b] are non-negative. TODO: Docs
 */
internal tailrec fun bezoutIdentityWithGCDInternalLogic(a: Long, b: Long, m1: Long, m2: Long, m3: Long, m4: Long): BezoutCoefficientsWithGCD<Long> =
    if (b == 0L) BezoutCoefficientsWithGCD(m1, m3, a)
    else {
        val quotient = a / b
        val reminder = a % b
        bezoutIdentityWithGCDInternalLogic(b, reminder, m2, m1 - quotient * m2, m4, m3 - quotient * m4)
    }

// endregion