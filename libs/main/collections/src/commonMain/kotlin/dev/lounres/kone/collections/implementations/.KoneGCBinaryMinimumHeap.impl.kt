/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneLinkedReifiedSet
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.LinkedHeapNode
import dev.lounres.kone.collections.LinkedMinimumHeap
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.disposedInstanceException
import dev.lounres.kone.collections.indexOutOfBoundsException
import dev.lounres.kone.collections.lastIndex
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.gt
import dev.lounres.kone.comparison.lt
import dev.lounres.kone.context.invoke
import dev.lounres.kone.scope


// TODO: Think about linear creation: https://en.wikipedia.org/wiki/Binary_heap#Building_a_heap
@Suppress("UNCHECKED_CAST")
public class KoneGCBinaryMinimumHeap<Element, Priority, out PriorityContext: Order<Priority>> internal constructor(
    public val priorityContext: PriorityContext,
    private var rootHolder: NodeHolder<Element, Priority>?,
    private var lastHolder: NodeHolder<Element, Priority>?,
): LinkedMinimumHeap<Element, Priority> {
    override var size: UInt = 0u
        private set

    private fun swapNodeHoldersIdentities(holder1: NodeHolder<Element, Priority>, holder2: NodeHolder<Element, Priority>) {
        holder1.node = holder2.node.also { holder2.node = holder1.node }
        holder1.node.holder = holder1
        holder2.node.holder = holder2
    }
    
    private tailrec fun siftTheNodeDownToTheRoot(holder: NodeHolder<Element, Priority>) {
        val parent = holder.parent ?: return
        if (priorityContext { parent.node.priority gt holder.node.priority }) {
            swapNodeHoldersIdentities(holder, parent)
            siftTheNodeDownToTheRoot(parent)
        }
    }
    
    private tailrec fun siftTheNodeUpToTheLeaf(holder: NodeHolder<Element, Priority>) {
        val firstChild = holder.firstChild
        val secondChild = holder.secondChild
        when {
            firstChild != null && secondChild != null ->
                when {
                    priorityContext { firstChild.node.priority lt holder.node.priority && firstChild.node.priority lt secondChild.node.priority } -> {
                        swapNodeHoldersIdentities(firstChild, holder)
                        siftTheNodeUpToTheLeaf(firstChild)
                    }
                    priorityContext { secondChild.node.priority lt holder.node.priority } -> {
                        swapNodeHoldersIdentities(secondChild, holder)
                        siftTheNodeUpToTheLeaf(secondChild)
                    }
                }
            firstChild != null && priorityContext { firstChild.node.priority lt holder.node.priority } -> {
                swapNodeHoldersIdentities(firstChild, holder)
                siftTheNodeUpToTheLeaf(firstChild)
            }
            secondChild != null && priorityContext { secondChild.node.priority lt holder.node.priority } -> {
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
                isItAFirstChild && isItASecondChild -> throw IllegalStateException("Holder's parent stores it as both the first child and the second child")
                isItAFirstChild -> parent.firstChild = null
                isItASecondChild -> parent.secondChild = null
                else -> throw IllegalStateException("Holder's parent does not store it as neither the first child or the second child")
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
    
    override val nodesView: KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>> = Nodes()
    override val elementsView: KoneList<Element> = Elements()

    override fun add(element: Element, priority: Priority): LinkedHeapNode<Element, Priority> {
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
                        else -> throw IllegalStateException("Chose parent with both children present to insert a new child in it")
                    }
                }
            }
        val result = newHolder.node
        lastHolder = newHolder
        size++
        
        siftTheNodeDownToTheRoot(newHolder)
        
        return result
    }
    
    override fun takeMinimum(): LinkedHeapNode<Element, Priority> {
        if (size == 0u) throw NoSuchElementException("Heap is empty")
        val root = rootHolder!!
        return root.node
    }
    
    override fun popMinimum(): LinkedHeapNode<Element, Priority> {
        if (size == 0u) throw NoSuchElementException("Heap is empty")
        val root = rootHolder!!
        return root.node.also { removeNode(root) }
    }
    
    internal class NodeHolder<Element, Priority>(
        heap: KoneGCBinaryMinimumHeap<Element, Priority, *>,
        val index: UInt,
        parent: NodeHolder<Element, Priority>?,
        previous: NodeHolder<Element, Priority>?,
        priority: Priority,
        element: Element,
    ) : Disposable {
        override var isDisposed: Boolean = false
            private set
        
        private var _heap: KoneGCBinaryMinimumHeap<Element, Priority, *>? = heap
        var heap: KoneGCBinaryMinimumHeap<Element, Priority, *>
            get() = _heap!!
            set(value) { _heap = value }
        
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
    ): LinkedHeapNode<Element, Priority> {
        override var isDetached: Boolean = false
            private set
        
        private var _holder: NodeHolder<Element, Priority>? = holder
        var holder: NodeHolder<Element, Priority>
            get() = _holder!!
            set(value) { _holder = value }
        
        override var priority: Priority = priority
            set(value) {
                if (isDetached) detachedNodeException()
                field = value
                _holder!!.updatePlacement()
            }
        
        fun detach() {
            _holder = null
            isDetached = true
        }
        
        val heap: KoneGCBinaryMinimumHeap<Element, Priority, *>?
            get() = if (isDetached) detachedNodeException() else _holder?.heap
        override val nextNode: LinkedHeapNode<Element, Priority>?
            get() = if (isDetached) detachedNodeException() else _holder?.next?.node
        override val previousNode: LinkedHeapNode<Element, Priority>?
            get() = if (isDetached) detachedNodeException() else _holder?.previous?.node
        
        override fun remove() {
            if (isDetached) detachedNodeException()
            _holder!!.remove()
            _holder = null
            isDetached = true
        }
    }
    
    internal class NodesIterator<Element, Priority>(
        private var nextHolder: NodeHolder<Element, Priority>?,
        private val size: UInt,
    ): KoneLinearIterator<LinkedHeapNode<Element, Priority>> {
        private var previousHolder: NodeHolder<Element, Priority>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun nextIndex(): UInt {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): LinkedHeapNode<Element, Priority> {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextHolder!!.node
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        
        override fun hasPrevious(): Boolean = previousHolder != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): LinkedHeapNode<Element, Priority> {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return previousHolder!!.node
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            nextIndex++
            nextHolder = previousHolder
            previousHolder = previousHolder!!.previous
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class Nodes : KoneLinkedReifiedSet<LinkedHeapNode<Element, Priority>> {
        override val size: UInt get() = this@KoneGCBinaryMinimumHeap.size
        override fun get(index: UInt): LinkedHeapNode<Element, Priority> {
            TODO("Not yet implemented")
        }
        override fun contains(element: LinkedHeapNode<Element, Priority>): Boolean =
            element is Node<*, *> && element.heap === this@KoneGCBinaryMinimumHeap
        override fun iterator(): KoneLinearIterator<LinkedHeapNode<Element, Priority>> = NodesIterator(rootHolder, this@KoneGCBinaryMinimumHeap.size)
        override fun iteratorFrom(index: UInt): KoneLinearIterator<LinkedHeapNode<Element, Priority>> {
            TODO("Not yet implemented")
        }
    }
    
    internal class ElementsIterator<Element, Priority>(
        private var nextHolder: NodeHolder<Element, Priority>?,
        private val size: UInt,
    ): KoneLinearIterator<Element> {
        private var previousHolder: NodeHolder<Element, Priority>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun nextIndex(): UInt {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): Element {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            return nextHolder!!.node.element
        }
        override fun moveNext() {
            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        
        override fun hasPrevious(): Boolean = previousHolder != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): Element {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            return previousHolder!!.node.element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexOutOfBoundsException(nextIndex - 1u, size)
            nextIndex++
            nextHolder = previousHolder
            previousHolder = previousHolder!!.previous
        }
    }
    
    @OptIn(DelicateCollectionsInheritanceAPI::class)
    internal inner class Elements : KoneList<Element> {
        override val size: UInt get() = this@KoneGCBinaryMinimumHeap.size
        override fun get(index: UInt): Element {
            if (index >= size) indexOutOfBoundsException(index, size)
            val digits = scope {
                var rest = index + 1u
                KoneArrayFixedCapacityList<UInt>(32u).apply {
                    while (rest > 0u) {
                        add(rest % 2u)
                        rest /= 2u
                    }
                }
            }
            var currentHolder: NodeHolder<Element, Priority> = rootHolder!!
            for (index in digits.lastIndex - 1u downTo 0u)
                currentHolder =
                    if (digits[index] == 0u) currentHolder.firstChild!!
                    else currentHolder.secondChild!!
            return currentHolder.node.element
        }
        override fun iterator(): KoneLinearIterator<Element> = ElementsIterator(rootHolder, this@KoneGCBinaryMinimumHeap.size)
        override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> {
            TODO("Not yet implemented")
        }
    }
}