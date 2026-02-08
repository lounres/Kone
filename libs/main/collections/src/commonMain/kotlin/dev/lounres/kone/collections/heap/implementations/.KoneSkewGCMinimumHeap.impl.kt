/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

//import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
//import dev.lounres.kone.collections.Disposable
//import dev.lounres.kone.collections.accessRootOfEmptyHeapException
//import dev.lounres.kone.collections.detachedNodeException
//import dev.lounres.kone.collections.disposedInstanceException
//import dev.lounres.kone.collections.heap.HeapNode
//import dev.lounres.kone.collections.heap.MinimumHeap
//import dev.lounres.kone.collections.indexOutOfBoundsException
//import dev.lounres.kone.collections.iterables.KoneIterable
//import dev.lounres.kone.collections.iterables.KoneIterator
//import dev.lounres.kone.collections.set.KoneReifiedSet
//import dev.lounres.kone.collections.set.KoneSetIterator
//import dev.lounres.kone.contexts.invoke
//import dev.lounres.kone.relations.Order
//import dev.lounres.kone.relations.gt
//import dev.lounres.kone.relations.leq
//import dev.lounres.kone.relations.lt
//
//
//public class KoneSkewGCMinimumHeap<Element, Priority>(
//    private val priorityOrder: Order<Priority>,
//) : MinimumHeap<Element, Priority> {
//    override var size: UInt = 0u
//        internal set
//
//    @PublishedApi
//    internal var rootHolder: NodeHolder<Element, Priority>? = null
//
//    private fun swapNodeHoldersIdentities(holder1: NodeHolder<Element, Priority>, holder2: NodeHolder<Element, Priority>) {
//        holder1.node = holder2.node.also { holder2.node = holder1.node }
//        holder1.node.holder = holder1
//        holder2.node.holder = holder2
//    }
//
//    private tailrec fun siftTheNodeDownToTheRoot(holder: NodeHolder<Element, Priority>) {
//        val parent = holder.parent ?: return
//        if (priorityOrder { parent.node.priority gt holder.node.priority }) {
//            swapNodeHoldersIdentities(holder, parent)
//            siftTheNodeDownToTheRoot(parent)
//        }
//    }
//
//    @PublishedApi
//    internal tailrec fun siftTheNodeUpToTheLeaf(holder: NodeHolder<Element, Priority>) {
//        val firstChild = holder.firstChild
//        val secondChild = holder.secondChild
//        when {
//            firstChild != null && secondChild != null ->
//                when {
//                    priorityOrder { firstChild.node.priority lt holder.node.priority && firstChild.node.priority lt secondChild.node.priority } -> {
//                        swapNodeHoldersIdentities(firstChild, holder)
//                        siftTheNodeUpToTheLeaf(firstChild)
//                    }
//                    priorityOrder { secondChild.node.priority lt holder.node.priority } -> {
//                        swapNodeHoldersIdentities(secondChild, holder)
//                        siftTheNodeUpToTheLeaf(secondChild)
//                    }
//                }
//            firstChild != null && priorityOrder { firstChild.node.priority lt holder.node.priority } -> {
//                swapNodeHoldersIdentities(firstChild, holder)
//                siftTheNodeUpToTheLeaf(firstChild)
//            }
//            secondChild != null && priorityOrder { secondChild.node.priority lt holder.node.priority } -> {
//                swapNodeHoldersIdentities(secondChild, holder)
//                siftTheNodeUpToTheLeaf(secondChild)
//            }
//        }
//    }
//
//    private fun siftTheNode(holder: NodeHolder<Element, Priority>) {
//        siftTheNodeDownToTheRoot(holder)
//        siftTheNodeUpToTheLeaf(holder)
//    }
//
//    private tailrec fun meldAttaching(
//        rootHolder1: NodeHolder<Element, Priority>?,
//        rootHolder2: NodeHolder<Element, Priority>?,
//        parent: NodeHolder<Element, Priority>
//    ) {
//        when {
//            rootHolder1 == null -> {
//                parent.firstChild = rootHolder2
//                rootHolder2?.parent = parent
//                return
//            }
//            rootHolder2 == null -> {
//                parent.firstChild = rootHolder1
//                rootHolder1.parent = parent
//                return
//            }
//        }
//
//        val child: NodeHolder<Element, Priority>
//        val other: NodeHolder<Element, Priority>
//        if (priorityOrder { rootHolder1.node.priority leq rootHolder2.node.priority }) {
//            child = rootHolder1
//            other = rootHolder2
//        } else {
//            child = rootHolder2
//            other = rootHolder1
//        }
//        child.parent = parent
//        parent.firstChild = child
//
//        val firstGrandChild = child.firstChild
//        val secondGrandChild = child.secondChild
//        child.firstChild = null
//        child.secondChild = firstGrandChild
//        secondGrandChild?.parent = null
//
//        meldAttaching(other, secondGrandChild, child)
//    }
//
//    private fun meld(rootHolder1: NodeHolder<Element, Priority>?, rootHolder2: NodeHolder<Element, Priority>?): NodeHolder<Element, Priority>? {
//        when {
//            rootHolder1 == null -> return rootHolder2
//            rootHolder2 == null -> return rootHolder1
//        }
//
//        val newRoot: NodeHolder<Element, Priority>
//        val other: NodeHolder<Element, Priority>
//        if (priorityOrder { rootHolder1.node.priority leq rootHolder2.node.priority }) {
//            newRoot = rootHolder1
//            other = rootHolder2
//        } else {
//            newRoot = rootHolder2
//            other = rootHolder1
//        }
//
//        val firstChild = newRoot.firstChild
//        val secondChild = newRoot.secondChild
//        newRoot.firstChild = null
//        newRoot.secondChild = firstChild
//        secondChild?.parent = null
//
//        meldAttaching(other, secondChild, newRoot)
//
//        return newRoot
//    }
//
//    private fun removeNode(holder: NodeHolder<Element, Priority>) {
//        val oldRootHolder = rootHolder!!
//        val nodeToSift =
//            if (holder !== oldRootHolder) {
//                swapNodeHoldersIdentities(holder, oldRootHolder)
//                holder
//            } else null
//
//        val first = oldRootHolder.firstChild
//        val second = oldRootHolder.secondChild
//        first?.parent = null
//        second?.parent = null
//        oldRootHolder.node.detach()
//        oldRootHolder.dispose()
//        if (nodeToSift != null) siftTheNode(nodeToSift)
//        rootHolder = meld(first, second)
//
//        size--
//    }
//
//    private fun updatePlacement(holder: NodeHolder<Element, Priority>) {
//        siftTheNode(holder)
//    }
//
//    override val nodesView: KoneReifiedSet<HeapNode<Element, Priority>> = Nodes(this)
//    override val elementsView: KoneIterable<Element> = Elements(this)
//    override val prioritiesView: KoneIterable<Priority> = Priorities(this)
//
//    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
//        val newHolder = NodeHolder(
//            heap = this,
//            parent = null,
//            priority = priority,
//            element = element,
//        )
//        val result = newHolder.node
//
//        rootHolder = meld(rootHolder, newHolder)
//        size++
//
//        return result
//    }
//
//    override fun takeMinimum(): HeapNode<Element, Priority> {
//        if (size == 0u) accessRootOfEmptyHeapException()
//        val root = rootHolder!!
//        return root.node
//    }
//
//    override fun popMinimum(): HeapNode<Element, Priority> {
//        if (size == 0u) accessRootOfEmptyHeapException()
//        val root = rootHolder!!
//        return root.node.also { removeNode(root) }
//    }
//
//    @PublishedApi
//    internal class NodeHolder<Element, Priority>(
//        heap: KoneSkewGCMinimumHeap<Element, Priority>,
//        parent: NodeHolder<Element, Priority>?,
//        priority: Priority,
//        element: Element,
//    ) : Disposable {
//        override var isDisposed: Boolean = false
//            private set
//
//        private var _heap: KoneSkewGCMinimumHeap<Element, Priority>? = heap
//        var heap: KoneSkewGCMinimumHeap<Element, Priority>
//            get() = _heap!!
//            set(value) { _heap = value }
//
//        var parent: NodeHolder<Element, Priority>? = parent
//        var firstChild: NodeHolder<Element, Priority>? = null
//        var secondChild: NodeHolder<Element, Priority>? = null
//
//        private var _node: Node<Element, Priority>? = Node(element, priority, this)
//        var node: Node<Element, Priority>
//            get() = _node!!
//            set(value) { _node = value }
//
//        override fun dispose() {
//            if (isDisposed) return
//            _heap = null
//            parent = null
//            firstChild = null
//            secondChild = null
//            _node = null
//            isDisposed = true
//        }
//
//        fun updatePlacement() {
//            if (isDisposed) disposedInstanceException()
//            heap.updatePlacement(this)
//        }
//
//        fun remove() {
//            if (isDisposed) disposedInstanceException()
//            heap.removeNode(this)
//        }
//    }
//
//    internal class Node<Element, Priority>(
//        override var element: Element,
//        priority: Priority,
//        holder: NodeHolder<Element, Priority>,
//    ): HeapNode<Element, Priority> {
//        override val isDetached: Boolean get() = _holder == null
//
//        private var _holder: NodeHolder<Element, Priority>? = holder
//        var holder: NodeHolder<Element, Priority>
//            get() = _holder!!
//            set(value) { _holder = value }
//
//        override var priority: Priority = priority
//            set(value) {
//                field = value
//                if (!isDetached) _holder!!.updatePlacement()
//            }
//
//        fun detach() {
//            _holder = null
//        }
//
//        val heap: KoneSkewGCMinimumHeap<Element, Priority>?
//            get() = if (isDetached) detachedNodeException() else _holder?.heap
//
//        override fun remove() {
//            if (isDetached) detachedNodeException()
//            _holder!!.remove()
//        }
//    }
//
//    internal class NodesIterator<Element, Priority>(
//        private var nextHolder: NodeHolder<Element, Priority>?,
//        private val size: UInt,
//    ) : KoneSetIterator<HeapNode<Element, Priority>> {
//        private var nextIndex: UInt = 0u
//
//        override fun hasNext(): Boolean = nextHolder != null
//        override fun getNext(): HeapNode<Element, Priority> {
//            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
//            return nextHolder!!.node
//        }
//        override fun moveNext() {
//            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
//            nextIndex++
//            val nextHolder = nextHolder!!
//            if (nextHolder.secondChild != null) {
//                var nextNextHolder = nextHolder.secondChild!!
//                while (nextNextHolder.firstChild != null) nextNextHolder = nextNextHolder.firstChild!!
//                this.nextHolder = nextNextHolder
//            } else {
//                var nextNextHolder: NodeHolder<Element, Priority>? = nextHolder
//                while (nextNextHolder != null) {
//                    val parent = nextNextHolder.parent
//                    if (parent == null) {
//                        nextNextHolder = parent
//                        continue
//                    }
//                    if (nextNextHolder === parent.firstChild) {
//                        nextNextHolder = parent
//                        break
//                    }
//                    nextNextHolder = parent
//                }
//                this.nextHolder = nextNextHolder
//            }
//        }
//    }
//
//    @OptIn(DelicateCollectionsInheritanceAPI::class)
//    internal class Nodes<Element, Priority>(
//        private val heap: KoneSkewGCMinimumHeap<Element, Priority>,
//    ) : KoneReifiedSet<HeapNode<Element, Priority>> {
//        override val size: UInt get() = heap.size
//        override fun contains(element: HeapNode<Element, Priority>): Boolean =
//            element is Node<*, *> && element.heap === heap
//        override fun iterator(): KoneSetIterator<HeapNode<Element, Priority>> {
//            val root = heap.rootHolder
//            if (root == null) return NodesIterator(null, heap.size)
//            var firstNode: NodeHolder<Element, Priority> = root
//            while (firstNode.firstChild != null) firstNode = firstNode.firstChild!!
//            return NodesIterator(firstNode, heap.size)
//        }
//    }
//
//    internal class ElementsIterator<Element>(
//        private var nextHolder: NodeHolder<Element, *>?,
//        private val size: UInt,
//    ) : KoneSetIterator<Element> {
//        private var nextIndex: UInt = 0u
//
//        override fun hasNext(): Boolean = nextHolder != null
//        override fun getNext(): Element {
//            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
//            return nextHolder!!.node.element
//        }
//        override fun moveNext() {
//            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
//            nextIndex++
//            val nextHolder = nextHolder!!
//            if (nextHolder.secondChild != null) {
//                var nextNextHolder = nextHolder.secondChild!!
//                while (nextNextHolder.firstChild != null) nextNextHolder = nextNextHolder.firstChild!!
//                this.nextHolder = nextNextHolder
//            } else {
//                var nextNextHolder: NodeHolder<Element, *>? = nextHolder
//                while (nextNextHolder != null) {
//                    val parent = nextNextHolder.parent
//                    if (parent == null) {
//                        nextNextHolder = parent
//                        continue
//                    }
//                    if (nextNextHolder === parent.firstChild) {
//                        nextNextHolder = parent
//                        break
//                    }
//                    nextNextHolder = parent
//                }
//                this.nextHolder = nextNextHolder
//            }
//        }
//    }
//
//    @OptIn(DelicateCollectionsInheritanceAPI::class)
//    internal class Elements<Element>(
//        private val heap: KoneSkewGCMinimumHeap<Element, *>,
//    ) : KoneIterable<Element> {
//        override val size: UInt get() = heap.size
//        override fun iterator(): KoneIterator<Element> {
//            val root = heap.rootHolder
//            if (root == null) return ElementsIterator(null, heap.size)
//            var firstNode: NodeHolder<Element, *> = root
//            while (firstNode.firstChild != null) firstNode = firstNode.firstChild!!
//            return ElementsIterator(firstNode, heap.size)
//        }
//    }
//
//    internal class PrioritiesIterator<Priority>(
//        private var nextHolder: NodeHolder<*, Priority>?,
//        private val size: UInt,
//    ) : KoneSetIterator<Priority> {
//        private var nextIndex: UInt = 0u
//
//        override fun hasNext(): Boolean = nextHolder != null
//        override fun getNext(): Priority {
//            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
//            return nextHolder!!.node.priority
//        }
//        override fun moveNext() {
//            if (!hasNext()) indexOutOfBoundsException(nextIndex, size)
//            nextIndex++
//            val nextHolder = nextHolder!!
//            if (nextHolder.secondChild != null) {
//                var nextNextHolder = nextHolder.secondChild!!
//                while (nextNextHolder.firstChild != null) nextNextHolder = nextNextHolder.firstChild!!
//                this.nextHolder = nextNextHolder
//            } else {
//                var nextNextHolder: NodeHolder<*, Priority>? = nextHolder
//                while (nextNextHolder != null) {
//                    val parent = nextNextHolder.parent
//                    if (parent == null) {
//                        nextNextHolder = parent
//                        continue
//                    }
//                    if (nextNextHolder === parent.firstChild) {
//                        nextNextHolder = parent
//                        break
//                    }
//                    nextNextHolder = parent
//                }
//                this.nextHolder = nextNextHolder
//            }
//        }
//    }
//
//    @OptIn(DelicateCollectionsInheritanceAPI::class)
//    internal class Priorities<Priority>(
//        private val heap: KoneSkewGCMinimumHeap<*, Priority>,
//    ) : KoneIterable<Priority> {
//        override val size: UInt get() = heap.size
//        override fun iterator(): KoneIterator<Priority> {
//            val root = heap.rootHolder
//            if (root == null) return PrioritiesIterator(null, heap.size)
//            var firstNode: NodeHolder<*, Priority> = root
//            while (firstNode.firstChild != null) firstNode = firstNode.firstChild!!
//            return PrioritiesIterator(firstNode, heap.size)
//        }
//    }
//}