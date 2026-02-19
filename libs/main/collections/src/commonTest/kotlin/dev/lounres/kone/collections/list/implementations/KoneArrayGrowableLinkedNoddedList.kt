/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list.implementations

import dev.lounres.kone.algebraic.primaryFor
import dev.lounres.kone.collections.implementations.POWERS_OF_2
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneListValidator
import dev.lounres.kone.collections.list.ListImplementationDescription
import dev.lounres.kone.collections.list.contexts.KoneListProducer
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.repeat
import dev.lounres.kone.scope
import kotlin.test.fail


object KoneArrayGrowableLinkedNoddedListDescription : ListImplementationDescription {
    override val name get() = "KoneArrayGrowableLinkedNoddedList"
    
    internal object Validator {
        fun <Element: Any> validate(
            list: KoneArrayGrowableLinkedNoddedList<Element>,
        ) {
            if (list.isDisposed) fail("The list is invalid")
            
            val sizeUpperBound = list.sizeUpperBound
            val size = list.size
            val start = list.start
            val end = list.end
            val nextNodeIndex = list.nextNodeIndex
            val previousNodeIndex = list.previousNodeIndex
            val data = list.data
            
            if ((Equality.primaryFor(UInt)) { sizeUpperBound !in POWERS_OF_2 }) fail("The list is invalid")
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
        
        fun validateWithIterator(
            list: KoneArrayGrowableLinkedNoddedList<Any>,
            iterator: KoneArrayGrowableLinkedNoddedList.Iterator<Any>,
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
    
    override val listProducer: KoneListProducer get() = KoneArrayGrowableLinkedNoddedListProducer
    override val listValidator: KoneListValidator = object : KoneListValidator {
        override fun validate(
            list: KoneList<Any>,
        ) {
            if (list !is KoneArrayGrowableLinkedNoddedList<Any>) fail("The list is invalid")
            Validator.validate(list)
        }
        
        override fun validateWithIterator(
            list: KoneList<Any>,
            iterator: KoneIterator<Any>,
        ) {
            if (list !is KoneArrayGrowableLinkedNoddedList<Any>) fail("The list is invalid")
            if (iterator !is KoneArrayGrowableLinkedNoddedList.Iterator<Any>) fail("The iterator is invalid")
            Validator.validateWithIterator(list, iterator)
        }
    }
}