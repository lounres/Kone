/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListDisposabilityTest
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.repeat
import dev.lounres.kone.scope


object KoneArrayFixedCapacityLinkedListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayFixedCapacityLinkedList"
    
    internal object Validator {
        context(assertionScope: AssertionScope)
        fun <Element: Any> validate(
            list: KoneArrayFixedCapacityLinkedList<Element>,
        ) {
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
        
        context(assertionScope: AssertionScope)
        fun validateWithIterator(
            list: KoneArrayFixedCapacityLinkedList<Any>,
            iterator: KoneArrayFixedCapacityLinkedList.Iterator<Any>,
        ) {
            validate(list)
            
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
    }
    
    override val listProducer: KoneListProducer get() = KoneArrayFixedCapacityLinkedListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        context(assertionScope: AssertionScope)
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayFixedCapacityLinkedList<Any>) {
                fail("The list is invalid")
                return
            }
            Validator.validate(list)
        }
        
        context(assertionScope: AssertionScope)
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            if (list !is KoneArrayFixedCapacityLinkedList<Any>) {
                fail("The list is invalid")
                return
            }
            if (iterator !is KoneArrayFixedCapacityLinkedList.Iterator<Any>) {
                fail("The iterator is invalid")
                return
            }
            Validator.validateWithIterator(list, iterator)
        }
    }
    override val listDisposabilityTest: ListDisposabilityTest = object : ListDisposabilityTest {
        context(assertionScope: AssertionScope)
        override fun <Element : Any> test(list: KoneList<Element>) {
            if (list !is KoneArrayFixedCapacityLinkedList<Element>) {
                fail("The list is invalid")
                return
            }
            
            TODO()
            
            list.dispose()
        }
    }
}