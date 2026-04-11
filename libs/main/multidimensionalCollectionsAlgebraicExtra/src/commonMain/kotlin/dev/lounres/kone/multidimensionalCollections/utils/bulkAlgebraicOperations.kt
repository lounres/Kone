/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.utils

import dev.lounres.kone.algebraic.Monoid
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.multidimensionalCollections.MDIndex
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDList2


context(monoid: Monoid<E>)
public fun <E> MDList<E>.sum(): E = fold(monoid.zero) { acc, e -> acc + e }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList<E>.sumOf(selector: (E) -> A): A = fold(monoid.zero) { acc, e -> acc + selector(e) }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList<E>.sumOfIndexed(selector: (index: MDIndex, E) -> A): A = foldIndexed(monoid.zero) { index, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList1<E>.sumOfIndexed(selector: (index: UInt, E) -> A): A = foldIndexed(monoid.zero) { index: UInt, acc, e -> acc + selector(index, e) }

context(monoid: Monoid<A>)
public inline fun <E, A> MDList2<E>.sumOfIndexed(selector: (rowIndex: UInt, columnIndex: UInt, E) -> A): A = foldIndexed(monoid.zero) { row, column, acc, e -> acc + selector(row, column, e) }