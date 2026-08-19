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
import dev.lounres.kone.collections.set.KoneRemovableReifiedSet
import dev.lounres.kone.collections.set.KoneRemovableSetIterator
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt


// TODO: Make the implementation disposable
public class KoneBinaryGCMinimumHeap<Element, Priority> @PublishedApi internal constructor(
    internal val priorityOrder: Order<Priority>,
    @PublishedApi
    internal var rootHolder: NodeHolder<Element, Priority>?,
    @PublishedApi
    internal var lastHolder: NodeHolder<Element, Priority>?,
    size: UInt = 0u,
): MinimumHeap<Element, Priority> {
    override var size: UInt = size
        internal set

    private fun swapNodeHoldersIdentities(holder1: NodeHolder<Element, Priority>, holder2: NodeHolder<Element, Priority>) {
        holder1.node = holder2.node.also { holder2.node = holder1.node }
        holder1.node.holder = holder1
        holder2.node.holder = holder2
    }
    
    private tailrec fun siftTheNodeDownToTheRoot(holder: NodeHolder<Element, Priority>) {
        val parent = holder.parent ?: return
        if (priorityOrder { parent.node.priority gt holder.node.priority }) {
            swapNodeHoldersIdentities(holder, parent)
            siftTheNodeDownToTheRoot(parent)
        }
    }
    
    @PublishedApi
    internal tailrec fun siftTheNodeUpToTheLeaf(holder: NodeHolder<Element, Priority>) {
        val firstChild = holder.firstChild
        val secondChild = holder.secondChild
        when {
            firstChild != null && secondChild != null ->
                when {
                    priorityOrder { firstChild.node.priority lt holder.node.priority && firstChild.node.priority lt secondChild.node.priority } -> {
                        swapNodeHoldersIdentities(firstChild, holder)
                        siftTheNodeUpToTheLeaf(firstChild)
                    }
                    priorityOrder { secondChild.node.priority lt holder.node.priority } -> {
                        swapNodeHoldersIdentities(secondChild, holder)
                        siftTheNodeUpToTheLeaf(secondChild)
                    }
                }
            firstChild != null && priorityOrder { firstChild.node.priority lt holder.node.priority } -> {
                swapNodeHoldersIdentities(firstChild, holder)
                siftTheNodeUpToTheLeaf(firstChild)
            }
            secondChild != null && priorityOrder { secondChild.node.priority lt holder.node.priority } -> {
                swapNodeHoldersIdentities(secondChild, holder)
                siftTheNodeUpToTheLeaf(secondChild)
            }
        }
    }

    private fun siftTheNode(holder: NodeHolder<Element, Priority>) {
        siftTheNodeDownToTheRoot(holder)
        siftTheNodeUpToTheLeaf(holder)
    }

    private fun removeNode(holder: NodeHolder<Element, Priority>) {
        val oldLastHolder = lastHolder!!
        val nodeToSift =
            if (holder !== oldLastHolder) {
                swapNodeHoldersIdentities(holder, oldLastHolder)
                holder
            } else null

        val previous = oldLastHolder.previous
        check(oldLastHolder.next == null) { "Trying to internally remove not last node holder" }
        check(oldLastHolder.firstChild == null && oldLastHolder.secondChild == null) { "For some reason non-leaf node is being removed" }
        
        if (oldLastHolder === rootHolder) {
            rootHolder = null
            lastHolder = null
        } else {
            val parent = oldLastHolder.parent!!
            val isItAFirstChild = parent.firstChild === oldLastHolder
            val isItASecondChild = parent.secondChild === oldLastHolder
            when {
                isItAFirstChild && isItASecondChild -> error("Holder's parent stores it as both the first child and the second child")
                isItAFirstChild -> parent.firstChild = null
                isItASecondChild -> parent.secondChild = null
                else -> error("Holder's parent does not store it as neither the first child or the second child")
            }
        }
        
        oldLastHolder.node.detach()
        oldLastHolder.dispose()
        lastHolder = previous
        previous?.next = null
        size--
        if (size == 0u) rootHolder = null
        nodeToSift?.let { siftTheNode(it) }
    }
    
    private fun updatePlacement(holder: NodeHolder<Element, Priority>) {
        siftTheNode(holder)
    }
    
    override val nodesView: KoneRemovableReifiedSet<HeapNode<Element, Priority>> = Nodes(this)
    override val elementsView: KoneRemovableIterable<Element> = Elements(this)
    override val prioritiesView: KoneRemovableIterable<Priority> = Priorities(this)

    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
        val newHolder =
            if (size == 0u) {
                NodeHolder<Element, Priority>(
                    heap = this,
                    index = 0u,
                    parent = null,
                    previous = null,
                    priority = priority,
                    element = element,
                ).also {
                    rootHolder = it
                }
            } else {
                val previous = lastHolder!!
                val parent = when {
                    previous.index % 2u == 1u -> previous.parent
                    previous.parent == null -> previous
                    else -> previous.parent!!.next
                }
                NodeHolder<Element, Priority>(
                    heap = this,
                    index = previous.index + 1u,
                    parent = parent,
                    previous = previous,
                    priority = priority,
                    element = element,
                ).also {
                    previous.next = it
                    if (parent != null) when {
                        parent.firstChild == null -> parent.firstChild = it
                        parent.secondChild == null -> parent.secondChild = it
                        else -> error("Chose parent with both children present to insert a new child in it")
                    }
                }
            }
        val result = newHolder.node
        lastHolder = newHolder
        size++
        
        siftTheNodeDownToTheRoot(newHolder)
        
        return result
    }
    
    override fun takeMinimum(): HeapNode<Element, Priority> {
        if (size == 0u) accessExtremumOfEmptyHeapException()
        val root = rootHolder!!
        return root.node
    }
    
    override fun popMinimum(): HeapNode<Element, Priority> {
        if (size == 0u) accessExtremumOfEmptyHeapException()
        val root = rootHolder!!
        return root.node.also { removeNode(root) }
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = super.hashCode()
    override fun toString(): String = "${super.toString()}[size = $size]"
    
    @PublishedApi
    internal class NodeHolder<Element, Priority>(
        heap: KoneBinaryGCMinimumHeap<Element, Priority>,
        val index: UInt,
        parent: NodeHolder<Element, Priority>?,
        previous: NodeHolder<Element, Priority>?,
        priority: Priority,
        element: Element,
    ) : Disposable {
        override var isDisposed: Boolean = false
            private set
        
        private var _heap: KoneBinaryGCMinimumHeap<Element, Priority>? = heap
        val heap: KoneBinaryGCMinimumHeap<Element, Priority> get() = _heap!!
        
        var parent: NodeHolder<Element, Priority>? = parent
            private set
        var previous: NodeHolder<Element, Priority>? = previous
            private set
        var next: NodeHolder<Element, Priority>? = null
        var firstChild: NodeHolder<Element, Priority>? = null
        var secondChild: NodeHolder<Element, Priority>? = null
        
        private var _node: Node<Element, Priority>? = Node(element, priority, this)
        var node: Node<Element, Priority>
            get() = _node!!
            set(value) { _node = value }
        
        override fun dispose() {
            if (isDisposed) return
            _heap = null
            parent = null
            previous = null
            next = null
            firstChild = null
            secondChild = null
            _node = null
            isDisposed = true
        }
        
        fun updatePlacement() {
            if (isDisposed) disposedInstanceException()
            heap.updatePlacement(this)
        }
        
        fun remove() {
            if (isDisposed) disposedInstanceException()
            heap.removeNode(this)
        }
    }

    internal class Node<Element, Priority>(
        override var element: Element,
        priority: Priority,
        holder: NodeHolder<Element, Priority>,
    ): HeapNode<Element, Priority> {
        override val isDetached: Boolean get() = _holder == null
        
        private var _holder: NodeHolder<Element, Priority>? = holder
        var holder: NodeHolder<Element, Priority>
            get() = _holder!!
            set(value) { _holder = value }
        
        override var priority: Priority = priority
            set(value) {
                field = value
                if (!isDetached) _holder!!.updatePlacement()
            }
        
        fun detach() {
            _holder = null
        }
        
        val heap: KoneBinaryGCMinimumHeap<Element, Priority>?
            get() = if (isDetached) detachedNodeException() else _holder?.heap
        
        override fun remove() {
            if (isDetached) return
            _holder!!.remove()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = element.hashCode() * 31 + priority.hashCode()
        override fun toString(): String = "${super.toString()}[element = $element, priority = $priority]"
    }
    
    internal class NodesIterator<Element, Priority>(
        private var nextHolder: NodeHolder<Element, Priority>?,
    ): KoneRemovableSetIterator<HeapNode<Element, Priority>> {
        private var previousHolder: NodeHolder<Element, Priority>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun getNext(): HeapNode<Element, Priority> {
            if (!hasNext()) noNextElementInIteratorException()
            return nextHolder!!.node
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextHolder!!.node.remove()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[next node = ${nextHolder?.node}, previous node = ${previousHolder?.node}, next index = $nextIndex]"
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal class Nodes<Element, Priority>(
        private val heap: KoneBinaryGCMinimumHeap<Element, Priority>,
    ) : KoneRemovableReifiedSet<HeapNode<Element, Priority>> {
        override val size: UInt get() = heap.size
        override fun contains(element: HeapNode<Element, Priority>): Boolean =
            element is Node<*, *> && element.heap === heap
        override fun remove(element: HeapNode<Element, Priority>) {
            if (element !is Node || element.heap !== heap) return
            element.remove()
        }
        override fun removeAll() {
            while (heap.isNotEmpty()) heap.popMinimum() // TODO: Optimise this
        }
        override fun iterator(): KoneRemovableSetIterator<HeapNode<Element, Priority>> = NodesIterator(heap.rootHolder)
    }
    
    internal class ElementsIterator<Element>(
        private var nextHolder: NodeHolder<Element, *>?,
    ): KoneRemovableIterator<Element> {
        private var previousHolder: NodeHolder<Element, *>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun getNext(): Element {
            if (!hasNext()) noNextElementInIteratorException()
            return nextHolder!!.node.element
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextHolder!!.node.remove()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[next node = ${nextHolder?.node}, previous node = ${previousHolder?.node}, next index = $nextIndex]"
    }
    
    internal class Elements<Element>(
        private val heap: KoneBinaryGCMinimumHeap<Element, *>,
    ) : KoneRemovableIterable<Element> {
        override val size: UInt get() = heap.size
        override fun iterator(): KoneRemovableIterator<Element> = ElementsIterator(heap.rootHolder)
    }
    
    internal class PrioritiesIterator<Priority>(
        private var nextHolder: NodeHolder<*, Priority>?,
    ): KoneRemovableIterator<Priority> {
        private var previousHolder: NodeHolder<*, Priority>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun getNext(): Priority {
            if (!hasNext()) noNextElementInIteratorException()
            return nextHolder!!.node.priority
        }
        override fun moveNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        override fun removeNext() {
            if (!hasNext()) noNextElementInIteratorException()
            nextHolder!!.node.remove()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = super.hashCode()
        override fun toString(): String = "${super.toString()}[next node = ${nextHolder?.node}, previous node = ${previousHolder?.node}, next index = $nextIndex]"
    }
    
    internal class Priorities<Priority>(
        private val heap: KoneBinaryGCMinimumHeap<*, Priority>,
    ) : KoneRemovableIterable<Priority> {
        override val size: UInt get() = heap.size
        override fun iterator(): KoneRemovableIterator<Priority> = PrioritiesIterator(heap.rootHolder)
    }
}