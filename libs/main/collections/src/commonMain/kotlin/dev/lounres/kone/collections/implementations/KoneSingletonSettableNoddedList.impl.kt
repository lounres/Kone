/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


internal class KoneSingletonSettableNoddedList<Element>(
    var singleElement: Element,
) : KoneSettableNoddedList<Element> {
    override val size: UInt get() = 1u
    
    private val singleNode = Node()

    override fun get(index: UInt): Element {
        if (index >= 1u) indexException(index, size)
        return singleElement
    }
    override fun getNode(index: UInt): KoneSettableListNode<Element> = singleNode
    
    override fun set(index: UInt, element: Element) {
        if (index >= 1u) indexException(index, size)
        singleElement = element
    }
    
    override fun iterator(): KoneLinearIterator<Element> = KoneSingletonSettableLinearIterator(singleElement = singleElement)
    override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Element> =
        when(index) {
            0u -> KoneSingletonSettableLinearIterator(singleElement = singleElement)
            1u -> KoneSingletonSettableLinearIterator(singleElement = singleElement, currentlyBeforeSingleElement = false)
            else -> indexException(index, size)
        }

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = 31 + singleElement.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (other.size != 1u) return false

        return singleElement == other[0u]
    }
    
    inner class Node: KoneSettableListNode<Element> {
        override var element: Element
            get() = singleElement
            set(value) { singleElement = value }
        override val index: UInt get() = 0u
        
        override val nextNode: KoneSettableListNode<Element>? get() = null
        override val previousNode: KoneSettableListNode<Element>? get() = null
        
        override fun iteratorFromBeforeHere(): KoneSettableLinearIterator<Element> = iteratorFrom(0u)
        override fun iteratorFromAfterHere(): KoneSettableLinearIterator<Element> = iteratorFrom(1u)
    }
}