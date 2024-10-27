/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import kotlin.jvm.JvmName


// region Number-Int additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0`, the result is `this`.
 * - If `other == 1`, the result is `this + one`.
 * - If `other == 2`, the result is `this + (one + one)`.
 * - If `other == 3`, the result is `(this + one) + (one + one)`.
 * - If `other == 4`, the result is `this + ((one + one) + (one + one))`.
 * - If `other == -1`, the result is `this + -one`.
 * - If `other == -2`, the result is `this + (-one + -one)`.
 * - If `other == -3`, the result is `(this + -one) + (-one + -one)`.
 * - If `other == -4`, the result is `this + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: Int): N = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0`, the result is `this`.
 * - If `other == 1`, the result is `this + -one`.
 * - If `other == 2`, the result is `this + (-one + -one)`.
 * - If `other == 3`, the result is `(this + -one) + (-one + -one)`.
 * - If `other == 4`, the result is `this + ((-one + -one) + (-one + -one))`.
 * - If `other == -1`, the result is `this + one`.
 * - If `other == -2`, the result is `this + (one + one)`.
 * - If `other == -3`, the result is `(this + one) + (one + one)`.
 * - If `other == -4`, the result is `this + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: Int): N = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, multiply `-this` by [other]
 * if [other] is negative, or return [Ring.zero] if [other] is zero.
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0`, the result is `zero`.
 * - If `other == 1`, the result is `this`.
 * - If `other == 2`, the result is `this + this`.
 * - If `other == 3`, the result is `this + (this + this)`.
 * - If `other == 4`, the result is `(this + this) + (this + this)`.
 * - If `other == -1`, the result is `-this`.
 * - If `other == -2`, the result is `-this + -this`.
 * - If `other == -3`, the result is `-this + (-this + -this)`.
 * - If `other == -4`, the result is `(-this + -this) + (-this + -this)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this + this) + (this + this)` actual computation is equivalent to
 * `(this + this).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: Int): N = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Number-UInt additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0`, the result is `this`.
 * - If `other == 1`, the result is `this + one`.
 * - If `other == 2`, the result is `this + (one + one)`.
 * - If `other == 3`, the result is `(this + one) + (one + one)`.
 * - If `other == 4`, the result is `this + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: UInt): N = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0u`, the result is `this`.
 * - If `other == 1u`, the result is `this + -one`.
 * - If `other == 2u`, the result is `this + (-one + -one)`.
 * - If `other == 3u`, the result is `(this + -one) + (-one + -one)`.
 * - If `other == 4u`, the result is `this + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: UInt): N = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, or return [Ring.zero] if [other] is zero.
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0u`, the result is `zero`.
 * - If `other == 1u`, the result is `this`.
 * - If `other == 2u`, the result is `this + this`.
 * - If `other == 3u`, the result is `this + (this + this)`.
 * - If `other == 4u`, the result is `(this + this) + (this + this)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this + this) + (this + this)` actual computation is equivalent to
 * `(this + this).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: UInt): N = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }
// endregion

// region Number-Long additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, multiply `-this` by [other]
 * if [other] is negative, or return [Ring.zero] if [other] is zero.
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0L`, the result is `this`.
 * - If `other == 1L`, the result is `this + one`.
 * - If `other == 2L`, the result is `this + (one + one)`.
 * - If `other == 3L`, the result is `(this + one) + (one + one)`.
 * - If `other == 4L`, the result is `this + ((one + one) + (one + one))`.
 * - If `other == -1L`, the result is `this + -one`.
 * - If `other == -2L`, the result is `this + (-one + -one)`.
 * - If `other == -3L`, the result is `(this + -one) + (-one + -one)`.
 * - If `other == -4L`, the result is `this + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: Long): N = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0L`, the result is `this`.
 * - If `other == 1L`, the result is `this + -one`.
 * - If `other == 2L`, the result is `this + (-one + -one)`.
 * - If `other == 3L`, the result is `(this + -one) + (-one + -one)`.
 * - If `other == 4L`, the result is `this + ((-one + -one) + (-one + -one))`.
 * - If `other == -1L`, the result is `this + one`.
 * - If `other == -2L`, the result is `this + (one + one)`.
 * - If `other == -3L`, the result is `(this + one) + (one + one)`.
 * - If `other == -4L`, the result is `this + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: Long): N = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, multiply `-this` by [other]
 * if [other] is negative, or return [Ring.zero] if [other] is zero.
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0L`, the result is `zero`.
 * - If `other == 1L`, the result is `this`.
 * - If `other == 2L`, the result is `this + this`.
 * - If `other == 3L`, the result is `this + (this + this)`.
 * - If `other == 4L`, the result is `(this + this) + (this + this)`.
 * - If `other == -1L`, the result is `-this`.
 * - If `other == -2L`, the result is `-this + -this`.
 * - If `other == -3L`, the result is `-this + (-this + -this)`.
 * - If `other == -4L`, the result is `(-this + -this) + (-this + -this)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this + this) + (this + this)` actual computation is equivalent to
 * `(this + this).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: Long): N = rightMultiplyByDoubling(this, other, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region Number-ULong additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0L`, the result is `this`.
 * - If `other == 1L`, the result is `this + one`.
 * - If `other == 2L`, the result is `this + (one + one)`.
 * - If `other == 3L`, the result is `(this + one) + (one + one)`.
 * - If `other == 4L`, the result is `this + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingPlus(other: ULong): N = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [other] to the right of [this].
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0uL`, the result is `this`.
 * - If `other == 1uL`, the result is `this + -one`.
 * - If `other == 2uL`, the result is `this + (-one + -one)`.
 * - If `other == 3uL`, the result is `(this + -one) + (-one + -one)`.
 * - If `other == 4uL`, the result is `this + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingMinus(other: ULong): N = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, or return [Ring.zero] if [other] is zero.
 *
 * For example here are resulting expressions for the following values of [other]:
 * - If `other == 0uL`, the result is `zero`.
 * - If `other == 1uL`, the result is `this`.
 * - If `other == 2uL`, the result is `this + this`.
 * - If `other == 3uL`, the result is `this + (this + this)`.
 * - If `other == 4uL`, the result is `(this + this) + (this + this)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this + this) + (this + this)` actual computation is equivalent to
 * `(this + this).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> N.doublingTimes(other: ULong): N = rightMultiplyByDoubling(this, other, ::zero) { left, right -> left + right }
// endregion

// region Int-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0`, the result is `other`.
 * - If `this == 1`, the result is `other + one`.
 * - If `this == 2`, the result is `other + (one + one)`.
 * - If `this == 3`, the result is `(other + one) + (one + one)`.
 * - If `this == 4`, the result is `other + ((one + one) + (one + one))`.
 * - If `this == -1`, the result is `other + -one`.
 * - If `this == -2`, the result is `other + (-one + -one)`.
 * - If `this == -3`, the result is `(other + -one) + (-one + -one)`.
 * - If `this == -4`, the result is `other + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> Int.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0`, the result is `other`.
 * - If `this == 1`, the result is `other + -one`.
 * - If `this == 2`, the result is `other + (-one + -one)`.
 * - If `this == 3`, the result is `(other + -one) + (-one + -one)`.
 * - If `this == 4`, the result is `other + ((-one + -one) + (-one + -one))`.
 * - If `this == -1`, the result is `other + one`.
 * - If `this == -2`, the result is `other + (one + one)`.
 * - If `this == -3`, the result is `(other + one) + (one + one)`.
 * - If `this == -4`, the result is `other + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> Int.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, multiply `-other` by [this]
 * if [this] is negative, or return [Ring.zero] if [this] is zero.
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0`, the result is `zero`.
 * - If `this == 1`, the result is `other`.
 * - If `this == 2`, the result is `other + other`.
 * - If `this == 3`, the result is `other + (other + other)`.
 * - If `this == 4`, the result is `(other + other) + (other + other)`.
 * - If `this == -1`, the result is `-other`.
 * - If `this == -2`, the result is `-other + -other`.
 * - If `this == -3`, the result is `-other + (-other + -other)`.
 * - If `this == -4`, the result is `(-other + -other) + (-other + -other)`.
 * - And so on...
 *
 * But actually such sub-expression like `other + other` are not calculated several times. Instead of
 * `(other + other) + (other + other)` actual computation is equivalent to
 * `(other + other).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> Int.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0u`, the result is `other`.
 * - If `this == 1u`, the result is `other + one`.
 * - If `this == 2u`, the result is `other + (one + one)`.
 * - If `this == 3u`, the result is `(other + one) + (one + one)`.
 * - If `this == 4u`, the result is `other + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> UInt.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0u`, the result is `other`.
 * - If `this == 1u`, the result is `other + -one`.
 * - If `this == 2u`, the result is `other + (-one + -one)`.
 * - If `this == 3u`, the result is `(other + -one) + (-one + -one)`.
 * - If `this == 4u`, the result is `other + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> UInt.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, or return [Ring.zero] if [this] is zero.
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0u`, the result is `zero`.
 * - If `this == 1u`, the result is `other`.
 * - If `this == 2u`, the result is `other + other`.
 * - If `this == 3u`, the result is `other + (other + other)`.
 * - If `this == 4u`, the result is `(other + other) + (other + other)`.
 * - And so on...
 *
 * But actually such sub-expression like `other + other` are not calculated several times. Instead of
 * `(other + other) + (other + other)` actual computation is equivalent to
 * `(other + other).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> UInt.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }
// endregion

// region Long-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0L`, the result is `other`.
 * - If `this == 1L`, the result is `other + one`.
 * - If `this == 2L`, the result is `other + (one + one)`.
 * - If `this == 3L`, the result is `(other + one) + (one + one)`.
 * - If `this == 4L`, the result is `other + ((one + one) + (one + one))`.
 * - If `this == -1L`, the result is `other + -one`.
 * - If `this == -2L`, the result is `other + (-one + -one)`.
 * - If `this == -3L`, the result is `(other + -one) + (-one + -one)`.
 * - If `this == -4L`, the result is `other + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> Long.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0L`, the result is `other`.
 * - If `this == 1L`, the result is `other + -one`.
 * - If `this == 2L`, the result is `other + (-one + -one)`.
 * - If `this == 3L`, the result is `(other + -one) + (-one + -one)`.
 * - If `this == 4L`, the result is `other + ((-one + -one) + (-one + -one))`.
 * - If `this == -1L`, the result is `other + one`.
 * - If `this == -2L`, the result is `other + (one + one)`.
 * - If `this == -3L`, the result is `(other + one) + (one + one)`.
 * - If `this == -4L`, the result is `other + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> Long.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, multiply `-other` by [this]
 * if [this] is negative, or return [Ring.zero] if [this] is zero.
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0L`, the result is `zero`.
 * - If `this == 1L`, the result is `other`.
 * - If `this == 2L`, the result is `other + other`.
 * - If `this == 3L`, the result is `other + (other + other)`.
 * - If `this == 4L`, the result is `(other + other) + (other + other)`.
 * - If `this == -1L`, the result is `-other`.
 * - If `this == -2L`, the result is `-other + -other`.
 * - If `this == -3L`, the result is `-other + (-other + -other)`.
 * - If `this == -4L`, the result is `(-other + -other) + (-other + -other)`.
 * - And so on...
 *
 * But actually such sub-expression like `other + other` are not calculated several times. Instead of
 * `(other + other) + (other + other)` actual computation is equivalent to
 * `(other + other).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> Long.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0uL`, the result is `other`.
 * - If `this == 1uL`, the result is `other + one`.
 * - If `this == 2uL`, the result is `other + (one + one)`.
 * - If `this == 3uL`, the result is `(other + one) + (one + one)`.
 * - If `this == 4uL`, the result is `other + ((one + one) + (one + one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> ULong.doublingPlus(other: N): N = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [Ring.one] multiplied by integer [this] to the right of [other].
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0uL`, the result is `other`.
 * - If `this == 1uL`, the result is `other + -one`.
 * - If `this == 2uL`, the result is `other + (-one + -one)`.
 * - If `this == 3uL`, the result is `(other + -one) + (-one + -one)`.
 * - If `this == 4uL`, the result is `other + ((-one + -one) + (-one + -one))`.
 * - And so on...
 *
 * But actually such sub-expression like `one + one` are not calculated several times. Instead of
 * `(one + one) + (one + one)` actual computation is equivalent to
 * `(one + one).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> ULong.doublingMinus(other: N): N = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, or return [Ring.zero] if [this] is zero.
 *
 * For example here are resulting expressions for the following values of [this]:
 * - If `this == 0uL`, the result is `zero`.
 * - If `this == 1uL`, the result is `other`.
 * - If `this == 2uL`, the result is `other + other`.
 * - If `this == 3uL`, the result is `other + (other + other)`.
 * - If `this == 4uL`, the result is `(other + other) + (other + other)`.
 * - And so on...
 *
 * But actually such sub-expression like `other + other` are not calculated several times. Instead of
 * `(other + other) + (other + other)` actual computation is equivalent to
 * `(other + other).let { it + it }` that uses 2 calls of `+` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
public infix fun <N> ULong.doublingTimes(other: N): N = rightMultiplyByDoubling(other, this, ::zero) { left, right -> left + right }
// endregion

//region Multiplicative operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, raise `this.reciprocal`
 * in the power of [exponent] if [exponent] is negative, or return [Ring.one] if [exponent] is zero.
 *
 * For example here are resulting expressions for the following values of [exponent]:
 * - If `exponent == 0`, the result is `one`.
 * - If `exponent == 1`, the result is `this`.
 * - If `exponent == 2`, the result is `this * this`.
 * - If `exponent == 3`, the result is `this * (this * this)`.
 * - If `exponent == 4`, the result is `(this * this) * (this + this)`.
 * - If `exponent == -1`, the result is `this.reciprocal`.
 * - If `exponent == -2`, the result is `this.reciprocal * this.reciprocal`.
 * - If `exponent == -3`, the result is `this.reciprocal * (this.reciprocal * this.reciprocal)`.
 * - If `exponent == -4`, the result is `(this.reciprocal * this.reciprocal) * (this.reciprocal * this.reciprocal)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this * this) * (this * this)` actual computation is equivalent to
 * `(this * this).let { it * it }` that uses 2 calls of `*` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(Field<N>)
@JvmName("squaringPowerReceiver")
public infix fun <N> N.squaringPower(exponent: Int): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right }, { v -> v.reciprocal })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, or return [Ring.one] if [exponent] is zero.
 *
 * For example here are resulting expressions for the following values of [exponent]:
 * - If `exponent == 0u`, the result is `one`.
 * - If `exponent == 1u`, the result is `this`.
 * - If `exponent == 2u`, the result is `this * this`.
 * - If `exponent == 3u`, the result is `this * (this * this)`.
 * - If `exponent == 4u`, the result is `(this * this) * (this + this)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this * this) * (this * this)` actual computation is equivalent to
 * `(this * this).let { it * it }` that uses 2 calls of `*` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
@JvmName("squaringPowerUReceiver")
public infix fun <N> N.squaringPower(exponent: UInt): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, raise `this.reciprocal`
 * in the power of [exponent] if [exponent] is negative, or return [Ring.one] if [exponent] is zero.
 *
 * For example here are resulting expressions for the following values of [exponent]:
 * - If `exponent == 0L`, the result is `one`.
 * - If `exponent == 1L`, the result is `this`.
 * - If `exponent == 2L`, the result is `this * this`.
 * - If `exponent == 3L`, the result is `this * (this * this)`.
 * - If `exponent == 4L`, the result is `(this * this) * (this + this)`.
 * - If `exponent == -1L`, the result is `this.reciprocal`.
 * - If `exponent == -2L`, the result is `this.reciprocal * this.reciprocal`.
 * - If `exponent == -3L`, the result is `this.reciprocal * (this.reciprocal * this.reciprocal)`.
 * - If `exponent == -4L`, the result is `(this.reciprocal * this.reciprocal) * (this.reciprocal * this.reciprocal)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this * this) * (this * this)` actual computation is equivalent to
 * `(this * this).let { it * it }` that uses 2 calls of `*` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(Field<N>)
@JvmName("squaringPowerReceiver")
public infix fun <N> N.squaringPower(exponent: Long): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right }, { v -> v.reciprocal })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, or return [Ring.one] if [exponent] is zero.
 *
 * For example here are resulting expressions for the following values of [exponent]:
 * - If `exponent == 0uL`, the result is `one`.
 * - If `exponent == 1uL`, the result is `this`.
 * - If `exponent == 2uL`, the result is `this * this`.
 * - If `exponent == 3uL`, the result is `this * (this * this)`.
 * - If `exponent == 4uL`, the result is `(this * this) * (this + this)`.
 * - And so on...
 *
 * But actually such sub-expression like `this + this` are not calculated several times. Instead of
 * `(this * this) * (this * this)` actual computation is equivalent to
 * `(this * this).let { it * it }` that uses 2 calls of `*` instead of three.
 *
 * So one can say that [Ring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(Ring<N>)
@JvmName("squaringPowerUReceiver")
public infix fun <N> N.squaringPower(exponent: ULong): N = rightMultiplyByDoubling(this, exponent, ::one, { left, right -> left * right })
//endregion