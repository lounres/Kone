/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.collections.KoneMutableLinearIterator
import dev.lounres.kone.collections.KoneMutableListNode
import dev.lounres.kone.collections.KoneMutableNoddedList
import dev.lounres.kone.collections.KoneSettableLinearIterator
import dev.lounres.kone.collections.KoneSettableListNode
import dev.lounres.kone.collections.capacityOverflowException
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.getOrNull
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


@Suppress("UNCHECKED_CAST")
//@Serializable(with = KoneFixedCapacityArrayListWithContextSerializer::class)
public class KoneFixedCapacityArrayNoddedList<Element> @PublishedApi internal constructor(
    size: UInt,
    private val capacity: UInt = size,
    private var data: KoneMutableArray<Any?> = KoneMutableArray<Any?>(capacity) { null },
    private var nodes: KoneMutableArray<Node<Element>?> = KoneMutableArray<Node<Element>?>(capacity) { null },
): KoneMutableNoddedList<Element>, Disposable {
    override var size: UInt = size
        private set
    
    override fun dispose() {
        repeat(size) {
            data[it] = null
            nodes[it]!!.dispose()
            nodes[it] = null
        }
    }
    
    override fun get(index: UInt): Element {
        if (index >= size) indexException(index, size)
        return data[index] as Element
    }
    override fun getNode(index: UInt): KoneMutableListNode<Element> {
        if (index >= size) indexException(index, size)
        return nodes[index]!!
    }
    
    override fun set(index: UInt, element: Element) {
        if (index >= size) indexException(index, size)
        data[index] = element
    }
    
    override fun add(element: Element) {
        if (size == capacity) capacityOverflowException(capacity)
        data[size] = element
        size++
    }
    override fun addNode(element: Element): KoneMutableListNode<Element> {
        TODO("Not yet implemented")
    }
    override fun addAt(index: UInt, element: Element) {
        if (index > size) indexException(index, size)
        if (size == capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i+1u] = data[i]
        data[index] = element
        size++
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        var index = size
        repeat(number) { data[index++] = builder(it) }
        size = newSize
    }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> Element) {
        if (index > size) indexException(index, size)
        val newSize = size + number
        if (newSize > capacity) capacityOverflowException(capacity)
        if (size >= 1u) for (i in (size-1u) downTo index) data[i + number] = data[i]
        var index = index
        repeat(number) { data[index++] = builder(it) }
        size = newSize
    }
    override fun removeAt(index: UInt) {
        if (index >= size) indexException(index, size)
        val newSize = size - 1u
        for (i in index..<newSize) data[i] = data[i + 1u]
        data[size - 1u] = null
        size = newSize
    }
    
    override fun removeAllThatIndexed(predicate: (index: UInt, element: Element) -> Boolean) {
        val newSize: UInt
        scope {
            var checkingMark = 0u
            var resultMark = 0u
            while (checkingMark < size) {
                if (!predicate(checkingMark, data[checkingMark] as Element)) {
                    data[resultMark] = data[checkingMark]
                    resultMark++
                }
                checkingMark++
            }
            newSize = resultMark
        }
        for (i in newSize ..< size) data[i] = null
        size = newSize
    }
    
    override fun removeAll() {
        repeat(size) { data[it] = null }
        size = 0u
    }
    
    override fun iterator(): KoneMutableLinearIterator<Element> = Iterator()
    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<Element> = Iterator(index)
    
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
        repeat(size) {
            hashCode = 31 * hashCode + data[it].hashCode()
        }
        return hashCode
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false
        
        when (other) {
            is KoneFixedCapacityArrayNoddedList<*> ->
                repeat(size) {
                    if (this.data[it] != other.data[it]) return false
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
        list: KoneFixedCapacityArrayNoddedList<Element>,
        override val index: UInt,
    ) : KoneMutableListNode<Element>, Disposable {
        private var _list: KoneFixedCapacityArrayNoddedList<Element>? = list
        internal val list: KoneFixedCapacityArrayNoddedList<Element> get() = _list!!
        
        override var element: Element
            get() = list.data[index] as Element
            set(value) { list.data[index] = value }
        
        override fun remove() {
            TODO("Not yet implemented")
        }
        
        override val nextNode: KoneMutableListNode<Element>?
            get() = list.nodes.getOrNull(index + 1u)
        override val previousNode: KoneMutableListNode<Element>?
            get() = list.nodes.getOrNull(index - 1u)
        
        override fun iteratorFromAfterHere(): KoneMutableLinearIterator<Element> = list.iteratorFrom(index + 1u)
        override fun iteratorFromBeforeHere(): KoneMutableLinearIterator<Element> = list.iteratorFrom(index)
        
        override fun dispose() {
            _list = null
        }
    }
    
    internal inner class Iterator(var currentIndex: UInt = 0u): KoneMutableLinearIterator<Element> {
        init {
            if (currentIndex > size) indexException(currentIndex, size)
        }
        override fun hasNext(): Boolean = currentIndex < size
        override fun getNext(): Element {
            if (!hasNext()) indexException(currentIndex, size)
            return data[currentIndex] as Element
        }
        override fun moveNext() {
            if (!hasNext()) indexException(currentIndex, size)
            currentIndex++
        }
        override fun nextIndex(): UInt = if (hasNext()) currentIndex else indexException(currentIndex, size)
        override fun setNext(element: Element) {
            if (!hasNext()) indexException(currentIndex, size)
            data[currentIndex] = element
        }
        override fun addNext(element: Element) {
            addAt(currentIndex, element)
        }
        override fun removeNext() {
            if (!hasNext()) indexException(currentIndex, size)
            removeAt(currentIndex)
        }
        
        override fun hasPrevious(): Boolean = currentIndex > 0u
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexException(currentIndex, size)
            return data[currentIndex - 1u] as Element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            currentIndex--
        }
        override fun previousIndex(): UInt = if (hasPrevious()) currentIndex - 1u else indexException(currentIndex, size)
        override fun setPrevious(element: Element) {
            if (!hasPrevious()) indexException(currentIndex, size)
            data[currentIndex - 1u] = element
        }
        override fun addPrevious(element: Element) {
            addAt(currentIndex, element)
            currentIndex++
        }
        override fun removePrevious() {
            if (!hasPrevious()) indexException(currentIndex, size)
            removeAt(--currentIndex)
        }
    }
}