/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.algebraic.util

import kotlin.math.abs


// TODO: Check if lambda boxing is as optimised as possible

// region Int

public fun <C> rightMultiplyByDoubling(arg: C, multiplier: Int, lazyZero: () -> C, additionOp: (C, C) -> C, negationOp: (C) -> C): C =
    if (multiplier >= 0) rightMultiplyByDoubling(arg, multiplier.toUInt(), lazyZero, additionOp)
    else rightMultiplyByDoubling(negationOp(arg), (-multiplier).toUInt(), lazyZero, additionOp)

public fun <C> rightAddMultipliedByDoubling(base: C, arg: C, multiplier: Int, additionOp: (C, C) -> C, negationOp: (C) -> C): C =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, arg, multiplier.toUInt(), additionOp)
    else rightAddMultipliedByDoubling(base, negationOp(arg), (-multiplier).toUInt(), additionOp)

public fun <C> rightAddMultipliedByDoubling(base: C, arg: C, multiplier: Int, additionOp: (C, C) -> C, rightSubtractionOp: (C, C) -> C): C =
    rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toUInt(), additionOp, if(multiplier >= 0) additionOp else rightSubtractionOp)

// endregion

// region UInt

public fun <C> rightMultiplyByDoubling(arg: C, multiplier: UInt, lazyZero: () -> C, additionOp: (C, C) -> C): C {
    tailrec fun theLogic(arg: C, multiplier: UInt): C =
        when {
            multiplier == 0u -> lazyZero()
            multiplier == 1u -> arg
            multiplier and 1u == 0u -> theLogic(additionOp(arg, arg), multiplier shr 1)
            multiplier and 1u == 1u -> rightAddMultipliedByDoubling(arg, additionOp(arg, arg), multiplier shr 1, additionOp)
            else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
        }
    return theLogic(arg, multiplier)
}

public fun <C> rightAddMultipliedByDoubling(base: C, arg: C, multiplier: UInt, additionOp: (C, C) -> C): C =
    rightAddMultipliedByDoublingInternalLogic(base, arg, multiplier, additionOp, additionOp)

internal fun <C> rightAddMultipliedByDoublingInternalLogic(base: C, arg: C, multiplier: UInt, additionOp: (C, C) -> C, additionToBaseOp: (C, C) -> C): C {
    tailrec fun theLogic(base: C, arg: C, multiplier: UInt): C =
        when {
            multiplier == 0u -> base
            multiplier == 1u -> additionToBaseOp(base, arg)
            multiplier and 1u == 0u -> theLogic(base, additionOp(arg, arg), multiplier shr 1)
            multiplier and 1u == 1u -> theLogic(additionToBaseOp(base, arg), additionOp(arg, arg), multiplier shr 1)
            else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
        }
    return theLogic(base, arg, multiplier)
}

// endregion

// region Long

public fun <C> rightMultiplyByDoubling(arg: C, multiplier: Long, lazyZero: () -> C, additionOp: (C, C) -> C, negationOp: (C) -> C): C =
    if (multiplier >= 0) rightMultiplyByDoubling(arg, multiplier.toUInt(), lazyZero, additionOp)
    else rightMultiplyByDoubling(negationOp(arg), (-multiplier).toUInt(), lazyZero, additionOp)

public fun <C> rightAddMultipliedByDoubling(base: C, arg: C, multiplier: Long, additionOp: (C, C) -> C, negationOp: (C) -> C): C =
    if (multiplier >= 0) rightAddMultipliedByDoubling(base, arg, multiplier.toUInt(), additionOp)
    else rightAddMultipliedByDoubling(base, negationOp(arg), (-multiplier).toUInt(), additionOp)

public fun <C> rightAddMultipliedByDoubling(base: C, arg: C, multiplier: Long, additionOp: (C, C) -> C, rightSubtractionOp: (C, C) -> C): C =
    rightAddMultipliedByDoublingInternalLogic(base, arg, abs(multiplier).toUInt(), additionOp, if(multiplier >= 0) additionOp else rightSubtractionOp)

// endregion

// region ULong

public fun <C> rightMultiplyByDoubling(arg: C, multiplier: ULong, lazyZero: () -> C, additionOp: (C, C) -> C): C {
    tailrec fun theLogic(arg: C, multiplier: ULong): C =
        when {
            multiplier == 0uL -> lazyZero()
            multiplier == 1uL -> arg
            multiplier and 1uL == 0uL -> theLogic(additionOp(arg, arg), multiplier shr 1)
            multiplier and 1uL == 1uL -> rightAddMultipliedByDoubling(arg, additionOp(arg, arg), multiplier shr 1, additionOp)
            else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
        }
    return theLogic(arg, multiplier)
}

public fun <C> rightAddMultipliedByDoubling(base: C, arg: C, multiplier: ULong, additionOp: (C, C) -> C): C =
    rightAddMultipliedByDoublingInternalLogic(base, arg, multiplier, additionOp, additionOp)

internal fun <C> rightAddMultipliedByDoublingInternalLogic(base: C, arg: C, multiplier: ULong, additionOp: (C, C) -> C, additionToBaseOp: (C, C) -> C): C {
    tailrec fun theLogic(base: C, arg: C, multiplier: ULong): C =
        when {
            multiplier == 0uL -> base
            multiplier == 1uL -> additionToBaseOp(base, arg)
            multiplier and 1uL == 0uL -> theLogic(base, additionOp(arg, arg), multiplier shr 1)
            multiplier and 1uL == 1uL -> theLogic(additionToBaseOp(base, arg), additionOp(arg, arg), multiplier shr 1)
            else -> error("Error in multiplication group instant by unsigned integer: got reminder by division by 2 different from 0 and 1")
        }
    return theLogic(base, arg, multiplier)
}

// endregion