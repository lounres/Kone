/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections


public interface KoneCollection<out E> {
    public val size: UInt
    public operator fun contains(element: @UnsafeVariance E): Boolean
    public fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean
}

public interface KoneMutableCollection<E> : KoneCollection<E> {
    public fun add(element: E)
    public fun addAll(elements: KoneIterableCollection<E>)
    public fun remove(element: E)
    public fun removeAll(elements: KoneIterableCollection<E>)
    public fun retainAll(elements: KoneIterableCollection<E>)
    public fun clear()
}

public interface KoneList<out E> : KoneCollection<E> {
    public operator fun get(index: Int): E
    public fun indexOf(element: @UnsafeVariance E): UInt
    public fun lastIndexOf(element: @UnsafeVariance E): UInt
}

public interface KoneMutableList<E> : KoneList<E>, KoneMutableCollection<E> {
    public operator fun set(index: UInt, element: E): E
    public fun add(index: UInt, element: E)
    public fun addAll(index: UInt, elements: KoneCollection<E>): Boolean
    public fun removeAt(index: UInt): E
}

public interface KoneSet<out E> : KoneCollection<E>

public interface KoneMutableSet<E> : KoneSet<E>, KoneMutableCollection<E>