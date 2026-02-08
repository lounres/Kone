/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.heap.HeapEntry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt


public fun <Element, Priority> KoneFibonacciGCMinimumHeap(priorityOrder: Order<Priority>): KoneFibonacciGCMinimumHeap<Element, Priority> =
    KoneFibonacciGCMinimumHeap(
        priorityOrder = priorityOrder,
        numberOfChildren = 0u,
        size = 0u,
        firstChild = null,
        lastChild = null,
        minimumNode = null,
    )

public inline fun <Element, Priority> KoneFibonacciGCMinimumHeap(
    priorityOrder: Order<Priority>,
    size: UInt,
    elementInitializer: (index: UInt) -> Element,
    priorityInitializer: (index: UInt) -> Priority,
): KoneFibonacciGCMinimumHeap<Element, Priority> {
    if (size == 0u) return KoneFibonacciGCMinimumHeap(
        priorityOrder = priorityOrder,
        numberOfChildren = 0u,
        size = 0u,
        firstChild = null,
        lastChild = null,
        minimumNode = null,
    )
    
    var currentNode = KoneFibonacciGCMinimumHeap.Node(
        element = elementInitializer(0u),
        priority = priorityInitializer(0u),
        parent = null,
        previousSibling = null,
        nextSibling = null,
        heap = null,
        firstChild = null,
        lastChild = null,
        numberOfChildren = 0u,
        isMarked = false,
    )
    var minNode = currentNode
    val firstNode = currentNode
    for (index in 1u ..< size) {
        val nextNode = KoneFibonacciGCMinimumHeap.Node(
            element = elementInitializer(index),
            priority = priorityInitializer(index),
            parent = null,
            previousSibling = currentNode,
            nextSibling = null,
            heap = null,
            firstChild = null,
            lastChild = null,
            numberOfChildren = 0u,
            isMarked = false,
        )
        currentNode.nextSibling = nextNode
        currentNode = nextNode
        if (priorityOrder { currentNode.priority lt minNode.priority }) minNode = currentNode
    }
    
    val result = KoneFibonacciGCMinimumHeap(
        priorityOrder = priorityOrder,
        numberOfChildren = size,
        size = size,
        firstChild = firstNode,
        lastChild = currentNode,
        minimumNode = minNode,
    )
    
    minNode.heap = result
    
    return result
}

public inline fun <Element, Priority> KoneFibonacciGCMinimumHeap(
    priorityOrder: Order<Priority>,
    size: UInt,
    heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneFibonacciGCMinimumHeap<Element, Priority> {
    if (size == 0u) return KoneFibonacciGCMinimumHeap(
        priorityOrder = priorityOrder,
        numberOfChildren = 0u,
        size = 0u,
        firstChild = null,
        lastChild = null,
        minimumNode = null,
    )
    
    val firstEntry = heapEntryInitializer(0u)
    var currentNode = KoneFibonacciGCMinimumHeap.Node(
        element = firstEntry.element,
        priority = firstEntry.priority,
        parent = null,
        previousSibling = null,
        nextSibling = null,
        heap = null,
        firstChild = null,
        lastChild = null,
        numberOfChildren = 0u,
        isMarked = false,
    )
    var minNode = currentNode
    val firstNode = currentNode
    for (index in 1u ..< size) {
        val nextEntry = heapEntryInitializer(index)
        val nextNode = KoneFibonacciGCMinimumHeap.Node(
            element = nextEntry.element,
            priority = nextEntry.priority,
            parent = null,
            previousSibling = currentNode,
            nextSibling = null,
            heap = null,
            firstChild = null,
            lastChild = null,
            numberOfChildren = 0u,
            isMarked = false,
        )
        currentNode.nextSibling = nextNode
        currentNode = nextNode
        if (priorityOrder { currentNode.priority lt minNode.priority }) minNode = currentNode
    }
    
    return KoneFibonacciGCMinimumHeap(
        priorityOrder = priorityOrder,
        numberOfChildren = size,
        size = size,
        firstChild = firstNode,
        lastChild = currentNode,
        minimumNode = minNode,
    )
}