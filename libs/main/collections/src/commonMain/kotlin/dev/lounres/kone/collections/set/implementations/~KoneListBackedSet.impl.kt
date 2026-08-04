/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterable.contains
import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification


//@Serializable(with = KoneListBackedSetWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public open class KoneListBackedSet<Element> @PublishedApi internal constructor(
    public val elementEquality: Equality<Element>,
    internal val backingList: KoneList<Element>,
) : KoneSet<Element> {
    override val size: UInt get() = backingList.size

    override fun contains(element: Element): Boolean = elementEquality { element in backingList }

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
    
    public companion object
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneListBackedReifiedSet<Element> @PublishedApi internal constructor(
    public val elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    backingList: KoneList<Element>,
) : KoneListBackedSet<Element>(elementEquality, backingList), KoneReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementReification && super.contains(element)
    
    public companion object
}