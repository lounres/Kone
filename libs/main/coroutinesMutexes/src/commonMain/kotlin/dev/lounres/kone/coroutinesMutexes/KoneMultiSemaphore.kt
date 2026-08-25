/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMultiSemaphore<in Key> {
    public fun tryAcquiringFor(key: Key): KoneLock?
    public suspend fun awaitAcquireFor(key: Key): KoneLock
}

// TODO
//public suspend fun <Key> KoneMultiSemaphore<Key>.awaitAcquireFor(keys: KoneIterable<Key>) {
//    for (key in keys) awaitAcquireFor(key)
//}
//
//public suspend fun <Key> KoneMultiSemaphore<Key>.awaitAcquireFor(vararg keys: Key) {
//    for (key in keys) awaitAcquireFor(key)
//}

public suspend fun <Key> KoneMultiSemaphore<Key>.tryOrAwaitAcquireFor(key: Key): KoneLock = tryAcquiringFor(key) ?: awaitAcquireFor(key)

//public suspend fun <Key> KoneMultiSemaphore<Key>.tryOrAwaitAcquireFor(keys: KoneIterable<Key>) {
//    for (key in keys) tryOrAwaitAcquireFor(key)
//}
//
//public suspend fun <Key> KoneMultiSemaphore<Key>.tryOrAwaitAcquireFor(vararg keys: Key) {
//    for (key in keys) tryOrAwaitAcquireFor(key)
//}

public suspend inline fun <Key, Result> KoneMultiSemaphore<Key>.withAcquisitionFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitAcquireFor(key)
    return try {
        action()
    } finally {
        lock.release()
    }
}

//public suspend inline fun <Key, Result> KoneMultiSemaphore<Key>.withAcquisitionFor(keys: KoneIterable<Key>, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    tryOrAwaitAcquireFor(keys)
//    return try {
//        action()
//    } finally {
//        releaseFor(keys)
//    }
//}
//
//public suspend inline fun <Key, Result> KoneMultiSemaphore<Key>.withAcquisitionFor(vararg keys: Key, action: () -> Result): Result {
//    contract {
//        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
//    }
//
//    tryOrAwaitAcquireFor(*keys)
//    return try {
//        action()
//    } finally {
//        releaseFor(*keys)
//    }
//}