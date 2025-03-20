/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.accessRootOfEmptyHeapException
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.heap.LinkedHeapNode
import dev.lounres.kone.collections.heap.LinkedMinimumHeap
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.collections.iterables.KoneLinearIterator
import dev.lounres.kone.collections.iterables.KoneReversibleIterable
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSetIterator
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.context


public class KoneBinaryListBackedMinimumHeap<Element, Priority> @PublishedApi internal constructor(
    public val priorityContext: Order<Priority>,
    data: KoneMutableList<Node<Element, Priority>>?,
): LinkedMinimumHeap<Element, Priority>, Disposable {
    override val isDisposed: Boolean get() = _data == null
    
    private var _data: KoneMutableList<Node<Element, Priority>>? = data
    @PublishedApi
    internal var data: KoneMutableList<Node<Element, Priority>>
        get() = if (isDisposed) disposedInstanceException() else _data!!
        set(value) { _data = value }
    
    override fun dispose() {
        if (isDisposed) return
        data.forEach { it.detach() }
        data.let { if (it is Disposable) it.dispose() }
        _data = null
    }
    
    override val size: UInt get() = data.size
    
    private fun swapNodes(index1: UInt, index2: UInt) {
        data[index1] = data[index2].also { data[index2] = data[index1] }
        data[index1].index = index1
        data[index2].index = index2
    }
    
    private tailrec fun siftTheNodeDownToTheRoot(index: UInt) {
        if (index == 0u) return
        val parentIndex = (index - 1u) / 2u
        if (context(priorityContext) { data[parentIndex].priority gt data[index].priority }) {
            swapNodes(index, parentIndex)
            siftTheNodeDownToTheRoot(parentIndex)
        }
    }
    
    @PublishedApi
    internal tailrec fun siftTheNodeUpToTheLeaf(index: UInt) {
        val firstChildIndex = index * 2u + 1u
        val secondChildIndex = firstChildIndex + 1u
        when {
            firstChildIndex < size && secondChildIndex < size ->
                when {
                    context(priorityContext) { data[firstChildIndex].priority lt data[index].priority && data[firstChildIndex].priority lt data[secondChildIndex].priority } -> {
                        swapNodes(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex)
                    }
                    context(priorityContext) { data[secondChildIndex].priority lt data[index].priority } -> {
                        swapNodes(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex)
                    }
                }
            firstChildIndex < size && context(priorityContext) { data[firstChildIndex].priority lt data[index].priority } -> {
                swapNodes(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex)
            }
            secondChildIndex < size && context(priorityContext) { data[secondChildIndex].priority lt data[index].priority } -> {
                swapNodes(secondChildIndex, index)
                siftTheNodeUpToTheLeaf(secondChildIndex)
            }
        }
    }
    
    private fun siftTheNode(index: UInt) {
        siftTheNodeDownToTheRoot(index)
        siftTheNodeUpToTheLeaf(index)
    }
    
    private fun removeNode(index: UInt) {
        val oldLastNodeIndex = data.lastIndex
        val nodeToSift =
            if (index != oldLastNodeIndex) {
                swapNodes(index, oldLastNodeIndex)
                index
            } else null
        
        data[oldLastNodeIndex].detach()
        data.removeAt(oldLastNodeIndex)
        nodeToSift?.let { siftTheNode(it) }
    }
    
    private fun updatePlacement(index: UInt) {
        siftTheNode(index)
    }
    
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>> get() = Nodes()
    override val elementsView: KoneReversibleIterable<Element> get() = Elements()
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority> {
        val newNode = Node(element = element, priority = priority, heap = this, index = size)
        data.add(newNode)
        siftTheNodeDownToTheRoot(data.lastIndex)
        return newNode
    }
    
    override fun takeMinimum(): LinkedHeapNode<Element, Priority> {
        if (size == 0u) accessRootOfEmptyHeapException()
        return data[0u]
    }
    
    override fun popMinimum(): LinkedHeapNode<Element, Priority> {
        if (size == 0u) accessRootOfEmptyHeapException()
        return data[0u].also { removeNode(0u) }
    }
    
    @PublishedApi
    internal class Node<Element, Priority>(
        override var element: Element,
        priority: Priority,
        heap: KoneBinaryListBackedMinimumHeap<Element, Priority>,
        internal var index: UInt,
    ): LinkedHeapNode<Element, Priority> {
        private var _heap: KoneBinaryListBackedMinimumHeap<Element, Priority>? = heap
        internal val heap: KoneBinaryListBackedMinimumHeap<Element, Priority> get() = _heap!!
        override val isDetached: Boolean get() = _heap == null
        
        override var priority: Priority = priority
            set(value) {
                if (isDetached) detachedNodeException()
                field = value
                heap.updatePlacement(index)
            }
        
        fun detach() {
            _heap = null
        }
        
        override val nextNode: LinkedHeapNode<Element, Priority>?
            get() = when {
                isDetached -> detachedNodeException()
                index == heap.data.lastIndex -> null
                else -> heap.data[index + 1u]
            }
        override val previousNode: LinkedHeapNode<Element, Priority>?
            get() = when {
                isDetached -> detachedNodeException()
                index == 0u -> null
                else -> heap.data[index - 1u]
            }
        
        override fun remove() {
            if (isDetached) detachedNodeException()
            heap.removeNode(index)
        }
    }
    
    internal class NodesIterator<Element, Priority>(
        private val data: KoneMutableList<Node<Element, Priority>>,
        private var nextIndex: UInt = 0u,
    ): KoneLinkedSetIterator<LinkedHeapNode<Element, Priority>> {
        override fun hasNext(): Boolean = nextIndex != data.size
        override fun getNext(): LinkedHeapNode<Element, Priority> {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            return data[nextIndex]
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            nextIndex++
        }
        
        override fun hasPrevious(): Boolean = nextIndex > 0u
        override fun getPrevious(): LinkedHeapNode<Element, Priority> {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            return data[nextIndex - 1u]
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            nextIndex--
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class Nodes : KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>> {
        override val size: UInt get() = this@KoneBinaryListBackedMinimumHeap.size
        override fun contains(element: LinkedHeapNode<Element, Priority>): Boolean =
            element is Node<*, *> && element.heap === this@KoneBinaryListBackedMinimumHeap
        override fun iterator(): KoneLinkedSetIterator<LinkedHeapNode<Element, Priority>> = NodesIterator(this@KoneBinaryListBackedMinimumHeap.data, 0u)
    }
    
    internal class ElementsIterator<Element, Priority>(
        private val data: KoneMutableList<Node<Element, Priority>>,
        private var nextIndex: UInt = 0u,
    ): KoneLinearIterator<Element> {
        override fun hasNext(): Boolean = nextIndex != data.size
        override fun nextIndex(): UInt {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            return nextIndex
        }
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            return data[nextIndex].element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            nextIndex++
        }
        
        override fun hasPrevious(): Boolean = nextIndex > 0u
        override fun previousIndex(): UInt {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            return nextIndex - 1u
        }
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            return data[nextIndex - 1u].element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            nextIndex--
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class Elements : KoneReversibleIterable<Element> {
        override val size: UInt get() = this@KoneBinaryListBackedMinimumHeap.size
        override fun iterator(): KoneLinearIterator<Element> = ElementsIterator(this@KoneBinaryListBackedMinimumHeap.data, 0u)
    }
}