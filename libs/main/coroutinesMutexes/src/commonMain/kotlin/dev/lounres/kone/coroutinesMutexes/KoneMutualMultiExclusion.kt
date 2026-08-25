/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMutualMultiExclusion<in Key> {
    public fun tryLockingFor(key: Key): KoneLock?
    public suspend fun awaitLockFor(key: Key): KoneLock
}

// TODO
//public suspend fun <Key> KoneMutualMultiExclusion<Key>.awaitLockFor(keys: KoneIterable<Key>) {
//    for (key in keys) awaitLockFor(key)
//}
//
//public suspend fun <Key> KoneMutualMultiExclusion<Key>.awaitLockFor(vararg keys: Key) {
//    for (key in keys) awaitLockFor(key)
//}

public suspend fun <Key> KoneMutualMultiExclusion<Key>.tryOrAwaitLockFor(key: Key): KoneLock = tryLockingFor(key) ?: awaitLockFor(key)

//public suspend fun <Key> KoneMutualMultiExclusion<Key>.tryOrAwaitLockFor(keys: KoneIterable<Key>) {
//    for (key in keys) if (!tryLockingFor(key)) awaitLockFor(key)
//}
//
//public suspend fun <Key> KoneMutualMultiExclusion<Key>.tryOrAwaitLockFor(vararg keys: Key) {
//    for (key in keys) if (!tryLockingFor(key)) awaitLockFor(key)
//}

public suspend inline fun <Key, Result> KoneMutualMultiExclusion<Key>.withLockFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitLockFor(key)
    return try {
        action()
    } finally {
        lock.release()
    }
}

//public suspend fun <Key, Result> KoneMutualMultiExclusion<Key>.withLockFor(keys: KoneIterable<Key>, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    awaitLockFor(keys)
//    return try {
//        action()
//    } finally {
//        unlockFor(keys)
//    }
//}
//
//public suspend fun <Key, Result> KoneMutualMultiExclusion<Key>.withLockFor(vararg keys: Key, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    awaitLockFor(*keys)
//    return try {
//        action()
//    } finally {
//        unlockFor(*keys)
//    }
//}