/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.iterable.KoneRemovableIterable
import dev.lounres.kone.collections.set.KoneRemovableReifiedSet
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt
import dev.lounres.kone.scope


//public class KoneStrictFibonacciGCMinimumHeap<Element, Priority> @PublishedApi internal constructor(
//    internal val priorityOrder: Order<Priority>,
//    size: UInt,
//    root: NodeHolder<Element, Priority>?,
//) : MinimumHeap<Element, Priority> {
//    override var size: UInt = size
//        private set
//
//    internal var root: NodeHolder<Element, Priority>? = root
//        private set
//    internal var firstInQueue: NodeHolder<Element, Priority>? = null
//    internal var lastInQueue: NodeHolder<Element, Priority>? = null
//    internal var firstRankNode: RankNode<Element, Priority>? = null
//    internal val mainActivenessFlag = ActivenessFlag(true)
//
//    override val nodesView: KoneRemovableReifiedSet<HeapNode<Element, Priority>>
//        get() = TODO("Not yet implemented")
//    override val elementsView: KoneRemovableIterable<Element>
//        get() = TODO("Not yet implemented")
//    override val prioritiesView: KoneRemovableIterable<Priority>
//        get() = TODO("Not yet implemented")
//
//    public companion object {
//        private fun <Element, Priority> NodeHolder<Element, Priority>.actualHeap(): KoneStrictFibonacciGCMinimumHeap<Element, Priority> {
//            var currentNode = this
//            while (true) {
//                val parent = currentNode.parent
//                if (parent === null) return currentNode.heap!!
//                else currentNode = parent
//            }
//        }
//
//        private val defaultPassiveFlag = ActivenessFlag(false)
//
//        private val NodeHolder<*, *>.isActive: Boolean get() = activenessFlag.isActive
//
//        private fun <Element, Priority> NodeHolder<Element, Priority>.unlink() {
//            val parent = this.parent ?: error("Root node must not be unlinked. But there is attempt to.")
//            this.parent = null
//            parent.firstChild = if (parent.firstChild === this) this.nextSibling else parent.firstChild
//            parent.lastChild = if (parent.lastChild === this) this.previousSibling else parent.lastChild
//            if (parent.parent === null) {
//
//            }
//            this.nextSibling?.previousSibling = this.previousSibling
//            this.previousSibling?.nextSibling = this.nextSibling
//            this.previousSibling = null
//            this.nextSibling = null
//            parent.numberOfChildren--
//            if (parent.isActive) {
//                parent.loss++
//            }
//        }
//    }
//
//    private fun performActiveRootReduction(x: NodeHolder<Element, Priority>, y: NodeHolder<Element, Priority>) {
//        check(x.isActive && y.isActive && x.rank == y.rank)
//
//        val larger: NodeHolder<Element, Priority>
//        val smaller: NodeHolder<Element, Priority>
//        scope {
//            if (priorityOrder { x.node.priority lt y.node.priority }) {
//                larger = y
//                smaller = x
//            } else {
//                larger = x
//                smaller = y
//            }
//        }
//
//        smaller
//    }
//
//    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
//        val newNode = Node(
//            element = element,
//            priority = priority,
//            holder = null,
//        )
//        val newHolder = NodeHolder(
//            node = newNode,
//            heap = null,
//            parent = null,
//            previousSibling = null,
//            nextSibling = null,
//            numberOfChildren = 0u,
//            firstChild = null,
//            lastChild = null,
//            firstPassiveLinkableChild = null,
//            nextInQueue = null,
//            previousInQueue = null,
//            activenessFlag = defaultPassiveFlag,
//            rank = 0u,
//            loss = 0u,
//            nextInFixList = null,
//            previousInFixList = null,
//            rankNode = null,
//        )
//        newNode.holder = newHolder
//
//        if (this.root === null) {
//            this.root = newHolder
//            return newNode
//        }
//
//        val larger: NodeHolder<Element, Priority>
//        val smaller: NodeHolder<Element, Priority>
//        scope {
//            val root = this.root!!
//            if (priorityOrder { root.node.priority lt newHolder.node.priority }) {
//                larger = newHolder
//                smaller = root
//            } else {
//                larger = root
//                smaller = newHolder
//            }
//        }
//
//        larger.heap = null
//        larger.parent = smaller
//        larger.previousSibling = smaller.lastChild
//        smaller.lastChild?.nextSibling = larger
//        smaller.firstChild = smaller.firstChild ?: larger
//        smaller.lastChild = larger
//
//        larger.previousInQueue = this.lastInQueue
//        this.lastInQueue?.nextInQueue = larger
//        this.firstInQueue = this.firstInQueue ?: larger
//        this.lastInQueue = larger
//
//    }
//
//    override fun takeMinimum(): HeapNode<Element, Priority> {
//        TODO("Not yet implemented")
//    }
//
//    override fun popMinimum(): HeapNode<Element, Priority> {
//        TODO("Not yet implemented")
//    }
//
//    internal class ActivenessFlag(var isActive: Boolean)
//
//    @PublishedApi
//    internal class Node<Element, Priority>(
//        override var element: Element,
//        priority: Priority,
//        var holder: NodeHolder<Element, Priority>?,
//    ): HeapNode<Element, Priority> {
//        override val isDetached: Boolean get() = holder === null
//
//        override var priority: Priority = priority
//            set(value) {
//                val oldValue = field
//                field = value
//                val holder = this.holder
//                if (holder === null) return
//                TODO("Not yet implemented")
//            }
//
//        override fun remove() {
//            val holder = this.holder
//            if (holder === null) return
//            TODO("Not yet implemented")
//        }
//    }
//
//    @PublishedApi
//    internal class NodeHolder<Element, Priority>(
//        var node: Node<Element, Priority>,
//        var heap: KoneStrictFibonacciGCMinimumHeap<Element, Priority>?,
//        var parent: NodeHolder<Element, Priority>?,
//        var previousSibling: NodeHolder<Element, Priority>?,
//        var nextSibling: NodeHolder<Element, Priority>?,
//        var numberOfChildren: UInt,
//        var firstChild: NodeHolder<Element, Priority>?,
//        var lastChild: NodeHolder<Element, Priority>?,
//        var firstPassiveLinkableChild: NodeHolder<Element, Priority>?,
//        var nextInQueue: NodeHolder<Element, Priority>?,
//        var previousInQueue: NodeHolder<Element, Priority>?,
//        var activenessFlag: ActivenessFlag,
//        var rank: UInt,
//        var loss: UInt,
//        var nextInFixList: NodeHolder<Element, Priority>?,
//        var previousInFixList: NodeHolder<Element, Priority>?,
//        var rankNode: RankNode<Element, Priority>?,
//    )
//
//    internal class RankNode<Element, Priority>(
//        val rank: UInt,
//        var firstActiveRoot: NodeHolder<Element, Priority>?,
//        var firstActiveNodeWithLoss: NodeHolder<Element, Priority>?,
//        var next: RankNode<Element, Priority>?,
//        var previous: RankNode<Element, Priority>?,
//    )
//}