/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneReadWriteExclusion {
    public fun tryReadLocking(): KoneLock?
    public suspend fun awaitReadLock(): KoneLock
    public fun tryWriteLocking(): KoneLock?
    public suspend fun awaitWriteLock(): KoneLock
}

public suspend fun KoneReadWriteExclusion.tryOrAwaitReadLock(): KoneLock = tryReadLocking() ?: awaitReadLock()

public suspend fun KoneReadWriteExclusion.tryOrAwaitWriteLock(): KoneLock = tryWriteLocking() ?: awaitWriteLock()

public suspend inline fun <Result> KoneReadWriteExclusion.withReadLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitReadLock()
    return try {
        action()
    } finally {
        lock.release()
    }
}

public suspend inline fun <Result> KoneReadWriteExclusion.withWriteLock(action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitWriteLock()
    return try {
        action()
    } finally {
        lock.release()
    }
}

public fun KoneReadWriteExclusion.asReadSemaphore(): KoneSemaphore =
    object : KoneSemaphore {
        override fun tryAcquiring(): KoneLock? = tryReadLocking()
        override suspend fun awaitAcquire(): KoneLock = awaitReadLock()
    }

public fun KoneReadWriteExclusion.asWriteMutex(): KoneMutualExclusion =
    object : KoneMutualExclusion {
        override fun tryLocking(): KoneLock? = tryWriteLocking()
        override suspend fun awaitLock(): KoneLock = awaitWriteLock()
    }