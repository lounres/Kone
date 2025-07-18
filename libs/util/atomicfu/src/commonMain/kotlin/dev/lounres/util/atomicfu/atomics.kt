/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.util.atomicfu

import kotlinx.atomicfu.locks.ReentrantLock
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlinx.atomicfu.locks.withLock as originalWithLock


@Suppress("WRONG_INVOCATION_KIND")
public inline fun <Result> ReentrantLock.withLock(block: () -> Result): Result {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return originalWithLock { block() }
}