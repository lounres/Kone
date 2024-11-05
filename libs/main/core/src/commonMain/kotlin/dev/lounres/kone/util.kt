/*
 * Copyright © 2024 Gleb Minaev
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