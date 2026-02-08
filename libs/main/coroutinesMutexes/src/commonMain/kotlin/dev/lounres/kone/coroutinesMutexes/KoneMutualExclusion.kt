/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMutualExclusion {
    public fun tryLocking(): Boolean
    public suspend fun awaitLock()
    @IgnorableReturnValue
    public fun tryUnlocking(): Boolean
}

public suspend fun KoneMutualExclusion.tryOrAwaitLock() {
    if (!tryLocking()) awaitLock()
}

public fun KoneMutualExclusion.unlock() {
    if (!tryUnlocking()) error("KoneMutualExclusion is not locked")
}

public suspend inline fun <Result> KoneMutualExclusion.withLock(action: () -> Result): Result {
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