/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.singleton

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.KoneSettableListIterator


@OptIn(DelicateCollectionsInheritanceAPI::class)
internal class KoneSingletonSettableList<Element>(
    var singleElement: Element,
) : KoneSettableList<Element> {
    override val size: UInt get() = 1u

    override fun get(index: UInt): Element {
        if (index >= 1u) indexOutOfBoundsException(index, size)
        return singleElement
    }
    
    override fun set(index: UInt, element: Element) {
        if (index >= 1u) indexOutOfBoundsException(index, size)
        singleElement = element
    }
    
    override fun iterator(): KoneSettableListIterator<Element> =
        Iterator(list = this, currentlyBeforeSingleElement = true)
    override fun iteratorFrom(index: UInt): KoneSettableListIterator<Element> =
        when(index) {
            0u -> Iterator(list = this, currentlyBeforeSingleElement = true)
            1u -> Iterator(list = this, currentlyBeforeSingleElement = false)
            else -> indexOutOfBoundsException(index, size)
        }

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = 31 + singleElement.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (other.size != 1u) return false

        return singleElement == other[0u]
    }
    
    internal class Iterator<Element>(
        val list: KoneSingletonSettableList<Element>,
        var currentlyBeforeSingleElement: Boolean = true,
    ): KoneSettableListIterator<Element> {
        override fun hasNext(): Boolean = currentlyBeforeSingleElement
        override fun getNext(): Element =
            if (!hasNext()) noNextElementInIteratorException()
            else list.singleElement
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentlyBeforeSingleElement = false
        }
        override fun nextIndex(): UInt = if (hasNext()) 1u else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.singleElement = element
        }
        
        override fun hasPrevious(): Boolean = !currentlyBeforeSingleElement
        override fun getPrevious(): Element =
            if (!hasPrevious()) noPreviousElementInIteratorException()
            else list.singleElement
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentlyBeforeSingleElement = true
        }
        override fun previousIndex(): UInt = if (!hasPrevious()) noPreviousElementInIteratorException() else 0u
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.singleElement = element
        }
    }
}