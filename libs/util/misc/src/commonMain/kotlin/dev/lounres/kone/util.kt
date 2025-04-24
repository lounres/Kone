/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Simple function that is useful to create nested scopes.
 * It is the same as the [run] function but without extension-function overload.
 */
public inline fun <Result> scope(block: () -> Result): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block()
}

/**
 * Runs the [action] the [times] number of times providing arguments `0u`, `1u`, ..., `times-1u` consequently into it.
 *
 * It's a copy of [repeat] from Kotlin stdlib but for `UInt` argument.
 */
public inline fun repeat(times: UInt, action: (UInt) -> Unit) {
    for (index in 0u ..< times) action(index)
}

/**
 * Runs the [block].
 *
 * It's an analogue to [with] but for context parameter.
 */
public inline fun <Result> context(block: () -> Result): Result =
    block()

/**
 * Runs the [block] with the provided context parameter [context1].
 *
 * It's an analogue to [with] but for context parameter.
 */
public inline fun <Context1, Result> context(context1: Context1, block: context(Context1) () -> Result): Result =
    block(context1)

/**
 * Runs the [block] with the provided context parameters [context1], [context2].
 *
 * It's an analogue to [with] but for context parameters.
 */
public inline fun <Context1, Context2, Result> context(context1: Context1, context2: Context2, block: context(Context1, Context2) () -> Result): Result =
    block(context1, context2)

/**
 * Runs the [block] with the provided context parameters [context1], [context2], [context3].
 *
 * It's an analogue to [with] but for context parameters.
 */
public inline fun <Context1, Context2, Context3, Result> context(context1: Context1, context2: Context2, context3: Context3, block: context(Context1, Context2, Context3) () -> Result): Result =
    block(context1, context2, context3)

//context(context: Context)
//@Suppress("NOTHING_TO_INLINE")
//public inline fun <Context> inline(): Context = context