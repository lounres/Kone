/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.isNotEmpty
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.utils.filter
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public interface KoneMultiSemaphore<in Key> {
    public fun tryAcquiringFor(key: Key): Boolean
    public suspend fun awaitAcquireFor(key: Key)
    @IgnorableReturnValue
    public fun tryReleasingFor(key: Key): Boolean
}

public suspend fun <Key> KoneMultiSemaphore<Key>.awaitAcquireFor(keys: KoneIterable<Key>) {
    for (key in keys) awaitAcquireFor(key)
}

public suspend fun <Key> KoneMultiSemaphore<Key>.awaitAcquireFor(vararg keys: Key) {
    for (key in keys) awaitAcquireFor(key)
}

public suspend fun <Key> KoneMultiSemaphore<Key>.tryOrAwaitAcquireFor(key: Key) {
    if (!tryAcquiringFor(key)) awaitAcquireFor(key)
}

public suspend fun <Key> KoneMultiSemaphore<Key>.tryOrAwaitAcquireFor(keys: KoneIterable<Key>) {
    for (key in keys) tryOrAwaitAcquireFor(key)
}

public suspend fun <Key> KoneMultiSemaphore<Key>.tryOrAwaitAcquireFor(vararg keys: Key) {
    for (key in keys) tryOrAwaitAcquireFor(key)
}

public fun <Key> KoneMultiSemaphore<Key>.releaseFor(key: Key) {
    if (!tryReleasingFor(key)) error("KoneMultiSemaphore is not locked for key $key")
}

public fun <Key> KoneMultiSemaphore<Key>.releaseFor(keys: KoneIterable<Key>) {
    val failedKeys = keys.filter { !tryReleasingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneMultiSemaphore is not locked for keys $failedKeys")
}

public fun <Key> KoneMultiSemaphore<Key>.releaseFor(vararg keys: Key) {
    val failedKeys = keys.filter { !tryReleasingFor(it) }
    if (failedKeys.isNotEmpty()) error("KoneMultiSemaphore is not locked for keys $failedKeys")
}

public suspend inline fun <Key, Result> KoneMultiSemaphore<Key>.withPermitFor(key: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitAcquireFor(key)
    return try {
        action()
    } finally {
        releaseFor(key)
    }
}

public suspend inline fun <Key, Result> KoneMultiSemaphore<Key>.withPermitFor(keys: KoneIterable<Key>, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitAcquireFor(keys)
    return try {
        action()
    } finally {
        releaseFor(keys)
    }
}

public suspend inline fun <Key, Result> KoneMultiSemaphore<Key>.withPermitFor(vararg keys: Key, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    tryOrAwaitAcquireFor(*keys)
    return try {
        action()
    } finally {
        releaseFor(*keys)
    }
}