/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.utils.filter
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMutualMultiExclusion<in Key> {
    public fun tryLockingFor(key: Key): Boolean
    public suspend fun awaitLockFor(key: Key)
    public fun tryUnlockingFor(key: Key): Boolean
}

public suspend fun <Key> KoneMutualMultiExclusion<Key>.awaitLockFor(keys: KoneIterable<Key>) {
    for (key in keys) awaitLockFor(key)
}

public suspend fun <Key> KoneMutualMultiExclusion<Key>.awaitLockFor(vararg keys: Key) {
    for (key in keys) awaitLockFor(key)
}

public suspend fun <Key> KoneMutualMultiExclusion<Key>.tryOrAwaitLockFor(key: Key) {
    if (!tryLockingFor(key)) awaitLockFor(key)
}

public suspend fun <Key> KoneMutualMultiExclusion<Key>.tryOrAwaitLockFor(keys: KoneIterable<Key>) {
    for (key in keys) if (!tryLockingFor(key)) awaitLockFor(key)
}

public suspend fun <Key> KoneMutualMultiExclusion<Key>.tryOrAwaitLockFor(vararg keys: Key) {
    for (key in keys) if (!tryLockingFor(key)) awaitLockFor(key)
}

public fun <Key> KoneMutualMultiExclusion<Key>.unlockFor(key: Key) {
    if (!tryUnlockingFor(key)) error("KoneMutualMultiExclusion is not locked for key $key")
}

public fun <Key> KoneMutualMultiExclusion<Key>.unlockFor(keys: KoneIterable<Key>) {
    val failedKeys = keys.filter { !tryUnlockingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneMutualMultiExclusion is not locked for keys $failedKeys")
}

public fun <Key> KoneMutualMultiExclusion<Key>.unlockFor(vararg keys: Key) {
    val failedKeys = KoneArray(keys).filter { !tryUnlockingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneMutualMultiExclusion is not locked for keys $failedKeys")
}

public suspend inline fun <Key, Result> KoneMutualMultiExclusion<Key>.withLockFor(key: Key, action: () -> Result): Result {
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

public suspend fun <Key, Result> KoneMutualMultiExclusion<Key>.withLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
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

public suspend fun <Key, Result> KoneMutualMultiExclusion<Key>.withLockFor(vararg keys: Key, action: () -> Result): Result {
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