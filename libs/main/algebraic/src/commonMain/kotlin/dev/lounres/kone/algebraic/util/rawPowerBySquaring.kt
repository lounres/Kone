/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import kotlin.math.abs


// TODO: Check if lambda boxing is as optimised as possible

// region Int

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [arg] by integer [multiplier] if [multiplier] is positive, multiply `-arg` by [multiplier]
 * if [multiplier] is negative, or return `lazyZero()` if [multiplier] is zero.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0`, the result is `lazyZero()`.
 * - If `multiplier == 1`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - If `multiplier == -1`, the result is `additionOp(base, negationOp(arg))`.
 * - If `multiplier == -2`, the result is `additionOp(base, additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -3`, the result is `additionOp(additionOp(base, negationOp(arg)), additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -4`, the result is `additionOp(base, additionOp(additionOp(negationOp(arg), negationOp(arg)), additionOp(negationOp(arg), negationOp(arg))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightMultiplyByDoubling(arg: Number, multiplier: Int, lazyZero: () -> Number, additionOp: (Number, Number) -> Number, negationOp: (Number) -> Number): Number =
    if (multiplier >= 0) rightMultiplyByDoubling(arg, multiplier.toUInt(), lazyZero, additionOp)
    else rightMultiplyByDoubling(negationOp(arg), (-multiplier).toUInt(), lazyZero, additionOp)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0`, the result is `base`.
 * - If `multiplier == 1`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - If `multiplier == -1`, the result is `additionOp(base, negationOp(arg))`.
 * - If `multiplier == -2`, the result is `additionOp(base, additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -3`, the result is `additionOp(additionOp(base, negationOp(arg)), additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -4`, the result is `additionOp(base, additionOp(additionOp(negationOp(arg), negationOp(arg)), additionOp(negationOp(arg), negationOp(arg))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: Int, additionOp: (Number, Number) -> Number, negationOp: (Number) -> Number): Number =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, arg, multiplier.toUInt(), additionOp)
    else rightAddMultipliedByDoubling(base, negationOp(arg), (-multiplier).toUInt(), additionOp)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0`, the result is `base`.
 * - If `multiplier == 1`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - If `multiplier == -1`, the result is `rightSubtractionOp(base, arg)`.
 * - If `multiplier == -2`, the result is `rightSubtractionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == -3`, the result is `rightSubtractionOp(rightSubtractionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == -4`, the result is `rightSubtractionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that both [additionOp] and [rightSubtractionOp] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: Int, additionOp: (Number, Number) -> Number, rightSubtractionOp: (Number, Number) -> Number): Number =
    if(multiplier >= 0) rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toUInt(), additionOp, additionOp)
    else rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toUInt(), additionOp, rightSubtractionOp)

// endregion

// region UInt

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [arg] by integer [multiplier] or return result of [lazyZero] if [multiplier] is `0uL`.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0u`, the result is `lazyZero()`.
 * - If `multiplier == 1u`, the result is `additionOp(arg, arg)`.
 * - If `multiplier == 2u`, the result is `additionOp(arg, additionOp(arg, arg))`.
 * - If `multiplier == 3u`, the result is `additionOp(additionOp(arg, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4u`, the result is `additionOp(arg, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses two calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
public inline fun <Number> rightMultiplyByDoubling(arg: Number, multiplier: UInt, lazyZero: () -> Number, additionOp: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(arg: N, multiplier: UInt): N =
//        when {
//            multiplier == 1u -> arg
//            multiplier and 1u == 0u -> theLogic(additionOp(arg, arg), multiplier shr 1)
//            else -> rightAddMultipliedByDoubling(arg, additionOp(arg, arg), multiplier shr 1, additionOp)
//        }
//    return if (multiplier == 0u) lazyZero() else theLogic(arg, multiplier)
    // Next is manually inlined code

    if (multiplier == 0u) lazyZero()
    var arg = arg
    var multiplier = multiplier
    while (true) {
        when {
            multiplier == 1u -> return arg
            multiplier and 1u == 0u -> {
                arg = additionOp(arg, arg)
                multiplier = multiplier shr 1
            }
            else -> return rightAddMultipliedByDoubling(arg, additionOp(arg, arg), multiplier shr 1, additionOp)
        }
    }
}

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0u`, the result is `base`.
 * - If `multiplier == 1u`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2u`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3u`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4u`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: UInt, additionOp: (Number, Number) -> Number): Number =
    rightAddMultipliedByDoublingInternalLogic(base, arg, multiplier, additionOp, additionOp)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0u`, the result is `base`.
 * - If `multiplier == 1u`, the result is `additionToBaseOp(base, arg)`.
 * - If `multiplier == 2u`, the result is `additionToBaseOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3u`, the result is `additionToBaseOp(additionToBaseOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4u`, the result is `additionToBaseOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that both [additionOp] and [additionToBaseOp] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
@PublishedApi
internal inline fun <Number> rightAddMultipliedByDoublingInternalLogic(base: Number, arg: Number, multiplier: UInt, additionOp: (Number, Number) -> Number, additionToBaseOp: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(base: N, arg: N, multiplier: UInt): N =
//        if (multiplier == 1u) additionToBaseOp(base, arg)
//        else theLogic(
//            base = if (multiplier and 1u == 0u) base else additionToBaseOp(base, arg),
//            arg = additionOp(arg, arg),
//            multiplier = multiplier shr 1,
//        )
//    return if (multiplier == 0u) base else theLogic(base, arg, multiplier)
    // Next is manually inlined code

    if (multiplier == 0u) return base
    var base = base
    var arg = arg
    var multiplier = multiplier
    while (true) {
        if (multiplier == 1u) return additionToBaseOp(base, arg)
        else {
            base = if (multiplier and 1u == 0u) base else additionToBaseOp(base, arg)
            arg = additionOp(arg, arg)
            multiplier = multiplier shr 1
        }
    }
}

// endregion

// region Long

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [arg] by integer [multiplier] if [multiplier] is positive, multiply `-arg` by [multiplier]
 * if [multiplier] is negative, or return `lazyZero()` if [multiplier] is zero.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0L`, the result is `lazyZero()`.
 * - If `multiplier == 1L`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2L`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3L`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4L`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - If `multiplier == -1L`, the result is `additionOp(base, negationOp(arg))`.
 * - If `multiplier == -2L`, the result is `additionOp(base, additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -3L`, the result is `additionOp(additionOp(base, negationOp(arg)), additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -4L`, the result is `additionOp(base, additionOp(additionOp(negationOp(arg), negationOp(arg)), additionOp(negationOp(arg), negationOp(arg))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightMultiplyByDoubling(arg: Number, multiplier: Long, lazyZero: () -> Number, additionOp: (Number, Number) -> Number, negationOp: (Number) -> Number): Number =
    if (multiplier >= 0) rightMultiplyByDoubling(arg, multiplier.toULong(), lazyZero, additionOp)
    else rightMultiplyByDoubling(negationOp(arg), (-multiplier).toULong(), lazyZero, additionOp)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0L`, the result is `base`.
 * - If `multiplier == 1L`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2L`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3L`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4L`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - If `multiplier == -1L`, the result is `additionOp(base, negationOp(arg))`.
 * - If `multiplier == -2L`, the result is `additionOp(base, additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -3L`, the result is `additionOp(additionOp(base, negationOp(arg)), additionOp(negationOp(arg), negationOp(arg)))`.
 * - If `multiplier == -4L`, the result is `additionOp(base, additionOp(additionOp(negationOp(arg), negationOp(arg)), additionOp(negationOp(arg), negationOp(arg))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: Long, additionOp: (Number, Number) -> Number, negationOp: (Number) -> Number): Number =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, arg, multiplier.toULong(), additionOp)
    else rightAddMultipliedByDoubling(base, negationOp(arg), (-multiplier).toULong(), additionOp)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0L`, the result is `base`.
 * - If `multiplier == 1L`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2L`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3L`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4L`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - If `multiplier == -1L`, the result is `rightSubtractionOp(base, arg)`.
 * - If `multiplier == -2L`, the result is `rightSubtractionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == -3L`, the result is `rightSubtractionOp(rightSubtractionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == -4L`, the result is `rightSubtractionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that both [additionOp] and [rightSubtractionOp] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: Long, additionOp: (Number, Number) -> Number, rightSubtractionOp: (Number, Number) -> Number): Number =
    if(multiplier >= 0) rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toULong(), additionOp, additionOp)
    else rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toULong(), additionOp, rightSubtractionOp)

// endregion

// region ULong

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [arg] by integer [multiplier] or return result of [lazyZero] if [multiplier] is `0uL`.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0uL`, the result is `lazyZero()`.
 * - If `multiplier == 1uL`, the result is `additionOp(arg, arg)`.
 * - If `multiplier == 2uL`, the result is `additionOp(arg, additionOp(arg, arg))`.
 * - If `multiplier == 3uL`, the result is `additionOp(additionOp(arg, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4uL`, the result is `additionOp(arg, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses two calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
public inline fun <Number> rightMultiplyByDoubling(arg: Number, multiplier: ULong, lazyZero: () -> Number, additionOp: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(arg: N, multiplier: ULong): N =
//        when {
//            multiplier == 1uL -> arg
//            multiplier and 1uL == 0uL -> theLogic(additionOp(arg, arg), multiplier shr 1)
//            else -> rightAddMultipliedByDoubling(arg, additionOp(arg, arg), multiplier shr 1, additionOp)
//        }
//    return if (multiplier == 0uL) lazyZero() else theLogic(arg, multiplier)
    // Next is manually inlined code

    if (multiplier == 0uL) lazyZero()
    var arg = arg
    var multiplier = multiplier
    while (true) {
        when {
            multiplier == 1uL -> return arg
            multiplier and 1uL == 0uL -> {
                arg = additionOp(arg, arg)
                multiplier = multiplier shr 1
            }
            else -> return rightAddMultipliedByDoubling(arg, additionOp(arg, arg), multiplier shr 1, additionOp)
        }
    }
}

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0uL`, the result is `base`.
 * - If `multiplier == 1uL`, the result is `additionOp(base, arg)`.
 * - If `multiplier == 2uL`, the result is `additionOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3uL`, the result is `additionOp(additionOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4uL`, the result is `additionOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that [additionOp] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, arg: Number, multiplier: ULong, additionOp: (Number, Number) -> Number): Number =
    rightAddMultipliedByDoublingInternalLogic(base, arg, multiplier, additionOp, additionOp)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [arg] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0uL`, the result is `base`.
 * - If `multiplier == 1uL`, the result is `additionToBaseOp(base, arg)`.
 * - If `multiplier == 2uL`, the result is `additionToBaseOp(base, additionOp(arg, arg))`.
 * - If `multiplier == 3uL`, the result is `additionToBaseOp(additionToBaseOp(base, arg), additionOp(arg, arg))`.
 * - If `multiplier == 4uL`, the result is `additionToBaseOp(base, additionOp(additionOp(arg, arg), additionOp(arg, arg)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOp(arg, arg)` are not calculated several times. Instead of
 * `additionOp(additionOp(arg, arg), additionOp(arg, arg))` actual computation is equivalent to
 * `additionOp(arg, arg).let { additionOp(it, it) }` that uses 2 calls of `additionOp` instead of three.
 *
 * So one can say that both [additionOp] and [additionToBaseOp] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
@PublishedApi
internal inline fun <Number> rightAddMultipliedByDoublingInternalLogic(base: Number, arg: Number, multiplier: ULong, additionOp: (Number, Number) -> Number, additionToBaseOp: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(base: N, arg: N, multiplier: ULong): N =
//        if (multiplier == 1uL) additionToBaseOp(base, arg)
//        else theLogic(
//            base = if (multiplier and 1uL == 0uL) base else additionToBaseOp(base, arg),
//            arg = additionOp(arg, arg),
//            multiplier = multiplier shr 1,
//        )
//    return if (multiplier == 0uL) base else theLogic(base, arg, multiplier)
    // Next is manually inlined code

    if (multiplier == 0uL) return base
    var base = base
    var arg = arg
    var multiplier = multiplier
    while (true) {
        if (multiplier == 1uL) additionToBaseOp(base, arg)
        else {
            base = if (multiplier and 1uL == 0uL) base else additionToBaseOp(base, arg)
            arg = additionOp(arg, arg)
            multiplier = multiplier shr 1
        }
    }
}

// endregion