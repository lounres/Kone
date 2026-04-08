/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

// TODO: MOVE ME TO THE NEW API!!!

//import de.infix.testBalloon.framework.core.testSuite
//import dev.lounres.kone.repeat
//import io.kotest.assertions.withClue
//import io.kotest.core.spec.style.FunSpec
//import io.kotest.property.Arb
//import io.kotest.property.arbitrary.arbitrary
//import io.kotest.property.arbitrary.uInt
//import io.kotest.property.checkAll
//
//
//sealed interface SundellTsigasNoddedDequeueOperation<out Element> {
//    data class GetFirst<out Element>(val expected: Element) : SundellTsigasNoddedDequeueOperation<Element>
//    data class GetLast<out Element>(val expected: Element) : SundellTsigasNoddedDequeueOperation<Element>
//    data class AddFirst<out Element>(val element: Element) : SundellTsigasNoddedDequeueOperation<Element>
//    data class AddLast<out Element>(val element: Element) : SundellTsigasNoddedDequeueOperation<Element>
//    data object RemoveFirst : SundellTsigasNoddedDequeueOperation<Nothing>
//    data object RemoveLast : SundellTsigasNoddedDequeueOperation<Nothing>
//}
//
//fun <Element> arbSundellTsigasNoddedDequeueOperation(
//    arbElements: Arb<Element>,
//    numberOfOperations: UInt,
//): Arb<List<SundellTsigasNoddedDequeueOperation<Element>>> = arbitrary { source ->
//    var currentDeque = listOf<Element>()
//    buildList<SundellTsigasNoddedDequeueOperation<Element>> {
//        repeat(numberOfOperations) {
//            when {
//                currentDeque.isNotEmpty() && source.random.nextBoolean() -> {
//                    add(
//                        if (source.random.nextBoolean()) SundellTsigasNoddedDequeueOperation.GetFirst(expected = currentDeque.first())
//                        else SundellTsigasNoddedDequeueOperation.GetLast(expected = currentDeque.last())
//                    )
//                }
//
//                currentDeque.isNotEmpty() && source.random.nextBoolean() -> {
//                    if (source.random.nextBoolean()) {
//                        add(SundellTsigasNoddedDequeueOperation.RemoveFirst)
//                        currentDeque = currentDeque.subList(1, currentDeque.size)
//                    } else {
//                        add(SundellTsigasNoddedDequeueOperation.RemoveLast)
//                        currentDeque = currentDeque.subList(0, currentDeque.size - 1)
//                    }
//                }
//
//                else -> {
//                    val newElement = arbElements.bind()
//                    if (source.random.nextBoolean()) {
//                        add(SundellTsigasNoddedDequeueOperation.AddFirst(element = newElement))
//                        currentDeque = listOf(newElement) + currentDeque
//                    } else {
//                        add(SundellTsigasNoddedDequeueOperation.AddLast(element = newElement))
//                        currentDeque = currentDeque + listOf(newElement)
//                    }
//                }
//            }
//        }
//    }
//}
//
//class KoneConcurrentSundellTsigasNoddedDequeueLogicTestOld : FunSpec({
//    threads = 16
//    concurrency = 16
//
//    test("test mutability") {
//        checkAll(arbSundellTsigasNoddedDequeueOperation(arbElements = Arb.uInt(), numberOfOperations = 100u)) { arbData ->
//            val deque = KoneConcurrentSundellTsigasNoddedDequeue<UInt>()
//
//            for ((val index, val operation = value) in arbData.withIndex())
//                withClue({ "at iteration $index with operation $operation" }) {
//                    when (operation) {
//                        is SundellTsigasNoddedDequeueOperation.GetFirst<UInt> -> /*deque.getFirst() shouldBe operation.expected*/ {} // TODO
//                        is SundellTsigasNoddedDequeueOperation.GetLast<UInt> -> /*deque.getLast() shouldBe operation.expected*/ {}  // TODO
//                        is SundellTsigasNoddedDequeueOperation.AddFirst<UInt> -> deque.addFirst(operation.element)
//                        is SundellTsigasNoddedDequeueOperation.AddLast<UInt> -> deque.addLast(operation.element)
//                        SundellTsigasNoddedDequeueOperation.RemoveFirst -> deque.popFirstMaybe()
//                        SundellTsigasNoddedDequeueOperation.RemoveLast -> deque.popLastMaybe()
//                    }
////                    validator.shouldValidate(deque)
//                }
//        }
//    }
//})
//
//val KoneConcurrentSundellTsigasNoddedDequeueLogicTest by testSuite {
//    test("test mutability") {
//
//    }
//}