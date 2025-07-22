/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.castOrNull
import dev.lounres.kone.collections.Disposable
import dev.lounres.kone.collections.accessExtremumOfEmptyHeapException
import dev.lounres.kone.collections.array.KoneMutableArray
import dev.lounres.kone.collections.detachedNodeException
import dev.lounres.kone.collections.heap.HeapNode
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.implementations.fibonacciNumberIndexGreaterOrEqualTo
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneMutableListNode
import dev.lounres.kone.collections.list.KoneMutableNoddedList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt
import dev.lounres.kone.relations.lt
import dev.lounres.kone.scope


public class KoneFibonacciGCWithProvidedListMinimumHeap<Element, Priority> @PublishedApi internal constructor(
    private val priorityOrder: Order<Priority>,
    private var roots: KoneMutableNoddedList<Node<Element, Priority>>,
    internal var numberOfChildren: UInt,
    size: UInt,
    private var minimumNodeListNode: KoneMutableListNode<Node<Element, Priority>>?,
    private val listProducer: () -> KoneMutableNoddedList<Any?>,
    private val listMelder: (first: KoneMutableNoddedList<Any?>, second: KoneMutableNoddedList<Any?>) -> KoneMutableNoddedList<Any?>,
) : MinimumHeap<Element, Priority> {
    override var size: UInt = size
        private set
    
    override val nodesView: KoneReifiedSet<HeapNode<Element, Priority>>
        get() = TODO("Not yet implemented")
    override val elementsView: KoneIterable<Element>
        get() = TODO("Not yet implemented")
    override val prioritiesView: KoneIterable<Priority>
        get() = TODO("Not yet implemented")
    
    override fun add(element: Element, priority: Priority): HeapNode<Element, Priority> {
        @Suppress("UNCHECKED_CAST")
        val newNode = Node(
            element = element,
            priority = priority,
            parent = null,
            heap = null,
            children = listProducer() as KoneMutableNoddedList<Node<Element, Priority>>,
            numberOfChildren = 0u,
            isMarked = false,
        )
        newNode.parentsChildrenListNode = roots.addNode(newNode)
        numberOfChildren++
        size++
        
        val minNode = minimumNodeListNode?.element
        when {
            minNode == null -> {
                minimumNodeListNode = newNode.parentsChildrenListNode
            }
            priorityOrder { newNode.priority lt minNode.priority } -> {
                minimumNodeListNode = newNode.parentsChildrenListNode
                minNode.heap = null
                newNode.heap = this
            }
        }
        
        return newNode
    }
    
    override fun takeMinimum(): HeapNode<Element, Priority> =
        minimumNodeListNode?.element ?: accessExtremumOfEmptyHeapException()
    
    override fun popMinimum(): HeapNode<Element, Priority> {
        val minListNode = minimumNodeListNode ?: accessExtremumOfEmptyHeapException()
        val minNode = minListNode.element
        minListNode.remove()
        numberOfChildren--
        size--
        for (child in minNode.children) {
            child.parent = null
            child.isMarked = false
        }
        @Suppress("UNCHECKED_CAST")
        roots = listMelder(roots as KoneMutableNoddedList<Any?>, minNode.children as KoneMutableNoddedList<Any?>) as KoneMutableNoddedList<Node<Element, Priority>>
        numberOfChildren += minNode.numberOfChildren
        minNode.detach()
        
        
        if (size != 0u) {
            val degrees = KoneMutableArray<KoneMutableListNode<Node<Element, Priority>>?>(fibonacciNumberIndexGreaterOrEqualTo(size)) { null }
            var currentListNode: KoneMutableListNode<Node<Element, Priority>>? = roots.getNode(0u)
            while (currentListNode != null) {
                while (true) {
                    val listNodeOfTheSameDegree = degrees[currentListNode.element.numberOfChildren]
                    if (listNodeOfTheSameDegree == null) {
                        degrees[currentListNode.element.numberOfChildren] = currentListNode
                        break
                    }
                    degrees[currentListNode.element.numberOfChildren] = null
                    val minListNode: Node<Element, Priority>
                    val maxListNode: Node<Element, Priority>
                    if (priorityOrder { currentListNode.element.priority lt listNodeOfTheSameDegree.element.priority }) {
                        minListNode = currentListNode.element
                        maxListNode = listNodeOfTheSameDegree.element
                    } else {
                        minListNode = listNodeOfTheSameDegree.element
                        maxListNode = currentListNode.element
                    }
                    maxListNode.parentsChildrenListNode = minListNode.children.addNode(maxListNode)
                    minListNode.numberOfChildren++
                    minListNode.parentsChildrenListNode = currentListNode
                    currentListNode.element = minListNode
                    listNodeOfTheSameDegree.remove()
                    numberOfChildren--
                }
                currentListNode = currentListNode.nextNode
            }
            
            var currentFinalListNode: KoneMutableListNode<Node<Element, Priority>>? = roots.getNode(0u)
            var minListNode = currentFinalListNode!!
            currentFinalListNode = currentFinalListNode.nextNode
            while (currentFinalListNode != null) {
                if (priorityOrder { currentFinalListNode.element.priority lt minListNode.element.priority })
                    minListNode = currentFinalListNode
                currentFinalListNode = currentFinalListNode.nextNode
            }
            
            minimumNodeListNode = minListNode
        }
        
        return minNode
    }
    
    internal class Node<Element, Priority>(
        override var element: Element,
        priority: Priority,
        var parent: Node<Element, Priority>?,
        var heap: KoneFibonacciGCWithProvidedListMinimumHeap<Element, Priority>?,
        children: KoneMutableNoddedList<Node<Element, Priority>>,
        var numberOfChildren: UInt,
        var isMarked: Boolean,
    ): HeapNode<Element, Priority> {
        override var isDetached: Boolean = false
            private set
        
        override var priority: Priority = priority
            set(value) {
                field = value
                if (!isDetached) {
                    val heap = actualHeap()
                    when {
                        parent != null && heap.priorityOrder { value lt parent!!.priority } -> {
                            parentsChildrenListNode.remove()
                            parent!!.numberOfChildren--
                            parent = null
                            parentsChildrenListNode = heap.roots.addNode(this)
                            heap.numberOfChildren++
                            isMarked = false
                            val minNode = heap.minimumNodeListNode!!.element
                            if (heap.priorityOrder { value lt minNode.priority }) {
                                heap.minimumNodeListNode = parentsChildrenListNode
                                minNode.heap = null
                                this.heap = heap
                            }
                            
                            var currentNode = parent!!
                            while (currentNode.isMarked) {
                                val parent = currentNode.parent!!
                                currentNode.parentsChildrenListNode.remove()
                                currentNode.parent!!.numberOfChildren--
                                currentNode.parent = null
                                currentNode.parentsChildrenListNode = heap.roots.addNode(currentNode)
                                heap.numberOfChildren++
                                currentNode.isMarked = false
                                val minNode = heap.minimumNodeListNode!!.element
                                if (heap.priorityOrder { currentNode.priority lt minNode.priority }) {
                                    heap.minimumNodeListNode = currentNode.parentsChildrenListNode
                                    minNode.heap = null
                                    currentNode.heap = heap
                                }
                                currentNode = parent
                            }
                            if (currentNode.parent != null) currentNode.isMarked = true
                        }
                        children.any { child -> heap.priorityOrder { value gt child.priority } } -> {
                            heap.minimumNodeListNode!!.element.heap = null
                            heap.minimumNodeListNode = null
                            val parentForCascadingCut = parent
                            if (parent != null) {
                                parentsChildrenListNode.remove()
                                parent!!.numberOfChildren--
                                parent = null
                                parentsChildrenListNode = heap.roots.addNode(this)
                                heap.numberOfChildren++
                            }
                            this.heap = null
                            isMarked = false
                            @Suppress("UNCHECKED_CAST")
                            heap.roots = heap.listMelder(heap.roots as KoneMutableNoddedList<Any?>, children as KoneMutableNoddedList<Any?>) as KoneMutableNoddedList<Node<Element, Priority>>
                            heap.numberOfChildren += numberOfChildren
                            @Suppress("UNCHECKED_CAST")
                            children = heap.listProducer() as KoneMutableNoddedList<Node<Element, Priority>>
                            numberOfChildren = 0u
                            
                            if (parentForCascadingCut != null) {
                                var currentNode: Node<Element, Priority> = parentForCascadingCut
                                while (currentNode.isMarked) {
                                    val parent = currentNode.parent!!
                                    currentNode.parentsChildrenListNode.remove()
                                    currentNode.parent = null
                                    currentNode.parentsChildrenListNode = heap.roots.addNode(currentNode)
                                    heap.numberOfChildren++
                                    currentNode.isMarked = false
                                    val minNode = heap.minimumNodeListNode!!.element
                                    if (heap.priorityOrder { currentNode.priority lt minNode.priority }) {
                                        heap.minimumNodeListNode = currentNode.parentsChildrenListNode
                                        minNode.heap = null
                                        currentNode.heap = heap
                                    }
                                    currentNode = parent
                                }
                                if (currentNode.parent != null) currentNode.isMarked = true
                            }
                            
                            val degrees = KoneMutableArray<KoneMutableListNode<Node<Element, Priority>>?>(fibonacciNumberIndexGreaterOrEqualTo(heap.size)) { null }
                            var currentListNode: KoneMutableListNode<Node<Element, Priority>>? = heap.roots.getNode(0u)
                            while (currentListNode != null) {
                                while (true) {
                                    val listNodeOfTheSameDegree = degrees[currentListNode.element.numberOfChildren]
                                    if (listNodeOfTheSameDegree == null) {
                                        degrees[currentListNode.element.numberOfChildren] = currentListNode
                                        break
                                    }
                                    degrees[currentListNode.element.numberOfChildren] = null
                                    val minListNode: Node<Element, Priority>
                                    val maxListNode: Node<Element, Priority>
                                    if (heap.priorityOrder { currentListNode.element.priority lt listNodeOfTheSameDegree.element.priority }) {
                                        minListNode = currentListNode.element
                                        maxListNode = listNodeOfTheSameDegree.element
                                    } else {
                                        minListNode = listNodeOfTheSameDegree.element
                                        maxListNode = currentListNode.element
                                    }
                                    maxListNode.parentsChildrenListNode = minListNode.children.addNode(maxListNode)
                                    minListNode.numberOfChildren++
                                    minListNode.parentsChildrenListNode = currentListNode
                                    currentListNode.element = minListNode
                                    listNodeOfTheSameDegree.remove()
                                    numberOfChildren--
                                }
                                currentListNode = currentListNode.nextNode
                            }
                            
                            var currentFinalListNode: KoneMutableListNode<Node<Element, Priority>>? = heap.roots.getNode(0u)
                            var minListNode = currentFinalListNode!!
                            currentFinalListNode = currentFinalListNode.nextNode
                            while (currentFinalListNode != null) {
                                if (heap.priorityOrder { currentFinalListNode.element.priority lt minListNode.element.priority })
                                    minListNode = currentFinalListNode
                                currentFinalListNode = currentFinalListNode.nextNode
                            }
                            
                            heap.minimumNodeListNode = minListNode
                        }
                    }
                }
            }
        
        private var _parentsChildrenListNode: KoneMutableListNode<Node<Element, Priority>>? = null
        var parentsChildrenListNode: KoneMutableListNode<Node<Element, Priority>>
            get() = _parentsChildrenListNode!!
            set(value) { _parentsChildrenListNode = value }
        
        private var _children: KoneMutableNoddedList<Node<Element, Priority>>? = children
        var children: KoneMutableNoddedList<Node<Element, Priority>>
            get() = _children!!
            set(value) { _children = value }
        
        private fun actualHeap(): KoneFibonacciGCWithProvidedListMinimumHeap<Element, Priority> {
            var node = this
            while (node.parent != null) node = node.parent!!
            val rootListNode = node.parentsChildrenListNode
            
            rootListNode.element.heap?.let { return it }
            
            scope {
                var currentNode = rootListNode.nextNode
                while (currentNode != null) {
                    currentNode.element.heap?.let { return it }
                    currentNode = currentNode.nextNode
                }
            }
            
            scope {
                var currentNode = rootListNode.previousNode
                while (currentNode != null) {
                    currentNode.element.heap?.let { return it }
                    currentNode = currentNode.previousNode
                }
            }
            
            error("Did not found heap reference in root nodes")
        }
        
        fun detach() {
            if (isDetached) return
            parent = null
            _parentsChildrenListNode = null
            _children?.castOrNull<Disposable>()?.dispose()
            _children = null
            isDetached = true
        }
        
        override fun remove() {
            if (isDetached) detachedNodeException()
            
            TODO()
        }
    }
}