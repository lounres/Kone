/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMutableMap
import dev.lounres.kone.collections.buildKoneMap
import dev.lounres.kone.collections.iterator
import dev.lounres.kone.collections.next
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.Some
import kotlin.contracts.InvocationKind.AT_MOST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE


//public inline fun <K, V, R> KoneMap<in K, V>.computeOn(key: K, compute: (Option<V>) -> R): R
//contract [ callsInPlace(compute, EXACTLY_ONCE) ] = compute(getMaybe(key))
//
//public inline fun <K, V, R> KoneMap<K, V>.computeOnOrElse(key: K, defaultResult: () -> R, compute: (value: V) -> R): R
//contract [ callsInPlace(defaultResult, AT_MOST_ONCE), callsInPlace(compute, AT_MOST_ONCE) ] =
//    when (val value = getMaybe(key)) {
//        is Some<V> -> compute(value.value)
//        None -> defaultResult()
//    }
//
//public inline fun <K, V> KoneMutableMap<in K, V>.change(key: K, transform: (currentValue: V) -> V): Unit
//contract [ callsInPlace(transform, AT_MOST_ONCE) ] =
//    when (val value = getMaybe(key)) {
//        is Some<V> -> this[key] = transform(value.value)
//        None -> {}
//    }
//
//public inline fun <K, V> KoneMutableMap<K, V>.putOrChange(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): V
//contract [ callsInPlace(valueOnPut, AT_MOST_ONCE), callsInPlace(transformOnChange, AT_MOST_ONCE) ] =
//    computeOnOrElse(key, valueOnPut, transformOnChange).also { this[key] = it }
//
//public inline fun <K, V> KoneMap<K, V>.withChanged(key: K, transform: (currentValue: V) -> V): KoneMap<K, V>
//contract [ callsInPlace(transform, AT_MOST_ONCE) ] =
//    buildKoneMap(size) {
//        setAllFrom(this@withChanged)
//        change(key, transform)
//    }
//
//public inline fun <K, V> KoneMap<out K, V>.withPutOrChanged(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): KoneMap<K, V>
//contract [ callsInPlace(valueOnPut, AT_MOST_ONCE), callsInPlace(transformOnChange, AT_MOST_ONCE) ] =
//    buildKoneMap(size + 1u) {
//        setAllFrom(this@withPutOrChanged)
//        putOrChange(key, valueOnPut, transformOnChange)
//    }
//
//public fun <K, V, D: KoneMutableMap<K, V>> KoneMap<out K, V>.copyTo(destination: D): D {
//    for ((key, value) in this) {
//        destination[key] = value
//    }
//    return destination
//}
//
//public inline fun <K, V: W, W, D: KoneMutableMap<K, W>> KoneMap<out K, V>.copyToBy(destination: D, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
//    for ((key, value) in this) {
//        destination.putOrChange(key, { value }, { resolve(key, it, value) })
//    }
//    return destination
//}
//
//public inline fun <K, V, W, D: KoneMutableMap<K, W>> KoneMap<out K, V>.copyMapTo(destination: D, transform: (KoneMapEntry<K, V>) -> W): D {
//    for (entry in this) {
//        destination[entry.key] = transform(entry)
//    }
//    return destination
//}
//
//public inline fun <K, V, W, D: KoneMutableMap<K, W>> KoneMap<out K, V>.copyMapToBy(destination: D, transform: (KoneMapEntry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
//    for (entry in this) {
//        val (key, value) = entry
//        destination.putOrChange(key, { transform(entry) }, { resolve(key, it, value) })
//    }
//    return destination
//}
//
//public fun <K, V, D: KoneMutableMap<in K, in V>> mergeTo(map1: KoneMap<out K, V>, map2: KoneMap<out K, V>, destination: D): D {
//    for ((key, value) in map1) {
//        destination.set(key, value)
//    }
//    for ((key, value) in map2) {
//        destination.set(key, value)
//    }
//    return destination
//}
//
//public inline fun <K, V1: W, V2: W, W, D: KoneMutableMap<K, W>> mergeToBy(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, destination: D, resolve: (key: K, value1: V1, value2: V2) -> W): D {
//    for (key in map2.keysView) {
//        destination.remove(key)
//    }
//    for ((key, value) in map1) {
//        destination.set(key, value)
//    }
//    for ((key, value) in map2) {
//        @Suppress("UNCHECKED_CAST")
//        destination.putOrChange(key, { value }, { resolve(key, it as V1, value) })
//    }
//    return destination
//}
//
//public fun <K, V1: W, V2: W, W> merge(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>): KoneMap<K, W> {
//    val result = LinkedHashMap<K, W>(map1.size + map2.size)
//    return mergeTo(map1, map2, result)
//}
//
//public inline fun <K, V1: W, V2: W, W> mergeBy(map1: KoneMap<out K, V1>, map2: KoneMap<out K, V2>, resolve: (key: K, value1: V1, value2: V2) -> W): KoneMap<K, W> {
//    val result = LinkedHashMap<K, W>(map1.size + map2.size)
//    return mergeToBy(map1, map2, result, resolve)
//}
//
//public inline fun <T, K, V, D : KoneMutableMap<K, V>> Iterable<T>.associateTo(destination: D, transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
//    for (element in this) {
//        val (key, value) = transform(element)
//        destination.putOrChange(key, { value }, { resolve(key, it, value) })
//    }
//    return destination
//}
//
//public inline fun <T, K, V, D : KoneMutableMap<K, V>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
//    for (element in this) {
//        val key = keySelector(element)
//        val value = valueTransform(element)
//        destination.putOrChange(key, { value }, { resolve(key, it, value) })
//    }
//    return destination
//}
//
//public inline fun <T, K, D : KoneMutableMap<K, T>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): D {
//    for (element in this) {
//        val key = keySelector(element)
//        destination.putOrChange(key, { element }, { resolve(key, it, element) })
//    }
//    return destination
//}
//
//public inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
//    associateTo(LinkedHashMap(), transform, resolve)
//
//public inline fun <T, K, V> Iterable<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
//    associateByTo(LinkedHashMap(), keySelector, valueTransform, resolve)
//
//public inline fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): Map<K, T> =
//    associateByTo(LinkedHashMap(), keySelector, resolve)
//
//public inline fun <K, V, W, D : KoneMutableMap<K, W>> KoneMap<out K, V>.mapValuesTo(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: W) -> W): D =
//    entries.associateByTo(destination, { it.key }, transform, resolve)
//
//public inline fun <K, V, L, D : KoneMutableMap<L, V>> KoneMap<out K, V>.mapKeysTo(destination: D, transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): D =
//    entries.associateByTo(destination, transform, { it.value }, resolve)
//
//public inline fun <K, V, L> KoneMap<out K, V>.mapKeys(transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): Map<L, V> =
//    mapKeysTo(LinkedHashMap(size), transform, resolve)
//
//public inline fun <K, V, R> KoneMap<out K, V>.fold(initial: R, operation: (acc: R, KoneMapEntry<K, V>) -> R): R {
//    var accumulator = initial
//    for (element in this) accumulator = operation(accumulator, element)
//    return accumulator
//}
//
//public inline fun <K, V, T: R, R> KoneMap<out K, V>.mapReduce(transform: (KoneMapEntry<K, V>) -> T, operation: (acc: R, T) -> R): R {
//    val iterator = this.iterator()
//    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
//    var accumulator: R = transform(iterator.next())
//    for (element in iterator) accumulator = operation(accumulator, transform(element))
//    return accumulator
//}
//
//public inline fun <K, V, T: R, R> KoneMap<out K, V>.mapReduceOrNull(transform: (KoneMapEntry<K, V>) -> T, operation: (acc: R, T) -> R): R? {
//    val iterator = this.iterator()
//    if (!iterator.hasNext()) return null
//    var accumulator: R = transform(iterator.next())
//    for (element in iterator) accumulator = operation(accumulator, transform(element))
//    return accumulator
//}
//
//public inline fun <K, V, T: R, R> KoneMap<out K, V>.mapReduceMaybe(transform: (KoneMapEntry<K, V>) -> T, operation: (acc: R, T) -> R): Option<R> {
//    val iterator = this.iterator()
//    if (!iterator.hasNext()) return None
//    var accumulator: R = transform(iterator.next())
//    for (element in iterator) accumulator = operation(accumulator, transform(element))
//    return Some(accumulator)
//}
//
//public inline fun <K, V1, V2, R> mergingFold(
//    map1: KoneMap<out K, V1>,
//    map2: KoneMap<out K, V2>,
//    initial: R,
//    operation1: (acc: R, KoneMapEntry<K, V1>) -> R,
//    operation2: (acc: R, KoneMapEntry<K, V2>) -> R,
//    operationMerge: (acc: R, key: K, value1: V1, value2: V2) -> R
//): R {
//    var accumulator = initial
//    for (element in map2) if (element.key !in map1) accumulator = operation2(accumulator, element)
//    for (element in map1) accumulator =
//        if (element.key !in map2) operation1(accumulator, element)
//        else operationMerge(accumulator, element.key, element.value, map2[element.key]!!)
//    return accumulator
//}
//
//public inline fun <K, V1, V2> mergingAny(
//    map1: Map<out K, V1>,
//    map2: Map<out K, V2>,
//    operation1: (Map.Entry<K, V1>) -> Boolean,
//    operation2: (Map.Entry<K, V2>) -> Boolean,
//    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
//): Boolean {
//    for (element in map2) if (element.key !in map1 && operation2(element)) return true
//    for (element in map1)
//        if (element.key !in map2) { if(operation1(element)) return true }
//        else if(operationMerge(element.key, element.value, map2[element.key]!!)) return true
//    return false
//}
//
//public inline fun <K, V1, V2> mergingAll(
//    map1: Map<out K, V1>,
//    map2: Map<out K, V2>,
//    operation1: (Map.Entry<K, V1>) -> Boolean,
//    operation2: (Map.Entry<K, V2>) -> Boolean,
//    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
//): Boolean {
//    for (element in map2) if (element.key !in map1 && !operation2(element)) return false
//    for (element in map1)
//        if (element.key !in map2) { if (!operation1(element)) return false }
//        else if(!operationMerge(element.key, element.value, map2[element.key]!!)) return false
//    return true
//}
//
//public inline fun <K, V1, V2> mergingNone(
//    map1: Map<out K, V1>,
//    map2: Map<out K, V2>,
//    operation1: (Map.Entry<K, V1>) -> Boolean,
//    operation2: (Map.Entry<K, V2>) -> Boolean,
//    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
//): Boolean {
//    for (element in map2) if (element.key !in map1 && operation2(element)) return false
//    for (element in map1)
//        if (element.key !in map2) { if(operation1(element)) return false }
//        else if(operationMerge(element.key, element.value, map2[element.key]!!)) return false
//    return true
//}