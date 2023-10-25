/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic.util

import kotlin.math.abs


// TODO: Check if lambda boxing is as optimised as possible

// region Int

public inline fun <N> rightMultiplyByDoubling(arg: N, multiplier: Int, lazyZero: () -> N, additionOp: (N, N) -> N, negationOp: (N) -> N): N =
    if (multiplier >= 0) rightMultiplyByDoubling(arg, multiplier.toUInt(), lazyZero, additionOp)
    else rightMultiplyByDoubling(negationOp(arg), (-multiplier).toUInt(), lazyZero, additionOp)

public inline fun <N> rightAddMultipliedByDoubling(base: N, arg: N, multiplier: Int, additionOp: (N, N) -> N, negationOp: (N) -> N): N =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, arg, multiplier.toUInt(), additionOp)
    else rightAddMultipliedByDoubling(base, negationOp(arg), (-multiplier).toUInt(), additionOp)

public inline fun <N> rightAddMultipliedByDoubling(base: N, arg: N, multiplier: Int, additionOp: (N, N) -> N, rightSubtractionOp: (N, N) -> N): N =
    if(multiplier >= 0) rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toUInt(), additionOp, additionOp)
    else rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toUInt(), additionOp, rightSubtractionOp)

// endregion

// region UInt

@Suppress("NAME_SHADOWING")
public inline fun <N> rightMultiplyByDoubling(arg: N, multiplier: UInt, lazyZero: () -> N, additionOp: (N, N) -> N): N {
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

public inline fun <N> rightAddMultipliedByDoubling(base: N, arg: N, multiplier: UInt, additionOp: (N, N) -> N): N =
    rightAddMultipliedByDoublingInternalLogic(base, arg, multiplier, additionOp, additionOp)

@Suppress("NAME_SHADOWING")
@PublishedApi
internal inline fun <N> rightAddMultipliedByDoublingInternalLogic(base: N, arg: N, multiplier: UInt, additionOp: (N, N) -> N, additionToBaseOp: (N, N) -> N): N {
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

public inline fun <N> rightMultiplyByDoubling(arg: N, multiplier: Long, lazyZero: () -> N, additionOp: (N, N) -> N, negationOp: (N) -> N): N =
    if (multiplier >= 0) rightMultiplyByDoubling(arg, multiplier.toULong(), lazyZero, additionOp)
    else rightMultiplyByDoubling(negationOp(arg), (-multiplier).toULong(), lazyZero, additionOp)

public inline fun <N> rightAddMultipliedByDoubling(base: N, arg: N, multiplier: Long, additionOp: (N, N) -> N, negationOp: (N) -> N): N =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, arg, multiplier.toULong(), additionOp)
    else rightAddMultipliedByDoubling(base, negationOp(arg), (-multiplier).toULong(), additionOp)

public inline fun <N> rightAddMultipliedByDoubling(base: N, arg: N, multiplier: Long, additionOp: (N, N) -> N, rightSubtractionOp: (N, N) -> N): N =
    if(multiplier >= 0) rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toULong(), additionOp, additionOp)
    else rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toULong(), additionOp, rightSubtractionOp)

// endregion

// region ULong

@Suppress("NAME_SHADOWING")
public inline fun <N> rightMultiplyByDoubling(arg: N, multiplier: ULong, lazyZero: () -> N, additionOp: (N, N) -> N): N {
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

public inline fun <N> rightAddMultipliedByDoubling(base: N, arg: N, multiplier: ULong, additionOp: (N, N) -> N): N =
    rightAddMultipliedByDoublingInternalLogic(base, arg, multiplier, additionOp, additionOp)

@Suppress("NAME_SHADOWING")
@PublishedApi
internal inline fun <N> rightAddMultipliedByDoublingInternalLogic(base: N, arg: N, multiplier: ULong, additionOp: (N, N) -> N, additionToBaseOp: (N, N) -> N): N {
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