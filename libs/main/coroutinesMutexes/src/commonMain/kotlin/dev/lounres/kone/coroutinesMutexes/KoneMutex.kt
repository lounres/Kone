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
    @IgnorableReturnValue
    public fun tryUnlocking(): Boolean
}

public suspend fun KoneMutex.tryOrAwaitLock() {
    if (!tryLocking()) awaitLock()
}

public fun KoneMutex.unlock() {
    if (!tryUnlocking()) error("KoneMutex is not locked")
}

public suspend inline fun <Result> KoneMutex.withLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitLock()
    return try {
        action()
    } finally {
        unlock()
    }
}