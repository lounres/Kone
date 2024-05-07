/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.isNone
import dev.lounres.kone.option.orThrow


public interface KoneGrouping<E, out K> {
    public fun sourceIterator(): KoneIterator<E>
    public fun keyOf(element: E): K
}

public inline fun <E, K> KoneIterable<E>.groupingBy(crossinline keySelector: (E) -> K): KoneGrouping<E, K> =
    object : KoneGrouping<E, K> {
        override fun sourceIterator(): KoneIterator<E> = this@groupingBy.iterator()
        override fun keyOf(element: E): K = keySelector(element)
    }

public inline fun <E, K, R, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.aggregateTo(
    destination: D,
    operation: (key: K, accumulator: Option<R>, element: E, first: Boolean) -> R
): D {
    for (element in this.sourceIterator()) {
        val key = keyOf(element)
        val accumulator = destination.getMaybe(key)
        destination[key] = operation(key, accumulator, element, accumulator.isNone() && !destination.containsKey(key))
    }
    return destination
}

public inline fun <E, K, R> KoneGrouping<E, K>.aggregate(
    keyContext: Equality<K> = defaultEquality(),
    resultContext: Equality<R> = defaultEquality(),
    operation: (key: K, accumulator: Option<R>, element: E, first: Boolean) -> R
): KoneMap<K, R> = aggregateTo(koneMutableMapOf(keyContext = keyContext, valueContext = resultContext), operation)

public inline fun <E, K, R, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.foldTo(
    destination: D,
    initialValueSelector: (key: K, element: E) -> R,
    operation: (key: K, accumulator: R, element: E) -> R
): D =
    aggregateTo(destination) { key, acc, element, first -> operation(key, if (first) initialValueSelector(key, element) else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, element) }

public inline fun <E, K, R, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.foldTo(
    destination: D,
    initialValue: R,
    operation: (accumulator: R, element: E) -> R
): D =
    aggregateTo(destination) { _, acc, e, first -> operation(if (first) initialValue else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }

public inline fun <E, K, R> KoneGrouping<E, K>.fold(
    keyContext: Equality<K> = defaultEquality(),
    resultContext: Equality<R> = defaultEquality(),
    initialValueSelector: (key: K, element: E) -> R,
    operation: (key: K, accumulator: R, element: E) -> R
): KoneMap<K, R> =
    aggregate(keyContext = keyContext, resultContext = resultContext) { key, acc, e, first -> operation(key, if (first) initialValueSelector(key, e) else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }

public inline fun <E, K, R> KoneGrouping<E, K>.fold(
    keyContext: Equality<K> = defaultEquality(),
    resultContext: Equality<R> = defaultEquality(),
    initialValue: R,
    operation: (accumulator: R, element: E) -> R
): KoneMap<K, R> =
    aggregate(keyContext = keyContext, resultContext = resultContext) { _, acc, e, first -> operation(if (first) initialValue else acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }

public inline fun <E: R, R, K, D: KoneMutableMap<in K, R>> KoneGrouping<E, K>.reduceTo(
    destination: D,
    operation: (key: K, accumulator: R, element: E) -> R
): D =
    aggregateTo(destination) { key, acc, e, first -> if (first) e else operation(key, acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }

public inline fun <E : R, R, K> KoneGrouping<E, K>.reduce(
    keyContext: Equality<K> = defaultEquality(),
    resultContext: Equality<R> = defaultEquality(),
    operation: (key: K, accumulator: R, element: E) -> R
): KoneMap<K, R> =
    aggregate(keyContext = keyContext, resultContext = resultContext) { key, acc, e, first -> if (first) e else operation(key, acc.orThrow { IllegalStateException("For some reason accumulator is empty") }, e) }

public fun <E, K, D: KoneMutableMap<in K, UInt>> KoneGrouping<E, K>.eachCountTo(destination: D): D = foldTo(destination, 0u) { acc, _ -> acc + 1u }

public fun <T, K> KoneGrouping<T, K>.eachCount(keyContext: Equality<K> = defaultEquality()): KoneMap<K, UInt> = fold(keyContext = keyContext, initialValue = 0u) { acc, _ -> acc + 1u }