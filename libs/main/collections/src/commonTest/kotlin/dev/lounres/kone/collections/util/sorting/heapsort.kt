/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.util.sorting

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.utils.sorting.heapsort
import dev.lounres.kone.collections.utils.sorting.heapsortBy
import dev.lounres.kone.collections.utils.sorting.heapsortByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortDescending
import dev.lounres.kone.collections.utils.sorting.heapsortWith
import dev.lounres.kone.collections.utils.sorting.heapsortWithBy
import dev.lounres.kone.collections.utils.sorting.heapsortWithByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortWithDescending
import dev.lounres.kone.collections.utils.sorting.heapsorted
import dev.lounres.kone.collections.utils.sorting.heapsortedBy
import dev.lounres.kone.collections.utils.sorting.heapsortedByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortedDescending
import dev.lounres.kone.collections.utils.sorting.heapsortedWith
import dev.lounres.kone.collections.utils.sorting.heapsortedWithBy
import dev.lounres.kone.collections.utils.sorting.heapsortedWithByDescending
import dev.lounres.kone.collections.utils.sorting.heapsortedWithDescending
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Order
import dev.lounres.kone.context


object HeapsortingDescription : SortingDescription {
    override val name: String = "heapsort"
    override val applier: SortingApplier = object : SortingApplier {
        override fun <E: Comparable<E>> sort(list: KoneSettableList<E>) = list.heapsort()
        override fun <E> sort(order: Order<E>, list: KoneSettableList<E>) = context(order) { list.heapsort() }
        override fun <E> sortWith(list: KoneSettableList<E>, comparator: Comparator<E>) = list.heapsortWith(comparator)
        override fun <E: Comparable<E>> sortDescending(list: KoneSettableList<E>) = list.heapsortDescending()
        override fun <E> sortDescending(order: Order<E>, list: KoneSettableList<E>) = context(order) { list.heapsortDescending() }
        override fun <E> sortWithDescending(list: KoneSettableList<E>, comparator: Comparator<E>) = list.heapsortWithDescending(comparator)
        override fun <E, R: Comparable<R>> sortBy(list: KoneSettableList<E>, selector: (E) -> R) = list.heapsortBy(selector)
        override fun <E, R> sortBy(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R) = context(order) { list.heapsortBy(selector) }
        override fun <E, R> sortWithBy(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R) = list.heapsortWithBy(comparator, selector)
        override fun <E, R: Comparable<R>> sortByDescending(list: KoneSettableList<E>, selector: (E) -> R) = list.heapsortByDescending(selector)
        override fun <E, R> sortByDescending(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R) = context(order) { list.heapsortByDescending(selector) }
        override fun <E, R> sortWithByDescending(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R) = list.heapsortWithByDescending(comparator, selector)
        
        override fun <E: Comparable<E>> sorted(list: KoneIterable<E>): KoneList<E> = list.heapsorted()
        override fun <E> sorted(order: Order<E>, list: KoneIterable<E>): KoneList<E> = context(order) { list.heapsorted() }
        override fun <E> sortedWith(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E> = list.heapsortedWith(comparator)
        override fun <E: Comparable<E>> sortedDescending(list: KoneIterable<E>): KoneList<E> = list.heapsortedDescending()
        override fun <E> sortedDescending(order: Order<E>, list: KoneIterable<E>): KoneList<E> = context(order) { list.heapsortedDescending() }
        override fun <E> sortedWithDescending(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E> = list.heapsortedWithDescending(comparator)
        override fun <E, R: Comparable<R>> sortedBy(list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = list.heapsortedBy(selector)
        override fun <E, R> sortedBy(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = context(order) { list.heapsortedBy(selector) }
        override fun <E, R> sortedWithBy(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = list.heapsortedWithBy(comparator, selector)
        override fun <E, R: Comparable<R>> sortedByDescending(list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = list.heapsortedByDescending(selector)
        override fun <E, R> sortedByDescending(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = context(order) { list.heapsortedByDescending(selector) }
        override fun <E, R> sortedWithByDescending(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = list.heapsortedWithByDescending(comparator, selector)
    }
}