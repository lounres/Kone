/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.implementations

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.common.KoneIterableList
import dev.lounres.kone.collections.common.KoneIterableSet


public class KoneListBackedSet<E> internal constructor(
    internal val backingList: KoneIterableList<E>
) : KoneIterableSet<E> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: E): Boolean = element in backingList

    override fun iterator(): KoneIterator<E> = backingList.iterator()
}