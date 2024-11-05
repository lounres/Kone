/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneLinkedSet
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.LinkedHeapNode
import dev.lounres.kone.collections.LinkedMinimumHeap
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.lastIndex
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.gt
import dev.lounres.kone.comparison.lt
import dev.lounres.kone.context.invoke
import dev.lounres.kone.scope


// TODO: Think about linear creation: https://en.wikipedia.org/wiki/Binary_heap#Building_a_heap
@Suppress("UNCHECKED_CAST")
public class BinaryGCMinimumHeap<E, P, out PC: Order<P>> internal constructor(
    public val priorityContext: PC,
    private var rootHolder: NodeHolder<E, P>?,
    private var lastHolder: NodeHolder<E, P>?,
): LinkedMinimumHeap<E, P> {
    override var size: UInt = 0u
        private set

    private fun swapNodeHoldersIdentities(holder1: NodeHolder<E, P>, holder2: NodeHolder<E, P>) {
        holder1.priority = holder2.priority.also { holder2.priority = holder1.priority }
        holder1.node = holder2.node.also { holder2.node = holder1.node }
        holder1.node.holder = holder1
        holder2.node.holder = holder2
    }
    
    private tailrec fun siftTheNodeDownToTheRoot(holder: NodeHolder<E, P>) {
        val parent = holder.parent ?: return
        if (priorityContext { parent.priority gt holder.priority }) {
            swapNodeHoldersIdentities(holder, parent)
            siftTheNodeDownToTheRoot(parent)
        }
    }
    
    private tailrec fun siftTheNodeUpToTheLeaf(holder: NodeHolder<E, P>) {
        val firstChild = holder.firstChild
        val secondChild = holder.secondChild
        when {
            firstChild != null && secondChild != null ->
                when {
                    priorityContext { firstChild.priority lt holder.priority && firstChild.priority lt secondChild.priority } -> {
                        swapNodeHoldersIdentities(firstChild, holder)
                        siftTheNodeUpToTheLeaf(firstChild)
                    }
                    priorityContext { secondChild.priority lt holder.priority } -> {
                        swapNodeHoldersIdentities(secondChild, holder)
                        siftTheNodeUpToTheLeaf(secondChild)
                    }
                }
            firstChild != null && priorityContext { firstChild.priority lt holder.priority } -> {
                swapNodeHoldersIdentities(firstChild, holder)
                siftTheNodeUpToTheLeaf(firstChild)
            }
            secondChild != null && priorityContext { secondChild.priority lt holder.priority } -> {
                swapNodeHoldersIdentities(secondChild, holder)
                siftTheNodeUpToTheLeaf(secondChild)
            }
        }
    }

    private fun siftTheNode(holder: NodeHolder<E, P>) {
        siftTheNodeDownToTheRoot(holder)
        siftTheNodeUpToTheLeaf(holder)
    }

    private fun removeNode(holder: NodeHolder<E, P>) {
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
        
        oldLastHolder.dispose()
        lastHolder = previous
        previous?.next = null
        size--
        if (size == 0u) rootHolder = null
        nodeToSift?.let { siftTheNode(it) }
    }
    
    private fun changePriority(holder: NodeHolder<E, P>, priority: P) {
        holder.priority = priority
        siftTheNode(holder)
    }
    
    override val nodesView: KoneLinkedSet<LinkedHeapNode<E, P>> = Nodes()
    override val elementsView: KoneList<E> = Elements()

    override fun add(element: E, priority: P): LinkedHeapNode<E, P> {
        val newHolder =
            if (size == 0u) {
                NodeHolder<E, P>(
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
                NodeHolder<E, P>(
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
    
    override fun takeMinimum(): LinkedHeapNode<E, P> {
        if (size == 0u) throw NoSuchElementException("Heap is empty")
        val root = rootHolder!!
        return root.node
    }
    
    override fun popMinimum(): LinkedHeapNode<E, P> {
        if (size == 0u) throw NoSuchElementException("Heap is empty")
        val root = rootHolder!!
        return root.node.also { removeNode(root) }
    }
    
    internal class NodeHolder<E, P>(
        heap: BinaryGCMinimumHeap<E, P, *>,
        val index: UInt,
        parent: NodeHolder<E, P>?,
        previous: NodeHolder<E, P>?,
        priority: P,
        element: E,
    ) : Disposable {
        var heap: BinaryGCMinimumHeap<E, P, *>? = heap
        
        var parent: NodeHolder<E, P>? = parent
            private set
        var previous: NodeHolder<E, P>? = previous
            private set
        var next: NodeHolder<E, P>? = null
        var firstChild: NodeHolder<E, P>? = null
        var secondChild: NodeHolder<E, P>? = null
        
        private var _priority: P? = priority
        var priority: P
            get() = _priority as P
            set(value) { _priority = value }
        
        private var _node: Node<E, P>? = Node(element, this)
        var node: Node<E, P>
            get() = _node!!
            set(value) { _node = value }
        
        override fun dispose() {
            heap = null
            parent = null
            previous = null
            next = null
            firstChild = null
            secondChild = null
            _priority = null
            _node = null
        }
        
        fun changePriority(priority: P) {
            (heap ?: throw IllegalStateException("This node holder is disposed and cannot change its priority")).changePriority(this, priority)
        }
        
        fun remove() {
            (heap ?: throw IllegalStateException("This node holder is disposed and cannot be removed")).removeNode(this)
        }
    }

    internal class Node<E, P>(
        override var element: E,
        holder: NodeHolder<E, P>,
    ): LinkedHeapNode<E, P> {
        private var _holder: NodeHolder<E, P>? = holder
        var holder: NodeHolder<E, P>
            get() = _holder!!
            set(value) { _holder = value }
        
        override var priority: P
            get() = (_holder ?: throw IllegalStateException("The node has already been removed and therefore has no priority")).priority
            set(value) {
                _holder!!.changePriority(value)
            }
        
        val heap: BinaryGCMinimumHeap<E, P, *>? get() = _holder?.heap
        override val nextNode: LinkedHeapNode<E, P>? get() = _holder?.next?.node
        override val previousNode: LinkedHeapNode<E, P>? get() = _holder?.previous?.node
        
        override fun remove() {
            (_holder ?: throw IllegalStateException("The node has already been removed")).remove()
            _holder = null
        }
    }
    
    internal class NodesIterator<E, P>(
        private var nextHolder: NodeHolder<E, P>?,
        private val size: UInt,
    ): KoneLinearIterator<LinkedHeapNode<E, P>> {
        private var previousHolder: NodeHolder<E, P>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun nextIndex(): UInt {
            if (!hasNext()) indexException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): LinkedHeapNode<E, P> {
            if (!hasNext()) indexException(nextIndex, size)
            return nextHolder!!.node
        }
        override fun moveNext() {
            if (!hasNext()) indexException(nextIndex, size)
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        
        override fun hasPrevious(): Boolean = previousHolder != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) indexException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): LinkedHeapNode<E, P> {
            if (!hasPrevious()) indexException(nextIndex - 1u, size)
            return previousHolder!!.node
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexException(nextIndex - 1u, size)
            nextIndex++
            nextHolder = previousHolder
            previousHolder = previousHolder!!.previous
        }
    }
    
    internal inner class Nodes : KoneLinkedSet<LinkedHeapNode<E, P>> {
        override val size: UInt get() = this@BinaryGCMinimumHeap.size
        override fun get(index: UInt): LinkedHeapNode<E, P> {
            TODO("Not yet implemented")
        }
        override fun contains(element: LinkedHeapNode<E, P>): Boolean =
            element is Node<E, P> && element.heap === this@BinaryGCMinimumHeap
        override fun iterator(): KoneLinearIterator<LinkedHeapNode<E, P>> = NodesIterator(rootHolder, this@BinaryGCMinimumHeap.size)
        override fun iteratorFrom(index: UInt): KoneLinearIterator<LinkedHeapNode<E, P>> {
            TODO("Not yet implemented")
        }
    }
    
    internal class ElementsIterator<E, P>(
        private var nextHolder: NodeHolder<E, P>?,
        private val size: UInt,
    ): KoneLinearIterator<E> {
        private var previousHolder: NodeHolder<E, P>? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun nextIndex(): UInt {
            if (!hasNext()) indexException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): E {
            if (!hasNext()) indexException(nextIndex, size)
            return nextHolder!!.node.element
        }
        override fun moveNext() {
            if (!hasNext()) indexException(nextIndex, size)
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        
        override fun hasPrevious(): Boolean = previousHolder != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) indexException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): E {
            if (!hasPrevious()) indexException(nextIndex - 1u, size)
            return previousHolder!!.node.element
        }
        override fun movePrevious() {
            if (!hasPrevious()) indexException(nextIndex - 1u, size)
            nextIndex++
            nextHolder = previousHolder
            previousHolder = previousHolder!!.previous
        }
    }
    
    internal inner class Elements : KoneList<E> {
        override val size: UInt get() = this@BinaryGCMinimumHeap.size
        override fun get(index: UInt): E {
            if (index >= size) indexException(index, size)
            val digits = scope {
                var rest = index + 1u
                // TODO: Replace with KoneFixedCapacityArrayList with capacity 32
                KoneGrowableArrayList<UInt>().apply {
                    while (rest > 0u) {
                        add(rest % 2u)
                        rest /= 2u
                    }
                }
            }
            var currentHolder: NodeHolder<E, P> = rootHolder!!
            for (index in digits.lastIndex - 1u downTo 0u)
                currentHolder =
                    if (digits[index] == 0u) currentHolder.firstChild!!
                    else currentHolder.secondChild!!
            return currentHolder.node.element
        }
        override fun iterator(): KoneLinearIterator<E> = ElementsIterator(rootHolder, this@BinaryGCMinimumHeap.size)
        override fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
            TODO("Not yet implemented")
        }
    }
}