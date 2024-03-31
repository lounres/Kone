/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex

import dev.lounres.kone.collections.*


public interface KoneIterableCollection<out E> : KoneCollection<E>, KoneIterable<E>

public interface KoneReversibleIterableCollection<out E> : KoneIterableCollection<E>, KoneReversibleIterable<E>

public interface KoneExtendableIterableCollection<E> : KoneIterableCollection<E>, KoneExtendableCollection<E>

public interface KoneReversibleExtendableIterableCollection<E> : KoneExtendableIterableCollection<E>, KoneReversibleIterableCollection<E>

public interface KoneRemovableIterableCollection<out E> : KoneIterableCollection<E>, KoneRemovableIterable<E>, KoneRemovableCollection<E>

public interface KoneReversibleRemovableIterableCollection<out E> : KoneRemovableIterableCollection<E>, KoneReversibleIterableCollection<E>, KoneReversibleRemovableIterable<E>

public interface KoneMutableIterableCollection<E> : KoneExtendableIterableCollection<E>, KoneRemovableIterableCollection<E>

public interface KoneReversibleMutableIterableCollection<E> : KoneReversibleExtendableIterableCollection<E>, KoneReversibleRemovableIterableCollection<E>, KoneMutableIterableCollection<E>

public interface KoneIterableList<out E> : KoneList<E>, KoneIterableCollection<E>, KoneLinearIterable<E> {
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

public interface KoneSettableIterableList<E> : KoneIterableList<E>, KoneSettableList<E>, KoneSettableLinearIterable<E> {
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        for (i in 0u until index) iterator.moveNext()
        return iterator
    }
}

public interface KoneMutableIterableList<E> : KoneMutableList<E>, KoneSettableIterableList<E>, KoneReversibleMutableIterableCollection<E>, KoneMutableLinearIterable<E> {
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        for (i in 0u until index) iterator.moveNext()
        return iterator
    }
}

public interface KoneIterableSet<out E> : KoneSet<E>, KoneIterableCollection<E>

public interface KoneExtendableIterableSet<E> : KoneExtendableSet<E>, KoneIterableSet<E>, KoneExtendableIterableCollection<E>

public interface KoneRemovableIterableSet<out E> : KoneRemovableSet<E>, KoneIterableSet<E>, KoneRemovableIterableCollection<E>

public interface KoneMutableIterableSet<E> : KoneMutableSet<E>, KoneRemovableIterableSet<E>, KoneExtendableIterableSet<E>, KoneMutableIterableCollection<E>