/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.isNotEmpty
import dev.lounres.kone.collections.iterable.KoneRemovableIterable
import dev.lounres.kone.collections.iterator.KoneRemovableIterator
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.set.KoneRemovableReifiedSet
import dev.lounres.kone.collections.set.KoneRemovableSetIterator
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt


public class KoneBinaryListBackedMinimumHeap<Element, Priority> @PublishedApi internal constructor(
    public val priorityOrder: Order<Priority>,
    data: KoneMutableList<Node<Element, Priority>>?,
): MinimumHeap<Element, Priority>, Disposable {
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
    
    override val nodesView: KoneRemovableReifiedSet<HeapNode<Element, Priority>> = Nodes(this)
    override val elementsView: KoneRemovableIterable<Element> = Elements(this)
    override val prioritiesView: KoneRemovableIterable<Priority> = Priorities(this)
    
    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
        val newNode = Node(element = element, priority = priority, heap = this, index = size)
        data.add(newNode)
        siftTheNodeDownToTheRoot(data.lastIndex)
        return newNode
    }
    
    override fun takeMinimum(): HeapNode<Element, Priority> {
        if (size == 0u) accessExtremumOfEmptyHeapException()
        return data[0u]
    }
    
    override fun popMinimum(): HeapNode<Element, Priority> {
        if (size == 0u) accessExtremumOfEmptyHeapException()
        return data[0u].also { removeNode(0u) }
    }
    
    @PublishedApi
    internal class Node<Element, Priority>(
        override var element: Element,
        priority: Priority,
        heap: KoneBinaryListBackedMinimumHeap<Element, Priority>,
        internal var index: UInt,
    ): HeapNode<Element, Priority> {
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
        
        override fun remove() {
            if (isDetached) return
            heap.removeNode(index)
        }
    }
    
    internal class NodesIterator<Element, Priority>(
        private val heap: KoneBinaryListBackedMinimumHeap<Element, Priority>,
        private var nextIndex: UInt = 0u,
    ): KoneRemovableSetIterator<HeapNode<Element, Priority>> {
        override fun hasNext(): Boolean = nextIndex != heap.data.size
        override fun getNext(): HeapNode<Element, Priority> {
            if (!hasNext()) noNextElementInIteratorException()
            return heap.data[nextIndex]
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextIndex++
        }
        override fun removeNext() {
            heap.removeNode(nextIndex)
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[next index = $nextIndex]"
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class Nodes<Element, Priority>(
        private val heap: KoneBinaryListBackedMinimumHeap<Element, Priority>,
    ) : KoneRemovableReifiedSet<HeapNode<Element, Priority>> {
        override val size: UInt get() = heap.size
        override fun contains(element: HeapNode<Element, Priority>): Boolean =
            element is Node && element.heap === heap
        override fun remove(element: HeapNode<Element, Priority>) {
            if (element !is Node || element.heap !== heap) return
            element.remove()
        }
        override fun removeAll() {
            while (heap.isNotEmpty()) heap.popMinimum() // TODO: Optimise this
        }
        override fun iterator(): KoneRemovableIterator<HeapNode<Element, Priority>> = NodesIterator(heap, 0u)
    }
    
    internal class ElementsIterator<Element>(
        private val heap: KoneBinaryListBackedMinimumHeap<Element, *>,
        private var nextIndex: UInt = 0u,
    ): KoneRemovableIterator<Element> {
        override fun hasNext(): Boolean = nextIndex != heap.data.size
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return heap.data[nextIndex].element
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextIndex++
        }
        override fun removeNext() {
            heap.removeNode(nextIndex)
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[next index = $nextIndex]"
    }
    
    internal class Elements<Element>(
        private val heap: KoneBinaryListBackedMinimumHeap<Element, *>,
    ) : KoneRemovableIterable<Element> {
        override val size: UInt get() = heap.size
        override fun iterator(): KoneRemovableIterator<Element> = ElementsIterator(heap, 0u)
    }
    
    internal class PrioritiesIterator<Priority>(
        private val heap: KoneBinaryListBackedMinimumHeap<*, Priority>,
        private var nextIndex: UInt = 0u,
    ): KoneRemovableIterator<Priority> {
        override fun hasNext(): Boolean = nextIndex != heap.data.size
        override fun getNext(): Priority {
            if (!hasNext()) noNextElementInIteratorException()
            return heap.data[nextIndex].priority
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextIndex++
        }
        override fun removeNext() {
            heap.removeNode(nextIndex)
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[next index = $nextIndex]"
    }
    
    internal class Priorities<Priority>(
        private val heap: KoneBinaryListBackedMinimumHeap<*, Priority>,
    ) : KoneRemovableIterable<Priority> {
        override val size: UInt get() = heap.size
        override fun iterator(): KoneRemovableIterator<Priority> = PrioritiesIterator(heap, 0u)
    }
}