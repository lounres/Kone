/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque

import dev.lounres.kone.repeat
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll


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
    fun <Element: Any> validate(list: KoneDeque<Element>)
}

fun <Validator: KoneDequeValidator, Element: Any> Validator.shouldValidate(list: KoneDeque<Element>): Validator =
    apply { validate(list) }

interface DequeImplementationDescription {
    val name: String
    val dequeProducer: KoneDequeProducer
    val dequeValidator: KoneDequeValidator
}

val dequeImplementations = listOf<DequeImplementationDescription>(
//    KoneArrayFixedCapacityLinkedListDescription,
//    KoneArrayFixedCapacityLinkedNoddedListDescription,
//    KoneArrayGrowableLinkedListDescription,
//    KoneArrayGrowableLinkedNoddedListDescription,
//    KoneArrayResizableLinkedListDescription,
//    KoneArrayResizableLinkedNoddedListDescription,
//    KoneGCLinkedListDescription,
)

sealed interface DequeOperation<out Element> {
    data class GetFirst<out Element>(val expected: Element) : DequeOperation<Element>
    data class GetLast<out Element>(val expected: Element) : DequeOperation<Element>
    data class AddFirst<out Element>(val element: Element) : DequeOperation<Element>
    data class AddLast<out Element>(val element: Element) : DequeOperation<Element>
    data object RemoveFirst : DequeOperation<Nothing>
    data object RemoveLast : DequeOperation<Nothing>
}

fun <Element> arbDequeOperations(
    arbElements: Arb<Element>,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): Arb<List<DequeOperation<Element>>> = arbitrary { source ->
    var currentDeque = listOf<Element>()
    buildList<DequeOperation<Element>> {
        repeat(numberOfOperations) {
            when {
                currentDeque.isNotEmpty() && source.random.nextBoolean() -> {
                    add(
                        if (source.random.nextBoolean()) DequeOperation.GetFirst(expected = currentDeque.first())
                        else DequeOperation.GetLast(expected = currentDeque.last())
                    )
                }
                
                (capacity != null && currentDeque.size == capacity.toInt()) || (currentDeque.isNotEmpty() && source.random.nextBoolean()) -> {
                    if (source.random.nextBoolean()) {
                        add(DequeOperation.RemoveFirst)
                        currentDeque = currentDeque.subList(1, currentDeque.size)
                    } else {
                        add(DequeOperation.RemoveLast)
                        currentDeque = currentDeque.subList(0, currentDeque.size - 1)
                    }
                }
                
                else -> {
                    val newElement = arbElements.bind()
                    if (source.random.nextBoolean()) {
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

class DequeImplementationsTest : FunSpec({
    threads = 16
    concurrency = 16

    for (impl in dequeImplementations) context(impl.name) {
        val producer = impl.dequeProducer
        val validator = impl.dequeValidator
        
        fun <Element : Any> testKoneDequeMutabilityOperationsOn(
            arbData: List<DequeOperation<Element>>,
            deque: KoneDeque<Element>,
            validator: KoneDequeValidator,
        ) {
            for ((index, operation) in arbData.withIndex())
                withClue({ "at iteration $index with operation $operation" }) {
                    index
                    when (operation) {
                        is DequeOperation.GetFirst<Element> -> deque.getFirst() shouldBe operation.expected
                        is DequeOperation.GetLast<Element> -> deque.getLast() shouldBe operation.expected
                        is DequeOperation.AddFirst<Element> -> deque.addFirst(operation.element)
                        is DequeOperation.AddLast<Element> -> deque.addLast(operation.element)
                        DequeOperation.RemoveFirst -> deque.removeFirst()
                        DequeOperation.RemoveLast -> deque.removeLast()
                    }
                    validator.shouldValidate(deque)
                }
        }
        
        when (producer) {
            is KoneFixedCapacityDequeProducer -> {
                test("test mutability operations") {
                    checkAll(arbDequeOperations(arbElements = Arb.uInt(), capacity = 20u, numberOfOperations = 100u)) { arbData ->
                        val deque = producer.produce<UInt>(20u)
                        testKoneDequeMutabilityOperationsOn(
                            arbData = arbData,
                            deque = deque,
                            validator = validator,
                        )
                    }
                }
            }
            is KoneResizableDequeProducer -> {
                test("test mutability operations") {
                    checkAll(arbDequeOperations(arbElements = Arb.uInt(), numberOfOperations = 100u)) { arbData ->
                        val deque = producer.produce<UInt>()
                        testKoneDequeMutabilityOperationsOn(
                            arbData = arbData,
                            deque = deque,
                            validator = validator,
                        )
                    }
                }
            }
            is KoneGrowableDequeProducer -> {
                test("test mutability operations") {
                    checkAll(arbDequeOperations(arbElements = Arb.uInt(), capacity = 20u, numberOfOperations = 100u)) { arbData ->
                        val deque = producer.produce<UInt>()
                        testKoneDequeMutabilityOperationsOn(
                            arbData = arbData,
                            deque = deque,
                            validator = validator,
                        )
                    }
                }
                test("test mutability operations with ensured capacity") {
                    checkAll(arbDequeOperations(arbElements = Arb.uInt(), capacity = 20u, numberOfOperations = 100u)) { arbData ->
                        val deque = producer.produce<UInt>(20u)
                        testKoneDequeMutabilityOperationsOn(
                            arbData = arbData,
                            deque = deque,
                            validator = validator,
                        )
                    }
                }
            }
        }
    }
})