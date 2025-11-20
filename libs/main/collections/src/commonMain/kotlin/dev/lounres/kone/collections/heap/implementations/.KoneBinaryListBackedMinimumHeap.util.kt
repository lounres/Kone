/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.heap.HeapEntry
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableList
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableListProducer
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.relations.Order


public fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> =
    KoneBinaryListBackedMinimumHeap(
        priorityOrder = priorityOrder,
        data = KoneArrayResizableList(),
    )

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    size: UInt,
    elementInitializer: (index: UInt) -> Element,
    priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = KoneArrayResizableList.generate(size) {
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = elementInitializer(it),
            priority = priorityInitializer(it),
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    size: UInt,
    heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = KoneArrayResizableList.generate(size) {
        val entry = heapEntryInitializer(it)
        val element = entry.element
        val priority = entry.priority
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = element,
            priority = priority,
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneResizableMutableListProducer,
): KoneBinaryListBackedMinimumHeap<Element, Priority> =
    KoneBinaryListBackedMinimumHeap(
        priorityOrder = priorityOrder,
        data = listProducer.produce(),
    )

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneResizableMutableListProducer,
    size: UInt,
    crossinline elementInitializer: (index: UInt) -> Element,
    crossinline priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(size) {
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = elementInitializer(it),
            priority = priorityInitializer(it),
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneResizableMutableListProducer,
    size: UInt,
    crossinline heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(size) {
        val entry = heapEntryInitializer(it)
        val element = entry.element
        val priority = entry.priority
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = element,
            priority = priority,
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneGrowableMutableListProducer,
): KoneBinaryListBackedMinimumHeap<Element, Priority> =
    KoneBinaryListBackedMinimumHeap(
        priorityOrder = priorityOrder,
        data = listProducer.produce(),
    )

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneGrowableMutableListProducer,
    size: UInt,
    crossinline elementInitializer: (index: UInt) -> Element,
    crossinline priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(size) {
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = elementInitializer(it),
            priority = priorityInitializer(it),
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneGrowableMutableListProducer,
    size: UInt,
    crossinline heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(size) {
        val entry = heapEntryInitializer(it)
        val element = entry.element
        val priority = entry.priority
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = element,
            priority = priority,
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneGrowableMutableListProducer,
    initialCapacity: UInt,
): KoneBinaryListBackedMinimumHeap<Element, Priority> =
    KoneBinaryListBackedMinimumHeap(
        priorityOrder = priorityOrder,
        data = listProducer.produce(initialCapacity = initialCapacity),
    )

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneGrowableMutableListProducer,
    initialCapacity: UInt,
    size: UInt,
    crossinline elementInitializer: (index: UInt) -> Element,
    crossinline priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(initialCapacity = initialCapacity, number = size) {
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = elementInitializer(it),
            priority = priorityInitializer(it),
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneGrowableMutableListProducer,
    initialCapacity: UInt,
    size: UInt,
    crossinline heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(initialCapacity = initialCapacity, number = size) {
        val entry = heapEntryInitializer(it)
        val element = entry.element
        val priority = entry.priority
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = element,
            priority = priority,
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneFixedCapacityMutableListProducer,
    capacity: UInt,
): KoneBinaryListBackedMinimumHeap<Element, Priority> =
    KoneBinaryListBackedMinimumHeap(
        priorityOrder = priorityOrder,
        data = listProducer.produce(capacity = capacity),
    )

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneFixedCapacityMutableListProducer,
    size: UInt,
    crossinline elementInitializer: (index: UInt) -> Element,
    crossinline priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(number = size) {
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = elementInitializer(it),
            priority = priorityInitializer(it),
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneFixedCapacityMutableListProducer,
    size: UInt,
    crossinline heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(number = size) {
        val entry = heapEntryInitializer(it)
        val element = entry.element
        val priority = entry.priority
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = element,
            priority = priority,
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneFixedCapacityMutableListProducer,
    capacity: UInt,
    size: UInt,
    crossinline elementInitializer: (index: UInt) -> Element,
    crossinline priorityInitializer: (index: UInt) -> Priority,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(initialCapacity = capacity, number = size) {
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = elementInitializer(it),
            priority = priorityInitializer(it),
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}

public inline fun <Element, Priority> KoneBinaryListBackedMinimumHeap(
    priorityOrder: Order<Priority>,
    listProducer: KoneFixedCapacityMutableListProducer,
    capacity: UInt,
    size: UInt,
    crossinline heapEntryInitializer: (index: UInt) -> HeapEntry<Element, Priority>,
): KoneBinaryListBackedMinimumHeap<Element, Priority> {
    val result = KoneBinaryListBackedMinimumHeap<Element, Priority>(
        priorityOrder = priorityOrder,
        data = null,
    )
    val data = listProducer.produceBy(initialCapacity = capacity, number = size) {
        val entry = heapEntryInitializer(it)
        val element = entry.element
        val priority = entry.priority
        KoneBinaryListBackedMinimumHeap.Node<Element, Priority>(
            element = element,
            priority = priority,
            heap = result,
            index = it,
        )
    }
    result.data = data
    for (index in (size / 2u - 1u) downTo 0u) result.siftTheNodeUpToTheLeaf(index)
    return result
}