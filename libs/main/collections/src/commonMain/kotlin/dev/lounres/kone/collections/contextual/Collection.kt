/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


public interface KoneContextualCollection<out E, in EE: Equality<@UnsafeVariance E>> {
    public val size: UInt
    context(EE)
    public operator fun contains(element: @UnsafeVariance E): Boolean
}

public interface KoneContextualExtendableCollection<E, in EE: Equality<E>> : KoneContextualCollection<E, EE> {
    context(EE)
    public fun add(element: E)
    context(EE)
    public fun addAll(elements: KoneContextualIterableCollection<E, *>) {
        for (e in elements) add(e)
    }
}

public interface KoneContextualRemovableCollection<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualCollection<E, EE> {
    context(EE)
    public fun remove(element: @UnsafeVariance E)
    public fun removeAllThat(predicate: (element: E) -> Boolean)
    public fun clear()
}

public interface KoneContextualMutableCollection<E, in EE: Equality<E>> : KoneContextualExtendableCollection<E, EE>, KoneContextualRemovableCollection<E, EE>

public interface KoneContextualList<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualCollection<E, EE> {
    context(EE)
    override fun contains(element: @UnsafeVariance E): Boolean = indexThat { _, collectionElement -> element eq collectionElement } != size

    public operator fun get(index: UInt): E
    public fun getMaybe(index: UInt): Option<E> =
        if (index < size) Some(get(index))
        else None
    public fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = 0u
        while (i < size) {
            if (predicate(i, get(i))) break
            i++
        }
        return i
    }
    public fun lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = size - 1u
        while (i != UInt.MAX_VALUE) {
            if (predicate(i, get(i))) break
            i--
        }
        return i
    }
}

public interface KoneContextualSettableList<E, in EE: Equality<E>> : KoneContextualList<E, EE> {
    public operator fun set(index: UInt, element: E)
}

public interface KoneContextualExtendableList<E, in EE: Equality<E>>: KoneContextualList<E, EE>, KoneContextualExtendableCollection<E, EE> {
    public fun addAt(index: UInt, element: E)
    public fun addAllAt(index: UInt, elements: KoneContextualIterableCollection<E, *>)
    public fun addAtTheEnd(element: E)
    public fun addAllAtTheEnd(elements: KoneContextualIterableCollection<E, *>)

    context(EE)
    override fun add(element: E) {
        addAtTheEnd(element)
    }
    context(EE)
    override fun addAll(elements: KoneContextualIterableCollection<E, *>) {
        addAllAtTheEnd(elements)
    }
}

public interface KoneContextualRemovableList<out E, in EE: Equality<@UnsafeVariance E>>: KoneContextualList<E, EE>, KoneContextualRemovableCollection<E, EE> {
    public fun removeAt(index: UInt)
    public override fun removeAllThat(predicate: (element: E) -> Boolean) {
        removeAllThatIndexed { _, element -> predicate(element) }
    }
    public fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean)
}

public interface KoneContextualMutableList<E, in EE: Equality<E>> : KoneContextualSettableList<E, EE>, KoneContextualExtendableList<E, EE>, KoneContextualRemovableList<E, EE>, KoneContextualMutableCollection<E, EE> {
    // FIXME: KT-65793
    context(EE)
    override fun contains(element: E): Boolean = super<KoneContextualSettableList>.contains(element)
}

public interface KoneContextualSet<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualCollection<E, EE>

public interface KoneContextualExtendableSet<E, in EE: Equality<E>> : KoneContextualSet<E, EE>, KoneContextualExtendableCollection<E, EE>

public interface KoneContextualRemovableSet<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualSet<E, EE>, KoneContextualRemovableCollection<E, EE>

public interface KoneContextualMutableSet<E, in EE: Equality<@UnsafeVariance E>> : KoneContextualExtendableSet<E, EE>, KoneContextualRemovableSet<E, EE>, KoneContextualMutableCollection<E, EE>