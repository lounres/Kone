/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.accessExtremumOfEmptyHeapException
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.implementations.fibonacciNumberIndexLessOrEqualTo
import dev.lounres.kone.collections.iterable.KoneRemovableIterable
import dev.lounres.kone.collections.set.KoneRemovableReifiedSet
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.compareWith
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.scope


public class KoneFibonacciGCMinimumHeap<Element, Priority> @PublishedApi internal constructor(
    internal val priorityOrder: Order<Priority>,
    numberOfChildren: UInt,
    size: UInt,
    firstChild: Node<Element, Priority>?,
    lastChild: Node<Element, Priority>?,
    minimumNode: Node<Element, Priority>?,
) : MinimumHeap<Element, Priority> {
    internal var numberOfChildren: UInt = numberOfChildren
        private set
    override var size: UInt = size
        private set
    
    internal var firstChild: Node<Element, Priority>? = firstChild
        private set
    internal var lastChild: Node<Element, Priority>? = lastChild
        private set
    internal var minimumNode: Node<Element, Priority>? = minimumNode
        private set

    override val nodesView: KoneRemovableReifiedSet<HeapNode<Element, Priority>>
        get() = TODO("Not yet implemented")
    override val elementsView: KoneRemovableIterable<Element>
        get() = TODO("Not yet implemented")
    override val prioritiesView: KoneRemovableIterable<Priority>
        get() = TODO("Not yet implemented")

    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
        @Suppress("UNCHECKED_CAST")
        val newNode = Node(
            element = element,
            priority = priority,
            parent = null,
            previousSibling = null,
            nextSibling = null,
            heap = null,
            firstChild = null,
            lastChild = null,
            numberOfChildren = 0u,
            isMarked = false,
        )
        newNode.previousSibling = lastChild
        lastChild?.nextSibling = newNode
        lastChild = newNode
        if (firstChild === null) firstChild = newNode
        numberOfChildren++
        size++

        val minNode = minimumNode
        when {
            minNode === null -> {
                minimumNode = newNode
                newNode.heap = this
            }
            priorityOrder { newNode.priority lt minNode.priority } -> {
                minimumNode = newNode
                minNode.heap = null
                newNode.heap = this
            }
        }

        return newNode
    }

    override fun takeMinimum(): HeapNode<Element, Priority> = minimumNode ?: accessExtremumOfEmptyHeapException()

    override fun popMinimum(): HeapNode<Element, Priority> {
        val minNode = minimumNode ?: accessExtremumOfEmptyHeapException()
        minNode.previousSibling?.nextSibling = minNode.nextSibling
        minNode.nextSibling?.previousSibling = minNode.previousSibling
        if (firstChild === minNode) firstChild = minNode.nextSibling
        if (lastChild === minNode) lastChild = minNode.previousSibling
        numberOfChildren--
        size--
        scope {
            var child = minNode.firstChild
            while (child != null) {
                child.parent = null
                child.isMarked = false
                child = child.nextSibling
            }
        }
        minNode.firstChild?.previousSibling = this.lastChild
        this.lastChild?.nextSibling = minNode.firstChild
        this.firstChild = this.firstChild ?: minNode.firstChild
        this.lastChild = minNode.lastChild ?: this.lastChild
        numberOfChildren += minNode.numberOfChildren
        minNode.detach()

        if (size != 0u) {
            scope {
                val degrees = KoneMutableArray.generate<Node<Element, Priority>?>(fibonacciNumberIndexLessOrEqualTo(size) - 1u) { null }
                var currentNode: Node<Element, Priority>? = firstChild
                while (currentNode != null) {
                    val nextNode = currentNode.nextSibling
                    var currentRoot: Node<Element, Priority> = currentNode
                    while (true) {
                        val nodeOfTheSameDegree = degrees[currentRoot.numberOfChildren]
                        if (nodeOfTheSameDegree === null) {
                            degrees[currentRoot.numberOfChildren] = currentRoot
                            break
                        }
                        degrees[currentRoot.numberOfChildren] = null
                        val minNode: Node<Element, Priority>
                        val maxNode: Node<Element, Priority>
                        if (priorityOrder { currentRoot.priority lt nodeOfTheSameDegree.priority }) {
                            minNode = currentRoot
                            maxNode = nodeOfTheSameDegree
                        } else {
                            minNode = nodeOfTheSameDegree
                            maxNode = currentRoot
                        }
                        maxNode.nextSibling?.previousSibling = maxNode.previousSibling
                        maxNode.previousSibling?.nextSibling = maxNode.nextSibling
                        if (firstChild === maxNode) firstChild = maxNode.nextSibling
                        if (lastChild === maxNode) lastChild = maxNode.previousSibling
                        maxNode.parent = minNode
                        maxNode.nextSibling = null
                        maxNode.previousSibling = minNode.lastChild
                        minNode.lastChild?.nextSibling = maxNode
                        minNode.lastChild = maxNode
                        if (minNode.firstChild === null) minNode.firstChild = maxNode
                        minNode.numberOfChildren++
                        numberOfChildren--
                        currentRoot = minNode
                    }
                    currentNode = nextNode
                }
            }

            scope {
                var currentNode: Node<Element, Priority>? = firstChild
                var newMinNode = currentNode!!
                currentNode = currentNode.nextSibling
                while (currentNode != null) {
                    if (priorityOrder { currentNode.priority lt newMinNode.priority })
                        newMinNode = currentNode
                    currentNode = currentNode.nextSibling
                }

                minimumNode = newMinNode
                newMinNode.heap = this
            }
        } else {
            minimumNode = null
        }

        return minNode
    }

    @PublishedApi
    internal class Node<Element, Priority>(
        override var element: Element,
        priority: Priority,
        var parent: Node<Element, Priority>?,
        var previousSibling: Node<Element, Priority>?,
        var nextSibling: Node<Element, Priority>?,
        var heap: KoneFibonacciGCMinimumHeap<Element, Priority>?,
        var firstChild: Node<Element, Priority>?,
        var lastChild: Node<Element, Priority>?,
        var numberOfChildren: UInt,
        var isMarked: Boolean,
    ): HeapNode<Element, Priority> {
        override var isDetached: Boolean = false
            private set

        override var priority: Priority = priority
            set(value) {
                val oldValue = field
                field = value
                if (!isDetached) {
                    val heap = actualHeap()
                    when (heap.priorityOrder { value compareWith oldValue }) {
                        Equal -> {}
                        LeftIsLessThanRight -> when {
                            parent === null -> {
                                val min = heap.minimumNode!!
                                if (heap.priorityOrder { value lt min.priority }) {
                                    heap.minimumNode = this
                                    min.heap = null
                                    this.heap = heap
                                }
                            }
                            heap.priorityOrder { value lt parent!!.priority } -> {
                                nextSibling?.previousSibling = previousSibling
                                previousSibling?.nextSibling = nextSibling
                                parent!!.numberOfChildren--
                                parent = null
                                heap.lastChild?.nextSibling = this
                                this.previousSibling = heap.lastChild
                                this.nextSibling = null
                                heap.lastChild = this
                                heap.numberOfChildren++
                                isMarked = false
                                val minNode = heap.minimumNode!!
                                if (heap.priorityOrder { value lt minNode.priority }) {
                                    heap.minimumNode = this
                                    minNode.heap = null
                                    this.heap = heap
                                }
                                
                                var currentNode = parent!!
                                while (currentNode.isMarked) {
                                    val parent = currentNode.parent!!
                                    currentNode.nextSibling?.previousSibling = currentNode.previousSibling
                                    currentNode.previousSibling?.nextSibling = currentNode.nextSibling
                                    if (parent.firstChild === currentNode) parent.firstChild = currentNode.nextSibling
                                    if (parent.lastChild === currentNode) parent.lastChild = currentNode.previousSibling
                                    currentNode.parent!!.numberOfChildren--
                                    currentNode.parent = null
                                    currentNode.nextSibling = null
                                    currentNode.previousSibling = heap.lastChild
                                    heap.lastChild?.nextSibling = currentNode
                                    heap.numberOfChildren++
                                    currentNode.isMarked = false
                                    val minNode = heap.minimumNode!!
                                    if (heap.priorityOrder { currentNode.priority lt minNode.priority }) {
                                        heap.minimumNode = currentNode
                                        minNode.heap = null
                                        currentNode.heap = heap
                                    }
                                    currentNode = parent
                                }
                                if (currentNode.parent != null) currentNode.isMarked = true
                            }
                        }
                        LeftIsGreaterThanRight -> when {
                            scope {
                                var currentChildNode = firstChild
                                while (currentChildNode != null) {
                                    if (heap.priorityOrder { value gt currentChildNode.priority }) return@scope true
                                    currentChildNode = currentChildNode.nextSibling
                                }
                                false
                            } -> {
                                heap.minimumNode!!.heap = null
                                heap.minimumNode = null
                                val parentForCascadingCut = parent
                                if (parent != null) {
                                    this.nextSibling?.previousSibling = this.previousSibling
                                    this.previousSibling?.nextSibling = this.nextSibling
                                    parent!!.numberOfChildren--
                                    parent = null
                                    heap.lastChild?.nextSibling = this
                                    this.previousSibling = heap.lastChild
                                    heap.lastChild = this
                                    heap.numberOfChildren++
                                }
                                this.heap = null
                                isMarked = false
                                heap.lastChild?.nextSibling = this.firstChild
                                this.firstChild?.previousSibling = heap.lastChild
                                heap.lastChild = this.lastChild ?: heap.lastChild
                                heap.firstChild = heap.firstChild ?: this.firstChild
                                heap.numberOfChildren += numberOfChildren
                                numberOfChildren = 0u
                                
                                if (parentForCascadingCut != null) {
                                    var currentNode: Node<Element, Priority> = parentForCascadingCut
                                    while (currentNode.isMarked) {
                                        val parent = currentNode.parent!!
                                        currentNode.nextSibling?.previousSibling = currentNode.previousSibling
                                        currentNode.previousSibling?.nextSibling = currentNode.nextSibling
                                        currentNode.parent!!.numberOfChildren--
                                        currentNode.parent = null
                                        currentNode.previousSibling = heap.lastChild
                                        heap.lastChild?.previousSibling = currentNode
                                        heap.lastChild = currentNode
                                        heap.numberOfChildren++
                                        currentNode.isMarked = false
                                        val minNode = heap.minimumNode!!
                                        if (heap.priorityOrder { currentNode.priority lt minNode.priority }) {
                                            heap.minimumNode = currentNode
                                            minNode.heap = null
                                            currentNode.heap = heap
                                        }
                                        currentNode = parent
                                    }
                                    if (currentNode.parent != null) currentNode.isMarked = true
                                }
                                
                                scope {
                                    val degrees = KoneMutableArray.generate<Node<Element, Priority>?>(fibonacciNumberIndexLessOrEqualTo(heap.size)) { null }
                                    var currentNode: Node<Element, Priority>? = heap.firstChild
                                    while (currentNode != null) {
                                        val nextNode = currentNode.nextSibling
                                        while (true) {
                                            val nodeOfTheSameDegree = degrees[currentNode.numberOfChildren]
                                            if (nodeOfTheSameDegree === null) {
                                                degrees[currentNode.numberOfChildren] = currentNode
                                                break
                                            }
                                            degrees[currentNode.numberOfChildren] = null
                                            val minNode: Node<Element, Priority>
                                            val maxNode: Node<Element, Priority>
                                            if (heap.priorityOrder { currentNode.priority lt nodeOfTheSameDegree.priority }) {
                                                minNode = currentNode
                                                maxNode = nodeOfTheSameDegree
                                            } else {
                                                minNode = nodeOfTheSameDegree
                                                maxNode = currentNode
                                            }
                                            maxNode.nextSibling?.previousSibling = maxNode.previousSibling
                                            maxNode.previousSibling?.nextSibling = maxNode.nextSibling
                                            maxNode.parent = minNode
                                            maxNode.nextSibling = null
                                            maxNode.previousSibling = minNode.lastChild
                                            minNode.lastChild?.nextSibling = maxNode
                                            minNode.lastChild = maxNode
                                            if (minNode.firstChild === null) minNode.firstChild = maxNode
                                            minNode.numberOfChildren++
                                            nodeOfTheSameDegree.remove()
                                            heap.numberOfChildren--
                                        }
                                        currentNode = nextNode
                                    }
                                }
                                
                                scope {
                                    var currentNode: Node<Element, Priority>? = heap.firstChild
                                    var minNode = currentNode!!
                                    currentNode = currentNode.nextSibling
                                    while (currentNode != null) {
                                        if (heap.priorityOrder { currentNode.priority lt minNode.priority })
                                            minNode = currentNode
                                        currentNode = currentNode.nextSibling
                                    }
                                    
                                    heap.minimumNode = minNode
                                }
                            }
                            heap.minimumNode === this -> {
                                var currentNode: Node<Element, Priority>? = heap.firstChild
                                var minNode = currentNode!!
                                currentNode = currentNode.nextSibling
                                while (currentNode != null) {
                                    if (heap.priorityOrder { currentNode.priority lt minNode.priority }) minNode = currentNode
                                    currentNode = currentNode.nextSibling
                                }
                                
                                if (minNode !== this) {
                                    heap.minimumNode = minNode
                                    minNode.heap = heap
                                    this.heap = null
                                }
                            }
                        }
                    }
                }
            }

        private fun actualHeap(): KoneFibonacciGCMinimumHeap<Element, Priority> {
            var node = this
            while (node.parent != null) node = node.parent!!

            node.heap?.let { return it }

            scope {
                var currentNode = node.nextSibling
                while (currentNode != null) {
                    currentNode.heap?.let { return it }
                    currentNode = currentNode.nextSibling
                }
            }

            scope {
                var currentNode = node.previousSibling
                while (currentNode != null) {
                    currentNode.heap?.let { return it }
                    currentNode = currentNode.previousSibling
                }
            }

            error("Did not found heap reference in root nodes")
        }

        fun detach() {
            if (isDetached) return
            parent = null
            previousSibling = null
            nextSibling = null
            heap = null
            firstChild = null
            lastChild = null
            isDetached = true
        }

        override fun remove() {
            if (isDetached) return

            TODO()
        }
    }
}