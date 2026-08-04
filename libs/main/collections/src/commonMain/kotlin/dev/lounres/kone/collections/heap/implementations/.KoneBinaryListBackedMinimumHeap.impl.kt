/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.heap.LinkedHeapNode
import dev.lounres.kone.collections.heap.LinkedMinimumHeap
import dev.lounres.kone.collections.iterable.KoneReversibleIterable
import dev.lounres.kone.collections.iterator.KoneLinearIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSetIterator
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt


public class KoneBinaryListBackedMinimumHeap<Element, Priority> @PublishedApi internal constructor(
    public val priorityOrder: Order<Priority>,
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
        if (priorityOrder { data[parentIndex].priority gt data[index].priority }) {
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
                    priorityOrder { data[firstChildIndex].priority lt data[index].priority && data[firstChildIndex].priority lt data[secondChildIndex].priority } -> {
                        swapNodes(firstChildIndex, index)
                        siftTheNodeUpToTheLeaf(firstChildIndex)
                    }
                    priorityOrder { data[secondChildIndex].priority lt data[index].priority } -> {
                        swapNodes(secondChildIndex, index)
                        siftTheNodeUpToTheLeaf(secondChildIndex)
                    }
                }
            firstChildIndex < size && priorityOrder { data[firstChildIndex].priority lt data[index].priority } -> {
                swapNodes(firstChildIndex, index)
                siftTheNodeUpToTheLeaf(firstChildIndex)
            }
            secondChildIndex < size && priorityOrder { data[secondChildIndex].priority lt data[index].priority } -> {
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
    
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>> = Nodes(this)
    override val elementsView: KoneReversibleIterable<Element> = Elements(this)
    override val prioritiesView: KoneReversibleIterable<Priority> = Priorities(this)
    
    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority> {
        val newNode = Node(element = element, priority = priority, heap = this, index = size)
        data.add(newNode)
        siftTheNodeDownToTheRoot(data.lastIndex)
        return newNode
    }
    
    override fun takeMinimum(): LinkedHeapNode<Element, Priority> {
        if (size == 0u) accessExtremumOfEmptyHeapException()
        return data[0u]
    }
    
    override fun popMinimum(): LinkedHeapNode<Element, Priority> {
        if (size == 0u) accessExtremumOfEmptyHeapException()
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
                field = value
                if (!isDetached) heap.updatePlacement(index)
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
            if (isDetached) return
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
    internal class Nodes<Element, Priority>(
        private val heap: KoneBinaryListBackedMinimumHeap<Element, Priority>,
    ) : KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>> {
        override val size: UInt get() = heap.size
        override fun contains(element: LinkedHeapNode<Element, Priority>): Boolean =
            element is Node<*, *> && element.heap === heap
        override fun iterator(): KoneLinkedSetIterator<LinkedHeapNode<Element, Priority>> = NodesIterator(heap.data, 0u)
    }
    
    internal class ElementsIterator<Element>(
        private val data: KoneList<Node<Element, *>>,
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
    internal class Elements<Element>(
        private val heap: KoneBinaryListBackedMinimumHeap<Element, *>,
    ) : KoneReversibleIterable<Element> {
        override val size: UInt get() = heap.size
        override fun iterator(): KoneLinearIterator<Element> = ElementsIterator(heap.data, 0u)
    }
    
    internal class PrioritiesIterator<Priority>(
        private val data: KoneList<Node<*, Priority>>,
        private var nextIndex: UInt = 0u,
    ): KoneLinearIterator<Priority> {
        override fun hasNext(): Boolean = nextIndex != data.size
        override fun nextIndex(): UInt {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            return nextIndex
        }
        override fun getNext(): Priority {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, data.size)
            return data[nextIndex].priority
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
        override fun getPrevious(): Priority {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            return data[nextIndex - 1u].priority
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, data.size)
            nextIndex--
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class Priorities<Priority>(
        private val heap: KoneBinaryListBackedMinimumHeap<*, Priority>,
    ) : KoneReversibleIterable<Priority> {
        override val size: UInt get() = heap.size
        override fun iterator(): KoneLinearIterator<Priority> = PrioritiesIterator(heap.data, 0u)
    }
}