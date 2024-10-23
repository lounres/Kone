/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.HeapNode
import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneIterableListSet
import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.KoneListWithContext
import dev.lounres.kone.collections.MinimumHeap
import dev.lounres.kone.collections.indexException
import dev.lounres.kone.collections.noElementException
import dev.lounres.kone.collections.utils.lastIndex
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.scope


// TODO: Think about linear creation: https://en.wikipedia.org/wiki/Binary_heap#Building_a_heap
@Suppress("UNCHECKED_CAST")
public class BinaryGCMinimumHeap<E, out EC: Equality<E>, P, out PC: Order<P>> /*internal*/ constructor(
    public val elementContext: EC,
    public val priorityContext: PC,
): MinimumHeap<E, P> {
    override var size: UInt = 0u
        private set
    
    private var rootHolder: NodeHolder? = null
    private var lastHolder: NodeHolder? = null

    private fun swapNodeHoldersIdentities(holder1: NodeHolder, holder2: NodeHolder) {
        holder1.priority = holder2.priority.also { holder2.priority = holder1.priority }
        holder1.node = holder2.node.also { holder2.node = holder1.node }
        holder1.node.holder = holder1
        holder2.node.holder = holder2
    }
    
    private tailrec fun siftTheNodeDownToTheRoot(holder: NodeHolder) {
        val parent = holder.parent ?: return
        if (priorityContext { parent.priority > holder.priority }) {
            swapNodeHoldersIdentities(holder, parent)
            siftTheNodeDownToTheRoot(parent)
        }
    }
    
    private tailrec fun siftTheNodeUpToTheLeaf(holder: NodeHolder) {
        val firstChild = holder.firstChild
        val secondChild = holder.secondChild
        when {
            firstChild != null && secondChild != null ->
                when {
                    priorityContext { firstChild.priority < holder.priority && firstChild.priority < secondChild.priority } -> {
                        swapNodeHoldersIdentities(firstChild, holder)
                        siftTheNodeUpToTheLeaf(firstChild)
                    }
                    priorityContext { secondChild.priority < holder.priority } -> {
                        swapNodeHoldersIdentities(secondChild, holder)
                        siftTheNodeUpToTheLeaf(secondChild)
                    }
                }
            firstChild != null && priorityContext { firstChild.priority < holder.priority } -> {
                swapNodeHoldersIdentities(firstChild, holder)
                siftTheNodeUpToTheLeaf(firstChild)
            }
            secondChild != null && priorityContext { secondChild.priority < holder.priority } -> {
                swapNodeHoldersIdentities(secondChild, holder)
                siftTheNodeUpToTheLeaf(secondChild)
            }
        }
    }

    private fun siftTheNode(holder: NodeHolder) {
        siftTheNodeDownToTheRoot(holder)
        siftTheNodeUpToTheLeaf(holder)
    }

    private fun removeNode(holder: NodeHolder) {
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
    
    private fun changePriority(holder: NodeHolder, priority: P) {
        holder.priority = priority
        siftTheNode(holder)
    }
    
    override val nodesView: KoneIterableListSet<HeapNode<E, P>> = Nodes()
    override val elementsView: KoneIterableList<E> = Elements()

    override fun add(element: E, priority: P): HeapNode<E, P> {
        val newHolder =
            if (size == 0u) {
                NodeHolder(
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
                NodeHolder(
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
    
    override fun takeMinimum(): HeapNode<E, P> {
        if (size == 0u) throw NoSuchElementException("Heap is empty")
        val root = rootHolder!!
        return root.node
    }
    
    override fun popMinimum(): HeapNode<E, P> {
        if (size == 0u) throw NoSuchElementException("Heap is empty")
        val root = rootHolder!!
        return root.node.also { removeNode(root) }
    }
    
    internal inner class NodeHolder(
        val index: UInt,
        parent: NodeHolder?,
        previous: NodeHolder?,
        priority: P,
        element: E,
    ) : Disposable {
        var parent: BinaryGCMinimumHeap<E, @UnsafeVariance EC, P, @UnsafeVariance PC>.NodeHolder? = parent
            private set
        var previous: BinaryGCMinimumHeap<E, @UnsafeVariance EC, P, @UnsafeVariance PC>.NodeHolder? = previous
            private set
        var next: BinaryGCMinimumHeap<E, @UnsafeVariance EC, P, @UnsafeVariance PC>.NodeHolder? = null
        var firstChild: BinaryGCMinimumHeap<E, @UnsafeVariance EC, P, @UnsafeVariance PC>.NodeHolder? = null
        var secondChild: BinaryGCMinimumHeap<E, @UnsafeVariance EC, P, @UnsafeVariance PC>.NodeHolder? = null
        
        private var _priority: P? = priority
        var priority: P
            get() = _priority as P
            set(value) { _priority = value }
        
        private var _node: Node<E, P>? = Node(element, this)
        var node: Node<E, P>
            get() = _node!!
            set(value) { _node = value }
        
        override fun dispose() {
            parent = null
            previous = null
            next = null
            firstChild = null
            secondChild = null
            _priority = null
            _node = null
        }
        
        fun changePriority(priority: P) {
            changePriority(this, priority)
        }
        
        fun remove() {
            removeNode(this)
        }
    }

    internal class Node<E, P>(
        override var element: E,
        holder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder,
    ): HeapNode<E, P> {
        private var _holder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder? = holder
        var holder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder
            get() = _holder!!
            set(value) { _holder = value }
        
        override var priority: P
            get() = (_holder ?: throw IllegalStateException("The node has already been removed and therefore has no priority")).priority
            set(value) {
                _holder!!.changePriority(value)
            }
        
        override fun remove() {
            (_holder ?: throw IllegalStateException("The node has already been removed")).remove()
            _holder = null
        }
    }
    
    internal class NodesIterator<E, P>(
        private var nextHolder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder?,
        private val size: UInt,
    ): KoneLinearIterator<HeapNode<E, P>> {
        private var previousHolder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun nextIndex(): UInt {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): HeapNode<E, P> {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextHolder!!.node
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(nextIndex, size)
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        
        override fun hasPrevious(): Boolean = previousHolder != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): HeapNode<E, P> {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return previousHolder!!.node
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            nextIndex++
            nextHolder = previousHolder
            previousHolder = previousHolder!!.previous
        }
    }
    
    internal inner class Nodes : KoneIterableListSet<HeapNode<E, P>>, KoneListWithContext<HeapNode<E, P>, Equality<HeapNode<E, P>>> {
        override val elementContext: Equality<HeapNode<E, P>> get() = absoluteEquality()
        override val size: UInt get() = this@BinaryGCMinimumHeap.size
        override fun get(index: UInt): HeapNode<E, P> {
            if (index >= size) indexException(index, size)
            val digits = scope {
                var rest = index + 1u
                KoneGrowableArrayList<UInt>().apply {
                    while (rest > 0u) {
                        add(rest % 2u)
                        rest /= 2u
                    }
                }
            }
            var currentHolder: NodeHolder = rootHolder!!
            for (index in digits.lastIndex - 1u downTo 0u)
                currentHolder =
                    if (digits[index] == 0u) currentHolder.firstChild!!
                    else currentHolder.secondChild!!
            return currentHolder.node
        }
        override fun iterator(): KoneLinearIterator<HeapNode<E, P>> = NodesIterator(rootHolder, this@BinaryGCMinimumHeap.size)
    }
    
    internal class ElementsIterator<E, P>(
        private var nextHolder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder?,
        private val size: UInt,
    ): KoneLinearIterator<E> {
        private var previousHolder: BinaryGCMinimumHeap<E, *, P, *>.NodeHolder? = null
        private var nextIndex: UInt = 0u
        
        override fun hasNext(): Boolean = nextHolder != null
        override fun nextIndex(): UInt {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextIndex
        }
        override fun getNext(): E {
            if (!hasNext()) noElementException(nextIndex, size)
            return nextHolder!!.node.element
        }
        override fun moveNext() {
            if (!hasNext()) noElementException(nextIndex, size)
            nextIndex++
            previousHolder = nextHolder
            nextHolder = nextHolder!!.next
        }
        
        override fun hasPrevious(): Boolean = previousHolder != null
        override fun previousIndex(): UInt {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return nextIndex - 1u
        }
        override fun getPrevious(): E {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            return previousHolder!!.node.element
        }
        override fun movePrevious() {
            if (!hasPrevious()) noElementException(nextIndex - 1u, size)
            nextIndex++
            nextHolder = previousHolder
            previousHolder = previousHolder!!.previous
        }
    }
    
    internal inner class Elements : KoneIterableListSet<E>, KoneListWithContext<E, EC> {
        override val elementContext: EC get() = this@BinaryGCMinimumHeap.elementContext
        override val size: UInt get() = this@BinaryGCMinimumHeap.size
        override fun get(index: UInt): E {
            if (index >= size) indexException(index, size)
            val digits = scope {
                var rest = index + 1u
                KoneGrowableArrayList<UInt>().apply {
                    while (rest > 0u) {
                        add(rest % 2u)
                        rest /= 2u
                    }
                }
            }
            var currentHolder: NodeHolder = rootHolder!!
            for (index in digits.lastIndex - 1u downTo 0u)
                currentHolder =
                    if (digits[index] == 0u) currentHolder.firstChild!!
                    else currentHolder.secondChild!!
            return currentHolder.node.element
        }
        override fun iterator(): KoneLinearIterator<E> = ElementsIterator(rootHolder, this@BinaryGCMinimumHeap.size)
    }
}