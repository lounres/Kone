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