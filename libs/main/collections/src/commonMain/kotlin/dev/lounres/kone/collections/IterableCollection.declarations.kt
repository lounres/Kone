/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.serializers.DefaultKoneExtendableIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneIterableListSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneMutableIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneMutableIterableListSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneRemovableIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneReversibleExtendableIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneReversibleIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneReversibleMutableIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneReversibleRemovableIterableCollectionSerializer
import dev.lounres.kone.collections.serializers.DefaultKoneSettableIterableListSerializer
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.repeat
import kotlinx.serialization.Serializable


@Serializable(with = DefaultKoneIterableCollectionSerializer::class)
public interface KoneIterableCollection<out E> : KoneCollection<E>, KoneIterable<E>

@Serializable(with = DefaultKoneReversibleIterableCollectionSerializer::class)
public interface KoneReversibleIterableCollection<out E> : KoneIterableCollection<E>, KoneReversibleIterable<E>

@Serializable(with = DefaultKoneExtendableIterableCollectionSerializer::class)
public interface KoneExtendableIterableCollection<E> : KoneIterableCollection<E>, KoneExtendableCollection<E>

@Serializable(with = DefaultKoneReversibleExtendableIterableCollectionSerializer::class)
public interface KoneReversibleExtendableIterableCollection<E> : KoneExtendableIterableCollection<E>,
    KoneReversibleIterableCollection<E>

@Serializable(with = DefaultKoneRemovableIterableCollectionSerializer::class)
public interface KoneRemovableIterableCollection<out E> : KoneIterableCollection<E>, KoneRemovableIterable<E>, KoneRemovableCollection<E>

@Serializable(with = DefaultKoneReversibleRemovableIterableCollectionSerializer::class)
public interface KoneReversibleRemovableIterableCollection<out E> : KoneRemovableIterableCollection<E>,
    KoneReversibleIterableCollection<E>, KoneReversibleRemovableIterable<E>

@Serializable(with = DefaultKoneMutableIterableCollectionSerializer::class)
public interface KoneMutableIterableCollection<E> : KoneExtendableIterableCollection<E>,
    KoneRemovableIterableCollection<E>

@Serializable(with = DefaultKoneReversibleMutableIterableCollectionSerializer::class)
public interface KoneReversibleMutableIterableCollection<E> : KoneReversibleExtendableIterableCollection<E>,
    KoneReversibleRemovableIterableCollection<E>, KoneMutableIterableCollection<E>, KoneReversibleMutableIterable<E>

@Serializable(with = DefaultKoneIterableListSerializer::class)
public interface KoneIterableList<out E> : KoneList<E>, KoneReversibleIterableCollection<E>, KoneLinearIterable<E> {
    public fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        repeat(index) { iterator.moveNext() }
        return iterator
    }
    public override fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
        var i = 0u
        val iterator = iterator()
        for (element in iterator) {
            if (predicate(i, element)) break
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

@Serializable(with = DefaultKoneSettableIterableListSerializer::class)
public interface KoneSettableIterableList<E> : KoneIterableList<E>, KoneSettableList<E>, KoneSettableLinearIterable<E> {
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        repeat(index) { iterator.moveNext() }
        return iterator
    }
}

@Serializable(with = DefaultKoneMutableIterableListSerializer::class)
public interface KoneMutableIterableList<E> : KoneMutableList<E>, KoneSettableIterableList<E>,
    KoneReversibleMutableIterableCollection<E>, KoneMutableLinearIterable<E> {
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> {
        require(index <= size)
        val iterator = iterator()
        repeat(index) { iterator.moveNext() }
        return iterator
    }
}

public interface KoneIterableSet<out E> : KoneSet<E>, KoneIterableCollection<E>

public interface KoneExtendableIterableSet<E> : KoneExtendableSet<E>, KoneIterableSet<E>,
    KoneExtendableIterableCollection<E>

public interface KoneRemovableIterableSet<out E> : KoneRemovableSet<E>, KoneIterableSet<E>,
    KoneRemovableIterableCollection<E>

public interface KoneMutableIterableSet<E> : KoneMutableSet<E>, KoneRemovableIterableSet<E>,
    KoneExtendableIterableSet<E>, KoneMutableIterableCollection<E>

public interface KoneIterableListSet<out E> : KoneListSet<E>, KoneIterableList<E>, KoneIterableSet<E>