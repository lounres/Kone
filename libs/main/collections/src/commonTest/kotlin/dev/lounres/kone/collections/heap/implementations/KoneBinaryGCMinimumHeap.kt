/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.MinimumHeapProducer
import dev.lounres.kone.collections.heap.MinimumHeapImplementationDescription
import dev.lounres.kone.collections.heap.MinimumHeapValidator
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt
import io.kotest.assertions.fail
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs


object KoneBinaryGCMinimumHeapDescription : MinimumHeapImplementationDescription {
    override val name = "KoneBinaryGCMinimumHeap"
    override val producer: MinimumHeapProducer =
        object : MinimumHeapProducer.Resizable {
            override fun <Element, Priority> produce(priorityOrder: Order<Priority>): MinimumHeap<Element, Priority> =
                KoneBinaryGCMinimumHeap(priorityOrder)
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                size: UInt,
                elementInitializer: (UInt) -> Element,
                priorityInitializer: (UInt) -> Priority
            ): MinimumHeap<Element, Priority> =
                KoneBinaryGCMinimumHeap(priorityOrder, size, elementInitializer, priorityInitializer)
        }
    override val validator: MinimumHeapValidator =
        object : MinimumHeapValidator {
            override fun <Element, Priority> validate(heap: MinimumHeap<Element, Priority>) {
                if (heap !is KoneBinaryGCMinimumHeap<Element, Priority>) fail("The heap is invalid")
                
                if (heap.size == 0u) {
                    heap.rootHolder.shouldBeNull()
                    heap.lastHolder.shouldBeNull()
                } else {
                    heap.rootHolder.shouldNotBeNull()
                    heap.lastHolder.shouldNotBeNull()
                    
                    val nodes = KoneArrayFixedCapacityList<KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>>(heap.size)
                    
                    val rootHolder = heap.rootHolder!!
                    rootHolder.parent.shouldBeNull()
                    
                    nodes.add(rootHolder)
                    var currentIndex = 0u
                    while (true) {
                        currentIndex shouldBeLessThan heap.size
                        val currentHolder = nodes[currentIndex]
                        currentHolder.isDisposed.shouldBeFalse()
                        currentHolder.heap shouldBeSameInstanceAs heap
                        currentHolder.index shouldBe currentIndex
                        if (currentIndex > 0u) {
                            currentHolder.previous shouldBeSameInstanceAs nodes[currentIndex - 1u]
                            nodes[currentIndex - 1u].next shouldBeSameInstanceAs currentHolder
                        } else {
                            currentHolder.previous.shouldBeNull()
                        }
                        val node = currentHolder.node
                        node.holder shouldBeSameInstanceAs currentHolder
                        
                        val firstChildHolder = currentHolder.firstChild
                        val secondChildHolder = currentHolder.secondChild
                        
                        currentIndex++
                        
                        when {
                            firstChildHolder == null -> {
                                secondChildHolder.shouldBeNull()
                                break
                            }
                            secondChildHolder == null -> {
                                nodes.size + 1u shouldBeLessThanOrEqualTo heap.size
                                nodes.add(firstChildHolder)
                                firstChildHolder.parent shouldBeSameInstanceAs currentHolder
                                heap.priorityOrder {
                                    if (firstChildHolder.node.priority lt node.priority) fail("The heap is invalid")
                                }
                                break
                            }
                            else -> {
                                nodes.size + 2u shouldBeLessThanOrEqualTo heap.size
                                nodes.add(firstChildHolder)
                                nodes.add(secondChildHolder)
                                firstChildHolder.parent shouldBeSameInstanceAs currentHolder
                                secondChildHolder.parent shouldBeSameInstanceAs currentHolder
                                heap.priorityOrder {
                                    if (firstChildHolder.node.priority lt node.priority) fail("The heap is invalid")
                                    if (secondChildHolder.node.priority lt node.priority) fail("The heap is invalid")
                                }
                            }
                        }
                    }
                    nodes.size shouldBe heap.size
                    while (currentIndex < nodes.size) {
                        val currentHolder = nodes[currentIndex]
                        currentHolder.isDisposed.shouldBeFalse()
                        currentHolder.heap shouldBeSameInstanceAs heap
                        currentHolder.index shouldBe currentIndex
                        if (currentIndex > 0u) {
                            currentHolder.previous shouldBeSameInstanceAs nodes[currentIndex - 1u]
                            nodes[currentIndex - 1u].next shouldBeSameInstanceAs currentHolder
                        } else {
                            currentHolder.previous.shouldBeNull()
                        }
                        val node = currentHolder.node
                        node.holder shouldBeSameInstanceAs currentHolder
                        
                        currentHolder.firstChild.shouldBeNull()
                        currentHolder.secondChild.shouldBeNull()
                        
                        currentIndex++
                    }
                }
            }
        }
}