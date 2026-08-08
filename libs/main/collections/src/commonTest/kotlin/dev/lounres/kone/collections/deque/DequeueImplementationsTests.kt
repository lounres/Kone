/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque

import de.infix.testBalloon.framework.core.testSuite
import dev.lounres.kone.assertions.*
import dev.lounres.kone.collections.deque.implementations.KoneListBackedDequeDescription
import dev.lounres.kone.collections.list.mutableListImplementations
import dev.lounres.kone.repeat
import kotlin.random.Random
import kotlin.random.nextUInt


interface KoneDequeProducer
interface KoneFixedCapacityDequeProducer : KoneDequeProducer {
    fun <Element> produce(capacity: UInt): KoneDeque<Element>
}
interface KoneResizableDequeProducer : KoneDequeProducer {
    fun <Element> produce(): KoneDeque<Element>
}
interface KoneGrowableDequeProducer : KoneDequeProducer {
    fun <Element> produce(): KoneDeque<Element>
    fun <Element> produce(initialCapacity: UInt): KoneDeque<Element>
}

interface KoneDequeValidator {
    context(_: AssertionScope)
    fun <Element: Any> validate(deque: KoneDeque<Element>)
}

interface DequeImplementationDescription {
    val name: String
    val dequeProducer: KoneDequeProducer
    val dequeValidator: KoneDequeValidator
}

val dequeImplementations = buildList<DequeImplementationDescription> {
    mutableListImplementations.mapTo(this) { KoneListBackedDequeDescription(it) }
}

sealed interface DequeOperation<out Element> {
    data class GetFirst<out Element>(val expected: Element) : DequeOperation<Element>
    data class GetLast<out Element>(val expected: Element) : DequeOperation<Element>
    data class AddFirst<out Element>(val element: Element) : DequeOperation<Element>
    data class AddLast<out Element>(val element: Element) : DequeOperation<Element>
    data object RemoveFirst : DequeOperation<Nothing>
    data object RemoveLast : DequeOperation<Nothing>
}

fun <Element> Random.randomDequeOperations(
    randomElement: () -> Element,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): List<DequeOperation<Element>> {
    var currentDeque = listOf<Element>()
    return buildList<DequeOperation<Element>> {
        repeat(numberOfOperations) {
            when {
                currentDeque.isNotEmpty() && nextBoolean() -> {
                    add(
                        if (nextBoolean()) DequeOperation.GetFirst(expected = currentDeque.first())
                        else DequeOperation.GetLast(expected = currentDeque.last())
                    )
                }

                (capacity != null && currentDeque.size == capacity.toInt()) || (currentDeque.isNotEmpty() && nextBoolean()) -> {
                    if (nextBoolean()) {
                        add(DequeOperation.RemoveFirst)
                        currentDeque = currentDeque.subList(1, currentDeque.size)
                    } else {
                        add(DequeOperation.RemoveLast)
                        currentDeque = currentDeque.subList(0, currentDeque.size - 1)
                    }
                }

                else -> {
                    val newElement = randomElement()
                    if (nextBoolean()) {
                        add(DequeOperation.AddFirst(element = newElement))
                        currentDeque = listOf(newElement) + currentDeque
                    } else {
                        add(DequeOperation.AddLast(element = newElement))
                        currentDeque = currentDeque + listOf(newElement)
                    }
                }
            }
        }
    }
}

val DequeImplementationsTest by testSuite {
    for (impl in dequeImplementations) testSuite(impl.name) {
        val producer = impl.dequeProducer
        val validator = impl.dequeValidator

        context(_: AssertionScope)
        fun <Element : Any> testKoneDequeMutabilityOperationsOn(
            data: List<DequeOperation<Element>>,
            deque: KoneDeque<Element>,
            validator: KoneDequeValidator,
        ) {
            firmly {
                withClue({ "Data: $data" }) {
                    for ([index, operation] in data.withIndex())
                        withClue({ "at iteration $index with operation $operation" }) {
                            when (operation) {
                                is DequeOperation.GetFirst<Element> -> Expect of deque.getFirst() toBe operation.expected
                                is DequeOperation.GetLast<Element> -> Expect of deque.getLast() toBe operation.expected
                                is DequeOperation.AddFirst<Element> -> deque.addFirst(operation.element)
                                is DequeOperation.AddLast<Element> -> deque.addLast(operation.element)
                                DequeOperation.RemoveFirst -> deque.removeFirst()
                                DequeOperation.RemoveLast -> deque.removeLast()
                            }
                            validator.validate(deque)
                        }
                }
            }
        }

        when (producer) {
            is KoneFixedCapacityDequeProducer -> {
                test("test mutability operations") {
                    AssertionScope {
                        repeat(1000u) {
                            val data = Random.randomDequeOperations(randomElement = { Random.nextUInt() }, capacity = 20u, numberOfOperations = 100u)
                            val deque = producer.produce<UInt>(20u)
                            testKoneDequeMutabilityOperationsOn(
                                data = data,
                                deque = deque,
                                validator = validator,
                            )
                        }
                    }
                }
            }
            is KoneResizableDequeProducer -> {
                test("test mutability operations") {
                    AssertionScope {
                        repeat(1000u) {
                            val data = Random.randomDequeOperations(randomElement = { Random.nextUInt() }, numberOfOperations = 100u)
                            val deque = producer.produce<UInt>()
                            testKoneDequeMutabilityOperationsOn(
                                data = data,
                                deque = deque,
                                validator = validator,
                            )
                        }
                    }
                }
            }
            is KoneGrowableDequeProducer -> {
                test("test mutability operations") {
                    AssertionScope {
                        repeat(1000u) {
                            val data = Random.randomDequeOperations(randomElement = { Random.nextUInt() }, numberOfOperations = 100u)
                            val deque = producer.produce<UInt>()
                            testKoneDequeMutabilityOperationsOn(
                                data = data,
                                deque = deque,
                                validator = validator,
                            )
                        }
                    }
                }
                test("test mutability operations with ensured capacity") {
                    AssertionScope {
                        repeat(1000u) {
                            val data = Random.randomDequeOperations(randomElement = { Random.nextUInt() }, numberOfOperations = 100u)
                            val deque = producer.produce<UInt>(20u)
                            testKoneDequeMutabilityOperationsOn(
                                data = data,
                                deque = deque,
                                validator = validator,
                            )
                        }
                    }
                }
            }
        }
    }
}