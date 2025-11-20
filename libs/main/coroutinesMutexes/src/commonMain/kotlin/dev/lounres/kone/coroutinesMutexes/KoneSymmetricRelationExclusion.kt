/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.coroutinesMutexes

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// Accepts a symmetric relation.
public interface KoneSymmetricRelationExclusion<in Element> {
    public fun tryLockingBy(element: Element): Lock?
    public suspend fun awaitLockBy(element: Element): Lock
    
    public fun interface Lock {
        public fun release()
    }
}

public suspend fun <Element> KoneSymmetricRelationExclusion<Element>.tryOrAwaitLockBy(element: Element): KoneSymmetricRelationExclusion.Lock =
    tryLockingBy(element) ?: awaitLockBy(element)

public suspend inline fun <Element, Result> KoneSymmetricRelationExclusion<Element>.withLockBy(element: Element, action: () -> Result): Result {
    contract {
        callsInPlace(action, InvocationKind.EXACTLY_ONCE)
    }
    
    val lock = tryOrAwaitLockBy(element)
    return try {
        action()
    } finally {
        lock.release()
    }
}