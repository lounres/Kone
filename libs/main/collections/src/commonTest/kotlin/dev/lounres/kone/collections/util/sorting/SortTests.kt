/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.util.sorting

import de.infix.testBalloon.framework.core.TestSuite
import de.infix.testBalloon.framework.core.testSuite
import de.infix.testBalloon.framework.shared.TestRegistering
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.toKoneSettableList
import dev.lounres.kone.collections.utils.reverse
import dev.lounres.kone.collections.utils.reversed
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.defaultFor
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import kotlin.test.fail


interface SortingApplier {
    fun <E: Comparable<E>> sort(list: KoneSettableList<E>)
    fun <E> sort(order: Order<E>, list: KoneSettableList<E>)
    fun <E> sortWith(list: KoneSettableList<E>, comparator: Comparator<E>)
    fun <E: Comparable<E>> sortDescending(list: KoneSettableList<E>)
    fun <E> sortDescending(order: Order<E>, list: KoneSettableList<E>)
    fun <E> sortWithDescending(list: KoneSettableList<E>, comparator: Comparator<E>)
    fun <E, R: Comparable<R>> sortBy(list: KoneSettableList<E>, selector: (E) -> R)
    fun <E, R> sortBy(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R)
    fun <E, R> sortWithBy(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R)
    fun <E, R: Comparable<R>> sortByDescending(list: KoneSettableList<E>, selector: (E) -> R)
    fun <E, R> sortByDescending(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R)
    fun <E, R> sortWithByDescending(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R)
    
    fun <E: Comparable<E>> sorted(list: KoneIterable<E>): KoneList<E>
    fun <E> sorted(order: Order<E>, list: KoneIterable<E>): KoneList<E>
    fun <E> sortedWith(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E>
    fun <E: Comparable<E>> sortedDescending(list: KoneIterable<E>): KoneList<E>
    fun <E> sortedDescending(order: Order<E>, list: KoneIterable<E>): KoneList<E>
    fun <E> sortedWithDescending(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E>
    fun <E, R: Comparable<R>> sortedBy(list: KoneIterable<E>, selector: (E) -> R): KoneList<E>
    fun <E, R> sortedBy(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E>
    fun <E, R> sortedWithBy(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E>
    fun <E, R: Comparable<R>> sortedByDescending(list: KoneIterable<E>, selector: (E) -> R): KoneList<E>
    fun <E, R> sortedByDescending(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E>
    fun <E, R> sortedWithByDescending(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E>
}

interface SortingDescription {
    val name: String
    val applier: SortingApplier
}

val sortings = listOf<SortingDescription>(
    HeapsortingDescription,
    QuicksortingDescription,
    DefaultDescription,
)

fun <Element> testEquality(list1: KoneList<Element>, list2: KoneList<Element>) {
    withClue("Checking equality of lists $list1 and $list2") {
        val list1Iterator = list1.iterator()
        val list2Iterator = list2.iterator()
        var index = 0u
        while (true) {
            if (list1Iterator.hasNext() != list2Iterator.hasNext()) fail("List iterators ended not at the same time $index")
            if (list1Iterator.hasNext()) {
                withClue({ "Checking equality of elements at index $index" }) {
                    list1Iterator.getNext() shouldBe list2Iterator.getNext()
                }
                list1Iterator.moveNext()
                list2Iterator.moveNext()
                index++
                continue
            }
            break
        }
    }
}

val SortTests by testSuite {
    val listsToShuffle = listOf(KoneList.of(0u, 0u, 2u, 4u, 4u, 4u), KoneList.of(0u, 1u, 2u, 3u), KoneList.of(0u, 1u, 2u, 3u, 4u))
    
    for (desc in sortings) testSuite(desc.name) {
        
        @TestRegistering
        /*inline*/ fun TestSuite.testSortFunction(
            /*crossinline*/ sort: (list: KoneSettableList<UInt>) -> Unit,
        ) {
            for (init in listsToShuffle) testSuite(init.toString()) {
                val permutationsExhaustive = init.permutationsWithoutRepetitions()
                for (input in permutationsExhaustive) test(input.toString()) {
                    val target = input.toKoneSettableList()
                    
                    sort(target)
                    
                    testEquality(target, init)
                }
            }
        }
        
        testSuite("sort comparable") {
            testSortFunction {
                desc.applier.sort(it)
            }
        }
        testSuite("sort ordered") {
            testSortFunction {
                desc.applier.sort(Order.defaultFor(), it)
            }
        }
        testSuite("sort with comparator") {
            testSortFunction {
                desc.applier.sortWith(it, Comparator.defaultFor())
            }
        }
        testSuite("sort descending comparable") {
            testSortFunction {
                desc.applier.sortDescending(it)
                it.reverse()
            }
        }
        testSuite("sort descending ordered") {
            testSortFunction {
                desc.applier.sortDescending(Order.defaultFor(), it)
                it.reverse()
            }
        }
        testSuite("sort descending with comparator") {
            testSortFunction {
                desc.applier.sortWithDescending(it, Comparator.defaultFor())
                it.reverse()
            }
        }
        testSuite("sort by comparable") {
            testSortFunction {
                desc.applier.sortBy(it) { it }
            }
        }
        testSuite("sort by ordered") {
            testSortFunction {
                desc.applier.sortBy(Order.defaultFor(), it) { it }
            }
        }
        testSuite("sort by with comparator") {
            testSortFunction {
                desc.applier.sortWithBy(it, Comparator.defaultFor()) { it }
            }
        }
        testSuite("sort by descending comparable") {
            testSortFunction {
                desc.applier.sortByDescending(it) { it }
                it.reverse()
            }
        }
        testSuite("sort by descending ordered") {
            testSortFunction {
                desc.applier.sortByDescending(Order.defaultFor(), it) { it }
                it.reverse()
            }
        }
        testSuite("sort by descending with comparator") {
            testSortFunction {
                desc.applier.sortWithByDescending(it, Comparator.defaultFor()) { it }
                it.reverse()
            }
        }
        
        @TestRegistering
        /*inline*/ fun TestSuite.testSortedFunction(
            /*crossinline*/ sort: (list: KoneIterable<UInt>) -> KoneList<UInt>,
        ) {
            for (init in listsToShuffle) testSuite(init.toString()) {
                val permutationsExhaustive = init.permutationsWithoutRepetitions()
                for (input in permutationsExhaustive) test(input.toString()) {
                    val result = sort(input)
                    
                    testEquality(result, init)
                }
            }
        }
        
        testSuite("sorted comparable") {
            testSortedFunction { desc.applier.sorted(it) }
        }
        testSuite("sorted ordered") {
            testSortedFunction { desc.applier.sorted(Order.defaultFor(), it) }
        }
        testSuite("sorted with comparator") {
            testSortedFunction { desc.applier.sortedWith(it, Comparator.defaultFor()) }
        }
        testSuite("sorted descending comparable") {
            testSortedFunction { desc.applier.sortedDescending(it).reversed() }
        }
        testSuite("sorted descending ordered") {
            testSortedFunction { desc.applier.sortedDescending(Order.defaultFor(), it).reversed() }
        }
        testSuite("sorted descending with comparator") {
            testSortedFunction { desc.applier.sortedWithDescending(it, Comparator.defaultFor()).reversed() }
        }
        testSuite("sorted by comparable") {
            testSortedFunction { desc.applier.sortedBy(it) { it } }
        }
        testSuite("sorted by ordered") {
            testSortedFunction { desc.applier.sortedBy(Order.defaultFor(), it) { it } }
        }
        testSuite("sorted by with comparator") {
            testSortedFunction { desc.applier.sortedWithBy(it, Comparator.defaultFor()) { it } }
        }
        testSuite("sorted by descending comparable") {
            testSortedFunction { desc.applier.sortedByDescending(it) { it }.reversed() }
        }
        testSuite("sorted by descending ordered") {
            testSortedFunction { desc.applier.sortedByDescending(Order.defaultFor(), it) { it }.reversed() }
        }
        testSuite("sorted by descending with comparator") {
            testSortedFunction { desc.applier.sortedWithByDescending(it, Comparator.defaultFor()) { it }.reversed() }
        }
    }
}