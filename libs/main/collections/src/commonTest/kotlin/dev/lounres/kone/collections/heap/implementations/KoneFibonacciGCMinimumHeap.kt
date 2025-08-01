/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.collections.deque.KoneDeque
import dev.lounres.kone.collections.deque.empty
import dev.lounres.kone.collections.deque.isNotEmpty
import dev.lounres.kone.collections.deque.popFirst
import dev.lounres.kone.collections.heap.MinimumHeap
import dev.lounres.kone.collections.heap.MinimumHeapImplementationDescription
import dev.lounres.kone.collections.heap.MinimumHeapProducer
import dev.lounres.kone.collections.heap.MinimumHeapValidator
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.lt
import dev.lounres.kone.scope
import io.kotest.assertions.fail
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.comparables.shouldBeGreaterThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs


object KoneFibonacciGCMinimumHeapDescription : MinimumHeapImplementationDescription {
    override val name = "KoneFibonacciGCMinimumHeap"
    override val producer: MinimumHeapProducer =
        object : MinimumHeapProducer.Resizable {
            override fun <Element, Priority> produce(priorityOrder: Order<Priority>): MinimumHeap<Element, Priority> =
                KoneFibonacciGCMinimumHeap(priorityOrder)
            override fun <Element, Priority> produceBy(
                priorityOrder: Order<Priority>,
                size: UInt,
                elementInitializer: (UInt) -> Element,
                priorityInitializer: (UInt) -> Priority
            ): MinimumHeap<Element, Priority> =
                KoneFibonacciGCMinimumHeap(priorityOrder, size, elementInitializer, priorityInitializer)
        }
    override val validator: MinimumHeapValidator =
        object : MinimumHeapValidator {
            override fun <Element, Priority> validate(heap: MinimumHeap<Element, Priority>) {
                if (heap !is KoneFibonacciGCMinimumHeap<Element, Priority>) fail("The heap is invalid")
                
                if (heap.numberOfChildren == 0u) {
                    heap.size shouldBe 0u
                    heap.firstChild.shouldBeNull()
                    heap.lastChild.shouldBeNull()
                    heap.minimumNode.shouldBeNull()
                } else {
                    heap.size shouldBeGreaterThanOrEqualTo heap.numberOfChildren
                    heap.firstChild.shouldNotBeNull()
                    heap.lastChild.shouldNotBeNull()
                    heap.minimumNode.shouldNotBeNull()
                    
                    val children = KoneDeque.empty<KoneFibonacciGCMinimumHeap.Node<Element, Priority>>()
                    scope {
                        var child = heap.firstChild
                        child!!.previousSibling.shouldBeNull()
                        while (child != null) {
                            children.addLast(child)
                            child.isDetached.shouldBeFalse()
                            child.parent.shouldBeNull()
                            if (child === heap.minimumNode) {
                                child.heap shouldBeSameInstanceAs heap
                            } else {
                                child.heap.shouldBeNull()
                            }
                            val nextChild = child.nextSibling
                            if (nextChild != null) nextChild.previousSibling shouldBeSameInstanceAs child
                            child.isMarked.shouldBeFalse()
                            child = nextChild
                        }
                    }
                    children.size shouldBe heap.numberOfChildren
                    
                    var restSize = heap.size - heap.numberOfChildren
                    while (children.isNotEmpty()) {
                        val parent = children.popFirst()
                        if (parent.numberOfChildren == 0u) {
                            parent.firstChild.shouldBeNull()
                            parent.lastChild.shouldBeNull()
                        } else {
                            restSize shouldBeGreaterThanOrEqualTo parent.numberOfChildren
                            parent.firstChild.shouldNotBeNull()
                            parent.lastChild.shouldNotBeNull()
                            
                            scope {
                                var child = parent.firstChild
                                var childrenCounter = 0u
                                child!!.previousSibling.shouldBeNull()
                                while (child != null) {
                                    children.addLast(child)
                                    childrenCounter++
                                    child.isDetached.shouldBeFalse()
                                    heap.priorityOrder { if (child.priority lt parent.priority) fail("The heap is invalid") }
                                    child.parent shouldBeSameInstanceAs parent
                                    child.heap.shouldBeNull()
                                    val nextChild = child.nextSibling
                                    if (nextChild != null) nextChild.previousSibling shouldBeSameInstanceAs child
                                    child = nextChild
                                }
                                childrenCounter shouldBe parent.numberOfChildren
                            }
                            
                            restSize -= parent.numberOfChildren
                        }
                    }
                    
                    restSize shouldBe 0u
                }
            }
        }
}