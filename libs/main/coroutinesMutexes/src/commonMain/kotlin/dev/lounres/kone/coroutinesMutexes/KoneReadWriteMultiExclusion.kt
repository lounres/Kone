/*
 * Copyright © 2025 Gleb Minaev
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


public interface KoneReadWriteMultiExclusion<in Key> {
    public fun tryReadLockingFor(key: Key): Boolean
    public fun tryWriteLockingFor(key: Key): Boolean
    public suspend fun awaitReadLockFor(key: Key)
    public suspend fun awaitWriteLockFor(key: Key)
    @IgnorableReturnValue
    public fun tryReadUnlockingFor(key: Key): Boolean
    @IgnorableReturnValue
    public fun tryWriteUnlockingFor(key: Key): Boolean
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitReadLockFor(keys: KoneIterable<Key>) {
    for (key in keys) awaitReadLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitReadLockFor(vararg keys: Key) {
    for (key in keys) awaitReadLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitWriteLockFor(keys: KoneIterable<Key>) {
    for (key in keys) awaitWriteLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitWriteLockFor(vararg keys: Key) {
    for (key in keys) awaitWriteLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitReadLockFor(key: Key) {
    if (!tryReadLockingFor(key)) awaitReadLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitReadLockFor(keys: KoneIterable<Key>) {
    for (key in keys) tryOrAwaitReadLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitReadLockFor(vararg keys: Key) {
    for (key in keys) tryOrAwaitReadLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitWriteLockFor(key: Key) {
    if (!tryWriteLockingFor(key)) awaitWriteLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitWriteLockFor(keys: KoneIterable<Key>) {
    for (key in keys) tryOrAwaitWriteLockFor(key)
}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitWriteLockFor(vararg keys: Key) {
    for (key in keys) tryOrAwaitWriteLockFor(key)
}

public fun <Key> KoneReadWriteMultiExclusion<Key>.readUnlockFor(key: Key) {
    if (!tryReadUnlockingFor(key)) error("KoneReadWriteMultiExclusion is not read-locked for key $key")
}

public fun <Key> KoneReadWriteMultiExclusion<Key>.readUnlockFor(keys: KoneIterable<Key>) {
    val failedKeys = keys.filter { !tryReadUnlockingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneReadWriteMultiExclusion is not read-locked for keys $failedKeys")
}

public fun <Key> KoneReadWriteMultiExclusion<Key>.readUnlockFor(vararg keys: Key) {
    val failedKeys = KoneArray(keys).filter { !tryReadUnlockingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneReadWriteMultiExclusion is not read-locked for keys $failedKeys")
}

public fun <Key> KoneReadWriteMultiExclusion<Key>.writeUnlockFor(key: Key) {
    if (!tryWriteUnlockingFor(key)) error("KoneReadWriteMultiExclusion is not write-locked for key $key")
}

public fun <Key> KoneReadWriteMultiExclusion<Key>.writeUnlockFor(keys: KoneIterable<Key>) {
    val failedKeys = keys.filter { !tryWriteUnlockingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneReadWriteMultiExclusion is not write-locked for keys $failedKeys")
}

public fun <Key> KoneReadWriteMultiExclusion<Key>.writeUnlockFor(vararg keys: Key) {
    val failedKeys = keys.filter { !tryWriteUnlockingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneReadWriteMultiExclusion is not write-locked for keys $failedKeys")
}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withReadLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitReadLockFor(key)
    return try {
        action()
    } finally {
        readUnlockFor(key)
    }
}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withReadLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitReadLockFor(keys)
    return try {
        action()
    } finally {
        readUnlockFor(keys)
    }
}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withReadLockFor(vararg keys: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitReadLockFor(*keys)
    return try {
        action()
    } finally {
        readUnlockFor(*keys)
    }
}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withWriteLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitWriteLockFor(key)
    return try {
        action()
    } finally {
        writeUnlockFor(key)
    }
}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withWriteLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitWriteLockFor(keys)
    return try {
        action()
    } finally {
        writeUnlockFor(keys)
    }
}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withWriteLockFor(vararg keys: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitWriteLockFor(*keys)
    return try {
        action()
    } finally {
        writeUnlockFor(*keys)
    }
}