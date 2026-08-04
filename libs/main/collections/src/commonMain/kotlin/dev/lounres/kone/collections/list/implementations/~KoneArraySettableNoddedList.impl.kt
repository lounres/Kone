/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableListNode
import dev.lounres.kone.collections.list.KoneSettableNoddedList
import dev.lounres.kone.collections.list.KoneSettableNoddedListIterator
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.repeat
import kotlinx.serialization.Serializable


@Serializable(with = KoneArraySettableNoddedListSerializer::class)
@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneArraySettableNoddedList<Element> @PublishedApi internal constructor(
    internal val data: KoneMutableArray<Node<Element>?>,
) : KoneSettableNoddedList<Element>, Disposable {
    override var isDisposed: Boolean = false
        private set
    
    init {
        data.forEach { it!!.list = this }
    }
    
    override val size: UInt get() = data.size
    
    override fun dispose() {
        if (isDisposed) return
        repeat(size) {
            data[it]!!.detach()
            data[it] = null
        }
        isDisposed = true
    }
    
    override fun get(index: UInt): Element {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index]!!.element
    }
    override fun getNode(index: UInt): KoneSettableListNode<Element> {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index]!!
    }
    
    override fun set(index: UInt, element: Element) {
        if (isDisposed) disposedInstanceException()
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index]!!.element = element
    }
    
    override fun iterator(): KoneSettableNoddedListIterator<Element> =
        if (isDisposed) disposedInstanceException()
        else Iterator(this, 0u)
    public override fun iteratorFrom(index: UInt): KoneSettableNoddedListIterator<Element> =
        when {
            isDisposed -> disposedInstanceException()
            index > size -> indexOutOfBoundsException(index, size)
            else -> Iterator(this, index)
        }
        
    
    override fun toString(): String = buildString {
        if (isDisposed) disposedInstanceException()
        append('[')
        if (size > 0u) append(data[0u]!!.element)
        for (i in 1u..<size) {
            append(", ")
            append(data[i]!!.element)
        }
        append(']')
    }
    override fun hashCode(): Int {
        if (isDisposed) disposedInstanceException()
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i]!!.element.hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (isDisposed) disposedInstanceException()
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        when (other) {
            is KoneArraySettableNoddedList<*> ->
                for (i in 0u..<size) {
                    if (this.data[i]!!.element != other.data[i]!!.element) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[i]!!.element != otherIterator.getAndMoveNext()) return false
                }
            }
        }
        
        return true
    }
    
    @PublishedApi
    internal class Node<Element>(
        override var element: Element,
        override val index: UInt,
    ) : KoneSettableListNode<Element> {
        override var isDetached: Boolean = false
            private set
        
        private var _list: KoneArraySettableNoddedList<Element>? = null
        internal var list: KoneArraySettableNoddedList<Element>
            get() = _list!!
            set(value) { _list = value }
        
        internal constructor(list: KoneArraySettableNoddedList<Element>, element: Element, index: UInt) : this(element, index) {
            _list = list
        }
        
        override val nextNode: KoneSettableListNode<Element>?
            get() = when {
                isDetached -> detachedNodeException()
                index + 1u < list.size -> list.data[index]!!
                else -> null
            }
        override val previousNode: KoneSettableListNode<Element>?
            get() = when {
                isDetached -> detachedNodeException()
                index > 0u -> list.data[index - 1u]!!
                else -> null
            }
        
        override fun iteratorFromAfterHere(): KoneSettableNoddedListIterator<Element> = list.iteratorFrom(index + 1u)
        override fun iteratorFromBeforeHere(): KoneSettableNoddedListIterator<Element> = list.iteratorFrom(index)
        
        fun detach() {
            if (isDetached) return
            _list = null
            isDetached = true
        }
    }
    
    internal class Iterator<Element>(
        val list: KoneArraySettableNoddedList<Element>,
        var currentIndex: UInt,
    ): KoneSettableNoddedListIterator<Element> {
        override fun hasNext(): Boolean = if (list.isDisposed) disposedInstanceException() else currentIndex < list.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex]!!.element
        }
        override fun getNextNode(): KoneSettableListNode<Element> {
            if (!hasNext()) noNextElementInIteratorException()
            return list.data[currentIndex]!!
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else noNextElementInIteratorException()
        override fun setNext(element: Element) {
            if (!hasNext()) noNextElementInIteratorException()
            list.data[currentIndex]!!.element = element
        }
        
        override fun hasPrevious(): Boolean = if (list.isDisposed) disposedInstanceException() else currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[currentIndex - 1u]!!.element
        }
        override fun getPreviousNode(): KoneSettableListNode<Element> {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            return list.data[currentIndex - 1u]!!
        }
        override fun movePrevious() {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else noPreviousElementInIteratorException()
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) noPreviousElementInIteratorException()
            list.data[currentIndex - 1u]!!.element = element
        }
    }
}