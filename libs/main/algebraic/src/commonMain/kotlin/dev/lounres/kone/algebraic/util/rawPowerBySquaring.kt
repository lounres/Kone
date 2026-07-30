/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import kotlin.math.abs


// TODO: Check if lambda boxing is as optimised as possible

// region Int

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [argument] by integer [multiplier] if [multiplier] is positive, multiply `-argument` by [multiplier]
 * if [multiplier] is negative, or return `lazyZero()` if [multiplier] is zero.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0`, the result is `lazyZero()`.
 * - If `multiplier == 1`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - If `multiplier == -1`, the result is `additionOperation(base, negationOperation(argument))`.
 * - If `multiplier == -2`, the result is `additionOperation(base, additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -3`, the result is `additionOperation(additionOperation(base, negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -4`, the result is `additionOperation(base, additionOperation(additionOperation(negationOperation(argument), negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param argument The argument to multiply.
 * @param multiplier The integer multiplier.
 * @param lazyZero A lambda returning the zero element (used when [multiplier] is zero).
 * @param additionOperation The associative binary addition operation.
 * @param negationOperation The unary negation operation.
 * @return The result of multiplying [argument] by [multiplier].
 *
 * @usesMathJax
 */
public inline fun <Number> rightMultiplyByDoubling(argument: Number, multiplier: Int, lazyZero: () -> Number, additionOperation: (Number, Number) -> Number, negationOperation: (Number) -> Number): Number =
    if (multiplier >= 0) rightMultiplyByDoubling(argument, multiplier.toUInt(), lazyZero, additionOperation)
    else rightMultiplyByDoubling(negationOperation(argument), (-multiplier).toUInt(), lazyZero, additionOperation)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0`, the result is `base`.
 * - If `multiplier == 1`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - If `multiplier == -1`, the result is `additionOperation(base, negationOperation(argument))`.
 * - If `multiplier == -2`, the result is `additionOperation(base, additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -3`, the result is `additionOperation(additionOperation(base, negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -4`, the result is `additionOperation(base, additionOperation(additionOperation(negationOperation(argument), negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The integer multiplier.
 * @param additionOperation The associative binary addition operation.
 * @param negationOperation The unary negation operation.
 * @return The result of adding [argument] multiplied by [multiplier] to [base].
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, argument: Number, multiplier: Int, additionOperation: (Number, Number) -> Number, negationOperation: (Number) -> Number): Number =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, argument, multiplier.toUInt(), additionOperation)
    else rightAddMultipliedByDoubling(base, negationOperation(argument), (-multiplier).toUInt(), additionOperation)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0`, the result is `base`.
 * - If `multiplier == 1`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - If `multiplier == -1`, the result is `rightSubtractionOperation(base, argument)`.
 * - If `multiplier == -2`, the result is `rightSubtractionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == -3`, the result is `rightSubtractionOperation(rightSubtractionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == -4`, the result is `rightSubtractionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that both [additionOperation] and [rightSubtractionOperation] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which (or from which) the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The integer multiplier.
 * @param additionOperation The associative binary addition operation.
 * @param rightSubtractionOperation The binary subtraction operation (for negative [multiplier]).
 * @return The result of adding/subtracting [argument] multiplied by [multiplier] to/from [base].
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, argument: Number, multiplier: Int, additionOperation: (Number, Number) -> Number, rightSubtractionOperation: (Number, Number) -> Number): Number =
    if(multiplier >= 0) rightAddMultipliedByDoublingInternalLogic(base, argument, abs(multiplier).toUInt(), additionOperation, additionOperation)
    else rightAddMultipliedByDoublingInternalLogic(base, argument, abs(multiplier).toUInt(), additionOperation, rightSubtractionOperation)

// endregion

// region UInt

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [argument] by integer [multiplier] or return result of [lazyZero] if [multiplier] is `0uL`.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0u`, the result is `lazyZero()`.
 * - If `multiplier == 1u`, the result is `additionOperation(argument, argument)`.
 * - If `multiplier == 2u`, the result is `additionOperation(argument, additionOperation(argument, argument))`.
 * - If `multiplier == 3u`, the result is `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4u`, the result is `additionOperation(argument, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses two calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param argument The argument to multiply.
 * @param multiplier The non-negative integer multiplier.
 * @param lazyZero A lambda returning the zero element (used when [multiplier] is zero).
 * @param additionOperation The associative binary addition operation.
 * @return The result of multiplying [argument] by [multiplier].
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
public inline fun <Number> rightMultiplyByDoubling(argument: Number, multiplier: UInt, lazyZero: () -> Number, additionOperation: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(argument: N, multiplier: UInt): N =
//        when {
//            multiplier == 1u -> argument
//            multiplier and 1u == 0u -> theLogic(additionOperation(argument, argument), multiplier shr 1)
//            else -> rightAddMultipliedByDoubling(argument, additionOperation(argument, argument), multiplier shr 1, additionOperation)
//        }
//    return if (multiplier == 0u) lazyZero() else theLogic(argument, multiplier)
    // Next is manually inlined code

    if (multiplier == 0u) return lazyZero()
    var argument = argument
    var multiplier = multiplier
    while (true) {
        when {
            multiplier == 1u -> return argument
            multiplier and 1u == 0u -> {
                argument = additionOperation(argument, argument)
                multiplier = multiplier shr 1
            }
            else -> return rightAddMultipliedByDoubling(argument, additionOperation(argument, argument), multiplier shr 1, additionOperation)
        }
    }
}

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0u`, the result is `base`.
 * - If `multiplier == 1u`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2u`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3u`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4u`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The non-negative integer multiplier.
 * @param additionOperation The associative binary addition operation.
 * @return The result of adding [argument] multiplied by [multiplier] to [base].
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, argument: Number, multiplier: UInt, additionOperation: (Number, Number) -> Number): Number =
    rightAddMultipliedByDoublingInternalLogic(base, argument, multiplier, additionOperation, additionOperation)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0u`, the result is `base`.
 * - If `multiplier == 1u`, the result is `additionToBaseOperation(base, argument)`.
 * - If `multiplier == 2u`, the result is `additionToBaseOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3u`, the result is `additionToBaseOperation(additionToBaseOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4u`, the result is `additionToBaseOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that both [additionOperation] and [additionToBaseOperation] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The non-negative integer multiplier.
 * @param additionOperation The doubling operation (adds element to itself).
 * @param additionToBaseOperation The operation that adds the result to [base].
 * @return The result of adding [argument] multiplied by [multiplier] to [base].
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
@PublishedApi
internal inline fun <Number> rightAddMultipliedByDoublingInternalLogic(base: Number, argument: Number, multiplier: UInt, additionOperation: (Number, Number) -> Number, additionToBaseOperation: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(base: N, argument: N, multiplier: UInt): N =
//        if (multiplier == 1u) additionToBaseOperation(base, argument)
//        else theLogic(
//            base = if (multiplier and 1u == 0u) base else additionToBaseOperation(base, argument),
//            argument = additionOperation(argument, argument),
//            multiplier = multiplier shr 1,
//        )
//    return if (multiplier == 0u) base else theLogic(base, argument, multiplier)
    // Next is manually inlined code

    if (multiplier == 0u) return base
    var base = base
    var argument = argument
    var multiplier = multiplier
    while (true) {
        if (multiplier == 1u) return additionToBaseOperation(base, argument)
        else {
            base = if (multiplier and 1u == 0u) base else additionToBaseOperation(base, argument)
            argument = additionOperation(argument, argument)
            multiplier = multiplier shr 1
        }
    }
}

// endregion

// region Long

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [argument] by integer [multiplier] if [multiplier] is positive, multiply `-argument` by [multiplier]
 * if [multiplier] is negative, or return `lazyZero()` if [multiplier] is zero.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0L`, the result is `lazyZero()`.
 * - If `multiplier == 1L`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2L`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3L`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4L`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - If `multiplier == -1L`, the result is `additionOperation(base, negationOperation(argument))`.
 * - If `multiplier == -2L`, the result is `additionOperation(base, additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -3L`, the result is `additionOperation(additionOperation(base, negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -4L`, the result is `additionOperation(base, additionOperation(additionOperation(negationOperation(argument), negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param argument The argument to multiply.
 * @param multiplier The integer multiplier.
 * @param lazyZero A lambda returning the zero element (used when [multiplier] is zero).
 * @param additionOperation The associative binary addition operation.
 * @param negationOperation The unary negation operation.
 * @return The result of multiplying [argument] by [multiplier].
 *
 * @usesMathJax
 */
public inline fun <Number> rightMultiplyByDoubling(argument: Number, multiplier: Long, lazyZero: () -> Number, additionOperation: (Number, Number) -> Number, negationOperation: (Number) -> Number): Number =
    if (multiplier >= 0) rightMultiplyByDoubling(argument, multiplier.toULong(), lazyZero, additionOperation)
    else rightMultiplyByDoubling(negationOperation(argument), (-multiplier).toULong(), lazyZero, additionOperation)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0L`, the result is `base`.
 * - If `multiplier == 1L`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2L`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3L`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4L`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - If `multiplier == -1L`, the result is `additionOperation(base, negationOperation(argument))`.
 * - If `multiplier == -2L`, the result is `additionOperation(base, additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -3L`, the result is `additionOperation(additionOperation(base, negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument)))`.
 * - If `multiplier == -4L`, the result is `additionOperation(base, additionOperation(additionOperation(negationOperation(argument), negationOperation(argument)), additionOperation(negationOperation(argument), negationOperation(argument))))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The integer multiplier.
 * @param additionOperation The associative binary addition operation.
 * @param negationOperation The unary negation operation.
 * @return The result of adding [argument] multiplied by [multiplier] to [base].
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, argument: Number, multiplier: Long, additionOperation: (Number, Number) -> Number, negationOperation: (Number) -> Number): Number =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, argument, multiplier.toULong(), additionOperation)
    else rightAddMultipliedByDoubling(base, negationOperation(argument), (-multiplier).toULong(), additionOperation)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0L`, the result is `base`.
 * - If `multiplier == 1L`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2L`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3L`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4L`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - If `multiplier == -1L`, the result is `rightSubtractionOperation(base, argument)`.
 * - If `multiplier == -2L`, the result is `rightSubtractionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == -3L`, the result is `rightSubtractionOperation(rightSubtractionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == -4L`, the result is `rightSubtractionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that both [additionOperation] and [rightSubtractionOperation] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which (or from which) the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The integer multiplier.
 * @param additionOperation The associative binary addition operation.
 * @param rightSubtractionOperation The binary subtraction operation (for negative [multiplier]).
 * @return The result of adding/subtracting [argument] multiplied by [multiplier] to/from [base].
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, argument: Number, multiplier: Long, additionOperation: (Number, Number) -> Number, rightSubtractionOperation: (Number, Number) -> Number): Number =
    if(multiplier >= 0) rightAddMultipliedByDoublingInternalLogic(base, argument, abs(multiplier).toULong(), additionOperation, additionOperation)
    else rightAddMultipliedByDoublingInternalLogic(base, argument, abs(multiplier).toULong(), additionOperation, rightSubtractionOperation)

// endregion

// region ULong

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to multiply argument [argument] by integer [multiplier] or return result of [lazyZero] if [multiplier] is `0uL`.
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0uL`, the result is `lazyZero()`.
 * - If `multiplier == 1uL`, the result is `additionOperation(argument, argument)`.
 * - If `multiplier == 2uL`, the result is `additionOperation(argument, additionOperation(argument, argument))`.
 * - If `multiplier == 3uL`, the result is `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4uL`, the result is `additionOperation(argument, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses two calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param argument The argument to multiply.
 * @param multiplier The non-negative integer multiplier.
 * @param lazyZero A lambda returning the zero element (used when [multiplier] is zero).
 * @param additionOperation The associative binary addition operation.
 * @return The result of multiplying [argument] by [multiplier].
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
public inline fun <Number> rightMultiplyByDoubling(argument: Number, multiplier: ULong, lazyZero: () -> Number, additionOperation: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(argument: N, multiplier: ULong): N =
//        when {
//            multiplier == 1uL -> argument
//            multiplier and 1uL == 0uL -> theLogic(additionOperation(argument, argument), multiplier shr 1)
//            else -> rightAddMultipliedByDoubling(argument, additionOperation(argument, argument), multiplier shr 1, additionOperation)
//        }
//    return if (multiplier == 0uL) lazyZero() else theLogic(argument, multiplier)
    // Next is manually inlined code

    if (multiplier == 0uL) return lazyZero()
    var argument = argument
    var multiplier = multiplier
    while (true) {
        when {
            multiplier == 1uL -> return argument
            multiplier and 1uL == 0uL -> {
                argument = additionOperation(argument, argument)
                multiplier = multiplier shr 1
            }
            else -> return rightAddMultipliedByDoubling(argument, additionOperation(argument, argument), multiplier shr 1, additionOperation)
        }
    }
}

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0uL`, the result is `base`.
 * - If `multiplier == 1uL`, the result is `additionOperation(base, argument)`.
 * - If `multiplier == 2uL`, the result is `additionOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3uL`, the result is `additionOperation(additionOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4uL`, the result is `additionOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that [additionOperation] is used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The non-negative integer multiplier.
 * @param additionOperation The associative binary addition operation.
 * @return The result of adding [argument] multiplied by [multiplier] to [base].
 *
 * @usesMathJax
 */
public inline fun <Number> rightAddMultipliedByDoubling(base: Number, argument: Number, multiplier: ULong, additionOperation: (Number, Number) -> Number): Number =
    rightAddMultipliedByDoublingInternalLogic(base, argument, multiplier, additionOperation, additionOperation)

/**
 * Applies multiplication-by-doubling algorithm (a.k.a. [exponentiation by squaring](https://en.wikipedia.org/wiki/Exponentiation_by_squaring))
 * to add argument [argument] multiplied by integer [multiplier] to the right of [base].
 *
 * For example here are resulting expressions for the following values of [multiplier]:
 * - If `multiplier == 0uL`, the result is `base`.
 * - If `multiplier == 1uL`, the result is `additionToBaseOperation(base, argument)`.
 * - If `multiplier == 2uL`, the result is `additionToBaseOperation(base, additionOperation(argument, argument))`.
 * - If `multiplier == 3uL`, the result is `additionToBaseOperation(additionToBaseOperation(base, argument), additionOperation(argument, argument))`.
 * - If `multiplier == 4uL`, the result is `additionToBaseOperation(base, additionOperation(additionOperation(argument, argument), additionOperation(argument, argument)))`.
 * - And so on...
 *
 * But actually such sub-expression like `additionOperation(argument, argument)` are not calculated several times. Instead of
 * `additionOperation(additionOperation(argument, argument), additionOperation(argument, argument))` actual computation is equivalent to
 * `additionOperation(argument, argument).let { additionOperation(it, it) }` that uses 2 calls of `additionOperation` instead of three.
 *
 * So one can say that both [additionOperation] and [additionToBaseOperation] are used \(O(\log(\mathrm{multiplier}))\) times.
 *
 * @param Number The type of elements being manipulated.
 * @param base The base to which the multiplied result is added.
 * @param argument The argument to multiply by [multiplier].
 * @param multiplier The non-negative integer multiplier.
 * @param additionOperation The doubling operation (adds element to itself).
 * @param additionToBaseOperation The operation that adds the result to [base].
 * @return The result of adding [argument] multiplied by [multiplier] to [base].
 *
 * @usesMathJax
 */
@Suppress("NAME_SHADOWING")
@PublishedApi
internal inline fun <Number> rightAddMultipliedByDoublingInternalLogic(base: Number, argument: Number, multiplier: ULong, additionOperation: (Number, Number) -> Number, additionToBaseOperation: (Number, Number) -> Number): Number {
    // FIXME: KT-17579
//    tailrec fun theLogic(base: N, argument: N, multiplier: ULong): N =
//        if (multiplier == 1uL) additionToBaseOperation(base, argument)
//        else theLogic(
//            base = if (multiplier and 1uL == 0uL) base else additionToBaseOperation(base, argument),
//            argument = additionOperation(argument, argument),
//            multiplier = multiplier shr 1,
//        )
//    return if (multiplier == 0uL) base else theLogic(base, argument, multiplier)
    // Next is manually inlined code

    if (multiplier == 0uL) return base
    var base = base
    var argument = argument
    var multiplier = multiplier
    while (true) {
        if (multiplier == 1uL) return additionToBaseOperation(base, argument)
        else {
            base = if (multiplier and 1uL == 0uL) base else additionToBaseOperation(base, argument)
            argument = additionOperation(argument, argument)
            multiplier = multiplier shr 1
        }
    }
}

// endregion