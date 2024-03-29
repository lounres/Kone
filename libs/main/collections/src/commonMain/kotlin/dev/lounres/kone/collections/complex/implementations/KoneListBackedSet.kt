/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.complex.KoneIterableList
import dev.lounres.kone.collections.complex.KoneIterableSet
import dev.lounres.kone.comparison.Equality


public class KoneListBackedSet<E> internal constructor(
    override val context: Equality<E>,
    internal val backingList: KoneIterableList<E>,
) : KoneIterableSet<E> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: E): Boolean = element in backingList

    override fun iterator(): KoneIterator<E> = backingList.iterator()
}