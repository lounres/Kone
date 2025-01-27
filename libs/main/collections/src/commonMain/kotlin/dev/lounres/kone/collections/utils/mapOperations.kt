/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultReifiedEquality
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE


public inline fun <K, V, R> KoneMap<in K, V>.computeOn(key: K, compute: (Maybe<V>) -> R): R
contract [ callsInPlace(compute, EXACTLY_ONCE) ] = compute(getMaybe(key))

public inline fun <K, V, R> KoneMap<K, V>.computeOnOrElse(key: K, defaultResult: () -> R, compute: (value: V) -> R): R
contract [ callsInPlace(defaultResult, AT_MOST_ONCE), callsInPlace(compute, AT_MOST_ONCE) ] =
    when (val value = getMaybe(key)) {
        is Some<V> -> compute(value.value)
        None -> defaultResult()
    }

public inline fun <K, V> KoneMutableMap<in K, V>.change(key: K, transform: (currentValue: V) -> V): Unit
contract [ callsInPlace(transform, AT_MOST_ONCE) ] =
    when (val value = getMaybe(key)) {
        is Some<V> -> this[key] = transform(value.value)
        None -> {}
    }

public inline fun <K, V> KoneMutableMap<K, V>.setOrChange(key: K, valueOnSet: () -> V, transformOnChange: (currentValue: V) -> V): V
contract [ callsInPlace(valueOnPut, AT_MOST_ONCE), callsInPlace(transformOnChange, AT_MOST_ONCE) ] =
    computeOnOrElse(key, valueOnSet, transformOnChange).also { this[key] = it }

public inline fun <K, V> KoneMap<K, V>.withChanged(keyContext: Equality<K> = defaultEquality(), key: K, transform: (currentValue: V) -> V): KoneMap<K, V>
contract [ callsInPlace(transform, AT_MOST_ONCE) ] =
    buildKoneMap(size, keyContext = keyContext) {
        setAllFrom(this@withChanged)
        change(key, transform)
    }

public inline fun <K, V> KoneMap<K, V>.withChangedReified(keyContext: ReifiedEquality<K>, key: K, transform: (currentValue: V) -> V): KoneReifiedMap<K, V>
        contract [ callsInPlace(transform, AT_MOST_ONCE) ] =
    buildKoneReifiedMap(size, keyContext = keyContext) {
        setAllFrom(this@withChangedReified)
        change(key, transform)
    }

public inline fun <reified K, V> KoneMap<K, V>.withChangedReified(key: K, transform: (currentValue: V) -> V): KoneReifiedMap<K, V>
        contract [ callsInPlace(transform, AT_MOST_ONCE) ] =
    buildKoneReifiedMap(size) {
        setAllFrom(this@withChangedReified)
        change(key, transform)
    }

public inline fun <K, V> KoneMap<out K, V>.withSetOrChanged(keyContext: Equality<K> = defaultEquality(), key: K, valueOnSet: () -> V, transformOnChange: (currentValue: V) -> V): KoneMap<K, V>
contract [ callsInPlace(valueOnPut, AT_MOST_ONCE), callsInPlace(transformOnChange, AT_MOST_ONCE) ] =
    buildKoneMap(size + 1u, keyContext = keyContext) {
        setAllFrom(this@withSetOrChanged)
        setOrChange(key, valueOnSet, transformOnChange)
    }

public inline fun <K, V> KoneMap<out K, V>.withSetOrChangedReified(keyContext: ReifiedEquality<K>, key: K, valueOnSet: () -> V, transformOnChange: (currentValue: V) -> V): KoneReifiedMap<K, V>
        contract [ callsInPlace(valueOnPut, AT_MOST_ONCE), callsInPlace(transformOnChange, AT_MOST_ONCE) ] =
    buildKoneReifiedMap(size + 1u, keyContext = keyContext) {
        setAllFrom(this@withSetOrChangedReified)
        setOrChange(key, valueOnSet, transformOnChange)
    }

public inline fun <reified K, V> KoneMap<out K, V>.withSetOrChangedReified(key: K, valueOnSet: () -> V, transformOnChange: (currentValue: V) -> V): KoneReifiedMap<K, V>
        contract [ callsInPlace(valueOnPut, AT_MOST_ONCE), callsInPlace(transformOnChange, AT_MOST_ONCE) ] =
    buildKoneReifiedMap(size + 1u) {
        setAllFrom(this@withSetOrChangedReified)
        setOrChange(key, valueOnSet, transformOnChange)
    }

public fun <K, V, D: KoneMutableMap<K, V>> KoneMap<out K, V>.copyTo(destination: D): D {
    for ((key, value) in this) {
        destination[key] = value
    }
    return destination
}

public inline fun <K, V: W, W, D: KoneMutableMap<K, W>> KoneMap<out K, V>.copyToBy(destination: D, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for ((key, value) in this) {
        destination.setOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

public inline fun <K, V, W, D: KoneMutableMap<K, W>> KoneMap<out K, V>.copyMapTo(destination: D, transform: (KoneMapEntry<K, V>) -> W): D {
    for (entry in this) {
        destination[entry.key] = transform(entry)
    }
    return destination
}

public inline fun <K, V, W, D: KoneMutableMap<K, W>> KoneMap<out K, V>.copyMapToBy(destination: D, transform: (KoneMapEntry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for (entry in this) {
        val (key, value) = entry
        destination.setOrChange(key, { transform(entry) }, { resolve(key, it, value) })
    }
    return destination
}

public fun <K, V, D: KoneMutableMap<in K, in V>> mergeTo(map1: KoneMap<out K, V>, map2: KoneMap<out K, V>, destination: D): D {
    for ((key, value) in map1) {
        destination.set(key, value)
    }
    for ((key, value) in map2) {
        destination.set(key, value)
    }
    return destination
}

public inline fun <K, V1: W, V2: W, W, D: KoneMutableMap<K, W>> mergeToBy(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, destination: D, resolve: (key: K, value1: V1, value2: V2) -> W): D {
    for (key in map2.keysView) {
        destination.remove(key)
    }
    for ((key, value) in map1) {
        destination.set(key, value)
    }
    for ((key, value) in map2) {
        @Suppress("UNCHECKED_CAST")
        destination.setOrChange(key, { value }, { resolve(key, it as V1, value) })
    }
    return destination
}

public fun <K, V1: W, V2: W, W> merge(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, keyContext: Equality<K> = defaultEquality()): KoneMap<K, W> {
    val result = koneMutableMapOf<K, W>(keyContext) // TODO: Replace with growable map
    return mergeTo(map1, map2, result)
}

public fun <K, V1: W, V2: W, W> mergeReified(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, keyContext: ReifiedEquality<K>): KoneReifiedMap<K, W> {
    val result = koneMutableReifiedMapOf<K, W>(keyContext) // TODO: Replace with growable map
    return mergeTo(map1, map2, result)
}

public inline fun <reified K, V1: W, V2: W, W> mergeReified(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>): KoneReifiedMap<K, W> {
    val result = koneMutableReifiedMapOf<K, W>() // TODO: Replace with growable map
    return mergeTo(map1, map2, result)
}

public inline fun <K, V1: W, V2: W, W> mergeBy(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, keyContext: Equality<K> = defaultEquality(), resolve: (key: K, value1: V1, value2: V2) -> W): KoneMap<K, W> {
    val result = koneMutableMapOf<K, W>(keyContext) // TODO: Replace with growable map
    return mergeToBy(map1, map2, result, resolve)
}

public inline fun <K, V1: W, V2: W, W> mergeByReified(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, keyContext: ReifiedEquality<K>, resolve: (key: K, value1: V1, value2: V2) -> W): KoneReifiedMap<K, W> {
    val result = koneMutableReifiedMapOf<K, W>(keyContext) // TODO: Replace with growable map
    return mergeToBy(map1, map2, result, resolve)
}

public inline fun <reified K, V1: W, V2: W, W> mergeByReified(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, resolve: (key: K, value1: V1, value2: V2) -> W): KoneReifiedMap<K, W> {
    val result = koneMutableReifiedMapOf<K, W>() // TODO: Replace with growable map
    return mergeToBy(map1, map2, result, resolve)
}

public inline fun <T, K, V, D : KoneMutableMap<K, V>> KoneIterable<T>.associateTo(destination: D, transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val (key, value) = transform(element)
        destination.setOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

public inline fun <T, K, V, D : KoneMutableMap<in K, V>> KoneIterable<T>.associateByTo(destination: D, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val value = valueTransform(element)
        destination.setOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

public inline fun <T, K, D : KoneMutableMap<K, T>> KoneIterable<T>.associateByTo(destination: D, keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): D {
    for (element in this) {
        val key = keySelector(element)
        destination.setOrChange(key, { element }, { resolve(key, it, element) })
    }
    return destination
}

public inline fun <T, K, V> KoneIterable<T>.associate(keyContext: Equality<K> = defaultEquality(), transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): KoneMap<K, V> =
    associateTo(koneMutableMapOf(keyContext = keyContext), transform, resolve)

public inline fun <T, K, V> KoneIterable<T>.associateReified(keyContext: ReifiedEquality<K>, transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): KoneReifiedMap<K, V> =
    associateTo(koneMutableReifiedMapOf(keyContext = keyContext), transform, resolve)

public inline fun <T, reified K, V> KoneIterable<T>.associateReified(transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): KoneReifiedMap<K, V> =
    associateTo(koneMutableReifiedMapOf(keyContext = defaultReifiedEquality()), transform, resolve)

public inline fun <T, K, V> KoneIterable<T>.associateBy(keyContext: Equality<K> = defaultEquality(), keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(keyContext = keyContext), keySelector, valueTransform, resolve)

public inline fun <T, K, V> KoneIterable<T>.associateByReified(keyContext: ReifiedEquality<K>, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): KoneReifiedMap<K, V> =
    associateByTo(koneMutableReifiedMapOf(keyContext = keyContext), keySelector, valueTransform, resolve)

public inline fun <T, reified K, V> KoneIterable<T>.associateByReified(keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): KoneReifiedMap<K, V> =
    associateByTo(koneMutableReifiedMapOf(keyContext = defaultReifiedEquality()), keySelector, valueTransform, resolve)

public inline fun <T, K> KoneIterable<T>.associateBy(keyContext: Equality<K> = defaultEquality(), keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): KoneMap<K, T> =
    associateByTo(koneMutableMapOf(keyContext = keyContext), keySelector, resolve)

public inline fun <T, K> KoneIterable<T>.associateByReified(keyContext: ReifiedEquality<K>, keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): KoneReifiedMap<K, T> =
    associateByTo(koneMutableReifiedMapOf(keyContext = keyContext), keySelector, resolve)

public inline fun <T, reified K> KoneIterable<T>.associateByReified(keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): KoneReifiedMap<K, T> =
    associateByTo(koneMutableReifiedMapOf(keyContext = defaultReifiedEquality()), keySelector, resolve)

public inline fun <K, V, W, D : KoneMutableMap<in K, W>> KoneMap<out K, V>.mapValuesTo(destination: D, transform: (KoneMapEntry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: W) -> W): D =
    entriesView.associateByTo(destination, { it.key }, transform, resolve)

public inline fun <K, V, L, D : KoneMutableMap<in L, V>> KoneMap<out K, V>.mapKeysTo(destination: D, transform: (KoneMapEntry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): D =
    entriesView.associateByTo(destination, transform, { it.value }, resolve)

public inline fun <K, V, L> KoneMap<out K, V>.mapKeys(keyContext: Equality<L> = defaultEquality(), transform: (KoneMapEntry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): KoneMap<L, V> =
    mapKeysTo(koneMutableMapOf(keyContext), transform, resolve)

public inline fun <K, V, reified L> KoneMap<out K, V>.mapKeysReified(transform: (KoneMapEntry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): KoneReifiedMap<L, V> =
    mapKeysTo(koneMutableReifiedMapOf(defaultReifiedEquality()), transform, resolve)

public inline fun <K, V, L> KoneMap<out K, V>.mapKeysReified(keyContext: ReifiedEquality<L>, transform: (KoneMapEntry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): KoneReifiedMap<L, V> =
    mapKeysTo(koneMutableReifiedMapOf(keyContext), transform, resolve)

public inline fun <K, V, R> KoneMap<out K, V>.fold(initial: R, operation: (acc: R, KoneMapEntry<K, V>) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

public inline fun <K, V, T: R, R> KoneMap<out K, V>.mapReduce(transform: (KoneMapEntry<K, V>) -> T, operation: (acc: R, T) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = transform(iterator.next())
    for (element in iterator) accumulator = operation(accumulator, transform(element))
    return accumulator
}

public inline fun <K, V, T: R, R> KoneMap<out K, V>.mapReduceOrNull(transform: (KoneMapEntry<K, V>) -> T, operation: (acc: R, T) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = transform(iterator.next())
    for (element in iterator) accumulator = operation(accumulator, transform(element))
    return accumulator
}

public inline fun <K, V, T: R, R> KoneMap<out K, V>.mapReduceMaybe(transform: (KoneMapEntry<K, V>) -> T, operation: (acc: R, T) -> R): Maybe<R> {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return None
    var accumulator: R = transform(iterator.next())
    for (element in iterator) accumulator = operation(accumulator, transform(element))
    return Some(accumulator)
}

public inline fun <K, V1, V2, R> mergingFold(
    map1: KoneMap<K, V1>,
    map2: KoneMap<K, V2>,
    initial: R,
    operation1: (acc: R, KoneMapEntry<K, V1>) -> R,
    operation2: (acc: R, KoneMapEntry<K, V2>) -> R,
    operationMerge: (acc: R, key: K, value1: V1, value2: V2) -> R
): R {
    var accumulator = initial
    for (element in map2) if (element.key !in map1) accumulator = operation2(accumulator, element)
    for (element in map1) accumulator =
        if (element.key !in map2) operation1(accumulator, element)
        else operationMerge(accumulator, element.key, element.value, map2[element.key]!!)
    return accumulator
}

public inline fun <K, V1, V2> mergingAny(
    map1: KoneMap<K, V1>,
    map2: KoneMap<K, V2>,
    operation1: (KoneMapEntry<K, V1>) -> Boolean,
    operation2: (KoneMapEntry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map2) if (element.key !in map1 && operation2(element)) return true
    for (element in map1)
        if (element.key !in map2) { if(operation1(element)) return true }
        else if(operationMerge(element.key, element.value, map2[element.key]!!)) return true
    return false
}

public inline fun <K, V1, V2> mergingAll(
    map1: KoneMap<K, V1>,
    map2: KoneMap<K, V2>,
    operation1: (KoneMapEntry<K, V1>) -> Boolean,
    operation2: (KoneMapEntry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map2) if (element.key !in map1 && !operation2(element)) return false
    for (element in map1)
        if (element.key !in map2) { if (!operation1(element)) return false }
        else if(!operationMerge(element.key, element.value, map2[element.key]!!)) return false
    return true
}

public inline fun <K, V1, V2> mergingNone(
    map1: KoneMap<K, V1>,
    map2: KoneMap<K, V2>,
    operation1: (KoneMapEntry<K, V1>) -> Boolean,
    operation2: (KoneMapEntry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map2) if (element.key !in map1 && operation2(element)) return false
    for (element in map1)
        if (element.key !in map2) { if(operation1(element)) return false }
        else if(operationMerge(element.key, element.value, map2[element.key]!!)) return false
    return true
}