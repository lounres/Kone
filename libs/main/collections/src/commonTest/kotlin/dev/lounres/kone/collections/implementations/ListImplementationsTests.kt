/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.contains
import dev.lounres.kone.collections.producers.KoneFixedCapacityMutableListProducer
import dev.lounres.kone.collections.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.producers.KoneListProducer
import dev.lounres.kone.collections.producers.KoneResizableMutableListProducer
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.context.invoke
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.PropTestConfig
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.chunked
import io.kotest.property.arbitrary.uInt
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.ints
import kotlin.test.fail


interface KoneListValidator {
    fun <Element: Any> validate(
        list: KoneList<Element>,
    ): Boolean
    
    fun <Element: Any> validateWithIterator(
        list: KoneList<Element>,
        iterator: KoneIterator<Element>,
    ): Boolean
}

fun <Element: Any> KoneListValidator.shouldValidate(list: KoneList<Element>) {
    this should Matcher {
        MatcherResult(
            it.validate(list),
            { "The list is invalid" },
            { "The list is valid" }
        )
    }
}
fun <Element: Any> KoneListValidator.shouldValidate(list: KoneList<Element>, iterator: KoneIterator<Element>) {
    this should Matcher {
        MatcherResult(
            it.validateWithIterator(list, iterator),
            { "The list or the iterator is invalid" },
            { "The list and the iterator are valid" }
        )
    }
}

data class ListImplementationDescription (
    val name: String,
    val producer: KoneListProducer,
    val validator: KoneListValidator = object : KoneListValidator {
        override fun <Element: Any> validate(
            list: KoneList<Element>,
        ): Boolean = true
        
        override fun <Element: Any> validateWithIterator(
            list: KoneList<Element>,
            iterator: KoneIterator<Element>,
        ): Boolean = true
    },
)

// TODO: Add missing list validators
val listImplementations = listOf<ListImplementationDescription>(
    // Array fixed capacity implementations.
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityLinkedList",
        producer = KoneArrayFixedCapacityLinkedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayFixedCapacityLinkedList<Element>) return false
                if (list.isDisposed) return false
                
                val capacity = list.capacity
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data
                
                if (size > capacity) return false
                if (data.size != capacity || nextNodeIndex.size != capacity || previousNodeIndex.size != capacity) return false
                if (nextNodeIndex.any { it !in 0u..<capacity } || previousNodeIndex.any { it !in 0u..<capacity }) return false
                
                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(capacity) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == capacity - 1u)) return false
                    }
                }
                
                repeat(capacity) { if (previousNodeIndex[nextNodeIndex[it]] != it) return false }
                
                scope {
                    var currentIndex = start
                    repeat(capacity) { iteration ->
                        if ((iteration == (size + capacity - 1u).mod(capacity)) != (currentIndex == end)) return false
                        if ((iteration < size) != (data[currentIndex] != null)) return false
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayFixedCapacityLinkedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) return false
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityLinkedNoddedList",
        producer = KoneArrayFixedCapacityLinkedNoddedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayFixedCapacityLinkedNoddedList<Element>) return false
                if (list.isDisposed) return false
                
                val capacity = list.capacity
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data
                
                if (size > capacity) return false
                if (data.size != capacity || nextNodeIndex.size != capacity || previousNodeIndex.size != capacity) return false
                if (nextNodeIndex.any { it !in 0u..<capacity } || previousNodeIndex.any { it !in 0u..<capacity }) return false
                
                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(capacity) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == capacity - 1u)) return false
                    }
                }
                
                repeat(capacity) { if (previousNodeIndex[nextNodeIndex[it]] != it) return false }
                
                scope {
                    var currentIndex = start
                    repeat(capacity) { iteration ->
                        if ((iteration == (size + capacity - 1u).mod(capacity)) != (currentIndex == end)) return false
                        val currentNodeOrNull = data[currentIndex]
                        if (iteration < size) {
                            if (currentNodeOrNull == null) return false
                            if (currentNodeOrNull.actualIndex != currentIndex) return false
                            if (currentNodeOrNull.isDetached) return false
                            if (currentNodeOrNull.list !== list) return false
                        } else {
                            if (currentNodeOrNull != null) return false
                        }
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayFixedCapacityLinkedNoddedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) return false
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityList",
        producer = KoneArrayFixedCapacityListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayFixedCapacityList<Element>) return false
                if (list.isDisposed) return false
                
                val size = list.size
                val data = list.data
                
                if (size > data.size) return false
                
                repeat(data.size) { index ->
                    if ((index < size) != (data[index] != null)) return false
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayFixedCapacityList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                if (iterator.currentIndex > list.size) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayFixedCapacityNoddedList",
        producer = KoneArrayFixedCapacityNoddedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayFixedCapacityNoddedList<Element>) return false
                if (list.isDisposed) return false
                
                val size = list.size
                val data = list.data
                
                if (size > data.size) return false
                
                repeat(data.size) { index ->
                    if (index < size) {
                        val node = data[index]
                        if (node == null) return false
                        if (node.list !== list) return false
                        if (node.index != index) return false
                    } else {
                        if (data[index] != null) return false
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayFixedCapacityNoddedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                if (iterator.currentIndex > list.size) return false
                
                return true
            }
        },
    ),
    // Array growable implementations.
    ListImplementationDescription(
        name = "KoneArrayGrowableLinkedList",
        producer = KoneArrayGrowableLinkedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayGrowableLinkedList<Element>) return false
                if (list.isDisposed) return false
                
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data
                
                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) return false
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) return false
                
                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) return false
                    }
                }
                
                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) return false }
                
                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) return false
                        if ((iteration < size) != (data[currentIndex] != null)) return false
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayGrowableLinkedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) return false
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayGrowableLinkedNoddedList",
        producer = KoneArrayGrowableLinkedNoddedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayGrowableLinkedNoddedList<Element>) return false
                if (list.isDisposed) return false
                
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data
                
                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) return false
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) return false
                
                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) return false
                    }
                }
                
                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) return false }
                
                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) return false
                        val currentNodeOrNull = data[currentIndex]
                        if (iteration < size) {
                            if (currentNodeOrNull == null) return false
                            if (currentNodeOrNull.actualIndex != currentIndex) return false
                            if (currentNodeOrNull.isDetached) return false
                            if (currentNodeOrNull.list !== list) return false
                        } else {
                            if (currentNodeOrNull != null) return false
                        }
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayGrowableLinkedNoddedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) return false
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayGrowableList",
        producer = KoneArrayGrowableListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayGrowableList<Element>) return false
                if (list.isDisposed) return false
                
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data
                
                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound) return false
                
                repeat(sizeUpperBound) { index ->
                    if ((index < size) != (data[index] != null)) return false
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayGrowableList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                if (iterator.currentIndex > list.size) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayGrowableNoddedList",
        producer = KoneArrayGrowableNoddedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayGrowableNoddedList<Element>) return false
                if (list.isDisposed) return false
                
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data
                
                if (UInt.context { sizeUpperBound !in POWERS_OF_2 }) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound) return false
                
                repeat(sizeUpperBound) { index ->
                    val currentNodeOrNull = data[index]
                    if (index < size) {
                        if (currentNodeOrNull == null) return false
                        if (currentNodeOrNull.index != index) return false
                        if (currentNodeOrNull.isDetached) return false
                        if (currentNodeOrNull.list !== list) return false
                    } else {
                        if (currentNodeOrNull != null) return false
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayGrowableNoddedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                if (iterator.currentIndex > list.size) return false
                
                return true
            }
        },
    ),
    // Array resizable implementations.
    ListImplementationDescription(
        name = "KoneArrayResizableLinkedList",
        producer = KoneArrayResizableLinkedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayResizableLinkedList<Element>) return false
                if (list.isDisposed) return false
                
                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data
                
                if (dataSizeNumber !in 1u..31u) return false
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) return false
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) return false
                
                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) return false
                    }
                }
                
                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) return false }
                
                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) return false
                        if ((iteration < size) != (data[currentIndex] != null)) return false
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayResizableLinkedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) return false
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayResizableLinkedNoddedList",
        producer = KoneArrayResizableLinkedNoddedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayResizableLinkedNoddedList<Element>) return false
                if (list.isDisposed) return false
                
                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val start = list.start
                val end = list.end
                val nextNodeIndex = list.nextNodeIndex
                val previousNodeIndex = list.previousNodeIndex
                val data = list.data
                
                if (dataSizeNumber !in 1u..31u) return false
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound || nextNodeIndex.size != sizeUpperBound || previousNodeIndex.size != sizeUpperBound) return false
                if (nextNodeIndex.any { it !in 0u..<sizeUpperBound } || previousNodeIndex.any { it !in 0u..<sizeUpperBound }) return false
                
                scope {
                    var tortoise = 0u
                    var hare = 0u
                    repeat(sizeUpperBound) { iteration ->
                        tortoise = nextNodeIndex[tortoise]
                        hare = nextNodeIndex[nextNodeIndex[hare]]
                        if ((tortoise == hare) != (iteration == sizeUpperBound - 1u)) return false
                    }
                }
                
                repeat(sizeUpperBound) { if (previousNodeIndex[nextNodeIndex[it]] != it) return false }
                
                scope {
                    var currentIndex = start
                    repeat(sizeUpperBound) { iteration ->
                        if ((iteration == (size + sizeUpperBound - 1u).mod(sizeUpperBound)) != (currentIndex == end)) return false
                        val currentNodeOrNull = data[currentIndex]
                        if (iteration < size) {
                            if (currentNodeOrNull == null) return false
                            if (currentNodeOrNull.actualIndex != currentIndex) return false
                            if (currentNodeOrNull.isDetached) return false
                            if (currentNodeOrNull.list !== list) return false
                        } else {
                            if (currentNodeOrNull != null) return false
                        }
                        currentIndex = nextNodeIndex[currentIndex]
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayResizableLinkedNoddedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                val currentIndex = iterator.currentIndex
                if (currentIndex > list.size) return false
                val expectedActualCurrentIndex = scope {
                    var actualIndex = list.start
                    repeat(currentIndex) { actualIndex = list.nextNodeIndex[actualIndex] }
                    actualIndex
                }
                if (expectedActualCurrentIndex != iterator.actualCurrentIndex) return false
                
                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayResizableList",
        producer = KoneArrayResizableListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayResizableList<Element>) return false
                if (list.isDisposed) return false

                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data

                if (dataSizeNumber !in 1u..31u) return false
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound) return false

                repeat(sizeUpperBound) { index ->
                    if ((index < size) != (data[index] != null)) return false
                }

                return true
            }

            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false

                if (iterator !is KoneArrayResizableList.Iterator<Element>) return false
                if (iterator.list !== list) return false

                if (iterator.currentIndex > list.size) return false

                return true
            }
        },
    ),
    ListImplementationDescription(
        name = "KoneArrayResizableNoddedList",
        producer = KoneArrayResizableNoddedListProducer,
        validator = object : KoneListValidator {
            override fun <Element: Any> validate(
                list: KoneList<Element>,
            ): Boolean {
                if (list !is KoneArrayResizableNoddedList<Element>) return false
                if (list.isDisposed) return false
                
                val dataSizeNumber = list.dataSizeNumber
                val sizeLowerBound = list.sizeLowerBound
                val sizeUpperBound = list.sizeUpperBound
                val size = list.size
                val data = list.data
                
                if (dataSizeNumber !in 1u..31u) return false
                if (sizeLowerBound != POWERS_OF_2[dataSizeNumber - 1u] || sizeUpperBound != POWERS_OF_2[dataSizeNumber + 1u]) return false
                if (size > sizeUpperBound) return false
                if (data.size != sizeUpperBound) return false
                
                repeat(sizeUpperBound) { index ->
                    val currentNodeOrNull = data[index]
                    if (index < size) {
                        if (currentNodeOrNull == null) return false
                        if (currentNodeOrNull.index != index) return false
                        if (currentNodeOrNull.isDetached) return false
                        if (currentNodeOrNull.list !== list) return false
                    } else {
                        if (currentNodeOrNull != null) return false
                    }
                }
                
                return true
            }
            
            override fun <Element: Any> validateWithIterator(
                list: KoneList<Element>,
                iterator: KoneIterator<Element>,
            ): Boolean {
                if (!validate(list)) return false
                
                if (iterator !is KoneArrayResizableNoddedList.Iterator<Element>) return false
                if (iterator.list !== list) return false
                
                if (iterator.currentIndex > list.size) return false
                
                return true
            }
        },
    ),
    // Array settable implementations
    ListImplementationDescription(
        name = "KoneArraySettableList",
        producer = KoneArraySettableListProducer,
    ),
    ListImplementationDescription(
        name = "KoneArraySettableNoddedList",
        producer = KoneArraySettableNoddedListProducer,
    ),
    // GC (resizable) implementations
//    ListImplementationDescription(
//        name = "KoneGCLinkedList",
//        producer = KoneGCLinkedListProducer,
//    ),
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
        if ((capacity == null || capacity.toInt() > lastList.size) && (lastList.isEmpty() || source.random.nextBoolean())) {
            val newElement = arbElements.bind()
            val insertionIndex = source.random.nextInt(lastList.size + 1)
            operations.add(MutableListOperation.AddAt(insertionIndex.toUInt(), newElement))
            results.add(lastList.subList(0, insertionIndex) + newElement + lastList.subList(insertionIndex, lastList.size))
        } else {
            val deletionIndex = source.random.nextInt(lastList.size)
            operations.add(MutableListOperation.RemoveAt(deletionIndex.toUInt()))
            results.add(lastList.subList(0, deletionIndex) + lastList.subList(deletionIndex + 1, lastList.size))
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
            checkAll(PropTestConfig(seed = 8328161071279041350), arbMutableListOperationsWithResults(arbElements = Arb.uInt(), initialSize = 10u, numberOfOperations = 100u)) { arbData ->
                this
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
                this
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