/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality

public class KoneMutableListBackedSet<E, EC: Equality<E>> @PublishedApi internal constructor(
    override val elementContext: EC,
    internal val backingList: KoneMutableIterableList<E>,
) : KoneMutableIterableSet<E>, KoneMutableSetWithContext<E, EC> {
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