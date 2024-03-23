/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneRemovableIterator
import dev.lounres.kone.collections.contextual.KoneContextualMutableIterableList
import dev.lounres.kone.collections.contextual.KoneContextualMutableIterableSet
import dev.lounres.kone.comparison.Equality


public class KoneContextualMutableListBackedSet<E> internal constructor(
    internal val backingList: KoneContextualMutableIterableList<E, Equality<E>>
) : KoneContextualMutableIterableSet<E, Equality<E>> {
    override val size: UInt
        get() = backingList.size

    context(Equality<E>)
    override fun contains(element: E): Boolean = element in backingList

    context(Equality<E>)
    override fun add(element: E) {
        if (element !in backingList) backingList.add(element)
    }

    override fun clear() {
        backingList.clear()
    }

    context(Equality<E>)
    override fun remove(element: E) {
        backingList.remove(element)
    }
    override fun removeAllThat(predicate: (element: E) -> Boolean) {
        backingList.removeAllThat(predicate)
    }

    override fun iterator(): KoneRemovableIterator<E> = backingList.iterator()

    override fun toString(): String = backingList.toString()

    // TODO: Override `equals` and `hashCode`
}