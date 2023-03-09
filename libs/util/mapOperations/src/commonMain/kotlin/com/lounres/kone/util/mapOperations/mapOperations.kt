/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.util.mapOperations

import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or `null` if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the received value.
 * @return result of the computation of the lambda.
 */
public inline fun <K, V, R> Map<in K, V>.computeOn(key: K, compute: (V?) -> R): R {
    contract {
        callsInPlace(compute, EXACTLY_ONCE)
    }
    return compute(get(key))
}

/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or computes the given lambda
 * [defaultResult] if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the value corresponding to the [key].
 * @param defaultResult lambda that is computed if the [key] is not present.
 * @return result of [compute] lambda if the [key] is present or result of [defaultResult] otherwise.
 */
public inline fun <K, V, R> Map<K, V>.computeOnOrElse(key: K, defaultResult: () -> R, compute: (value: V) -> R): R {
    contract {
        callsInPlace(defaultResult, AT_MOST_ONCE)
        callsInPlace(compute, AT_MOST_ONCE)
    }
    @Suppress("UNCHECKED_CAST")
    return (if (key !in this) defaultResult() else compute(get(key) as V))
}

/**
 * Computes the given lambda [compute] on value corresponding to the provided [key] or computes the given lambda
 * [defaultResult] if the key is not present.
 *
 * @param key key which corresponding value will be used if it's present.
 * @param compute lambda that is computed on the value corresponding to the [key].
 * @param defaultResult default result that is returned in case of the [key]'s absence.
 * @return result of [compute] lambda if the [key] is present or [defaultResult] otherwise.
 */
public inline fun <K, V, R> Map<K, V>.computeOnOrElse(key: K, defaultResult: R, compute: (key: K, value: V) -> R): R {
    contract {
        callsInPlace(compute, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, { defaultResult }, { it -> compute(key, it) })
}

/**
 * Applies the [transformation][transform] to the value corresponding to the given [key] or null instead if it's not
 * present.
 *
 * @param key key to check.
 * @param transform transformation to apply.
 * @return result of the transformation
 */
public inline fun <K, V> MutableMap<in K, V>.applyToKey(key: K, transform: (currentValue: V?) -> V): V {
    contract {
        callsInPlace(transform, EXACTLY_ONCE)
    }
    return computeOn(key, transform).also { this[key] = it }
}

/**
 * Depending on presence of value corresponding to the given [key] either puts new value calculated by [valueOnPut] or
 * changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut lazily calculated value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value as a parameter.
 * @return result value corresponding to the [key].
 */
public inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): V {
    contract {
        callsInPlace(valueOnPut, AT_MOST_ONCE)
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, valueOnPut, transformOnChange).also { this[key] = it }
}

/**
 * Depending on presence of value corresponding to the given [key] either puts new value [valueOnPut] or
 * changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * the [key], current value, and new value as parameters.
 * @return result value corresponding to the [key].
 */
public inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: V, transformOnChange: (key: K, currentValue: V, newValue: V) -> V): V {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return putOrChange<K, V>(key, { valueOnPut }, { transformOnChange(key, it, valueOnPut) })
}

/**
 * Creates copy of [the map][this] and applies the [transformation][transform] to the value corresponding to the given
 * [key] in the copy or null instead if it's not present.
 *
 * @param key key to check.
 * @param transform transformation to apply.
 * @return the copy of [the map][this].
 */
public inline fun <K, V> Map<in K, V>.withAppliedToKey(key: K, transform: (currentValue: V?) -> V): Map<K, V> {
    contract {
        callsInPlace(transform, EXACTLY_ONCE)
    }
    return buildMap(size) {
        putAll(this)
        applyToKey(key, transform)
    }
}

/**
 * Creates copy of [the map][this] and depending on presence of value corresponding to the given [key] either puts new
 * value calculated by [valueOnPut] or changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut lazily calculated value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * current value as a parameter.
 * @return the copy of [the map][this].
 */
public inline fun <K, V> Map<out K, V>.withPutOrChanged(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): Map<K, V> {
    contract {
        callsInPlace(valueOnPut, AT_MOST_ONCE)
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return buildMap(size + 1) {
        putAll(this@withPutOrChanged)
        putOrChange(key, valueOnPut, transformOnChange)
    }
}

/**
 * Creates copy of [the map][this] and depending on presence of value corresponding to the given [key] either puts new
 * value [valueOnPut] or changes the present value with [transformOnChange].
 *
 * @param key key to check.
 * @param valueOnPut value to put in case of absence of the [key].
 * @param transformOnChange transform to apply to current value corresponding to the [key] in case of its presence. Uses
 * the [key], current value, and new value as parameters.
 * @return the copy of [the map][this].
 */
public inline fun <K, V> Map<out K, V>.withPutOrChanged(key: K, valueOnPut: V, transformOnChange: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> {
    contract {
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return withPutOrChanged<K, V>(key, { valueOnPut }, { transformOnChange(key, it, valueOnPut) })
}

/**
 * Copies entries of [this map][this] to the [destination] map overriding present ones if needed.
 *
 * @receiver map to be copied.
 * @param destination map to receive copies.
 * @return the [destination].
 */
public fun <K, V, D: MutableMap<K, V>> Map<out K, V>.copyTo(destination: D): D {
    for ((key, value) in this) {
        destination[key] = value
    }
    return destination
}

/**
 * Copies entries of [this map][this] to the [destination] map merging present entries with new ones using [resolve]
 * lambda.
 *
 * @receiver map to be copied.
 * @param destination map to receive copies.
 * @param resolve lambda function that resolves overriding. It takes a key, current value corresponding to the key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
public inline fun <K, V: W, W, D: MutableMap<K, W>> Map<out K, V>.copyToBy(destination: D, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for ((key, value) in this) {
        destination.putOrChange(key, value) { _, it, _ -> resolve(key, it, value) }
    }
    return destination
}

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map overriding present ones if needed. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyTo(destination)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @return the [destination].
 */
public inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapTo(destination: D, transform: (Map.Entry<K, V>) -> W): D {
    for (entry in this) {
        destination[entry.key] = transform(entry)
    }
    return destination
}

/**
 * Transforms values of entries of [this map][this] with [the given transformation][transform] and copies resulting
 * entries to the [destination] map merging present entries with new ones using [resolve] lambda. Is equivalent to
 * ```kotlin
 * this.mapValues(transform).copyToBy(destination, resolve)
 * ```
 *
 * @receiver map to be transformed and copied.
 * @param destination map to receive copies.
 * @param transform generates value of transformed entry using initial entry as an argument. Key of transformed entry is
 * the same as initial entry.
 * @param resolve lambda function that resolves overriding. It takes a key, current value corresponding to the key, and
 * a new one and returns value to associate to the key.
 * @return the [destination].
 */
public inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for (entry in this) {
        val (key, value) = entry
        destination.putOrChange(key, transform(entry)) { _, it, _ -> resolve(key, it, value) }
    }
    return destination
}

/**
 * Merges [the first map][map1] and [the second map][map2] prioritising the second one, puts result to the [destination]
 * and returns the [destination].
 *
 * Precisely, corresponding keys and values of the received maps are put into the destination overriding existing values
 * in the [destination] if needed. For every key appearing in both maps corresponding value from the second map is
 * chosen.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @param destination the map where result of the merge is put.
 * @return the destination.
 */
public fun <K, V, D: MutableMap<in K, in V>> mergeTo(map1: Map<out K, V>, map2: Map<out K, V>, destination: D): D {
    for ((key, value) in map1) {
        destination.put(key, value)
    }
    for ((key, value) in map2) {
        destination.put(key, value)
    }
    return destination
}

/**
 * Merges [the first map][map1] and [the second map][map2] resolving conflicts with [resolve] lambda, puts result to the
 * [destination] and returns the [destination].
 *
 * Precisely, corresponding keys and values of the received maps are put into the destination overriding existing values
 * in the [destination] if needed. For every key appearing in both maps corresponding value is a result of the [resolve]
 * lambda calculated on the key and its corresponding values from the merged maps.
 *
 * @param map1 the first map to merge.
 * @param map2 the second map to merge.
 * @param resolve lambda function that resolves merge conflicts.
 * @param destination the map where the result of the merge is put.
 * @return the destination.
 */
public inline fun <K, V1: W, V2: W, W, D: MutableMap<K, W>> mergeToBy(map1: Map<out K, V1>, map2: Map<out K, V2>, destination: D, resolve: (key: K, value1: V1, value2: V2) -> W): D {
    for (key in map2.keys) {
        destination.remove(key)
    }
    for ((key, value) in map1) {
        destination.put(key, value)
    }
    for ((key, value) in map2) {
        @Suppress("UNCHECKED_CAST")
        destination.putOrChange(key, value) { _, it, _ -> resolve(key, it as V1, value) }
    }
    return destination
}

/**
 * Merges [the first map][map1] and [the second map][map2] prioritising the second one.
 *
 * Precisely, corresponding keys and values of the received maps are put into a new empty map which is returned after
 * afterwards. For every key appearing in both maps corresponding value from the second map is chosen.
 *
 * @param map1 the first (less prioritised) map to merge.
 * @param map2 the second (more prioritised) map to merge.
 * @return the result of the merge.
 */
public fun <K, V1: W, V2: W, W> merge(map1: Map<out K, V1>, map2: Map<out K, V2>): Map<K, W> {
    val result = LinkedHashMap<K, W>(map1.size + map2.size)
    return mergeTo(map1, map2, result)
}

/**
 * Merges [the first map][map1] and [the second map][map2] resolving conflicts with [resolve] lambda.
 *
 * Precisely, corresponding keys and values of the received maps are put into a new empty map which is returned after
 * afterwards. For every key appearing in both maps corresponding value is a result of the [resolve] lambda calculated
 * on the key and its corresponding values from the merged maps.
 *
 * @param map1 the first map to merge.
 * @param map2 the second map to merge.
 * @param resolve lambda function that resolves merge conflicts.
 * @return the result of the merge.
 */
public inline fun <K, V1: W, V2: W, W> mergeBy(map1: Map<out K, V1>, map2: Map<out K, V2>, resolve: (key: K, value1: V1, value2: V2) -> W): Map<K, W> {
    val result = LinkedHashMap<K, W>(map1.size + map2.size)
    return mergeToBy(map1, map2, result, resolve)
}

/**
 * Populates the [destination] map with key-value pairs provided by [transform] function applied to each element of the
 * given collection resolving conflicts with [resolve] function and returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each element to key-value.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
public inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateTo(destination: D, transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val (key, value) = transform(element)
        destination.putOrChange(key, value, resolve)
    }
    return destination
}

/**
 * Populates the [destination] map with key-value pairs, where key is provided by [keySelector] function and value is
 * provided by [valueTransform] applied to each element of the given collection, resolving conflicts with [resolve]
 * function and returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param valueTransform lambda functions that generates value for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
public inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val value = valueTransform(element)
        destination.putOrChange(key, value, resolve)
    }
    return destination
}

/**
 * Populates the [destination] map with key-value pairs, where key is provided by [keySelector] function applied to each
 * element of the given collection and value is the element itself, resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
public inline fun <T, K, D : MutableMap<K, T>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): D {
    for (element in this) {
        val key = keySelector(element)
        destination.putOrChange(key, element, resolve)
    }
    return destination
}

/**
 * Returns a map containing key-value pairs provided by [transform] function applied to elements of the given collection.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with the
 * key is resolved with [resolve] function which takes the key, current value corresponding to the key, and new value
 * from the pair.
 *
 * @param transform function which transforms each element to key-value pair.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
public inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
    associateTo(LinkedHashMap(), transform, resolve)

/**
 * Returns a map containing the values provided by [valueTransform] and indexed by [keySelector] functions applied to
 * elements of the given collection.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes the key, current value corresponding to the key, and new
 * value from the pair.
 *
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param valueTransform lambda functions that generates value for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
public inline fun <T, K, V> Iterable<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
    associateByTo(LinkedHashMap(), keySelector, valueTransform, resolve)

/**
 * Returns a map containing the elements from the given collection indexed by the key returned from [keySelector]
 * function applied to each element.
 *
 * All pairs are added in order of iteration. If some key is already added to the map, adding new key-value pair with
 * the key is resolved with [resolve] function which takes the key, current value corresponding to the key, and new
 * value from the pair.
 *
 * @param keySelector lambda functions that generates keys for the key-value pairs.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
public inline fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): Map<K, T> =
    associateByTo(LinkedHashMap(), keySelector, resolve)

/**
 * Populates the given [destination] map with entries having the keys of this map and the values obtained
 * by applying the [transform] function to each entry in this map resolving conflicts with [resolve] function and
 * returns the [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new value.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
public inline fun <K, V, W, D : MutableMap<K, W>> Map<out K, V>.mapValuesTo(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: W) -> W): D =
    entries.associateByTo(destination, { it.key }, transform, resolve)

/**
 * Populates the given [destination] map with entries having the keys obtained by applying the [transform] function to
 * each entry in this map and the values of this map, resolving conflicts with [resolve] function and returns the
 * [destination].
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param destination the destination of the generated key-value pairs.
 * @param transform function which transforms each key-value pair to new key.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the [destination].
 */
public inline fun <K, V, L, D : MutableMap<L, V>> Map<out K, V>.mapKeysTo(destination: D, transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): D =
    entries.associateByTo(destination, transform, { it.value }, resolve)

/**
 * Returns a new map with entries having the keys obtained by applying the [transform] function to each entry in this
 * map and the values of this map and resolving conflicts with [resolve] function.
 *
 * All pairs are added and resolved in order of iteration.
 *
 * @param transform function which transforms each key-value pair to a new key.
 * @param resolve lambda function that resolves merge conflicts which receives some key, its current, and new
 * corresponding values.
 * @return the result map.
 */
public inline fun <K, V, L> Map<out K, V>.mapKeys(transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): Map<L, V> =
    mapKeysTo(LinkedHashMap(size), transform, resolve)

/**
 * Accumulates value starting with [initial] value and applying [operation]
 * to current accumulator value and each entry of the map.
 *
 * Returns the specified [initial] value if the map is empty.
 *
 * @param initial initial value of the accumulation.
 * @param operation function that takes current accumulator value and an entry of the map and calculates the next accumulator value.
 */
public inline fun <K, V, R> Map<out K, V>.fold(initial: R, operation: (acc: R, Map.Entry<K, V>) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

/**
 * Lazily applies the given [transform] function to each entry of the map getting a new element
 * and accumulates value starting with the first element and applying [operation] one-by-one
 * to current accumulator value and each element.
 *
 * Iterator of the map's entries is obtained with `entries.iterator()` method.
 * Thus, iteration order is specified by the map's `entries` set `iterator` implementation.
 *
 * Throws an exception if this map is empty. If the map can be empty in an expected way,
 * please use [mapReduceOrNull] instead. It returns `null` when its receiver is empty.
 *
 * @param transform function which transforms each key-value pair to a new element which will be processed with given [operation].
 * @param operation function that takes current accumulator value and transformed into a new element entry of the map and calculates the next accumulator value.
 */
@Suppress("UNREACHABLE_CODE")
public inline fun <K, V, T: R, R> Map<out K, V>.mapReduce(transform: (Map.Entry<K, V>) -> T, operation: (acc: R, T) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = transform(iterator.next())
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, transform(iterator.next()))
    }
    return accumulator
}

/**
 * Lazily applies the given [transform] function to each entry of the map getting a new element
 * and accumulates value starting with the first element and applying [operation] one-by-one
 * to current accumulator value and each element.
 *
 * Iterator of the map's entries is obtained with `entries.iterator()` method.
 * Thus, iteration order is specified by the map's `entries` set `iterator` implementation.
 *
 * Returns null if this map is empty.
 *
 * @param transform function which transforms each key-value pair to a new element which will be processed with given [operation].
 * @param operation function that takes current accumulator value and transformed into a new element entry of the map and calculates the next accumulator value.
 */
public inline fun <K, V, T: R, R> Map<out K, V>.mapReduceOrNull(transform: (Map.Entry<K, V>) -> T, operation: (acc: R, T) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = transform(iterator.next())
    while (iterator.hasNext()) {
        accumulator = operation(accumulator, transform(iterator.next()))
    }
    return accumulator
}

/**
 * Performs [operation1] on each `key`-`value` pair from `map1` which `key` does not appear in `map2`, performs [operation2]
 * on each `key`-`value` pair from `map2` which `key` does not appear in `map1`, and [operationMerge] on each
 * `key`-`value1`-`value2` triple where `key`-`value1` is an entry of `map1` and `key`-`value2` is an entry of `map2`.
 *
 * There is no guaranty of order in which the elements will be processed.
 */
public inline fun <K, V1, V2> mergingForEach(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Unit,
    operation2: (Map.Entry<K, V2>) -> Unit,
    operationMerge: (key: K, value1: V1, value2: V2) -> Unit
) {
    for (element in map1) if (element.key !in map2) operation1(element)
    for (element in map2)
        if (element.key !in map1) operation2(element)
        else operationMerge(element.key, map1[element.key]!!, element.value)
}

/**
 * Accumulates value starting with [initial] value and applying
 * [operation1] to current accumulator value and each entry of [map1] which key does not appear in [map2],
 * [operation2] to current accumulator value and each entry of [map2] which key does not appear in [map2],
 * and [operationMerge] to current accumulator value and triple of a common key of the maps and both its corresponding values in [map1] and [map2].
 *
 * @param map1 the first map to fold with another.
 * @param map2 the second map to fold with another.
 * @param initial initial value of the accumulation.
 * @param operation1 function that takes current accumulator value and an entry of the [map1] which key does not appear in [map2] and calculates the next accumulator value.
 * @param operation2 function that takes current accumulator value and an entry of the [map2] which key does not appear in [map1] and calculates the next accumulator value.
 * @param operationMerge function that takes current accumulator value and triple `(key, value1, value2)` where `key=value1` is an entry of [map1] and `key=value2` is an entry of [map2] and calculates the next accumulator value.
 */
public inline fun <K, V1, V2, R> mergingFold(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    initial: R,
    operation1: (acc: R, Map.Entry<K, V1>) -> R,
    operation2: (acc: R, Map.Entry<K, V2>) -> R,
    operationMerge: (acc: R, key: K, value1: V1, value2: V2) -> R
): R {
    var accumulator = initial
    for (element in map1) if (element.key !in map1) accumulator = operation1(accumulator, element)
    for (element in map2) accumulator =
        if (element.key !in map1) operation2(accumulator, element)
        else operationMerge(accumulator, element.key, map1[element.key]!!, element.value)
    return accumulator
}

/**
 * Returns `false` if:
 * - for every entry `e` of [map1] which key does not appear in [map2] result of [operation1]`(e)` is `false`,
 * - for every entry `e` of [map2] which key does not appear in [map1] result of [operation2]`(e)` is `false`,
 * - for every key `k` appearing in [map2] with `v1` and in [map2] with `v2` result of [operationMerge]`(k, v1, v2)` is `false`.
 * Returns `true` otherwise.
 *
 * @param map1 the first map to test with another.
 * @param map2 the second map to test with another.
 * @param operation1 predicate that tests an entry of the [map1] which key does not appear in [map2].
 * @param operation2 predicate that tests an entry of the [map2] which key does not appear in [map1].
 * @param operationMerge predicate that tests triple `(key, value1, value2)` where `key=value1` is an entry of [map1] and `key=value2` is an entry of [map2].
 */
public inline fun <K, V1, V2> mergingAny(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Boolean,
    operation2: (Map.Entry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map1) if (element.key !in map2 && operation1(element)) return true
    for (element in map2)
        if (element.key !in map1) { if (operation2(element)) return true }
        else { if (operationMerge(element.key, map1[element.key]!!, element.value)) return true }
    return false
}

/**
 * Returns `true` if:
 * - for every entry `e` of [map1] which key does not appear in [map2] result of [operation1]`(e)` is `true`,
 * - for every entry `e` of [map2] which key does not appear in [map1] result of [operation2]`(e)` is `true`,
 * - for every key `k` appearing in [map2] with `v1` and in [map2] with `v2` result of [operationMerge]`(k, v1, v2)` is `true`.
 * Returns `false` otherwise.
 *
 * @param map1 the first map to test with another.
 * @param map2 the second map to test with another.
 * @param operation1 predicate that tests an entry of the [map1] which key does not appear in [map2].
 * @param operation2 predicate that tests an entry of the [map2] which key does not appear in [map1].
 * @param operationMerge predicate that tests triple `(key, value1, value2)` where `key=value1` is an entry of [map1] and `key=value2` is an entry of [map2].
 */
public inline fun <K, V1, V2> mergingAll(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Boolean,
    operation2: (Map.Entry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map1) if (element.key !in map2 && !operation1(element)) return false
    for (element in map2)
        if (element.key !in map1) { if (!operation2(element)) return false }
        else { if (!operationMerge(element.key, map1[element.key]!!, element.value)) return false }
    return true
}

/**
 * Returns `true` if:
 * - for every entry `e` of [map1] which key does not appear in [map2] result of [operation1]`(e)` is `false`,
 * - for every entry `e` of [map2] which key does not appear in [map1] result of [operation2]`(e)` is `false`,
 * - for every key `k` appearing in [map2] with `v1` and in [map2] with `v2` result of [operationMerge]`(k, v1, v2)` is `false`.
 * Returns `false` otherwise.
 *
 * @param map1 the first map to test with another.
 * @param map2 the second map to test with another.
 * @param operation1 predicate that tests an entry of the [map1] which key does not appear in [map2].
 * @param operation2 predicate that tests an entry of the [map2] which key does not appear in [map1].
 * @param operationMerge predicate that tests triple `(key, value1, value2)` where `key=value1` is an entry of [map1] and `key=value2` is an entry of [map2].
 */
public inline fun <K, V1, V2> mergingNone(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Boolean,
    operation2: (Map.Entry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map1) if (element.key !in map2 && operation1(element)) return false
    for (element in map2)
        if (element.key !in map1) { if (operation2(element)) return false }
        else { if (operationMerge(element.key, map1[element.key]!!, element.value)) return false }
    return true
}