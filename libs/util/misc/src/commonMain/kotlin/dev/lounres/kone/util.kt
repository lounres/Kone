/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone

import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Simple function that is useful to create nested scopes.
 * It is the same as the [run] function but without extension-function overload.
 */
@IgnorableReturnValue
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

public inline fun <reified T> Any?.checkCast(): Boolean = this is T

public inline fun <reified T> Any?.cast(): T = this as T

public inline fun <reified T> Any?.castOrNull(): T? = this as? T

public inline fun <reified T> Any?.castMaybe(): Maybe<T> = if (this is T) Some(this) else None

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Result> context(
    context1: Context1,
    context2: Context2,
    context3: Context3,
    context4: Context4,
    context5: Context5,
    context6: Context6,
    context7: Context7,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        context1,
        context2,
        context3,
        context4,
        context5,
        context6,
        context7,
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Result> context(
    context1: Context1,
    context2: Context2,
    context3: Context3,
    context4: Context4,
    context5: Context5,
    context6: Context6,
    context7: Context7,
    context8: Context8,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        context1,
        context2,
        context3,
        context4,
        context5,
        context6,
        context7,
        context8,
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Result> context(
    context1: Context1,
    context2: Context2,
    context3: Context3,
    context4: Context4,
    context5: Context5,
    context6: Context6,
    context7: Context7,
    context8: Context8,
    context9: Context9,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        context1,
        context2,
        context3,
        context4,
        context5,
        context6,
        context7,
        context8,
        context9,
    )
}

public inline fun <Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10, Result> context(
    context1: Context1,
    context2: Context2,
    context3: Context3,
    context4: Context4,
    context5: Context5,
    context6: Context6,
    context7: Context7,
    context8: Context8,
    context9: Context9,
    context10: Context10,
    block: context(Context1, Context2, Context3, Context4, Context5, Context6, Context7, Context8, Context9, Context10) () -> Result
): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        context1,
        context2,
        context3,
        context4,
        context5,
        context6,
        context7,
        context8,
        context9,
        context10,
    )
}