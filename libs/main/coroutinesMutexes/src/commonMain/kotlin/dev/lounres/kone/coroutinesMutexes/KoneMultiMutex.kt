/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMultiMutex<in Key> {
    public fun tryLockingFor(key: Key): Boolean
    public suspend fun awaitLockFor(key: Key)
    public fun unlockFor(key: Key)
}

public suspend inline fun <Key, Result> KoneMultiMutex<Key>.withLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    awaitLockFor(key)
    return try {
        action()
    } finally {
        unlockFor(key)
    }
}