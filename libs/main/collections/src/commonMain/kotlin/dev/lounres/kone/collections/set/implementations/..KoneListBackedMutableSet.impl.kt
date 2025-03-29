/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.iterables.KoneRemovableIterator
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.utils.firstIndexOf
import dev.lounres.kone.collections.utils.iterator
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.repeat
import dev.lounres.kone.contexts.invoke


//@Serializable(with = KoneListBackedMutableSetWithContextSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public open class KoneListBackedMutableSet<Element> @PublishedApi internal constructor(
    public val elementEquality: Equality<Element>,
    internal val backingList: KoneMutableList<Element>,
) : KoneMutableSet<Element> { // TODO: Make it `KoneMutableLinkedSet`
    override val size: UInt
        get() = backingList.size

    override fun contains(element: Element): Boolean = elementEquality { element in backingList }

    override fun add(element: Element) {
        if (elementEquality { element !in backingList }) backingList.add(element)
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        repeat(number) { add(builder(it)) }
    }

    override fun removeAll() {
        backingList.removeAll()
    }

    override fun remove(element: Element) {
        val index = elementEquality { backingList.firstIndexOf(element) }
        if (index != backingList.size) backingList.removeAt(index)
    }
    override fun removeAllThat(predicate: (element: Element) -> Boolean) {
        backingList.removeAllThat(predicate)
    }

    override fun iterator(): KoneRemovableIterator<Element> = backingList.iterator()

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
public class KoneListBackedMutableReifiedSet<Element> @PublishedApi internal constructor(
    public val elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    backingList: KoneMutableList<Element>,
) : KoneListBackedMutableSet<Element>(
    elementEquality = elementEquality,
    backingList = backingList,
), KoneMutableReifiedSet<Element> {
    override fun contains(element: Element): Boolean = element in elementReification && super.contains(element)
}