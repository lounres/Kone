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
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt


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
            context(assertionScope: AssertionScope)
            override fun <Element, Priority> validate(heap: MinimumHeap<Element, Priority>) {
                if (heap !is KoneBinaryGCMinimumHeap<Element, Priority>) {
                    fail("The heap is invalid")
                    return
                }
                
                if (heap.size == 0u) {
                    Expect of heap.rootHolder toBe null
                    Expect of heap.lastHolder toBe null
                } else {
                    Expect of heap.rootHolder notToBe null
                    Expect of heap.lastHolder notToBe null
                    
                    val nodes = KoneArrayFixedCapacityList<KoneBinaryGCMinimumHeap.NodeHolder<Element, Priority>>(heap.size)
                    
                    val rootHolder = heap.rootHolder!!
                    Expect of rootHolder.parent toBe null
                    
                    nodes.add(rootHolder)
                    var currentIndex = 0u
                    while (true) {
                        Expect of currentIndex toBeLessThan heap.size
                        val currentHolder = nodes[currentIndex]
                        Expect of currentHolder.isDisposed toBe false
                        Expect of currentHolder.heap toBeTheSameInstanceAs heap
                        Expect of currentHolder.index toBe currentIndex
                        if (currentIndex > 0u) {
                            Expect of currentHolder.previous toBeTheSameInstanceAs nodes[currentIndex - 1u]
                            Expect of nodes[currentIndex - 1u].next toBeTheSameInstanceAs currentHolder
                        } else {
                            Expect of currentHolder.previous toBe null
                        }
                        val node = currentHolder.node
                        Expect of node.holder toBeTheSameInstanceAs currentHolder
                        
                        val firstChildHolder = currentHolder.firstChild
                        val secondChildHolder = currentHolder.secondChild
                        
                        currentIndex++
                        
                        when {
                            firstChildHolder == null -> {
                                Expect of secondChildHolder toBe null
                                break
                            }
                            secondChildHolder == null -> {
                                Expect of nodes.size + 1u toBeLessThanOrEqualTo heap.size
                                nodes.add(firstChildHolder)
                                Expect of firstChildHolder.parent toBeTheSameInstanceAs currentHolder
                                heap.priorityOrder {
                                    if (firstChildHolder.node.priority lt node.priority) fail("The heap is invalid")
                                }
                                break
                            }
                            else -> {
                                Expect of nodes.size + 2u toBeLessThanOrEqualTo heap.size
                                nodes.add(firstChildHolder)
                                nodes.add(secondChildHolder)
                                Expect of firstChildHolder.parent toBeTheSameInstanceAs currentHolder
                                Expect of secondChildHolder.parent toBeTheSameInstanceAs currentHolder
                                heap.priorityOrder {
                                    if (firstChildHolder.node.priority lt node.priority) fail("The heap is invalid")
                                    if (secondChildHolder.node.priority lt node.priority) fail("The heap is invalid")
                                }
                            }
                        }
                    }
                    Expect of nodes.size toBe heap.size
                    while (currentIndex < nodes.size) {
                        val currentHolder = nodes[currentIndex]
                        Expect of currentHolder.isDisposed toBe false
                        Expect of currentHolder.heap toBeTheSameInstanceAs heap
                        Expect of currentHolder.index toBe currentIndex
                        if (currentIndex > 0u) {
                            Expect of currentHolder.previous toBeTheSameInstanceAs nodes[currentIndex - 1u]
                            Expect of nodes[currentIndex - 1u].next toBeTheSameInstanceAs currentHolder
                        } else {
                            Expect of currentHolder.previous toBe null
                        }
                        val node = currentHolder.node
                        Expect of node.holder toBeTheSameInstanceAs currentHolder
                        
                        Expect of currentHolder.firstChild toBe null
                        Expect of currentHolder.secondChild toBe null
                        
                        currentIndex++
                    }
                }
            }
        }
}