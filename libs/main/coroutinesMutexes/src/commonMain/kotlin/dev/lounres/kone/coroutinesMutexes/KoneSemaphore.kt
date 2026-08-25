/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneSemaphore {
    public fun tryAcquiring(): KoneLock?
    public suspend fun awaitAcquire(): KoneLock
}

public suspend fun KoneSemaphore.tryOrAwaitAcquire(): KoneLock = tryAcquiring() ?: awaitAcquire()

public suspend inline fun <Result> KoneSemaphore.withAcquisition(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitAcquire()
    return try {
        action()
    } finally {
        lock.release()
    }
}