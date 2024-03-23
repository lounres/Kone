/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.KoneRemovableIterator
import dev.lounres.kone.collections.common.KoneMutableIterableList
import dev.lounres.kone.collections.common.KoneMutableIterableSet


public class KoneMutableListBackedSet<E> internal constructor(
    internal val backingList: KoneMutableIterableList<E>
) : KoneMutableIterableSet<E> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: E): Boolean = element in backingList

    override fun add(element: E) {
        if (element !in backingList) backingList.add(element)
    }

    override fun clear() {
        backingList.clear()
    }

    override fun remove(element: E) {
        backingList.remove(element)
    }
    override fun removeAllThat(predicate: (element: E) -> Boolean) {
        backingList.removeAllThat(predicate)
    }

    override fun iterator(): KoneRemovableIterator<E> = backingList.iterator()
}