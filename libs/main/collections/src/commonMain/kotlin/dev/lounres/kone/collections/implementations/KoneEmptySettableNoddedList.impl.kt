/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal open class KoneEmptySettableNoddedListTemplate<Element> : KoneSettableNoddedList<Element> {
    override val size: UInt get() = 0u
    
    override fun getNode(index: UInt): KoneSettableListNode<Element> = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")
    override fun get(index: UInt): Element = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")
    
    override fun set(index: UInt, element: Element) = throw IndexOutOfBoundsException("Empty list doesn't contain element at index $index.")

    override fun iterator(): KoneSettableNoddedListIterator<Element> = KoneEmptySettableNoddedListIterator as KoneSettableNoddedListIterator<Element>
    override fun iteratorFrom(index: UInt): KoneSettableNoddedListIterator<Element> =
        if (index > 0u) indexOutOfBoundsException(index, 0u)
        else KoneEmptySettableNoddedListIterator as KoneSettableNoddedListIterator<Element>

    override fun toString(): String = "[]"
    override fun hashCode(): Int = 1
    override fun equals(other: Any?): Boolean = other is KoneList<*> && other.isEmpty()
}

internal object KoneEmptySettableNoddedList : KoneEmptySettableNoddedListTemplate<Nothing>()