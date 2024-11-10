/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneSettableLinearIterator
import dev.lounres.kone.collections.KoneSettableListNode
import dev.lounres.kone.collections.KoneSettableNoddedList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.getOrNull
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.repeat


@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneSettableArrayListWithContextSerializer::class)
/*@JvmInline*/ // FIXME: Await support of `equals` and `hashCode` methods support in value classes and multifield value classes to make the class be value class
public /*value*/ class KoneArraySettableNoddedList<Element> @PublishedApi internal constructor(
    private val data: KoneMutableArray<Any?>,
) : KoneSettableNoddedList<Element>, Disposable {
    override val size: UInt get() = data.size
    
    private val nodes = KoneMutableArray<Node<Element>?>(data.size) { Node(this, it) }
    
    override fun dispose() {
        repeat(size) {
            data[it] = null
            nodes[it]!!.dispose()
            nodes[it] = null
        }
    }
    
    override fun get(index: UInt): Element {
        if (index >= size) indexOutOfBoundsException(index, size)
        return data[index] as Element
    }
    override fun getNode(index: UInt): KoneSettableListNode<Element> {
        if (index >= size) indexOutOfBoundsException(index, size)
        return nodes[index]!!
    }
    
    override fun set(index: UInt, element: Element) {
        if (index >= size) indexOutOfBoundsException(index, size)
        data[index] = element
    }
    
    override fun iterator(): KoneSettableLinearIterator<Element> = Iterator(data)
    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<Element> = Iterator(data, index)
    
    override fun toString(): String = buildString {
        append('[')
        if (size > 0u) append(data[0u])
        for (i in 1u..<size) {
            append(", ")
            append(data[i])
        }
        append(']')
    }
    override fun hashCode(): Int {
        var hashCode = 1
        for (i in 0u..<size) {
            hashCode = 31 * hashCode + this.data[i].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        when (other) {
            is KoneArraySettableNoddedList<*> ->
                for (i in 0u..<size) {
                    if (this.data[i] != other.data[i]) return false
                }
            else -> {
                val otherIterator = other.iterator()
                for (i in 0u ..< size) {
                    if (this.data[i] != otherIterator.getAndMoveNext()) return false
                }
            }
        }
        
        return true
    }
    
    internal class Node<Element>(
        list: KoneArraySettableNoddedList<Element>,
        override val index: UInt,
    ) : KoneSettableListNode<Element>, Disposable {
        private var _list: KoneArraySettableNoddedList<Element>? = list
        internal val list: KoneArraySettableNoddedList<Element> get() = _list!!
        
        override var element: Element
            get() = list.data[index] as Element
            set(value) { list.data[index] = value }
        
        override val nextNode: KoneSettableListNode<Element>?
            get() = list.nodes.getOrNull(index + 1u)
        override val previousNode: KoneSettableListNode<Element>?
            get() = list.nodes.getOrNull(index - 1u)
        
        override fun iteratorFromAfterHere(): KoneSettableLinearIterator<Element> = list.iteratorFrom(index + 1u)
        override fun iteratorFromBeforeHere(): KoneSettableLinearIterator<Element> = list.iteratorFrom(index)
        
        override fun dispose() {
            _list = null
        }
    }
    
    internal class Iterator<Element>(val data: KoneMutableArray<Any?>, var currentIndex: UInt = 0u): KoneSettableLinearIterator<Element> {
        init {
            if (currentIndex > data.size) indexOutOfBoundsException(currentIndex, data.size)
        }
        override fun hasNext(): Boolean = currentIndex < data.size
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, data.size)
            return data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, data.size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexOutOfBoundsException(currentIndex, data.size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexOutOfBoundsException(currentIndex, data.size)
            data[currentIndex] = element
        }
        
        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, data.size)
            return data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, data.size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexOutOfBoundsException(currentIndex, data.size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexOutOfBoundsException(currentIndex, data.size)
            data[currentIndex - 1u] = element
        }
    }
}