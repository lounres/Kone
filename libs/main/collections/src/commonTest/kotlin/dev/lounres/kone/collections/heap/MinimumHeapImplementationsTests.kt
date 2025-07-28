/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap

import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeapDescription
import dev.lounres.kone.collections.heap.implementations.KoneBinaryListBackedMinimumHeapOverFixedCapacityListDescription
import dev.lounres.kone.collections.heap.implementations.KoneBinaryListBackedMinimumHeapOverGrowableListDescription
import dev.lounres.kone.collections.heap.implementations.KoneBinaryListBackedMinimumHeapOverResizableListDescription
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.defaultOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.of


interface MinimumHeapProducer {
    fun <Element, Priority> produceBy(
        priorityOrder: Order<Priority>,
        size: UInt,
        elementInitializer: (index: UInt) -> Element,
        priorityInitializer: (index: UInt) -> Priority,
    ): MinimumHeap<Element, Priority>
    
    interface Resizable : MinimumHeapProducer {
        fun <Element, Priority> produce(priorityOrder: Order<Priority>): MinimumHeap<Element, Priority>
        override fun <Element, Priority> produceBy(
            priorityOrder: Order<Priority>,
            size: UInt,
            elementInitializer: (index: UInt) -> Element,
            priorityInitializer: (index: UInt) -> Priority,
        ): MinimumHeap<Element, Priority>
    }
    interface Growable : MinimumHeapProducer {
        fun <Element, Priority> produce(priorityOrder: Order<Priority>): MinimumHeap<Element, Priority>
        override fun <Element, Priority> produceBy(
            priorityOrder: Order<Priority>,
            size: UInt,
            elementInitializer: (index: UInt) -> Element,
            priorityInitializer: (index: UInt) -> Priority,
        ): MinimumHeap<Element, Priority>
        fun <Element, Priority> produce(priorityOrder: Order<Priority>, initialCapacity: UInt): MinimumHeap<Element, Priority>
        fun <Element, Priority> produceBy(
            priorityOrder: Order<Priority>,
            initialCapacity: UInt,
            size: UInt,
            elementInitializer: (index: UInt) -> Element,
            priorityInitializer: (index: UInt) -> Priority,
        ): MinimumHeap<Element, Priority>
    }
    interface FixedCapacity : MinimumHeapProducer {
        override fun <Element, Priority> produceBy(
            priorityOrder: Order<Priority>,
            size: UInt,
            elementInitializer: (index: UInt) -> Element,
            priorityInitializer: (index: UInt) -> Priority,
        ): MinimumHeap<Element, Priority>
        fun <Element, Priority> produce(priorityOrder: Order<Priority>, capacity: UInt): MinimumHeap<Element, Priority>
        fun <Element, Priority> produceBy(
            priorityOrder: Order<Priority>,
            capacity: UInt,
            size: UInt,
            elementInitializer: (index: UInt) -> Element,
            priorityInitializer: (index: UInt) -> Priority,
        ): MinimumHeap<Element, Priority>
    }
}

interface MinimumHeapValidator {
    fun <Element, Priority> validate(heap: MinimumHeap<Element, Priority>)
}

interface MinimumHeapImplementationDescription {
    val name: String
    val producer: MinimumHeapProducer
    val validator: MinimumHeapValidator
}

val minHeapImplementations = listOf<MinimumHeapImplementationDescription>(
    KoneBinaryGCMinimumHeapDescription,
    KoneBinaryListBackedMinimumHeapOverResizableListDescription,
    KoneBinaryListBackedMinimumHeapOverGrowableListDescription,
    KoneBinaryListBackedMinimumHeapOverFixedCapacityListDescription,
)

class MinimumHeapImplementationsTests : FunSpec({
    threads = 16
    concurrency = 16
    
    val listsToShuffle = Exhaustive.of(KoneList.of(0u, 0u, 2u, 4u, 4u, 4u), KoneList.of(0u, 1u, 2u, 3u), KoneList.of(0u, 1u, 2u, 3u, 4u))
    
    for (impl in minHeapImplementations) context(impl.name) {
        val producer = impl.producer
        
        test("test generative construction") {
            checkAll(listsToShuffle) { init ->
                val permutationsExhaustive = init.permutationsWithoutRepetitions().toList().exhaustive()
                checkAll(permutationsExhaustive) { input ->
                    val heap = producer.produceBy<String, UInt>(
                        defaultOrder(),
                        input.size,
                        { "$it" },
                        { input[it] }
                    )
                    
                    impl.validator.validate(heap)
                    
                    for (item in init) {
                        val min = heap.takeMinimum()
                        heap.takeMinimum() shouldBeSameInstanceAs min
                        heap.popMinimum() shouldBeSameInstanceAs min
                        impl.validator.validate(heap)
                        min.priority shouldBe item
                        val index = min.element.toUInt()
                        input[index] shouldBe item
                    }
                }
            }
        }
        
        suspend /*inline*/ fun testHeapFillingAndEmptying(
            /*crossinline*/ buildHeap: (init: KoneList<UInt>, input: KoneList<UInt>) -> MinimumHeap<String, UInt>,
        ) {
            checkAll(listsToShuffle) { init ->
                val permutationsExhaustive = init.permutationsWithoutRepetitions().toList().exhaustive()
                checkAll(permutationsExhaustive) { input ->
                    val heap = buildHeap(init, input)
                    
                    for ((index, item) in input.withIndex()) {
                        val node = heap.add("$index", item)
                        impl.validator.validate(heap)
                        node.element shouldBe "$index"
                        node.priority shouldBe item
                        heap.size shouldBe index + 1u
                    }
                    
                    for (item in init) {
                        val min = heap.takeMinimum()
                        heap.takeMinimum() shouldBeSameInstanceAs min
                        heap.popMinimum() shouldBeSameInstanceAs min
                        impl.validator.validate(heap)
                        min.priority shouldBe item
                        val index = min.element.toUInt()
                        input[index] shouldBe item
                    }
                }
            }
        }
        
        if (producer is MinimumHeapProducer.Resizable)
            test("test filling and emptying") {
                testHeapFillingAndEmptying { _, _ -> producer.produce<String, UInt>(defaultOrder()) }
            }
        
        if (producer is MinimumHeapProducer.Growable) {
            test("test filling and emptying") {
                testHeapFillingAndEmptying { _, _ -> producer.produce<String, UInt>(defaultOrder()) }
            }
            test("test filling and emptying with predefined capacity") {
                testHeapFillingAndEmptying { init, _ -> producer.produce<String, UInt>(defaultOrder(), init.size) }
            }
        }
        
        if (producer is MinimumHeapProducer.FixedCapacity)
            test("test filling and emptying") {
                testHeapFillingAndEmptying { init, _ -> producer.produce<String, UInt>(defaultOrder(), init.size) }
            }
    }
})