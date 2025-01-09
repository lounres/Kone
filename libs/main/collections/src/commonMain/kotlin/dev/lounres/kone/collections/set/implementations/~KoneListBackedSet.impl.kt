/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.context.invoke


//@Serializable(with = KoneListBackedSetWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public open class KoneListBackedSet<Element, ElementContext: Equality<Element>> @PublishedApi internal constructor(
    public val elementContext: ElementContext,
    internal val backingList: KoneList<Element>,
) : KoneSet<Element> {
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

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneListBackedReifiedSet<Element, ElementContext: ReifiedEquality<Element>> @PublishedApi internal constructor(
    elementContext: ElementContext,
    backingList: KoneList<Element>,
) : KoneListBackedSet<Element, ElementContext>(elementContext, backingList), KoneReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementContext && super.contains(element)
}