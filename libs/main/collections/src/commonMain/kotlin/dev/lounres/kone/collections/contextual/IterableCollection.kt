/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality


public interface KoneContextualIterableCollection<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualCollection<E, EE>,
    KoneIterable<E>

public interface KoneContextualReversibleIterableCollection<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualIterableCollection<E, EE>,
    KoneReversibleIterable<E>

public interface KoneContextualExtendableIterableCollection<E, in EE: Equality<@UnsafeVariance E>> : KoneContextualIterableCollection<E, EE>, KoneContextualExtendableCollection<E, EE>

public interface KoneContextualReversibleExtendableIterableCollection<E, in EE: Equality<@UnsafeVariance E>> : KoneContextualExtendableIterableCollection<E, EE>, KoneContextualReversibleIterableCollection<E, EE>

public interface KoneContextualRemovableIterableCollection<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualIterableCollection<E, EE>,
    KoneRemovableIterable<E>, KoneContextualRemovableCollection<E, EE>

public interface KoneContextualReversibleRemovableIterableCollection<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualRemovableIterableCollection<E, EE>, KoneContextualReversibleIterableCollection<E, EE>,
    KoneReversibleRemovableIterable<E>

public interface KoneContextualMutableIterableCollection<E, in EE: Equality<@UnsafeVariance E>> : KoneContextualExtendableIterableCollection<E, EE>, KoneContextualRemovableIterableCollection<E, EE>

public interface KoneContextualReversibleMutableIterableCollection<E, in EE: Equality<@UnsafeVariance E>> : KoneContextualReversibleExtendableIterableCollection<E, EE>, KoneContextualReversibleRemovableIterableCollection<E, EE>, KoneContextualMutableIterableCollection<E, EE>

public interface KoneContextualIterableList<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualList<E, EE>, KoneContextualIterableCollection<E, EE>, KoneLinearIterable<E> {
    public fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        for (i in 0u until index) iterator.moveNext()
        return iterator
    }
    public override fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = 0u
        val iterator = iterator()
        while (iterator.hasNext()) {
            if (predicate(i, iterator.getAndMoveNext())) break
            i++
        }
        return i
    }
    public override fun lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = size - 1u
        val iterator = iteratorFrom(size)
        while (iterator.hasPrevious()) {
            if (predicate(i, iterator.getAndMovePrevious())) break
            i--
        }
        return i
    }
}

public interface KoneContextualSettableIterableList<E, in EE: Equality<E>> : KoneContextualIterableList<E, EE>, KoneContextualSettableList<E, EE>, KoneSettableLinearIterable<E> {
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        for (i in 0u until index) iterator.moveNext()
        return iterator
    }
}

public interface KoneContextualMutableIterableList<E, in EE: Equality<E>> : KoneContextualMutableList<E, EE>, KoneContextualSettableIterableList<E, EE>, KoneContextualReversibleMutableIterableCollection<E, EE>, KoneMutableLinearIterable<E> {
    // FIXME: KT-65793
    context(EE)
    override fun contains(element: E): Boolean = super<KoneContextualMutableList>.contains(element)
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        for (i in 0u until index) iterator.moveNext()
        return iterator
    }
}

public interface KoneContextualIterableSet<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualSet<E, EE>, KoneContextualIterableCollection<E, EE>

public interface KoneContextualExtendableIterableSet<E, in EE: Equality<E>> : KoneContextualExtendableSet<E, EE>, KoneContextualIterableSet<E, EE>, KoneContextualExtendableIterableCollection<E, EE>

public interface KoneContextualRemovableIterableSet<out E, in EE: Equality<@UnsafeVariance E>> : KoneContextualRemovableSet<E, EE>, KoneContextualIterableSet<E, EE>, KoneContextualRemovableIterableCollection<E, EE>

public interface KoneContextualMutableIterableSet<E, in EE: Equality<E>> : KoneContextualMutableSet<E, EE>, KoneContextualRemovableIterableSet<E, EE>, KoneContextualExtendableIterableSet<E, EE>, KoneContextualMutableIterableCollection<E, EE>