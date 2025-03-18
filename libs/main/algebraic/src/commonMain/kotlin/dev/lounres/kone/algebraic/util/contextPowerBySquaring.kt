/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.Semiring
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.reciprocal
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.algebraic.zero
import kotlin.jvm.JvmName


// region Number-Int additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingPlus(other: Int): Number = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-other][other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingMinus(other: Int): Number = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, multiply `-this` by [other]
 * if [other] is negative, or return [zero][Semiring.zero] if [other] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingTimes(other: Int): Number = rightMultiplyByDoubling(this, other, { zero }, { left, right -> left + right }, { c -> -c })
// endregion

// region Number-UInt additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> Number.doublingPlus(other: UInt): Number = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-other][other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingMinus(other: UInt): Number = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, or return [zero][Semiring.zero] if [other] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> Number.doublingTimes(other: UInt): Number = rightMultiplyByDoubling(this, other, { zero }) { left, right -> left + right }
// endregion

// region Number-Long additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, multiply `-this` by [other]
 * if [other] is negative, or return [zero][Semiring.zero] if [other] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingPlus(other: Long): Number = rightAddMultipliedByDoubling(this, one, other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-other][other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingMinus(other: Long): Number = rightAddMultipliedByDoubling(this, one, -other, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, multiply `-this` by [other]
 * if [other] is negative, or return [zero][Semiring.zero] if [other] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingTimes(other: Long): Number = rightMultiplyByDoubling(this, other, { zero }, { left, right -> left + right }, { c -> -c })
// endregion

// region Number-ULong additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> Number.doublingPlus(other: ULong): Number = rightAddMultipliedByDoubling(this, one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-other][other] to the right of [this].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Number.doublingMinus(other: ULong): Number = rightAddMultipliedByDoubling(this, -one, other) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [this] by integer [other] if [other] is positive, or return [zero][Semiring.zero] if [other] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{other}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> Number.doublingTimes(other: ULong): Number = rightMultiplyByDoubling(this, other, { zero }) { left, right -> left + right }
// endregion

// region Int-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Int.doublingPlus(other: Number): Number = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-this][this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Int.doublingMinus(other: Number): Number = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, multiply `-other` by [this]
 * if [this] is negative, or return [zero][Semiring.zero] if [this] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Int.doublingTimes(other: Number): Number = rightMultiplyByDoubling(other, this, { zero }, { left, right -> left + right }, { c -> -c })
// endregion

// region UInt-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> UInt.doublingPlus(other: Number): Number = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-this][this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> UInt.doublingMinus(other: Number): Number = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, or return [zero][Semiring.zero] if [this] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> UInt.doublingTimes(other: Number): Number = rightMultiplyByDoubling(other, this, { zero }) { left, right -> left + right }
// endregion

// region Long-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Long.doublingPlus(other: Number): Number = rightAddMultipliedByDoubling(other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-this][this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Long.doublingMinus(other: Number): Number = rightAddMultipliedByDoubling(-other, one, this, { left, right -> left + right }, { left, right -> left - right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, multiply `-other` by [this]
 * if [this] is negative, or return [zero][Semiring.zero] if [this] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> Long.doublingTimes(other: Number): Number = rightMultiplyByDoubling(other, this, { zero }, { left, right -> left + right }, { c -> -c })
// endregion

// region ULong-Number additive operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> ULong.doublingPlus(other: Number): Number = rightAddMultipliedByDoubling(other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add [one][Semiring.one] multiplied by integer [-this][this] to the right of [other].
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Ring<Number>)
public infix fun <Number> ULong.doublingMinus(other: Number): Number = rightAddMultipliedByDoubling(-other, one, this) { left, right -> left + right }
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [other] by integer [this] if [this] is positive, or return [zero][Semiring.zero] if [this] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{this}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
public infix fun <Number> ULong.doublingTimes(other: Number): Number = rightMultiplyByDoubling(other, this, { zero }) { left, right -> left + right }
// endregion

//region Multiplicative operations
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, raise `this.reciprocal`
 * in the power of [exponent] if [exponent] is negative, or return [one][Semiring.one] if [exponent] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(_: Field<Number>)
@JvmName("squaringPowerReceiver")
public infix fun <Number> Number.squaringPower(exponent: Int): Number = rightMultiplyByDoubling(this, exponent, { one }, { left, right -> left * right }, { v -> v.reciprocal })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, or return [one][Semiring.one] if [exponent] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
@JvmName("squaringPowerUReceiver")
public infix fun <Number> Number.squaringPower(exponent: UInt): Number = rightMultiplyByDoubling(this, exponent, { one }, { left, right -> left * right })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, raise `this.reciprocal`
 * in the power of [exponent] if [exponent] is negative, or return [one][Semiring.one] if [exponent] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(_: Field<Number>)
@JvmName("squaringPowerReceiver")
public infix fun <Number> Number.squaringPower(exponent: Long): Number = rightMultiplyByDoubling(this, exponent, { one }, { left, right -> left * right }, { v -> v.reciprocal })
/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to raise argument [this] in the power of integer [exponent] if [exponent] is positive, or return [one][Semiring.one] if [exponent] is zero.
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
 * So one can say that [plus][Semiring.plus] is used \(O(\log(\mathrm{exponent}))\) times.
 *
 * @usesMathJax
 */
context(_: Semiring<Number>)
@JvmName("squaringPowerUReceiver")
public infix fun <Number> Number.squaringPower(exponent: ULong): Number = rightMultiplyByDoubling(this, exponent, { one }, { left, right -> left * right })
//endregion