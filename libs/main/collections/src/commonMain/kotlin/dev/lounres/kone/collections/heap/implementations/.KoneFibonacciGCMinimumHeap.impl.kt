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
    
    public companion object {
        private fun <Element, Priority> Node<Element, Priority>.actualHeap(): KoneFibonacciGCMinimumHeap<Element, Priority> {
            var currentNode = this
            while (true) {
                val parent = currentNode.parent
                if (parent === null) return currentNode.heap!!
                else currentNode = parent
            }
        }
        
        private fun <Element, Priority> Node<Element, Priority>.unlink() {
            this.previousSibling?.nextSibling = this.nextSibling
            this.nextSibling?.previousSibling = this.previousSibling
            this.previousSibling = null
            this.nextSibling = null
        }
        
        private fun <Element, Priority> Node<Element, Priority>.unlinkFor(parent: Node<Element, Priority>) {
            if (parent.firstChild === this) parent.firstChild = this.nextSibling
            if (parent.lastChild === this) parent.lastChild = this.previousSibling
            this.parent = null
            this.unlink()
            parent.numberOfChildren--
        }
        
        private fun <Element, Priority> Node<Element, Priority>.unlinkFor(heap: KoneFibonacciGCMinimumHeap<Element, Priority>) {
            if (heap.firstChild === this) heap.firstChild = this.nextSibling
            if (heap.lastChild === this) heap.lastChild = this.previousSibling
            this.heap = null
            this.unlink()
            heap.numberOfChildren--
        }
        
        private fun <Element, Priority> Node<Element, Priority>.linkLastFor(parent: Node<Element, Priority>) {
            this.previousSibling = parent.lastChild
            parent.lastChild?.nextSibling = this
            parent.firstChild = parent.firstChild ?: this
            parent.lastChild = this
            this.parent = parent
            parent.numberOfChildren++
        }
        
        private fun <Element, Priority> Node<Element, Priority>.linkLastFor(heap: KoneFibonacciGCMinimumHeap<Element, Priority>) {
            this.previousSibling = heap.lastChild
            heap.lastChild?.nextSibling = this
            heap.firstChild = heap.firstChild ?: this
            heap.lastChild = this
            this.heap = heap
            heap.numberOfChildren++
        }
        
        private fun <Element, Priority> Node<Element, Priority>.rebaseChildrenOntoTop(heap: KoneFibonacciGCMinimumHeap<Element, Priority>) {
            scope {
                var child = this.firstChild
                while (child != null) {
                    child.isMarked = false
                    child.parent = null
                    child.heap = heap
                    child = child.nextSibling
                }
            }
            this.firstChild?.previousSibling = heap.lastChild
            heap.lastChild?.nextSibling = this.firstChild
            heap.firstChild = heap.firstChild ?: this.firstChild
            heap.lastChild = this.lastChild ?: heap.lastChild
            heap.numberOfChildren += this.numberOfChildren
            this.firstChild = null
            this.lastChild = null
            this.numberOfChildren = 0u
        }
    }
    
    private fun compress() {
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
                maxNode.unlinkFor(this)
                maxNode.linkLastFor(minNode)
                currentRoot = minNode
            }
            currentNode = nextNode
        }
    }
    
    private fun recomputeMinimalNode() {
        var currentNode: Node<Element, Priority>? = firstChild
        var newMinNode = currentNode!!
        currentNode = currentNode.nextSibling
        while (currentNode != null) {
            if (priorityOrder { currentNode.priority lt newMinNode.priority }) newMinNode = currentNode
            currentNode = currentNode.nextSibling
        }
        
        minimumNode = newMinNode
        newMinNode.heap = this
    }
    
    private fun updateMinimalNode(newNode: Node<Element, Priority>) {
        val currentMinimumNode = minimumNode
        if (currentMinimumNode === null || priorityOrder { newNode.priority lt currentMinimumNode.priority }) {
            minimumNode = newNode
        }
    }
    
    private fun performCascadingCutsFrom(node: Node<Element, Priority>) {
        var currentNode = node
        while (currentNode.isMarked) {
            val parent = currentNode.parent!!
            currentNode.unlinkFor(parent)
            currentNode.linkLastFor(this)
            currentNode.isMarked = false
            val minNode = this.minimumNode!!
            if (this.priorityOrder { currentNode.priority lt minNode.priority }) this.minimumNode = currentNode
            currentNode = parent
        }
        if (currentNode.parent != null) currentNode.isMarked = true
    }

    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
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
        
        newNode.linkLastFor(this)
        size++
        updateMinimalNode(newNode)

        return newNode
    }

    override fun takeMinimum(): HeapNode<Element, Priority> = minimumNode ?: accessExtremumOfEmptyHeapException()

    override fun popMinimum(): HeapNode<Element, Priority> {
        val minNode = minimumNode ?: accessExtremumOfEmptyHeapException()
        minNode.unlinkFor(this)
        minNode.rebaseChildrenOntoTop(this)
        size--
        minNode.detach()

        if (size != 0u) {
            compress()
            recomputeMinimalNode()
        } else {
            minimumNode = null
        }

        return minNode
    }
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = super.hashCode()
    override fun toString(): String = "${super.toString()}[size = $size]"

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
                if (isDetached) return
                val heap = actualHeap()
                when (heap.priorityOrder { value compareWith oldValue }) {
                    Equal -> {}
                    LeftIsLessThanRight -> when {
                        parent === null -> {
                            heap.updateMinimalNode(this)
                        }
                        heap.priorityOrder { value lt parent!!.priority } -> {
                            val parent = parent!!
                            this.unlinkFor(parent)
                            this.linkLastFor(heap)
                            this.isMarked = false
                            heap.updateMinimalNode(this)
                            
                            heap.performCascadingCutsFrom(parent)
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
                            heap.minimumNode = null
                            val parentForCascadingCut = parent
                            if (this.parent != null) {
                                this.unlinkFor(this.parent!!)
                                this.linkLastFor(heap)
                                this.isMarked = false
                            }
                            this.rebaseChildrenOntoTop(heap)
                            
                            if (parentForCascadingCut != null) heap.performCascadingCutsFrom(parentForCascadingCut)
                            
                            heap.compress()
                            heap.recomputeMinimalNode()
                        }
                        heap.minimumNode === this -> {
                            heap.recomputeMinimalNode()
                        }
                    }
                }
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
            
            val heap = actualHeap()
            
            heap.minimumNode = null
            val parentForCascadingCut = parent
            if (parent != null) {
                this.unlinkFor(this.parent!!)
                this.linkLastFor(heap)
                this.isMarked = false
            }
            this.rebaseChildrenOntoTop(heap)
            this.unlinkFor(heap)
            heap.size--
            isDetached = true
            
            if (parentForCascadingCut != null) heap.performCascadingCutsFrom(parentForCascadingCut)
            
            heap.compress()
            heap.recomputeMinimalNode()
        }
        
        override fun equals(other: Any?): Boolean = this === other
        override fun hashCode(): Int = element.hashCode() * 31 + priority.hashCode()
        override fun toString(): String = "${super.toString()}[element = $element, priority = $priority]"
    }
}