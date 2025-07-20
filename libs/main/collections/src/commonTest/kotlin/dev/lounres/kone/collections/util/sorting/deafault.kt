/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.util.sorting

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.utils.sort
import dev.lounres.kone.collections.utils.sortBy
import dev.lounres.kone.collections.utils.sortByDescending
import dev.lounres.kone.collections.utils.sortDescending
import dev.lounres.kone.collections.utils.sortWith
import dev.lounres.kone.collections.utils.sortWithBy
import dev.lounres.kone.collections.utils.sortWithByDescending
import dev.lounres.kone.collections.utils.sortWithDescending
import dev.lounres.kone.collections.utils.sorted
import dev.lounres.kone.collections.utils.sortedBy
import dev.lounres.kone.collections.utils.sortedByDescending
import dev.lounres.kone.collections.utils.sortedDescending
import dev.lounres.kone.collections.utils.sortedWith
import dev.lounres.kone.collections.utils.sortedWithBy
import dev.lounres.kone.collections.utils.sortedWithByDescending
import dev.lounres.kone.collections.utils.sortedWithDescending
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Comparator
import dev.lounres.kone.relations.Order


object DefaultDescription : SortingDescription {
    override val name: String = "default"
    override val applier: SortingApplier = object : SortingApplier {
        override fun <E: Comparable<E>> sort(list: KoneSettableList<E>) = list.sort()
        override fun <E> sort(order: Order<E>, list: KoneSettableList<E>) = order { list.sort() }
        override fun <E> sortWith(list: KoneSettableList<E>, comparator: Comparator<E>) = list.sortWith(comparator)
        override fun <E: Comparable<E>> sortDescending(list: KoneSettableList<E>) = list.sortDescending()
        override fun <E> sortDescending(order: Order<E>, list: KoneSettableList<E>) = order { list.sortDescending() }
        override fun <E> sortWithDescending(list: KoneSettableList<E>, comparator: Comparator<E>) = list.sortWithDescending(comparator)
        override fun <E, R: Comparable<R>> sortBy(list: KoneSettableList<E>, selector: (E) -> R) = list.sortBy(selector)
        override fun <E, R> sortBy(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R) = order { list.sortBy(selector) }
        override fun <E, R> sortWithBy(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R) = list.sortWithBy(comparator, selector)
        override fun <E, R: Comparable<R>> sortByDescending(list: KoneSettableList<E>, selector: (E) -> R) = list.sortByDescending(selector)
        override fun <E, R> sortByDescending(order: Order<R>, list: KoneSettableList<E>, selector: (E) -> R) = order { list.sortByDescending(selector) }
        override fun <E, R> sortWithByDescending(list: KoneSettableList<E>, comparator: Comparator<R>, selector: (E) -> R) = list.sortWithByDescending(comparator, selector)
        
        override fun <E: Comparable<E>> sorted(list: KoneIterable<E>): KoneList<E> = list.sorted()
        override fun <E> sorted(order: Order<E>, list: KoneIterable<E>): KoneList<E> = order { list.sorted() }
        override fun <E> sortedWith(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E> = list.sortedWith(comparator)
        override fun <E: Comparable<E>> sortedDescending(list: KoneIterable<E>): KoneList<E> = list.sortedDescending()
        override fun <E> sortedDescending(order: Order<E>, list: KoneIterable<E>): KoneList<E> = order { list.sortedDescending() }
        override fun <E> sortedWithDescending(list: KoneIterable<E>, comparator: Comparator<E>): KoneList<E> = list.sortedWithDescending(comparator)
        override fun <E, R: Comparable<R>> sortedBy(list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = list.sortedBy(selector)
        override fun <E, R> sortedBy(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = order { list.sortedBy(selector) }
        override fun <E, R> sortedWithBy(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = list.sortedWithBy(comparator, selector)
        override fun <E, R: Comparable<R>> sortedByDescending(list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = list.sortedByDescending(selector)
        override fun <E, R> sortedByDescending(order: Order<R>, list: KoneIterable<E>, selector: (E) -> R): KoneList<E> = order { list.sortedByDescending(selector) }
        override fun <E, R> sortedWithByDescending(list: KoneIterable<E>, comparator: Comparator<R>, selector: (E) -> R): KoneList<E> = list.sortedWithByDescending(comparator, selector)
    }
}