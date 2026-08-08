/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.util.sorting

import de.infix.testBalloon.framework.core.TestSuite
import de.infix.testBalloon.framework.core.testSuite
import de.infix.testBalloon.framework.shared.TestRegistering
import dev.lounres.kone.assertions.AssertionScope
import dev.lounres.kone.assertions.Expect
import dev.lounres.kone.assertions.fail
import dev.lounres.kone.assertions.of
import dev.lounres.kone.assertions.softly
import dev.lounres.kone.assertions.toBe
import dev.lounres.kone.assertions.withClue
import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterator.next
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
//    QuicksortingDescription,
    DefaultDescription,
)

context(assertionScope: AssertionScope)
fun <Element> testEquality(list1: KoneList<Element>, list2: KoneList<Element>) {
    withClue("Checking equality of lists $list1 and $list2") {
        val list1Iterator = list1.iterator()
        val list2Iterator = list2.iterator()
        var index = 0u
        while (true) {
            if (list1Iterator.hasNext() != list2Iterator.hasNext()) fail("List iterators ended not at the same time $index")
            if (list1Iterator.hasNext()) {
                withClue({ "Checking equality of elements at index $index" }) {
                    Expect of list1Iterator.getNext() toBe list2Iterator.getNext()
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
        context(assertionScope: AssertionScope)
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
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sort(it)
                }
            }
        }
        testSuite("sort ordered") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sort(Order.defaultFor(), it)
                }
            }
        }
        testSuite("sort with comparator") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortWith(it, Comparator.defaultFor())
                }
            }
        }
        testSuite("sort descending comparable") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortDescending(it)
                    it.reverse()
                }
            }
        }
        testSuite("sort descending ordered") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortDescending(Order.defaultFor(), it)
                    it.reverse()
                }
            }
        }
        testSuite("sort descending with comparator") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortWithDescending(it, Comparator.defaultFor())
                    it.reverse()
                }
            }
        }
        testSuite("sort by comparable") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortBy(it) { it }
                }
            }
        }
        testSuite("sort by ordered") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortBy(Order.defaultFor(), it) { it }
                }
            }
        }
        testSuite("sort by with comparator") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortWithBy(it, Comparator.defaultFor()) { it }
                }
            }
        }
        testSuite("sort by descending comparable") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortByDescending(it) { it }
                    it.reverse()
                }
            }
        }
        testSuite("sort by descending ordered") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortByDescending(Order.defaultFor(), it) { it }
                    it.reverse()
                }
            }
        }
        testSuite("sort by descending with comparator") {
            AssertionScope.softly {
                testSortFunction {
                    desc.applier.sortWithByDescending(it, Comparator.defaultFor()) { it }
                    it.reverse()
                }
            }
        }
        
        @TestRegistering
        context(assertionScope: AssertionScope)
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
            AssertionScope.softly {
                testSortedFunction { desc.applier.sorted(it) }
            }
        }
        testSuite("sorted ordered") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sorted(Order.defaultFor(), it) }
            }
        }
        testSuite("sorted with comparator") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedWith(it, Comparator.defaultFor()) }
            }
        }
        testSuite("sorted descending comparable") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedDescending(it).reversed() }
            }
        }
        testSuite("sorted descending ordered") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedDescending(Order.defaultFor(), it).reversed() }
            }
        }
        testSuite("sorted descending with comparator") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedWithDescending(it, Comparator.defaultFor()).reversed() }
            }
        }
        testSuite("sorted by comparable") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedBy(it) { it } }
            }
        }
        testSuite("sorted by ordered") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedBy(Order.defaultFor(), it) { it } }
            }
        }
        testSuite("sorted by with comparator") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedWithBy(it, Comparator.defaultFor()) { it } }
            }
        }
        testSuite("sorted by descending comparable") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedByDescending(it) { it }.reversed() }
            }
        }
        testSuite("sorted by descending ordered") {
            AssertionScope.softly {
                testSortedFunction { desc.applier.sortedByDescending(Order.defaultFor(), it) { it }.reversed() }
            }
        }
        testSuite("sorted by descending with comparator") {
            AssertionScope.softly {
                testSortedFunction {
                    desc.applier.sortedWithByDescending(it, Comparator.defaultFor()) { it }.reversed()
                }
            }
        }
    }
}