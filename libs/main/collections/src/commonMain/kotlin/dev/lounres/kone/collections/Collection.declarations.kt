/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some
import dev.lounres.kone.repeat


public interface KoneCollection<out E> {
    public val size: UInt
    public operator fun contains(element: @UnsafeVariance E): Boolean
}

public interface KoneExtendableCollection<E> : KoneCollection<E> {
    public fun add(element: E)
    public fun addSeveral(number: UInt, builder: (index: UInt) -> E) {
        repeat(number) { add(builder(it)) }
    }
    public fun addAllFrom(elements: KoneIterableCollection<E>) {
        val iterator = elements.iterator()
        addSeveral(elements.size) { iterator.getAndMoveNext() }
    }
}

public interface KoneRemovableCollection<out E> : KoneCollection<E> {
    public fun remove(element: @UnsafeVariance E)
    public fun removeAllThat(predicate: (element: E) -> Boolean)
    public fun removeAll()
}

public interface KoneMutableCollection<E> : KoneExtendableCollection<E>, KoneRemovableCollection<E>

public interface KoneList<out E> : KoneCollection<E> {
    public operator fun get(index: UInt): E
    public fun getMaybe(index: UInt): Option<E> =
        if (index < size) Some(get(index))
        else None
    override fun contains(element: @UnsafeVariance E): Boolean
    public fun indexOf(element: @UnsafeVariance E): UInt
    public fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = 0u
        while (i < size) {
            if (predicate(i, get(i))) break
            i++
        }
        return i
    }
    public fun lastIndexOf(element: @UnsafeVariance E): UInt
    public fun lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = size - 1u
        while (i != UInt.MAX_VALUE) {
            if (predicate(i, get(i))) break
            i--
        }
        return i
    }
}

public interface KoneSettableList<E> : KoneList<E> {
    public operator fun set(index: UInt, element: E)
}

public interface KoneExtendableList<E>: KoneList<E>, KoneExtendableCollection<E> {
    public fun addAt(index: UInt, element: E)
    public fun addSeveralAt(number: UInt, index: UInt, builder: (index: UInt) -> E)
    public fun addAllFromAt(index: UInt, elements: KoneIterableCollection<E>)
}

public interface KoneRemovableList<E>: KoneList<E>, KoneRemovableCollection<E> {
    public fun removeAt(index: UInt)
    public override fun removeAllThat(predicate: (element: E) -> Boolean) {
        removeAllThatIndexed { _, element -> predicate(element) }
    }
    public fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean)
}

public interface KoneMutableList<E> : KoneSettableList<E>, KoneExtendableList<E>, KoneRemovableList<E>,
    KoneMutableCollection<E>

public interface KoneSet<out E> : KoneCollection<E>

public interface KoneExtendableSet<E> : KoneSet<E>, KoneExtendableCollection<E>

public interface KoneRemovableSet<out E> : KoneSet<E>, KoneRemovableCollection<E>

public interface KoneMutableSet<E> : KoneExtendableSet<E>, KoneRemovableSet<E>, KoneMutableCollection<E>

public interface KoneListSet<out E> : KoneList<E>, KoneSet<E>