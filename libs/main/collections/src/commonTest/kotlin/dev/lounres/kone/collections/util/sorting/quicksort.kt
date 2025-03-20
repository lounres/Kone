/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.util.sorting

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.utils.sorting.quicksort
import dev.lounres.kone.collections.utils.sorting.quicksortBy
import dev.lounres.kone.collections.utils.sorting.quicksortByDescending
import dev.lounres.kone.collections.utils.sorting.quicksortDescending
import dev.lounres.kone.collections.utils.sorting.quicksortWith
import dev.lounres.kone.collections.utils.sorting.quicksortWithBy
import dev.lounres.kone.collections.utils.sorting.quicksortWithByDescending
import dev.lounres.kone.collections.utils.sorting.quicksortWithDescending
import dev.lounres.kone.collections.utils.sorting.quicksorted
import dev.lounres.kone.collections.utils.sorting.quicksortedBy
import dev.lounres.kone.collections.utils.sorting.quicksortedByDescending
import dev.lounres.kone.collections.utils.sorting.quicksortedDescending
import dev.lounres.kone.collections.utils.sorting.quicksortedWith
import dev.lounres.kone.collections.utils.sorting.quicksortedWithBy
import dev.lounres.kone.collections.utils.sorting.quicksortedWithByDescending
import dev.lounres.kone.collections.utils.sorting.quicksortedWithDescending
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Order
import dev.lounres.kone.context


object QuicksortingDescription : SortingDescription {
    override val name: String = "quicksort"
    override val applier: SortingApplier = object : SortingApplier {
        override fun <E: Comparable<E>> sort(list: KoneSettableList<E>) = list.quicksort()
        override fun <E> sort(order: Order<E>, list: KoneSettableList<E>) = context(order) { list.quicksort() }
        override fun <E> sortWith(list: KoneSettableList<E>, comparator: Comparator<E>) = list.quicksortWith(comparator)
        override fun <E: Comparable<E>> sortDescending(list: KoneSettableList<E>) = list.quicksortDescending()
        override fun <E> sortDescending(order: Order<E>, list: KoneSettableList<E>) = context(order) { list.quicksortDescending() }
        override fun <E> sortWithDescending(list: KoneSettableList<E>, comparator: Comparator<E>) = list.quicksortWithDescending(comparator)
        override fun <E, R: Comparable<R>> sortBy(list: KoneSettableList<E>, selector: (E) -> R) = list.quicksortBy(selector)
        override fun <E, R> sortBy(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R) = context(order) { list.quicksortByDescending(selector) }
        override fun <E, R> sortWithBy(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R) = list.quicksortWithBy(comparator, selector)
        override fun <E, R: Comparable<R>> sortByDescending(list: KoneSettableList<E>, selector: (E) -> R) = list.quicksortByDescending(selector)
        override fun <E, R> sortByDescending(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R) = context(order) { list.quicksortByDescending(selector) }
        override fun <E, R> sortWithByDescending(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R) = list.quicksortWithByDescending(comparator, selector)
        
        override fun <E: Comparable<E>> sorted(list: KoneIterable<E>): KoneList<E> = list.quicksorted()
        override fun <E> sorted(order: Order<E>, list: KoneIterable<E>): KoneList<E> = context(order) { list.quicksorted() }
        override fun <E> sortedWith(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E> = list.quicksortedWith(comparator)
        override fun <E: Comparable<E>> sortedDescending(list: KoneIterable<E>): KoneList<E> = list.quicksortedDescending()
        override fun <E> sortedDescending(order: Order<E>, list: KoneIterable<E>): KoneList<E> = context(order) { list.quicksortedDescending() }
        override fun <E> sortedWithDescending(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E> = list.quicksortedWithDescending(comparator)
        override fun <E, R: Comparable<R>> sortedBy(list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = list.quicksortedBy(selector)
        override fun <E, R> sortedBy(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = context(order) { list.quicksortedBy(selector) }
        override fun <E, R> sortedWithBy(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = list.quicksortedWithBy(comparator, selector)
        override fun <E, R: Comparable<R>> sortedByDescending(list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = list.quicksortedByDescending(selector)
        override fun <E, R> sortedByDescending(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = context(order) { list.quicksortedByDescending(selector) }
        override fun <E, R> sortedWithByDescending(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = list.quicksortedWithByDescending(comparator, selector)
    }
}