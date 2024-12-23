/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


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
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(1u, 1u)
            return list.singleElement
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(1u, 1u)
            currentlyBeforeSingleElement = false
        }
        override fun nextIndex(): UInt = if (hasNext()) 1u else indexOutOfBoundsException(1u, 1u)
        override fun setNext(element: Element) {
            if (!hasNext()) indexOutOfBoundsException(1u, 1u)
            list.singleElement = element
        }
        
        override fun hasPrevious(): Boolean = !currentlyBeforeSingleElement
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
            return list.singleElement
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
            currentlyBeforeSingleElement = true
        }
        override fun previousIndex(): UInt = if (hasPrevious()) 0u else indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
            list.singleElement = element
        }
    }
}