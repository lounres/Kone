/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


internal class KoneSingletonNoddedList<Element>(
    val singleElement: Element,
) : KoneNoddedList<Element> {
    override val size: UInt = 1u
    
    private val singleNode = Node()

    override fun get(index: UInt): Element {
        if (index >= 1u) indexException(index, size)
        return singleElement
    }
    override fun getNode(index: UInt): KoneListNode<Element> = singleNode
    
    override fun iterator(): KoneLinearIterator<Element> = KoneSingletonLinearIterator(singleElement = singleElement)
    override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> =
        when(index) {
            0u -> KoneSingletonLinearIterator(singleElement = singleElement)
            1u -> KoneSingletonLinearIterator(singleElement = singleElement, currentlyBeforeSingleElement = false)
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
    
    inner class Node: KoneListNode<Element> {
        override val element: Element get() = singleElement
        override val index: UInt get() = 0u
        
        override val nextNode: KoneListNode<Element>? get() = null
        override val previousNode: KoneListNode<Element>? get() = null
        
        override fun iteratorFromBeforeHere(): KoneLinearIterator<Element> = iteratorFrom(0u)
        override fun iteratorFromAfterHere(): KoneLinearIterator<Element> = iteratorFrom(1u)
    }
}