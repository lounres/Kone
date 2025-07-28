/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.util.sorting

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.toKoneSettableList
import dev.lounres.kone.collections.utils.reverse
import dev.lounres.kone.collections.utils.reversed
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.defaultComparator
import dev.lounres.kone.relations.defaultOrder
import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Exhaustive
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.of


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

class SortTests : FunSpec({
    threads = 16
    concurrency = 16
    
    val listsToShuffle = Exhaustive.of(KoneList.of(0u, 0u, 2u, 4u, 4u, 4u), KoneList.of(0u, 1u, 2u, 3u), KoneList.of(0u, 1u, 2u, 3u, 4u))
    
    for (desc in sortings) context(desc.name) {
        
        suspend /*inline*/ fun testSortFunction(
            /*crossinline*/ sort: (list: KoneSettableList<UInt>) -> Unit,
        ) {
            checkAll(listsToShuffle) { init ->
                val permutationsExhaustive = init.permutationsWithoutRepetitions().toList().exhaustive()
                checkAll(permutationsExhaustive) { input ->
                    val target = input.toKoneSettableList()
                    
                    sort(target)
                    
                    testEquality(target, init)
                }
            }
        }
        
        test("sort comparable") {
            testSortFunction {
                desc.applier.sort(it)
            }
        }
        test("sort ordered") {
            testSortFunction {
                desc.applier.sort(defaultOrder(), it)
            }
        }
        test("sort with comparator") {
            testSortFunction {
                desc.applier.sortWith(it, defaultComparator())
            }
        }
        test("sort descending comparable") {
            testSortFunction {
                desc.applier.sortDescending(it)
                it.reverse()
            }
        }
        test("sort descending ordered") {
            testSortFunction {
                desc.applier.sortDescending(defaultOrder(), it)
                it.reverse()
            }
        }
        test("sort descending with comparator") {
            testSortFunction {
                desc.applier.sortWithDescending(it, defaultComparator())
                it.reverse()
            }
        }
        test("sort by comparable") {
            testSortFunction {
                desc.applier.sortBy(it) { it }
            }
        }
        test("sort by ordered") {
            testSortFunction {
                desc.applier.sortBy(defaultOrder(), it) { it }
            }
        }
        test("sort by with comparator") {
            testSortFunction {
                desc.applier.sortWithBy(it, defaultComparator()) { it }
            }
        }
        test("sort by descending comparable") {
            testSortFunction {
                desc.applier.sortByDescending(it) { it }
                it.reverse()
            }
        }
        test("sort by descending ordered") {
            testSortFunction {
                desc.applier.sortByDescending(defaultOrder(), it) { it }
                it.reverse()
            }
        }
        test("sort by descending with comparator") {
            testSortFunction {
                desc.applier.sortWithByDescending(it, defaultComparator()) { it }
                it.reverse()
            }
        }
        
        suspend /*inline*/ fun testSortedFunction(
            /*crossinline*/ sort: (list: KoneIterable<UInt>) -> KoneList<UInt>,
        ) {
            checkAll(listsToShuffle) { init ->
                val permutationsExhaustive = init.permutationsWithoutRepetitions().toList().exhaustive()
                checkAll(permutationsExhaustive) { input ->
                    val result = sort(input)
                    
                    testEquality(result, init)
                }
            }
        }
        
        test("sorted comparable") {
            testSortedFunction { desc.applier.sorted(it) }
        }
        test("sorted ordered") {
            testSortedFunction { desc.applier.sorted(defaultOrder(), it) }
        }
        test("sorted with comparator") {
            testSortedFunction { desc.applier.sortedWith(it, defaultComparator()) }
        }
        test("sorted descending comparable") {
            testSortedFunction { desc.applier.sortedDescending(it).reversed() }
        }
        test("sorted descending ordered") {
            testSortedFunction { desc.applier.sortedDescending(defaultOrder(), it).reversed() }
        }
        test("sorted descending with comparator") {
            testSortedFunction { desc.applier.sortedWithDescending(it, defaultComparator()).reversed() }
        }
        test("sorted by comparable") {
            testSortedFunction { desc.applier.sortedBy(it) { it } }
        }
        test("sorted by ordered") {
            testSortedFunction { desc.applier.sortedBy(defaultOrder(), it) { it } }
        }
        test("sorted by with comparator") {
            testSortedFunction { desc.applier.sortedWithBy(it, defaultComparator()) { it } }
        }
        test("sorted by descending comparable") {
            testSortedFunction { desc.applier.sortedByDescending(it) { it }.reversed() }
        }
        test("sorted by descending ordered") {
            testSortedFunction { desc.applier.sortedByDescending(defaultOrder(), it) { it }.reversed() }
        }
        test("sorted by descending with comparator") {
            testSortedFunction { desc.applier.sortedWithByDescending(it, defaultComparator()) { it }.reversed() }
        }
    }
})