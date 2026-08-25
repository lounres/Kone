/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneReadWriteMultiExclusion<in Key> {
    public fun tryReadLockingFor(key: Key): KoneLock?
    public suspend fun awaitReadLockFor(key: Key): KoneLock
    public fun tryWriteLockingFor(key: Key): KoneLock?
    public suspend fun awaitWriteLockFor(key: Key): KoneLock
}

// TODO
//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitReadLockFor(keys: KoneIterable<Key>) {
//    for (key in keys) awaitReadLockFor(key)
//}
//
//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitReadLockFor(vararg keys: Key) {
//    for (key in keys) awaitReadLockFor(key)
//}
//
//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitWriteLockFor(keys: KoneIterable<Key>) {
//    for (key in keys) awaitWriteLockFor(key)
//}
//
//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.awaitWriteLockFor(vararg keys: Key) {
//    for (key in keys) awaitWriteLockFor(key)
//}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitReadLockFor(key: Key): KoneLock = tryReadLockingFor(key) ?: awaitReadLockFor(key)

//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitReadLockFor(keys: KoneIterable<Key>) {
//    for (key in keys) tryOrAwaitReadLockFor(key)
//}
//
//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitReadLockFor(vararg keys: Key) {
//    for (key in keys) tryOrAwaitReadLockFor(key)
//}

public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitWriteLockFor(key: Key): KoneLock = tryWriteLockingFor(key) ?: awaitWriteLockFor(key)

//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitWriteLockFor(keys: KoneIterable<Key>) {
//    for (key in keys) tryOrAwaitWriteLockFor(key)
//}
//
//public suspend fun <Key> KoneReadWriteMultiExclusion<Key>.tryOrAwaitWriteLockFor(vararg keys: Key) {
//    for (key in keys) tryOrAwaitWriteLockFor(key)
//}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withReadLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitReadLockFor(key)
    return try {
        action()
    } finally {
        lock.release()
    }
}

//public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withReadLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    tryOrAwaitReadLockFor(keys)
//    return try {
//        action()
//    } finally {
//        readUnlockFor(keys)
//    }
//}
//
//public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withReadLockFor(vararg keys: Key, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    tryOrAwaitReadLockFor(*keys)
//    return try {
//        action()
//    } finally {
//        readUnlockFor(*keys)
//    }
//}

public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withWriteLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitWriteLockFor(key)
    return try {
        action()
    } finally {
        lock.release()
    }
}

//public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withWriteLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    tryOrAwaitWriteLockFor(keys)
//    return try {
//        action()
//    } finally {
//        writeUnlockFor(keys)
//    }
//}
//
//public suspend inline fun <Key, Result> KoneReadWriteMultiExclusion<Key>.withWriteLockFor(vararg keys: Key, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    tryOrAwaitWriteLockFor(*keys)
//    return try {
//        action()
//    } finally {
//        writeUnlockFor(*keys)
//    }
//}