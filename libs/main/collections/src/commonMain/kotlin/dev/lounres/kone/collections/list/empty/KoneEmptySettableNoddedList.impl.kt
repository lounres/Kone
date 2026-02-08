/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.empty

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableListNode
import dev.lounres.kone.collections.list.KoneSettableNoddedList
import dev.lounres.kone.collections.list.KoneSettableNoddedListIterator


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneEmptySettableNoddedListTemplate<Element> : KoneSettableNoddedList<Element> {
    override val size: UInt get() = 0u
    
    override fun getNode(index: UInt): KoneSettableListNode<Element> = indexOutOfBoundsException(index = index, size = 0u)
    override fun get(index: UInt): Element = indexOutOfBoundsException(index = index, size = 0u)
    
    override fun set(index: UInt, element: Element) = indexOutOfBoundsException(index = index, size = 0u)

    override fun iterator(): KoneSettableNoddedListIterator<Element> = KoneEmptySettableNoddedListIterator as KoneSettableNoddedListIterator<Element>
    override fun iteratorFrom(index: UInt): KoneSettableNoddedListIterator<Element> =
        if (index > 0u) indexOutOfBoundsException(index = index, size = 0u)
        else KoneEmptySettableNoddedListIterator as KoneSettableNoddedListIterator<Element>

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 1
    override fun equals(other: Any?): Boolean = other is KoneList<*> && other.isEmpty()
}

@PublishedApi
internal object KoneEmptySettableNoddedList : KoneEmptySettableNoddedListTemplate<Nothing>()