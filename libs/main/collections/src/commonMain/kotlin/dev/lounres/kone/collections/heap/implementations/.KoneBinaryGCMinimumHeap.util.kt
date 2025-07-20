/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.heap.HeapEntry
import dev.lounres.kone.relations.Order


public fun <Element, Priority> KoneBinaryGCMinimumHeap(priorityOrder: Order<Priority>): KoneBinaryGCMinimumHeap<Element, Priority> =
    KoneBinaryGCMinimumHeap(
        priorityOrder = priorityOrder,
        rootHolder = null,
        lastHolder = null,
    )

public inline fun <Element, Priority> KoneBinaryGCMinimumHeap(
    priorityOrder: Order<Priority>,
    size: UInt,
    elementInitializer: (index: UInt) -> Element,
    priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryGCMinimumHeap<Element, Priority> {
    if (size == 0u) return KoneBinaryGCMinimumHeap(
        priorityOrder = priorityOrder,
        rootHolder = null,
        lastHolder = null,
    )
    
    val result = KoneBinaryGCMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        rootHolder = null,
        lastHolder = null,
        size = size,
    )
    
    val rootHolder = KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>(
        heap = result,
        index = 0u,
        parent = null,
        previous = null,
        priority = priorityInitializer(0u),
        element = elementInitializer(0u),
    )
    var currentIndex = 1u
    var currentParentHolder = rootHolder
    var currentLastHolder = rootHolder
    while (currentIndex < size) {
        val holder = KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>(
            heap = result,
            index = currentIndex,
            parent = currentParentHolder,
            previous = currentLastHolder,
            priority = priorityInitializer(currentIndex),
            element = elementInitializer(currentIndex),
        )
        currentLastHolder.next = holder
        currentLastHolder = holder
        if (currentIndex % 2u == 1u) {
            currentParentHolder.firstChild = holder
        } else {
            currentParentHolder.secondChild = holder
            currentParentHolder = currentParentHolder.next!!
        }
        currentIndex++
    }
    result.rootHolder = rootHolder
    result.lastHolder = currentLastHolder
    
    var currentSubheapRoot: KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>? = currentParentHolder
    while (currentSubheapRoot != null) {
        result.siftTheNodeUpToTheLeaf(currentSubheapRoot)
        currentSubheapRoot = currentSubheapRoot.previous
    }
    
    return result
}

public inline fun <Element, Priority> KoneBinaryGCMinimumHeap(
    priorityOrder: Order<Priority>,
    size: UInt,
    heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryGCMinimumHeap<Element, Priority> {
    if (size == 0u) return KoneBinaryGCMinimumHeap(
        priorityOrder = priorityOrder,
        rootHolder = null,
        lastHolder = null,
    )
    
    val result = KoneBinaryGCMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        rootHolder = null,
        lastHolder = null,
        size = size,
    )
    
    val rootEntry = heapEntryInitializer(0u)
    val rootElement = rootEntry.element
    val rootPriority = rootEntry.priority
    val rootHolder = KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>(
        heap = result,
        index = 0u,
        parent = null,
        previous = null,
        element = rootElement,
        priority = rootPriority,
    )
    var currentIndex = 1u
    var currentParentHolder = rootHolder
    var currentLastHolder = rootHolder
    while (currentIndex < size) {
        val entry = heapEntryInitializer(currentIndex)
        val element = entry.element
        val priority = entry.priority
        val holder = KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>(
            heap = result,
            index = currentIndex,
            parent = currentParentHolder,
            previous = currentLastHolder,
            priority = priority,
            element = element,
        )
        currentLastHolder.next = holder
        currentLastHolder = holder
        if (currentIndex % 2u == 1u) {
            currentParentHolder.firstChild = holder
        } else {
            currentParentHolder.secondChild = holder
            currentParentHolder = currentParentHolder.next!!
        }
        currentIndex++
    }
    result.rootHolder = rootHolder
    result.lastHolder = currentLastHolder
    
    var currentSubheapRoot: KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>? = currentParentHolder
    while (currentSubheapRoot != null) {
        result.siftTheNodeUpToTheLeaf(currentSubheapRoot)
        currentSubheapRoot = currentSubheapRoot.previous
    }
    
    return result
}