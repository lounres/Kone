/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneReadWriteExclusion {
    public fun tryReadLocking(): Boolean
    public fun tryWriteLocking(): Boolean
    public suspend fun awaitReadLock()
    public suspend fun awaitWriteLock()
    @IgnorableReturnValue
    public fun tryReadUnlocking(): Boolean
    @IgnorableReturnValue
    public fun tryWriteUnlocking(): Boolean
}

public suspend fun KoneReadWriteExclusion.tryOrAwaitReadLock() {
    if (!tryReadLocking()) awaitReadLock()
}

public suspend fun KoneReadWriteExclusion.tryOrAwaitWriteLock() {
    if (!tryWriteLocking()) awaitWriteLock()
}

public fun KoneReadWriteExclusion.readUnlock() {
    if (!tryReadUnlocking()) error("KoneReadWriteExclusion is not locked")
}

public fun KoneReadWriteExclusion.writeUnlock() {
    if (!tryWriteUnlocking()) error("KoneReadWriteExclusion is not locked")
}

public suspend inline fun <Result> KoneReadWriteExclusion.withReadLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitReadLock()
    return try {
        action()
    } finally {
        readUnlock()
    }
}

public suspend inline fun <Result> KoneReadWriteExclusion.withWriteLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitWriteLock()
    return try {
        action()
    } finally {
        writeUnlock()
    }
}

public fun KoneReadWriteExclusion.asReadSemaphore(): KoneSemaphore =
    object : KoneSemaphore {
        override fun tryAcquiring(): Boolean  = tryReadLocking()
        override suspend fun awaitAcquire() {
            awaitReadLock()
        }
        override fun tryReleasing(): Boolean = tryReadUnlocking()
    }

public fun KoneReadWriteExclusion.asWriteMutex(): KoneMutualExclusion =
    object : KoneMutualExclusion {
        override fun tryLocking(): Boolean = tryWriteLocking()
        override suspend fun awaitLock() {
            awaitWriteLock()
        }
        override fun tryUnlocking(): Boolean = tryWriteUnlocking()
    }