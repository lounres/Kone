/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import kotlinx.serialization.Serializable


@Serializable(with = KoneListBackedSetWithContextSerializer::class)
public class KoneListBackedSet<E, EC: Equality<E>> @PublishedApi internal constructor(
    override val elementContext: EC,
    internal val backingList: KoneIterableList<E>,
) : KoneIterableSet<E>, KoneSetWithContext<E, EC> {
    override val size: UInt
        get() = backingList.size

    override fun contains(element: E): Boolean = element in backingList

    override fun iterator(): KoneIterator<E> = backingList.iterator()

    // TODO: Override equals and `hashCode`

    override fun toString(): String = buildString {
        append('[')
        val iterator = backingList.iterator()
        if (iterator.hasNext()) append(iterator.getAndMoveNext())
        for (element in iterator) {
            append(", ")
            append(element)
        }
        append(']')
    }
}