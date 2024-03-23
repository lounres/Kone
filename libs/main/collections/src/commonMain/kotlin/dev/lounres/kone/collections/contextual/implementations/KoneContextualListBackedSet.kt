/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.implementations

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.collections.contextual.KoneContextualIterableSet
import dev.lounres.kone.comparison.Equality


public class KoneContextualListBackedSet<E> internal constructor(
    internal val backingList: KoneContextualIterableList<E, Equality<E>>
) : KoneContextualIterableSet<E, Equality<E>> {
    override val size: UInt
        get() = backingList.size

    context(Equality<E>)
    override fun contains(element: E): Boolean = element in backingList

    override fun iterator(): KoneIterator<E> = backingList.iterator()

    override fun toString(): String = backingList.toString()

    // TODO: Override `equals` and `hashCode`
}