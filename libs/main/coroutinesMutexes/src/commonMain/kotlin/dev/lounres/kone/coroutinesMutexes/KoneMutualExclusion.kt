/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMutualExclusion {
    public fun tryLocking(): KoneLock?
    public suspend fun awaitLock(): KoneLock
}

public suspend fun KoneMutualExclusion.tryOrAwaitLock(): KoneLock = tryLocking() ?: awaitLock()

public suspend inline fun <Result> KoneMutualExclusion.withLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitLock()
    return try {
        action()
    } finally {
        lock.release()
    }
}