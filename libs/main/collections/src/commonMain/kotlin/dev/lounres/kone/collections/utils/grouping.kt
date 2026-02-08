/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils


// TODO: Review grouping API

//public interface KoneGrouping<E, out K> {
//    public fun sourceIterator(): KoneIterator<E>
//    public fun keyOf(element: E): K
//}
//
//public inline fun <E, K> KoneIterable<E>.groupingBy(crossinline keySelector: (E) -> K): KoneGrouping<E, K> =
//    object : KoneGrouping<E, K> {
//        override fun sourceIterator(): KoneIterator<E> = this@groupingBy.iterator()
//        override fun keyOf(element: E): K = keySelector(element)
//    }
//
//public inline fun <E, K, R, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.aggregateTo(
//    destination: D,
//    operation: (key: K, accumulator: Maybe<R>, element: E, first: Boolean) -> R
//): D {
//    for (element in this.sourceIterator()) {
//        val key = keyOf(element)
//        val accumulator = destination.getMaybe(key)
//        destination[key] = operation(key, accumulator, element, accumulator.isNone() && !destination.containsKey(key))
//    }
//    return destination
//}
//
//public inline fun <E, K, R> KoneGrouping<E, K>.aggregate(
//    keyEquality: Equality<K> = defaultEquality(),
//    keyHashing: Hashing<K>? = null,
//    keyOrder: Order<K>? = null,
//    operation: (key: K, accumulator: Maybe<R>, element: E, first: Boolean) -> R
//): KoneMap<K, R> = aggregateTo(
//    koneMutableMapOf(
//        keyEquality = keyEquality,
//        keyHashing = keyHashing,
//        keyOrder = keyOrder,
//    ),
//    operation
//)
//
//context(_: KoneContextRegistry)
//public inline fun <E, K, R> KoneGrouping<E, K>.aggregateContextual(
//    keyType: SuppliedType<K>,
//    operation: (key: K, accumulator: Maybe<R>, element: E, first: Boolean) -> R
//): KoneMap<K, R> = aggregateTo(
//    koneContextualMutableMapOf(keyType = keyType),
//    operation
//)
//
//public inline fun <E, K, R, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.foldTo(
//    destination: D,
//    initialValueSelector: (key: K, element: E) -> R,
//    operation: (key: K, accumulator: R, element: E) -> R
//): D =
//    aggregateTo(destination) { key, acc, element, first -> operation(key, if (first) initialValueSelector(key, element) else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, element) }
//
//public inline fun <E, K, R, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.foldTo(
//    destination: D,
//    initialValue: R,
//    operation: (accumulator: R, element: E) -> R
//): D =
//    aggregateTo(destination) { _, acc, e, first -> operation(if (first) initialValue else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }
//
//public inline fun <E, K, R> KoneGrouping<E, K>.fold(
//    keyEquality: Equality<K> = defaultEquality(),
//    initialValueSelector: (key: K, element: E) -> R,
//    operation: (key: K, accumulator: R, element: E) -> R
//): KoneMap<K, R> =
//    aggregate(keyEquality = keyEquality) { key, acc, e, first -> operation(key, if (first) initialValueSelector(key, e) else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }
//
//public inline fun <E, K, R> KoneGrouping<E, K>.fold(
//    keyEquality: Equality<K> = defaultEquality(),
//    initialValue: R,
//    operation: (accumulator: R, element: E) -> R
//): KoneMap<K, R> =
//    aggregate(keyEquality = keyEquality) { _, acc, e, first -> operation(if (first) initialValue else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }
//
//public inline fun <E: R, R, K, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.reduceTo(
//    destination: D,
//    operation: (key: K, accumulator: R, element: E) -> R
//): D =
//    aggregateTo(destination) { key, acc, e, first -> if (first) e else operation(key, acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }
//
//public inline fun <E : R, R, K> KoneGrouping<E, K>.reduce(
//    keyEquality: Equality<K> = defaultEquality(),
//    operation: (key: K, accumulator: R, element: E) -> R
//): KoneMap<K, R> =
//    aggregate(keyEquality = keyEquality) { key, acc, e, first -> if (first) e else operation(key, acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }
//
//public fun <E, K, D: KoneMutableMap<in K, UInt>> KoneGrouping<E, K>.eachCountTo(destination: D): D = foldTo(destination, 0u) { acc, _ -> acc + 1u }
//
//public fun <T, K> KoneGrouping<T, K>.eachCount(keyEquality: Equality<K> = defaultEquality()): KoneMap<K, UInt> = fold(keyEquality = keyEquality, initialValue = 0u) { acc, _ -> acc + 1u }