/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.KoneRemovableIterator
import dev.lounres.kone.collections.complex.KoneMutableIterableList
import dev.lounres.kone.collections.complex.KoneMutableIterableSet
import dev.lounres.kone.collections.getAndMoveNext

public class KoneMutableListBackedSet<E> internal constructor(
    internal val backingList: KoneMutableIterableList<E>,
) : KoneMutableIterableSet<E> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: E): Boolean = element in backingList

    override fun add(element: E) {
        if (element !in backingList) backingList.add(element)
    }

    override fun removeAll() {
        backingList.removeAll()
    }

    override fun remove(element: E) {
        backingList.remove(element)
    }
    override fun removeAllThat(predicate: (element: E) -> Boolean) {
        backingList.removeAllThat(predicate)
    }

    override fun iterator(): KoneRemovableIterator<E> = backingList.iterator()

    // TODO: Override equals and `hashCode`

    override fun toString(): String = buildString {
        append('[')
        val iterator = backingList.iterator()
        if (iterator.hasNext()) append(iterator.getAndMoveNext())
        while (iterator.hasNext()) {
            append(", ")
            append(iterator.getAndMoveNext())
        }
        append(']')
    }
}