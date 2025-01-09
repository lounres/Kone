/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.list.implementations.KoneTwoThreeTreeList.Companion.size
import dev.lounres.kone.collections.list.producers.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.producers.KoneListProducer
import dev.lounres.kone.collections.list.producers.KoneResizableMutableListProducer
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.context.invoke
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import kotlin.random.nextUInt


interface KoneListValidator {
    fun validate(
        list: KoneList<Any>,
    )
    
    fun validateWithIterator(
        list: KoneList<Any>,
        iterator: KoneIterator<Any>,
    )
}

fun <Validator: KoneListValidator> Validator.shouldValidate(list: KoneList<Any>): Validator =
    apply { validate(list) }
fun <Validator: KoneListValidator> Validator.shouldValidate(list: KoneList<Any>, iterator: KoneIterator<Any>): Validator =
    apply { validateWithIterator(list, iterator) }

data class ListImplementationDescription (
    val name: String,
    val producer: KoneListProducer,
    val validator: KoneListValidator = object : KoneListValidator {
        override fun validate(
            list: KoneList<Any>,
        ) {}
        
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {}
    },
)

val listImplementations = listOf<ListImplementationDescription>(
    // Array fixed capacity implementations.
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityLinkedList",
        producer = KoneArrayFixedCapacityLinkedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayFixedCapacityLinkedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val capacity = list.capacity
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data

                if (size > capacity) fail("The list is invalid")
                if (data.size != capacity || nextNodeIndex.size != capacity || previousNodeIndex.size != capacity) fail("The list is invalid")
                if (nextNodeIndex.any { it !in 0u..<capacity } || previousNodeIndex.any { it !in 0u..<capacity }) fail("The list is invalid")

                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(capacity) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == capacity - 1u)) fail("The list is invalid")
                    }
                }

                repeat(capacity) { if (previousNodeIndex[nextNodeIndex[it]] != it) fail("The list is invalid") }

                scope {
                    var currentIndex = start
                    repeat(capacity) { iteration ->
                        if ((iteration == (size + capacity - 1u).mod(capacity)) != (currentIndex == end)) fail("The list is invalid")
                        if ((iteration < size) != (data[currentIndex] != null)) fail("The list is invalid")
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayFixedCapacityLinkedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) fail("The iterator is invalid")
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityLinkedNoddedList",
        producer = KoneArrayFixedCapacityLinkedNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayFixedCapacityLinkedNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val capacity = list.capacity
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data

                if (size > capacity) fail("The list is invalid")
                if (data.size != capacity || nextNodeIndex.size != capacity || previousNodeIndex.size != capacity) fail("The list is invalid")
                if (nextNodeIndex.any { it !in 0u..<capacity } || previousNodeIndex.any { it !in 0u..<capacity }) fail("The list is invalid")

                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(capacity) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == capacity - 1u)) fail("The list is invalid")
                    }
                }

                repeat(capacity) { if (previousNodeIndex[nextNodeIndex[it]] != it) fail("The list is invalid") }

                scope {
                    var currentIndex = start
                    repeat(capacity) { iteration ->
                        if ((iteration == (size + capacity - 1u).mod(capacity)) != (currentIndex == end)) fail("The list is invalid")
                        val currentNodeOrNull = data[currentIndex]
                        if (iteration < size) {
                            if (currentNodeOrNull == null) fail("The list is invalid")
                            if (currentNodeOrNull.actualIndex != currentIndex) fail("The list is invalid")
                            if (currentNodeOrNull.isDetached) fail("The list is invalid")
                            if (currentNodeOrNull.list !== list) fail("The list is invalid")
                        } else {
                            if (currentNodeOrNull != null) fail("The list is invalid")
                        }
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayFixedCapacityLinkedNoddedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) fail("The iterator is invalid")
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityList",
        producer = KoneArrayFixedCapacityListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayFixedCapacityList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val size = list.size
                val data = list.data

                if (size > data.size) fail("The list is invalid")

                repeat(data.size) { index ->
                    if ((index < size) != (data[index] != null)) fail("The list is invalid")
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayFixedCapacityList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityNoddedList",
        producer = KoneArrayFixedCapacityNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayFixedCapacityNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val size = list.size
                val data = list.data

                if (size > data.size) fail("The list is invalid")

                repeat(data.size) { index ->
                    if (index < size) {
                        val node = data[index]
                        if (node == null) fail("The list is invalid")
                        if (node.list !== list) fail("The list is invalid")
                        if (node.index != index) fail("The list is invalid")
                    } else {
                        if (data[index] != null) fail("The list is invalid")
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayFixedCapacityNoddedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        },
    ),
    // Array growable implementations.
    ListImplementationDescription(
        name = "KoneArrayGrowableLinkedList",
        producer = KoneArrayGrowableLinkedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayGrowableLinkedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data

                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) fail("The list is invalid")
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) fail("The list is invalid")

                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) fail("The list is invalid")
                    }
                }

                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) fail("The list is invalid") }

                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) fail("The list is invalid")
                        if ((iteration < size) != (data[currentIndex] != null)) fail("The list is invalid")
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayGrowableLinkedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) fail("The iterator is invalid")
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayGrowableLinkedNoddedList",
        producer = KoneArrayGrowableLinkedNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayGrowableLinkedNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data

                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) fail("The list is invalid")
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) fail("The list is invalid")

                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) fail("The list is invalid")
                    }
                }

                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) fail("The list is invalid") }

                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) fail("The list is invalid")
                        val currentNodeOrNull = data[currentIndex]
                        if (iteration < size) {
                            if (currentNodeOrNull == null) fail("The list is invalid")
                            if (currentNodeOrNull.actualIndex != currentIndex) fail("The list is invalid")
                            if (currentNodeOrNull.isDetached) fail("The list is invalid")
                            if (currentNodeOrNull.list !== list) fail("The list is invalid")
                        } else {
                            if (currentNodeOrNull != null) fail("The list is invalid")
                        }
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayGrowableLinkedNoddedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) fail("The iterator is invalid")
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayGrowableList",
        producer = KoneArrayGrowableListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayGrowableList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data

                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound) fail("The list is invalid")

                repeat(sizeUpperBound) { index ->
                    if ((index < size) != (data[index] != null)) fail("The list is invalid")
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayGrowableList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayGrowableNoddedList",
        producer = KoneArrayGrowableNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayGrowableNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data

                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound) fail("The list is invalid")

                repeat(sizeUpperBound) { index ->
                    val currentNodeOrNull = data[index]
                    if (index < size) {
                        if (currentNodeOrNull == null) fail("The list is invalid")
                        if (currentNodeOrNull.index != index) fail("The list is invalid")
                        if (currentNodeOrNull.isDetached) fail("The list is invalid")
                        if (currentNodeOrNull.list !== list) fail("The list is invalid")
                    } else {
                        if (currentNodeOrNull != null) fail("The list is invalid")
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayGrowableNoddedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        },
    ),
    // Array resizable implementations.
    ListImplementationDescription(
        name = "KoneArrayResizableLinkedList",
        producer = KoneArrayResizableLinkedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayResizableLinkedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data

                if (dataSizeNumber !in 1u..31u) fail("The list is invalid")
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) fail("The list is invalid")
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) fail("The list is invalid")

                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) fail("The list is invalid")
                    }
                }

                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) fail("The list is invalid") }

                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) fail("The list is invalid")
                        if ((iteration < size) != (data[currentIndex] != null)) fail("The list is invalid")
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayResizableLinkedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) fail("The iterator is invalid")
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayResizableLinkedNoddedList",
        producer = KoneArrayResizableLinkedNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayResizableLinkedNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data

                if (dataSizeNumber !in 1u..31u) fail("The list is invalid")
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) fail("The list is invalid")
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) fail("The list is invalid")

                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) fail("The list is invalid")
                    }
                }

                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) fail("The list is invalid") }

                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) fail("The list is invalid")
                        val currentNodeOrNull = data[currentIndex]
                        if (iteration < size) {
                            if (currentNodeOrNull == null) fail("The list is invalid")
                            if (currentNodeOrNull.actualIndex != currentIndex) fail("The list is invalid")
                            if (currentNodeOrNull.isDetached) fail("The list is invalid")
                            if (currentNodeOrNull.list !== list) fail("The list is invalid")
                        } else {
                            if (currentNodeOrNull != null) fail("The list is invalid")
                        }
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayResizableLinkedNoddedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) fail("The iterator is invalid")
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayResizableList",
        producer = KoneArrayResizableListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayResizableList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data

                if (dataSizeNumber !in 1u..31u) fail("The list is invalid")
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound) fail("The list is invalid")

                repeat(sizeUpperBound) { index ->
                    if ((index < size) != (data[index] != null)) fail("The list is invalid")
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayResizableList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayResizableNoddedList",
        producer = KoneArrayResizableNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(
                list: KoneList<Any>,
            ) {
                if (list !is KoneArrayResizableNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data

                if (dataSizeNumber !in 1u..31u) fail("The list is invalid")
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) fail("The list is invalid")
                if (size > sizeUpperBound) fail("The list is invalid")
                if (data.size != sizeUpperBound) fail("The list is invalid")

                repeat(sizeUpperBound) { index ->
                    val currentNodeOrNull = data[index]
                    if (index < size) {
                        if (currentNodeOrNull == null) fail("The list is invalid")
                        if (currentNodeOrNull.index != index) fail("The list is invalid")
                        if (currentNodeOrNull.isDetached) fail("The list is invalid")
                        if (currentNodeOrNull.list !== list) fail("The list is invalid")
                    } else {
                        if (currentNodeOrNull != null) fail("The list is invalid")
                    }
                }
            }

            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>,
            ) {
                validate(list)

                if (iterator !is KoneArrayResizableNoddedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        },
    ),
    // Array settable implementations
    ListImplementationDescription(
        name = "KoneArraySettableList",
        producer = KoneArraySettableListProducer,
        validator = object : KoneListValidator {
            override fun validate(list: KoneList<Any>) {
                if (list !is KoneArraySettableList<Any>) fail("The list is invalid")
            }
            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>
            ) {
                validate(list)
                list as KoneArraySettableList<Any>

                if (iterator !is KoneArraySettableList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.data.array !== list.data.array) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        }
    ),
    ListImplementationDescription(
        name = "KoneArraySettableNoddedList",
        producer = KoneArraySettableNoddedListProducer,
        validator = object : KoneListValidator {
            override fun validate(list: KoneList<Any>) {
                if (list !is KoneArraySettableNoddedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")

                val data = list.data

                repeat(data.size) { index ->
                    val currentNodeOrNull = data[index]
                    if (currentNodeOrNull == null) fail("The list is invalid")
                    if (currentNodeOrNull.index != index) fail("The list is invalid")
                    if (currentNodeOrNull.isDetached) fail("The list is invalid")
                    if (currentNodeOrNull.list !== list) fail("The list is invalid")
                }
            }
            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>
            ) {
                validate(list)
                list as KoneArraySettableList<Any>

                if (iterator !is KoneArraySettableList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.data.array !== list.data.array) fail("The iterator is invalid")

                if (iterator.currentIndex > list.size) fail("The iterator is invalid")
            }
        }
    ),
    // GC (resizable) implementations
    ListImplementationDescription(
        name = "KoneGCLinkedList",
        producer = KoneGCLinkedListProducer,
        validator = object : KoneListValidator {
            override fun validate(list: KoneList<Any>) {
                if (list !is KoneGCLinkedList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")
                
                val size = list.size
                val start = list.start
                val end = list.end
                
                if (size == 0u) {
                    if (start != null || end != null) fail("The list is invalid")
                } else {
                    if (start == null || end == null) fail("The list is invalid")
                    
                    if (start.previousNode != null) fail("The list is invalid")
                    
                    var index = 0u
                    var currentNode = start
                    while (currentNode != null) {
                        if (index >= size) fail("The list is invalid")
                        val nextNode = currentNode._nextNode
                        if (nextNode != null && nextNode._previousNode != currentNode) fail("The list is invalid")
                        currentNode = nextNode
                        index++
                    }
                    if (index != size) fail("The list is invalid")
                }
            }
            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>
            ) {
                validate(list)
                list as KoneGCLinkedList<Any>

                if (iterator !is KoneGCLinkedList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")
                
                val nextNode = iterator.nextNode
                val nextIndex = iterator._nextIndex
                
                if (nextNode != null && nextNode.list !== list) fail("The iterator is invalid")
                if (nextIndex != null && nextIndex != (nextNode?.index ?: list.size)) fail("The iterator is invalid")
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneTwoThreeTreeList",
        producer = KoneTwoThreeTreeListProducer,
        validator = object : KoneListValidator {
            override fun validate(list: KoneList<Any>) {
                if (list !is KoneTwoThreeTreeList<Any>) fail("The list is invalid")
                if (list.isDisposed) fail("The list is invalid")
                
                val size = list.size
                val rootHolder = list.rootHolder
                val firstNode = list.firstNode
                val lastNode = list.lastNode
                
                if (size == 0u) {
                    if (rootHolder != null || firstNode != null || lastNode != null) fail("The list is invalid")
                } else {
                    if (rootHolder == null || firstNode == null || lastNode == null) fail("The list is invalid")
                    
                    if (rootHolder.size != size) fail("The list is invalid")
                    
                    tailrec fun KoneTwoThreeTreeList.NodeHolder<Any>?.heightAddedTo(number: UInt): UInt =
                        when (this) {
                            null -> number
                            is KoneTwoThreeTreeList.TwoNodeHolder<Any> -> this.firstChild.heightAddedTo(number + 1u)
                            is KoneTwoThreeTreeList.ThreeNodeHolder<Any> -> this.firstChild.heightAddedTo(number + 1u)
                        }
                    
                    val depth = rootHolder.heightAddedTo(0u)
                    
                    fun validateSubtree(
                        holder: KoneTwoThreeTreeList.NodeHolder<Any>,
                        depth: UInt,
                        firstNode: KoneTwoThreeTreeList.Node<Any>,
                        lastNode: KoneTwoThreeTreeList.Node<Any>,
                    ) {
                        if (holder.isDisposed) fail("The list is invalid")
                        if (holder.tree !== list) fail("The list is invalid")
                        if (depth == 0u) fail("The list is invalid")
                        if (depth == 1u) {
                            if (!holder.isItBottom) fail("The list is invalid")
                            when (holder) {
                                is KoneTwoThreeTreeList.TwoNodeHolder<Any> -> {
                                    if (holder.firstChild != null || holder.secondChild != null) fail("The list is invalid")
                                    if (holder.firstChildSize != 0u || holder.secondChildSize != 0u) fail("The list is invalid")
                                    val actualNode = holder.element
                                    if (actualNode !== firstNode || actualNode !== lastNode) fail("The list is invalid")
                                }
                                is KoneTwoThreeTreeList.ThreeNodeHolder<Any> -> {
                                    if (holder.firstChild != null || holder.secondChild != null || holder.thirdChild != null) fail("The list is invalid")
                                    if (holder.firstChildSize != 0u || holder.secondChildSize != 0u || holder.thirdChildSize != 0u) fail("The list is invalid")
                                    val firstActualNode = holder.firstElement
                                    val secondActualNode = holder.secondElement
                                    if (firstNode !== firstActualNode || lastNode !== secondActualNode) fail("The list is invalid")
                                    if (firstActualNode.nextNode !== secondActualNode || secondActualNode.previousNode !== firstActualNode) fail("The list is invalid")
                                    if (firstActualNode.holder !== holder || secondActualNode.holder !== holder) fail("The list is invalid")
                                }
                            }
                        } else {
                            if (holder.isItBottom) fail("The list is invalid")
                            when (holder) {
                                is KoneTwoThreeTreeList.TwoNodeHolder<Any> -> {
                                    if (holder.firstChild == null || holder.secondChild == null) fail("The list is invalid")
                                    if (holder.firstChild!!.parent !== holder || holder.secondChild!!.parent !== holder) fail("The list is invalid")
                                    if (holder.firstChildSize != holder.firstChild.size || holder.secondChildSize != holder.secondChild.size) fail("The list is invalid")
                                    val node = holder.element
                                    if (node.holder !== holder) fail("The list is invalid")
                                    val previousNode = node.previousNode ?: fail("The list is invalid")
                                    val nextNode = node.nextNode ?: fail("The list is invalid")
                                    if (previousNode.nextNode !== node || nextNode.previousNode !== node) fail("The list is invalid")
                                    validateSubtree(
                                        holder.firstChild!!,
                                        depth - 1u,
                                        firstNode,
                                        previousNode,
                                    )
                                    validateSubtree(
                                        holder.secondChild!!,
                                        depth - 1u,
                                        nextNode,
                                        lastNode
                                    )
                                }
                                is KoneTwoThreeTreeList.ThreeNodeHolder<Any> -> {
                                    if (holder.firstChild == null || holder.secondChild == null || holder.thirdChild == null) fail("The list is invalid")
                                    if (holder.firstChild!!.parent !== holder || holder.secondChild!!.parent !== holder) fail("The list is invalid")
                                    if (holder.firstChildSize != holder.firstChild.size || holder.secondChildSize != holder.secondChild.size || holder.thirdChildSize != holder.thirdChild.size) fail("The list is invalid")
                                    val node1 = holder.firstElement
                                    val node2 = holder.secondElement
                                    if (node1.holder !== holder || node2.holder !== holder) fail("The list is invalid")
                                    val previousNode1 = node1.previousNode ?: fail("The list is invalid")
                                    val nextNode1 = node1.nextNode ?: fail("The list is invalid")
                                    if (previousNode1.nextNode !== node1 || nextNode1.previousNode !== node1) fail("The list is invalid")
                                    val previousNode2 = node2.previousNode ?: fail("The list is invalid")
                                    val nextNode2 = node2.nextNode ?: fail("The list is invalid")
                                    if (previousNode2.nextNode !== node2 || nextNode2.previousNode !== node2) fail("The list is invalid")
                                    validateSubtree(
                                        holder.firstChild!!,
                                        depth - 1u,
                                        firstNode,
                                        previousNode1,
                                    )
                                    validateSubtree(
                                        holder.secondChild!!,
                                        depth - 1u,
                                        nextNode1,
                                        previousNode2,
                                    )
                                    validateSubtree(
                                        holder.thirdChild!!,
                                        depth - 1u,
                                        nextNode2,
                                        lastNode
                                    )
                                }
                            }
                        }
                    }
                    
                    validateSubtree(rootHolder, depth, firstNode, lastNode)
                }
            }
            override fun validateWithIterator(
                list: KoneList<Any>,
                iterator: KoneIterator<Any>
            ) {
                validate(list)
                list as KoneTwoThreeTreeList<Any>
                
                if (iterator !is KoneTwoThreeTreeList.Iterator<Any>) fail("The iterator is invalid")
                if (iterator.list !== list) fail("The iterator is invalid")
                
                val nextNode = iterator.nextNode
                val nextIndex = iterator._nextIndex
                
                if (nextIndex != null && (nextNode?.index ?: list.size) != nextIndex) fail("The iterator is invalid")
            }
        },
    )
)

fun <Element> testEqualityByIteration(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists by iteration through them") {
        val listIterator = list1.iterator()
        for (i in 0u ..< list2.size.toUInt()) {
            if (!listIterator.hasNext()) fail("List iterator stopped before the length ended")
            withClue({ "Checking equality of elements at index $i" }) {
                listIterator.getNext() shouldBe list2[i.toInt()]
            }
            listIterator.moveNext()
        }
        if (listIterator.hasNext()) fail("List iterator has extra elements")
    }
}

fun <Element> testEqualityByStringRepresentation(list1: KoneList<Element>, list2: List<Element>) {
    withClue("Checking equality of the lists' string representations") {
        list1.toString() shouldBe list2.toString()
    }
}

fun <Element> testEquality(list1: KoneList<Element>, list2: List<Element>) {
    testEqualityByIteration(list1, list2)
    testEqualityByStringRepresentation(list1, list2)
}

sealed interface MutableListOperation<out Element> {
    data class SetAt<out Element>(val index: UInt, val element: Element): MutableListOperation<Element>
    data class AddAt<out Element>(val index: UInt, val element: Element): MutableListOperation<Element>
    data class RemoveAt(val index: UInt): MutableListOperation<Nothing>
}

data class MutableListOperationWithResult<out Element>(
    val initialList: List<Element>,
    val numberOfOperations: UInt,
    val operations: List<MutableListOperation<Element>>,
    val results: List<List<Element>>,
)

fun <Element> arbMutableListOperationsWithResults(
    arbElements: Arb<Element>,
    initialSize: UInt,
    capacity: UInt? = null,
    numberOfOperations: UInt,
): Arb<MutableListOperationWithResult<Element>> = arbitrary { source ->
    val initialList = List(initialSize.toInt()) { arbElements.bind() }
    val operations = mutableListOf<MutableListOperation<Element>>()
    val results = mutableListOf<List<Element>>()
    repeat(numberOfOperations) {
        val lastList = results.lastOrNull() ?: initialList
        when {
            lastList.isNotEmpty() && source.random.nextUInt(3u) == 0u -> {
                val newElement = arbElements.bind()
                val settingIndex = source.random.nextInt(lastList.size)
                operations.add(MutableListOperation.SetAt(settingIndex.toUInt(), newElement))
                results.add(lastList.subList(0, settingIndex) + newElement + lastList.subList(settingIndex + 1, lastList.size))
            }
            (capacity == null || capacity.toInt() > lastList.size) && (lastList.isEmpty() || source.random.nextBoolean()) -> {
                val newElement = arbElements.bind()
                val insertionIndex = source.random.nextInt(lastList.size + 1)
                operations.add(MutableListOperation.AddAt(insertionIndex.toUInt(), newElement))
                results.add(lastList.subList(0, insertionIndex) + newElement + lastList.subList(insertionIndex, lastList.size))
            }
            else -> {
                val deletionIndex = source.random.nextInt(lastList.size)
                operations.add(MutableListOperation.RemoveAt(deletionIndex.toUInt()))
                results.add(lastList.subList(0, deletionIndex) + lastList.subList(deletionIndex + 1, lastList.size))
            }
        }
    }
    MutableListOperationWithResult(
        initialList,
        numberOfOperations,
        operations,
        results,
    )
}

class ListImplementationsTests: FunSpec({
    for (impl in listImplementations) context(impl.name) {
        val producer = impl.producer
        
        test("test generative construction") {
            checkAll(Exhaustive.ints(0 .. 20)) { length ->
                checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                    val list = producer.produceBy(length.toUInt()) { input[it.toInt()] }
                    impl.validator.shouldValidate(list)
                    testEquality(list, input)
                }
            }
        }
        
        if (producer is KoneResizableMutableListProducer) {
            test("test element-by-element extension") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>()
                        impl.validator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) {
                            list.add(input[index.toInt()])
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element reduction") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                        impl.validator.shouldValidate(list)
                        testEquality(list, input)
                        for (index in length - 1 downTo 0) {
                            list.removeAt(index.toUInt())
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index))
                        }
                    }
                }
            }
        }
        
        if (producer is KoneGrowableMutableListProducer) {
            test("test element-by-element extension") {
                checkAll(Exhaustive.ints(0..20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>()
                        impl.validator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) {
                            list.add(input[index.toInt()])
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element extension with ensured capacity") {
                checkAll(Exhaustive.ints(0..20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>(length.toUInt())
                        impl.validator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) {
                            list.add(input[index.toInt()])
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element reduction") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                        impl.validator.shouldValidate(list)
                        testEquality(list, input)
                        for (index in length - 1 downTo 0) {
                            list.removeAt(index.toUInt())
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index))
                        }
                    }
                }
            }
        }
        
        if (producer is KoneFixedCapacityMutableListProducer) {
            test("test element-by-element extension") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produce<UInt>(30u)
                        impl.validator.shouldValidate(list)
                        testEquality(list, emptyList())
                        for (index in 0 ..< length) withClue("at iteration $index") {
                            list.add(input[index.toInt()])
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index + 1))
                        }
                    }
                }
            }
            test("test element-by-element reduction") {
                checkAll(Exhaustive.ints(0 .. 20)) { length ->
                    checkAll(10, Arb.uInt().chunked(length, length)) { input ->
                        val list = producer.produceBy<UInt>(length.toUInt()) { input[it.toInt()] }
                        impl.validator.shouldValidate(list)
                        testEquality(list, input)
                        for (index in length - 1 downTo 0) {
                            list.removeAt(index.toUInt())
                            impl.validator.shouldValidate(list)
                            testEquality(list, input.subList(0, index))
                        }
                    }
                }
            }
        }
        
        fun <Element: Any> testKoneMutableListMutabilityOperationsOn(
            arbData: MutableListOperationWithResult<Element>,
            mutableList: KoneMutableList<Element>,
            validator: KoneListValidator,
        ) {
            repeat(arbData.numberOfOperations) {
                val operation = arbData.operations[it.toInt()]
                val expected = arbData.results[it.toInt()]
                withClue({ "at iteration $it with current state $mutableList, operation $operation, and expected result $expected" }) {
                    when (operation) {
                        is MutableListOperation.SetAt<Element> -> mutableList[operation.index] = operation.element
                        is MutableListOperation.AddAt<Element> -> mutableList.addAt(operation.index, operation.element)
                        is MutableListOperation.RemoveAt -> mutableList.removeAt(operation.index)
                    }
                    validator.shouldValidate(mutableList)
                    testEquality(mutableList, expected)
                }
            }
        }
        
        if (producer is KoneResizableMutableListProducer) test("test mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListMutabilityOperationsOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    validator = impl.validator,
                )
            }
        }
        
        if (producer is KoneGrowableMutableListProducer) {
            test("test mutability operations") {
                checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListMutabilityOperationsOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        validator = impl.validator,
                    )
                }
            }
            test("test mutability operations with ensured capacity") {
                checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListMutabilityOperationsOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        validator = impl.validator,
                    )
                }
            }
        }
        
        if (producer is KoneFixedCapacityMutableListProducer) test("test mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListMutabilityOperationsOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    validator = impl.validator,
                )
            }
        }
        
        fun <Element: Any> testKoneMutableListIteratorOn(
            arbData: MutableListOperationWithResult<Element>,
            mutableList: KoneMutableList<Element>,
            nextIteratorIndex: UInt,
            validator: KoneListValidator,
        ) {
            var nextIteratorIndex = nextIteratorIndex
            val iterator = mutableList.iteratorFrom(nextIteratorIndex)
            repeat(arbData.numberOfOperations) {
                val operation = arbData.operations[it.toInt()]
                val expected = arbData.results[it.toInt()]
                withClue("at iteration $it with current state $mutableList, current next iterator index $nextIteratorIndex, operation $operation, and expected result $expected") {
                    when (operation) {
                        is MutableListOperation.SetAt<Element> -> {
                            if (operation.index >= nextIteratorIndex) {
                                while (operation.index > nextIteratorIndex) {
                                    iterator.hasNext().shouldBeTrue()
                                    iterator.nextIndex() shouldBe nextIteratorIndex
                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
                                    iterator.moveNext()
                                    nextIteratorIndex++
                                }
                                iterator.hasNext().shouldBeTrue()
                                iterator.nextIndex() shouldBe operation.index
                                iterator.setNext(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                iterator.hasNext().shouldBeTrue()
                                iterator.nextIndex() shouldBe operation.index
                            } else {
                                while (operation.index < nextIteratorIndex - 1u) {
                                    iterator.hasPrevious().shouldBeTrue()
                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
                                    iterator.movePrevious()
                                    nextIteratorIndex--
                                }
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.previousIndex() shouldBe operation.index
                                iterator.setPrevious(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.previousIndex() shouldBe operation.index
                            }
                        }
                        is MutableListOperation.AddAt<Element> -> {
                            if (operation.index >= nextIteratorIndex) {
                                while (operation.index > nextIteratorIndex) {
                                    iterator.hasNext().shouldBeTrue()
                                    iterator.nextIndex() shouldBe nextIteratorIndex
                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
                                    iterator.moveNext()
                                    nextIteratorIndex++
                                }
                                iterator.addNext(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                iterator.hasNext().shouldBeTrue()
                                iterator.getNext() shouldBe operation.element
                                iterator.nextIndex() shouldBe nextIteratorIndex
                            } else {
                                while (operation.index < nextIteratorIndex) {
                                    iterator.hasPrevious().shouldBeTrue()
                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
                                    iterator.movePrevious()
                                    nextIteratorIndex--
                                }
                                iterator.addPrevious(operation.element)
                                validator.shouldValidate(mutableList, iterator)
                                nextIteratorIndex++
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.getPrevious() shouldBe operation.element
                                iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                            }
                        }
                        is MutableListOperation.RemoveAt -> {
                            if (operation.index >= nextIteratorIndex) {
                                while (operation.index > nextIteratorIndex) {
                                    iterator.hasNext().shouldBeTrue()
                                    iterator.nextIndex() shouldBe nextIteratorIndex
                                    iterator.getNext() shouldBe mutableList[nextIteratorIndex]
                                    iterator.moveNext()
                                    nextIteratorIndex++
                                }
                                iterator.hasNext().shouldBeTrue()
                                iterator.nextIndex() shouldBe operation.index
                                iterator.removeNext()
                                validator.shouldValidate(mutableList, iterator)
                            } else {
                                while (operation.index < nextIteratorIndex - 1u) {
                                    iterator.hasPrevious().shouldBeTrue()
                                    iterator.previousIndex() shouldBe nextIteratorIndex - 1u
                                    iterator.getPrevious() shouldBe mutableList[nextIteratorIndex - 1u]
                                    iterator.movePrevious()
                                    nextIteratorIndex--
                                }
                                iterator.hasPrevious().shouldBeTrue()
                                iterator.previousIndex() shouldBe operation.index
                                iterator.removePrevious()
                                validator.shouldValidate(mutableList, iterator)
                                nextIteratorIndex--
                            }
                        }
                    }
                    testEquality(mutableList, expected)
                }
            }
        }
        
        if (producer is KoneResizableMutableListProducer) test("test iterator mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListIteratorOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    nextIteratorIndex = 5u,
                    validator = impl.validator,
                )
            }
        }
        
        if (producer is KoneGrowableMutableListProducer) {
            test("test iterator mutability operations") {
                checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy<UInt>(arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListIteratorOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        nextIteratorIndex = 5u,
                        validator = impl.validator,
                    )
                }
            }
            test("test iterator mutability operations with ensured capacity") {
                checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                    val mutableList = producer.produceBy<UInt>(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                    testKoneMutableListIteratorOn(
                        arbData = arbData,
                        mutableList = mutableList,
                        nextIteratorIndex = 5u,
                        validator = impl.validator,
                    )
                }
            }
        }
        
        if (producer is KoneFixedCapacityMutableListProducer) test("test iterator mutability operations") {
            checkAll(arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, capacity = 20u, numberOfOperations = 100u)) { arbData ->
                val mutableList = producer.produceBy(20u, arbData.initialList.size.toUInt()) { arbData.initialList[it.toInt()] }
                testKoneMutableListIteratorOn(
                    arbData = arbData,
                    mutableList = mutableList,
                    nextIteratorIndex = 5u,
                    validator = impl.validator,
                )
            }
        }
    }
})