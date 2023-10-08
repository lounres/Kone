/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneCollection<out E> {
    public val size: UInt
    public fun isEmpty(): Boolean = size == 0u
    public operator fun contains(element: @UnsafeVariance E): Boolean
    public fun containsAll(elements: KoneIterableCollection<@UnsafeVariance E>): Boolean {
        for (e in elements) if (e !in this) return false
        return true
    }
}

public interface KoneExtendableCollection<E> : KoneCollection<E> {
    public fun add(element: E)
    public fun addAll(elements: KoneIterableCollection<E>) {
        for (e in elements) add(e)
    }
}

public interface KoneRemovableCollection<out E> : KoneCollection<E> {
    public fun remove(element: @UnsafeVariance E)
    public fun removeAllThat(predicate: (E) -> Boolean)
    public fun clear()
}

public interface KoneMutableCollection<E> : KoneExtendableCollection<E>, KoneRemovableCollection<E>

public interface KoneList<out E> : KoneCollection<E> {
    public operator fun get(index: UInt): E
    // TODO: Replace with `public fun indexOfThat(predicate: (E) -> Boolean): UInt`
    public fun indexOf(element: @UnsafeVariance E): UInt
    // TODO: Replace with `public fun lastIndexOfThat(predicate: (E) -> Boolean): UInt`
    public fun lastIndexOf(element: @UnsafeVariance E): UInt
}

public interface KoneSettableList<E> : KoneList<E> {
    public operator fun set(index: UInt, element: E)
}

public interface KoneMutableList<E> : KoneSettableList<E>, KoneMutableCollection<E> {
    public fun addAt(index: UInt, element: E)
    public fun addAllAt(index: UInt, elements: KoneIterableCollection<E>)
    public fun removeAt(index: UInt)
}

public interface KoneSet<out E> : KoneCollection<E>

public interface KoneMutableSet<E> : KoneSet<E>, KoneMutableCollection<E>