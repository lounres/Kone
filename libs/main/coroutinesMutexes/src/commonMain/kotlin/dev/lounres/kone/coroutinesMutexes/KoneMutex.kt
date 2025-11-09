/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMutex {
    public fun tryLocking(): Boolean
    public suspend fun awaitLock()
    public fun unlock()
}

public suspend inline fun <Result> KoneMutex.withLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    awaitLock()
    return try {
        action()
    } finally {
        unlock()
    }
}