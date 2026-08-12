/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.deque.implementations.KoneArrayFixedCapacityCircularDeque
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
            context(assertionScope: AssertionScope)
            override fun <Element, Priority> validate(heap: MinimumHeap<Element, Priority>) {
                if (heap !is KoneFibonacciGCMinimumHeap<Element, Priority>) {
                    fail("The heap is invalid")
                    return
                }
                
                if (heap.numberOfChildren == 0u) {
                    Expect of heap.size toBe 0u
                    Expect of heap.firstChild toBe null
                    Expect of heap.lastChild toBe null
                    Expect of heap.minimumNode toBe null
                } else {
                    Expect of heap.size toBeGreaterThanOrEqualTo heap.numberOfChildren
                    Expect of heap.firstChild notToBe null
                    Expect of heap.lastChild notToBe null
                    Expect of heap.minimumNode notToBe null
                    
                    val children = KoneArrayFixedCapacityCircularDeque<KoneFibonacciGCMinimumHeap.Node<Element, Priority>>(heap.size)
                    scope {
                        var child = heap.firstChild
                        Expect of child!!.previousSibling toBe null
                        while (child != null) {
                            children.addLast(child)
                            Expect of child.isDetached toBe false
                            Expect of child.parent toBe null
                            Expect of child.heap toBeTheSameInstanceAs heap
                            val nextChild = child.nextSibling
                            if (nextChild != null) Expect of nextChild.previousSibling toBeTheSameInstanceAs child
                            else Expect of heap.lastChild toBeTheSameInstanceAs child
                            Expect of child.isMarked toBe false
                            child = nextChild
                        }
                    }
                    Expect of children.size toBe heap.numberOfChildren
                    
                    var restSize = heap.size - heap.numberOfChildren
                    while (children.isNotEmpty()) {
                        val parent = children.popFirst()
                        Expect of restSize toBeGreaterThanOrEqualTo parent.numberOfChildren
                        if (parent.numberOfChildren == 0u) {
                            Expect of parent.firstChild toBe null
                            Expect of parent.lastChild toBe null
                        } else {
                            Expect of parent.firstChild notToBe null
                            Expect of parent.lastChild notToBe null
                            
                            scope {
                                var child = parent.firstChild
                                var childrenCounter = 0u
                                Expect of child!!.previousSibling toBe null
                                while (child != null) {
                                    children.addLast(child)
                                    childrenCounter++
                                    Expect of child.isDetached toBe false
                                    heap.priorityOrder { if (child.priority lt parent.priority) fail("The heap is invalid") }
                                    Expect of child.parent toBeTheSameInstanceAs parent
                                    Expect of child.heap toBe null
                                    val nextChild = child.nextSibling
                                    if (nextChild != null) Expect of nextChild.previousSibling toBeTheSameInstanceAs child
                                    else Expect of parent.lastChild toBeTheSameInstanceAs child
                                    child = nextChild
                                }
                                Expect of childrenCounter toBe parent.numberOfChildren
                            }
                            
                            restSize -= parent.numberOfChildren
                        }
                    }
                    
                    Expect of restSize toBe 0u
                }
            }
        }
}