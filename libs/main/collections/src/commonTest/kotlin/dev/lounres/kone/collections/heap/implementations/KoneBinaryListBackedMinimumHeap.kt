/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.MinimumHeapImplementationDescription
import dev.lounres.kone.collections.heap.MinimumHeapProducer
import dev.lounres.kone.collections.heap.MinimumHeapValidator
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityListProducer
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableListProducer
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableListProducer
import dev.lounres.kone.collections.list.indices
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.gt


object KoneBinaryListBackedMinimumHeapValidator : MinimumHeapValidator {
    context(assertionScope: AssertionScope)
    override fun <Element, Priority> validate(heap: MinimumHeap<Element, Priority>) {
        if (heap !is KoneBinaryListBackedMinimumHeap<Element, Priority>) {
            fail("The heap is invalid")
            return
        }
        if (heap.isDisposed) fail("The heap is invalid")
        
        val data = heap.data
        for (index in data.indices) {
            val node = data[index]
            Expect of node.heap toBeTheSameInstanceAs heap
            Expect of node.isDetached toBe false
            Expect of node.index toBe index
            if (index != 0u) {
                val parent = data[(index - 1u) / 2u]
                heap.priorityOrder { if (parent.priority gt node.priority) fail("The heap is invalid") }
            }
        }
    }
}

object KoneBinaryListBackedMinimumHeapOverResizableListDescription : MinimumHeapImplementationDescription {
    override val name = "KoneBinaryListBackedMinimumHeap over resizable list"
    override val producer: MinimumHeapProducer =
        object : MinimumHeapProducer.Resizable  {
            override fun <Element, Priority> produce(priorityOrder: Order<Priority>): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayResizableListProducer,
                )
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                size: UInt,
                elementInitializer: (UInt) -> Element,
                priorityInitializer: (UInt) -> Priority
            ): MinimumHeap<Element, Priority> = KoneBinaryListBackedMinimumHeap(
                priorityOrder = priorityOrder,
                listProducer = KoneArrayResizableListProducer,
                size = size,
                elementInitializer = elementInitializer,
                priorityInitializer = priorityInitializer,
            )
        }
    override val validator: MinimumHeapValidator get() = KoneBinaryListBackedMinimumHeapValidator
}

object KoneBinaryListBackedMinimumHeapOverGrowableListDescription : MinimumHeapImplementationDescription {
    override val name = "KoneBinaryListBackedMinimumHeap over growable list"
    override val producer: MinimumHeapProducer =
        object : MinimumHeapProducer.Growable  {
            override fun <Element, Priority> produce(priorityOrder: Order<Priority>): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayGrowableListProducer,
                )
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                size: UInt,
                elementInitializer: (index: UInt) -> Element,
                priorityInitializer: (index: UInt) -> Priority,
            ): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayGrowableListProducer,
                    size = size,
                    elementInitializer = elementInitializer,
                    priorityInitializer = priorityInitializer,
                )
            override fun <Element, Priority> produce(
                priorityOrder: Order<Priority>,
                initialCapacity: UInt
            ): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayGrowableListProducer,
                    initialCapacity = initialCapacity,
                )
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                initialCapacity: UInt,
                size: UInt,
                elementInitializer: (index: UInt) -> Element,
                priorityInitializer: (index: UInt) -> Priority,
            ): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayGrowableListProducer,
                    initialCapacity = initialCapacity,
                    size = size,
                    elementInitializer = elementInitializer,
                    priorityInitializer = priorityInitializer,
                )
        }
    override val validator: MinimumHeapValidator get() = KoneBinaryListBackedMinimumHeapValidator
}

object KoneBinaryListBackedMinimumHeapOverFixedCapacityListDescription : MinimumHeapImplementationDescription {
    override val name = "KoneBinaryListBackedMinimumHeap over fixed capacity list"
    override val producer: MinimumHeapProducer =
        object : MinimumHeapProducer.FixedCapacity  {
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                size: UInt,
                elementInitializer: (index: UInt) -> Element,
                priorityInitializer: (index: UInt) -> Priority,
            ): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayFixedCapacityListProducer,
                    size = size,
                    elementInitializer = elementInitializer,
                    priorityInitializer = priorityInitializer,
                )
            override fun <Element, Priority> produce(priorityOrder: Order<Priority>, capacity: UInt): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayFixedCapacityListProducer,
                    capacity = capacity,
                )
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                capacity: UInt,
                size: UInt,
                elementInitializer: (index: UInt) -> Element,
                priorityInitializer: (index: UInt) -> Priority,
            ): MinimumHeap<Element, Priority> =
                KoneBinaryListBackedMinimumHeap(
                    priorityOrder = priorityOrder,
                    listProducer = KoneArrayFixedCapacityListProducer,
                    capacity = capacity,
                    size = size,
                    elementInitializer = elementInitializer,
                    priorityInitializer = priorityInitializer,
                )
        }
    override val validator: MinimumHeapValidator get() = KoneBinaryListBackedMinimumHeapValidator
}