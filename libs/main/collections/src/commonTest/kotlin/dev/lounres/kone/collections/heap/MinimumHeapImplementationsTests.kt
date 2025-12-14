/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap

import de.infix.testBalloon.framework.core.TestSuite
import de.infix.testBalloon.framework.core.testSuite
import de.infix.testBalloon.framework.shared.TestRegistering
import dev.lounres.kone.collections.array.KoneMutableBooleanArray
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.heap.implementations.KoneBinaryGCMinimumHeapDescription
import dev.lounres.kone.collections.heap.implementations.KoneBinaryListBackedMinimumHeapOverFixedCapacityListDescription
import dev.lounres.kone.collections.heap.implementations.KoneBinaryListBackedMinimumHeapOverGrowableListDescription
import dev.lounres.kone.collections.heap.implementations.KoneBinaryListBackedMinimumHeapOverResizableListDescription
import dev.lounres.kone.collections.heap.implementations.KoneFibonacciGCMinimumHeapDescription
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.indices
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.utils.flatMap
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.collections.utils.joinToString
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.max
import dev.lounres.kone.collections.utils.sort
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.combinatorics.enumerative.cartesianProduct
import dev.lounres.kone.combinatorics.enumerative.combinations
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.defaultFor
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs


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
    KoneFibonacciGCMinimumHeapDescription,
)

val MinimumHeapImplementationsTests by testSuite {
    val listsToShuffle = listOf(
        KoneList.of(0u, 1u, 2u, 3u),
        KoneList.of(0u, 0u, 2u, 4u, 4u, 4u),
        KoneList.of(0u, 1u, 2u, 3u, 4u),
    )
    
    for (impl in minHeapImplementations) testSuite(impl.name) {
        val producer = impl.producer
        
        testSuite("test generative construction") {
            for (init in listsToShuffle) testSuite("initial list $init") {
                for (input in init.permutationsWithoutRepetitions()) test("permutation $input") {
                    val heap = producer.produceBy<String, UInt>(
                        Order.defaultFor(),
                        input.size,
                        { "$it" },
                        { input[it] }
                    )

                    impl.validator.validate(heap)

                    for ((index, item) in init.withIndex()) withClue("Removing element # $index") {
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

        @TestRegistering
        /*inline*/ fun TestSuite.testHeapFillingAndEmptying(
            /*crossinline*/ buildHeap: (size: UInt) -> MinimumHeap<String, UInt>,
        ) {
            for (init in listsToShuffle) testSuite("initial list $init") {
                for (input in init.permutationsWithoutRepetitions()) test("permutation $input") {
                    val heap = buildHeap(init.size)
                    val nodes = KoneArrayFixedCapacityList<HeapNode<String, UInt>>(init.size)

                    for ((index, item) in input.withIndex()) withClue("Adding element # $index") {
                        val node = heap.add("$index", item)
                        impl.validator.validate(heap)
                        node.element shouldBe "$index"
                        node.priority shouldBe item
                        heap.size shouldBe index + 1u
                        nodes.add(node)
                        nodes.forEach { node.isDetached.shouldBeFalse() }
                    }

                    val isNodeDetached = KoneMutableBooleanArray.generate(nodes.size) { false }

                    for ((index, item) in init.withIndex()) withClue("Removing element # $index") {
                        val min = heap.takeMinimum()
                        min.priority shouldBe item
                        val minIndex = min.element.toUInt()
                        nodes[minIndex] shouldBeSameInstanceAs min
                        isNodeDetached[minIndex].shouldBeFalse()
                        for (index in nodes.indices) { nodes[index].isDetached shouldBe isNodeDetached[index] }
                        heap.takeMinimum() shouldBeSameInstanceAs min
                        for (index in nodes.indices) { nodes[index].isDetached shouldBe isNodeDetached[index] }
                        heap.popMinimum() shouldBeSameInstanceAs min
                        isNodeDetached[minIndex] = true
                        for (index in nodes.indices) { nodes[index].isDetached shouldBe isNodeDetached[index] }
                        impl.validator.validate(heap)
                    }
                }
            }
        }

        if (producer is MinimumHeapProducer.Resizable)
            testSuite("test filling and emptying") {
                testHeapFillingAndEmptying { producer.produce<String, UInt>(Order.defaultFor()) }
            }

        if (producer is MinimumHeapProducer.Growable) {
            testSuite("test filling and emptying") {
                testHeapFillingAndEmptying { producer.produce<String, UInt>(Order.defaultFor()) }
            }
            testSuite("test filling and emptying with predefined capacity") {
                testHeapFillingAndEmptying { producer.produce<String, UInt>(Order.defaultFor(), it) }
            }
        }

        if (producer is MinimumHeapProducer.FixedCapacity)
            testSuite("test filling and emptying") {
                testHeapFillingAndEmptying { producer.produce<String, UInt>(Order.defaultFor(), it) }
            }
        
        @TestRegistering
        /*inline*/ fun TestSuite.testHeapFillingChangingAndEmptying(
            /*crossinline*/ buildHeap: (size: UInt) -> MinimumHeap<String, UInt>,
        ) {
            for (init in listsToShuffle) testSuite("initial list $init") {
                for (input in init.permutationsWithoutRepetitions()) testSuite("permutation $input") {
                    val newInput = input.map { it * 2u + 1u }
                    val limit = newInput.max() + 1u

                    data class Change(
                        val index: UInt,
                        val newValue: UInt,
                    )

                    for (
                    changes in newInput.indices.toKoneList()
                        .combinations(3u)
                        .flatMap { indicesToChange ->
                            cartesianProduct(indicesToChange.map { (0u .. limit).toKoneList() })
                                .map { newValues ->
                                    KoneList.generate(indicesToChange.size) { Change(indicesToChange[it], newValues[it]) }
                                }
                        }
                    ) test("changing elements: ${changes.joinToString { "#${it.index}: ${newInput[it.index]} -> ${it.newValue}" }}") {
                        val newInit = KoneList.build {
                            addAllFrom(newInput)
                            for (change in changes) this[change.index] = change.newValue
                            sort()
                        }

                        val heap = buildHeap(newInput.size)
                        val nodes = KoneArrayFixedCapacityList<HeapNode<String, UInt>>(newInput.size)

                        for ((index, item) in newInput.withIndex()) withClue("Adding element # $index") {
                            val node = heap.add("$index", item)
                            impl.validator.validate(heap)
                            node.element shouldBe "$index"
                            node.priority shouldBe item
                            heap.size shouldBe index + 1u
                            nodes.add(node)
                            nodes.forEach { node.isDetached.shouldBeFalse() }
                        }

                        for (change in changes) withClue("Changing element # ${change.index}") {
                            nodes[change.index].priority = change.newValue
                            impl.validator.validate(heap)
                            for (node in nodes) node.isDetached.shouldBeFalse()
                        }

                        val isNodeDetached = KoneMutableBooleanArray.generate(nodes.size) { false }

                        for ((index, item) in newInit.withIndex()) withClue("Removing element # $index") {
                            val min = heap.takeMinimum()
                            min.priority shouldBe item
                            val minIndex = min.element.toUInt()
                            nodes[minIndex] shouldBeSameInstanceAs min
                            isNodeDetached[minIndex].shouldBeFalse()
                            for (index in nodes.indices) nodes[index].isDetached shouldBe isNodeDetached[index]
                            heap.takeMinimum() shouldBeSameInstanceAs min
                            for (index in nodes.indices) nodes[index].isDetached shouldBe isNodeDetached[index]
                            heap.popMinimum() shouldBeSameInstanceAs min
                            isNodeDetached[minIndex] = true
                            for (index in nodes.indices) nodes[index].isDetached shouldBe isNodeDetached[index]
                            impl.validator.validate(heap)
                        }
                    }
                }
            }
        }

        if (producer is MinimumHeapProducer.Resizable)
            testSuite("test filling, changing, and emptying") {
                testHeapFillingChangingAndEmptying { producer.produce<String, UInt>(Order.defaultFor()) }
            }

        if (producer is MinimumHeapProducer.Growable) {
            testSuite("test filling, changing, and emptying") {
                testHeapFillingChangingAndEmptying { producer.produce<String, UInt>(Order.defaultFor()) }
            }
            testSuite("test filling, changing, and emptying with predefined capacity") {
                testHeapFillingChangingAndEmptying { producer.produce<String, UInt>(Order.defaultFor(), it) }
            }
        }

        if (producer is MinimumHeapProducer.FixedCapacity)
            testSuite("test filling, changing, and emptying") {
                testHeapFillingChangingAndEmptying { producer.produce<String, UInt>(Order.defaultFor(), it) }
            }
    }
}