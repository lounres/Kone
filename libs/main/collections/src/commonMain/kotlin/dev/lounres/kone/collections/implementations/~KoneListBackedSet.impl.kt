/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


//@Serializable(with = KoneListBackedSetWithContextSerializer::class)
public class KoneListBackedSet<Element, ElementContext: Equality<Element>> @PublishedApi internal constructor(
    override val elementContext: ElementContext,
    internal val backingList: KoneList<Element>,
) : KoneSetWithContext<Element, ElementContext> {
    override val size: UInt get() = backingList.size

    override fun contains(element: Element): Boolean = elementContext { element in backingList }

    override fun iterator(): KoneIterator<Element> = backingList.iterator()

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