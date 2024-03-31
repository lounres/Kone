/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex


public interface KoneCollectionWithGrowableCapacity<E>: KoneMutableList<E> {
    public fun ensureCapacity(minimalCapacity: UInt)
}

public interface KoneDequeue<E>: KoneMutableList<E> {
    public fun getFirst(): E
    public fun getLast(): E
    public fun addFirst(element: E)
    public fun addLast(element: E)
    public fun removeFirst()
    public fun removeLast()
}