/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMultiMutex<in Key> {
    public fun tryLockingFor(key: Key): Boolean
    public suspend fun awaitLockFor(key: Key)
    public fun tryUnlockingFor(key: Key): Boolean
}

public suspend fun <Key> KoneMultiMutex<Key>.awaitLockFor(keys: KoneIterable<Key>) {
    for (key in keys) awaitLockFor(key)
}

public suspend fun <Key> KoneMultiMutex<Key>.awaitLockFor(vararg keys: Key) {
    for (key in keys) awaitLockFor(key)
}

public suspend fun <Key> KoneMultiMutex<Key>.tryOrAwaitLockFor(key: Key) {
    if (!tryLockingFor(key)) awaitLockFor(key)
}

public suspend fun <Key> KoneMultiMutex<Key>.tryOrAwaitLockFor(keys: KoneIterable<Key>) {
    for (key in keys) if (!tryLockingFor(key)) awaitLockFor(key)
}

public suspend fun <Key> KoneMultiMutex<Key>.tryOrAwaitLockFor(vararg keys: Key) {
    for (key in keys) if (!tryLockingFor(key)) awaitLockFor(key)
}

public fun <Key> KoneMultiMutex<Key>.unlockFor(key: Key) {
    if (!tryUnlockingFor(key)) error("KoneMultiMutex is not locked for key $key")
}

public fun <Key> KoneMultiMutex<Key>.unlockFor(keys: KoneIterable<Key>) {
    for (key in keys) unlockFor(key)
}

public fun <Key> KoneMultiMutex<Key>.unlockFor(vararg keys: Key) {
    for (key in keys) unlockFor(key)
}

public suspend inline fun <Key, Result> KoneMultiMutex<Key>.withLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitLockFor(key)
    return try {
        action()
    } finally {
        unlockFor(key)
    }
}

public suspend fun <Key, Result> KoneMultiMutex<Key>.withLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    awaitLockFor(keys)
    return try {
        action()
    } finally {
        unlockFor(keys)
    }
}

public suspend fun <Key, Result> KoneMultiMutex<Key>.withLockFor(vararg keys: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    awaitLockFor(*keys)
    return try {
        action()
    } finally {
        unlockFor(*keys)
    }
}