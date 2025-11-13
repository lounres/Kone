/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneSemaphore {
    public fun tryAcquiring(): Boolean
    public suspend fun awaitAcquire()
    @IgnorableReturnValue
    public fun tryReleasing(): Boolean
}

public suspend fun KoneSemaphore.tryOrAwaitAcquire() {
    if (!tryAcquiring()) awaitAcquire()
}

public fun KoneSemaphore.release() {
    if (!tryReleasing()) error("KoneMutex is not locked")
}

public suspend inline fun <Result> KoneSemaphore.withPermit(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitAcquire()
    return try {
        action()
    } finally {
        release()
    }
}