/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.util.mapOperations

import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


/**
 * Computes [compute] on the value associated with [key], or on `null` if the key is absent.
 *
 * The [compute] lambda is invoked exactly once.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @param R The return type of [compute].
 * @receiver This map to read from.
 * @param key The key whose associated value is passed to [compute], or `null` if the key is absent.
 * @param compute Called with the value associated with [key], or with `null` if the key is absent.
 * @return The result returned by [compute].
 */
public inline fun <K, V, R> Map<in K, V>.computeOn(key: K, compute: (V?) -> R): R {
    contract {
        callsInPlace(compute, EXACTLY_ONCE)
    }
    return compute(get(key))
}

/**
 * Computes [compute] on the value associated with [key], or [defaultResult] if the key is absent.
 *
 * Exactly one of [compute] or [defaultResult] is invoked.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @param R The common return type of [compute] and [defaultResult].
 * @receiver This map to read from.
 * @param key The key whose associated value is passed to [compute] when present.
 * @param defaultResult Called when [key] is absent; its result is returned instead of invoking [compute].
 * @param compute Called with the non-null value associated with [key] when the key is present.
 * @return The result of [compute] if [key] is present, or the result of [defaultResult] otherwise.
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
 * Applies [transform] to the value associated with [key] (or to `null` if absent) and stores the result.
 *
 * The [transform] lambda is invoked exactly once. The map is updated in place.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @receiver This mutable map to update.
 * @param key The key whose associated value is transformed and written back.
 * @param transform Called with the current value associated with [key], or with `null` if the key is absent; returns the new value to store.
 * @return The value stored under [key] after the transformation.
 */
@IgnorableReturnValue
public inline fun <K, V> MutableMap<in K, V>.applyToKey(key: K, transform: (currentValue: V?) -> V): V {
    contract {
        callsInPlace(transform, EXACTLY_ONCE)
    }
    return computeOn(key, transform).also { this[key] = it }
}

/**
 * Inserts or updates the value associated with [key] in this mutable map.
 *
 * If [key] is absent, [valueOnPut] is invoked and its result is stored. If [key] is present, [transformOnChange]
 * is invoked on the current value and the result is stored. Exactly one of [valueOnPut] or [transformOnChange]
 * is invoked.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @receiver This mutable map to update.
 * @param key The key to insert or update.
 * @param valueOnPut Called when [key] is absent; its result is stored as the new value.
 * @param transformOnChange Called with the current value when [key] is present; its result replaces the stored value.
 * @return The value stored under [key] after the operation.
 */
@IgnorableReturnValue
public inline fun <K, V> MutableMap<K, V>.putOrChange(key: K, valueOnPut: () -> V, transformOnChange: (currentValue: V) -> V): V {
    contract {
        callsInPlace(valueOnPut, AT_MOST_ONCE)
        callsInPlace(transformOnChange, AT_MOST_ONCE)
    }
    return computeOnOrElse(key, valueOnPut, transformOnChange).also { this[key] = it }
}

/**
 * Returns a copy of this map with [transform] applied to the value associated with [key].
 *
 * If [key] is absent in the copy, [transform] receives `null`. The original map is not modified.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @receiver This map to copy.
 * @param key The key whose associated value is transformed in the copy.
 * @param transform Called with the current value associated with [key] in the copy, or with `null` if the key is absent.
 * @return A new map equal to this map except for the transformed entry at [key].
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
 * Returns a copy of this map with [putOrChange] applied to [key].
 *
 * If [key] is absent in the copy, [valueOnPut] supplies the new value. If [key] is present, [transformOnChange]
 * updates the existing value. The original map is not modified.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @receiver This map to copy.
 * @param key The key to insert or update in the copy.
 * @param valueOnPut Called when [key] is absent in the copy; its result is stored.
 * @param transformOnChange Called with the current value when [key] is present in the copy; its result replaces the stored value.
 * @return A new map equal to this map except for the entry inserted or updated at [key].
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
 * Copies all entries from this map into [destination], overwriting existing entries with the same keys.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @param D The destination map type.
 * @receiver This map whose entries are copied.
 * @param destination The mutable map that receives copies of all entries from this map.
 * @return [destination] after all entries have been copied.
 */
@IgnorableReturnValue
public fun <K, V, D: MutableMap<in K, in V>> Map<out K, V>.copyTo(destination: D): D {
    for ((val key, val value) in this) {
        destination[key] = value
    }
    return destination
}

/**
 * Copies all entries from this map into [destination], merging with existing entries using [resolve].
 *
 * For each entry in this map, if [destination] already contains the key, [resolve] chooses the stored value from
 * the current destination value and the new value from this map; otherwise the new value is inserted.
 *
 * @param K The key type of this map.
 * @param V The value type of entries in this map.
 * @param W The common value type stored in [destination].
 * @param D The destination map type.
 * @receiver This map whose entries are copied.
 * @param destination The mutable map that receives merged entries.
 * @param resolve Called when a key from this map already exists in [destination]; receives the key, the current
 * destination value, and the new value from this map, and returns the value to store.
 * @return [destination] after all entries have been merged.
 */
@IgnorableReturnValue
public inline fun <K, V: W, W, D: MutableMap<in K, W>> Map<out K, V>.copyToBy(destination: D, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for ((val key, val value) in this) {
        destination.putOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

/**
 * Transforms each entry of this map with [transform] and copies the results into [destination].
 *
 * Keys are preserved; values are replaced by [transform]. Existing entries in [destination] with the same keys are
 * overwritten. Equivalent to `this.mapValues(transform).copyTo(destination)`.
 *
 * @param K The key type of this map.
 * @param V The value type of entries in this map.
 * @param W The transformed value type stored in [destination].
 * @param D The destination map type.
 * @receiver This map whose entries are transformed and copied.
 * @param destination The mutable map that receives transformed entries.
 * @param transform Called with each entry of this map; its result becomes the value stored under the entry's key.
 * @return [destination] after all transformed entries have been copied.
 */
@IgnorableReturnValue
public inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapTo(destination: D, transform: (Map.Entry<K, V>) -> W): D {
    for (entry in this) {
        destination[entry.key] = transform(entry)
    }
    return destination
}

/**
 * Transforms each entry of this map with [transform] and merges the results into [destination] using [resolve].
 *
 * Keys are preserved; values are replaced by [transform]. When a transformed key already exists in [destination],
 * [resolve] chooses the stored value. Equivalent to `this.mapValues(transform).copyToBy(destination, resolve)`.
 *
 * @param K The key type of this map.
 * @param V The value type of entries in this map.
 * @param W The transformed value type stored in [destination].
 * @param D The destination map type.
 * @receiver This map whose entries are transformed and merged.
 * @param destination The mutable map that receives transformed entries.
 * @param transform Called with each entry of this map; its result is the new value candidate for the entry's key.
 * @param resolve Called when a transformed key already exists in [destination]; receives the key, the current
 * destination value, and the original entry value from this map, and returns the value to store.
 * @return [destination] after all transformed entries have been merged.
 */
@IgnorableReturnValue
public inline fun <K, V, W, D: MutableMap<K, W>> Map<out K, V>.copyMapToBy(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: V) -> W): D {
    for (entry in this) {
        val (key, value) = entry
        destination.putOrChange(key, { transform(entry) }, { resolve(key, it, value) })
    }
    return destination
}

/**
 * Merges [map1] and [map2] into [destination], with [map2] taking precedence on key conflicts.
 *
 * All entries from [map1] are copied first, then all entries from [map2]; entries in [destination] with matching
 * keys are overwritten.
 *
 * @param K The key type of the merged maps.
 * @param V The value type of the merged maps.
 * @param D The destination map type.
 * @param map1 The first map to merge; its entries are overwritten by [map2] on key conflicts.
 * @param map2 The second map to merge; its entries take precedence on key conflicts.
 * @param destination The mutable map that receives the merged entries.
 * @return [destination] after the merge completes.
 */
@IgnorableReturnValue
public fun <K, V, D: MutableMap<in K, in V>> mergeTo(map1: Map<out K, V>, map2: Map<out K, V>, destination: D): D {
    for ((val key, val value) in map1) {
        destination[key] = value
    }
    for ((val key, val value) in map2) {
        destination[key] = value
    }
    return destination
}

/**
 * Merges [map1] and [map2] into [destination], resolving key conflicts with [resolve].
 *
 * All entries from [map1] are copied first, then entries from [map2] are merged with [putOrChange]. For keys present
 * in both maps, [resolve] chooses the stored value.
 *
 * @param K The key type of the merged maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param W The common value type stored in [destination].
 * @param D The destination map type.
 * @param map1 The first map to merge.
 * @param map2 The second map to merge.
 * @param destination The mutable map that receives the merged entries.
 * @param resolve Called when a key from [map2] already exists in [destination] after [map1] has been copied; receives
 * the key, the value from [map1], and the value from [map2], and returns the value to store.
 * @return [destination] after the merge completes.
 */
@IgnorableReturnValue
public inline fun <K, V1: W, V2: W, W, D: MutableMap<K, W>> mergeToBy(map1: Map<out K, V1>, map2: Map<out K, V2>, destination: D, resolve: (key: K, value1: V1, value2: V2) -> W): D {
    for (key in map2.keys) {
        destination.remove(key)
    }
    for ((val key, val value) in map1) {
        destination[key] = value
    }
    for ((val key, val value) in map2) {
        @Suppress("UNCHECKED_CAST")
        destination.putOrChange(key, { value }, { resolve(key, it as V1, value) })
    }
    return destination
}

/**
 * Merges [map1] and [map2] into a new map, with [map2] taking precedence on key conflicts.
 *
 * Returns a new [LinkedHashMap] containing all entries from [map1] followed by all entries from [map2]; for keys
 * present in both maps, the value from [map2] is kept.
 *
 * @param K The key type of the merged maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param W The common value type of the result map.
 * @param map1 The first map to merge; its entries are overwritten by [map2] on key conflicts.
 * @param map2 The second map to merge; its entries take precedence on key conflicts.
 * @return A new map containing the merged entries.
 */
public fun <K, V1: W, V2: W, W> merge(map1: Map<out K, V1>, map2: Map<out K, V2>): Map<K, W> {
    val result = LinkedHashMap<K, W>(map1.size + map2.size)
    return mergeTo(map1, map2, result)
}

/**
 * Merges [map1] and [map2] into a new map, resolving key conflicts with [resolve].
 *
 * Returns a new [LinkedHashMap]. For keys present in both maps, [resolve] chooses the stored value.
 *
 * @param K The key type of the merged maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param W The common value type of the result map.
 * @param map1 The first map to merge.
 * @param map2 The second map to merge.
 * @param resolve Called for each key present in both maps; receives the key and the corresponding values from [map1]
 * and [map2], and returns the value to store.
 * @return A new map containing the merged entries.
 */
public inline fun <K, V1: W, V2: W, W> mergeBy(map1: Map<out K, V1>, map2: Map<out K, V2>, resolve: (key: K, value1: V1, value2: V2) -> W): Map<K, W> {
    val result = LinkedHashMap<K, W>(map1.size + map2.size)
    return mergeToBy(map1, map2, result, resolve)
}

/**
 * Populates [destination] with key–value pairs produced from each element of this iterable, resolving conflicts with [resolve].
 *
 * Pairs are added in iteration order. When a key already exists in [destination], [resolve] chooses the stored value.
 *
 * @param T The element type of this iterable.
 * @param K The key type of the destination map.
 * @param V The value type of the destination map.
 * @param D The destination map type.
 * @receiver This iterable whose elements are associated into [destination].
 * @param destination The mutable map that receives generated key–value pairs.
 * @param transform Called with each element; returns the key–value pair to insert.
 * @param resolve Called when a generated key already exists in [destination]; receives the key, the current value, and
 * the new value, and returns the value to store.
 * @return [destination] after all pairs have been added.
 */
@IgnorableReturnValue
public inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateTo(destination: D, transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        [val key, val value] = transform(element)
        destination.putOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

/**
 * Populates [destination] with key–value pairs derived from each element of this iterable, resolving conflicts with [resolve].
 *
 * For each element, [keySelector] produces the key and [valueTransform] produces the value. Pairs are added in
 * iteration order; duplicate keys are resolved with [resolve].
 *
 * @param T The element type of this iterable.
 * @param K The key type of the destination map.
 * @param V The value type of the destination map.
 * @param D The destination map type.
 * @receiver This iterable whose elements are associated into [destination].
 * @param destination The mutable map that receives generated key–value pairs.
 * @param keySelector Called with each element; returns the key for the generated pair.
 * @param valueTransform Called with each element; returns the value for the generated pair.
 * @param resolve Called when a generated key already exists in [destination]; receives the key, the current value, and
 * the new value, and returns the value to store.
 * @return [destination] after all pairs have been added.
 */
@IgnorableReturnValue
public inline fun <T, K, V, D : MutableMap<K, V>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): D {
    for (element in this) {
        val key = keySelector(element)
        val value = valueTransform(element)
        destination.putOrChange(key, { value }, { resolve(key, it, value) })
    }
    return destination
}

/**
 * Populates [destination] by indexing each element of this iterable with [keySelector], resolving conflicts with [resolve].
 *
 * For each element, [keySelector] produces the key and the element itself becomes the value. Pairs are added in
 * iteration order; duplicate keys are resolved with [resolve].
 *
 * @param T The element type of this iterable.
 * @param K The key type of the destination map.
 * @param D The destination map type.
 * @receiver This iterable whose elements are associated into [destination].
 * @param destination The mutable map that receives generated key–value pairs.
 * @param keySelector Called with each element; returns the key for the generated pair.
 * @param resolve Called when a generated key already exists in [destination]; receives the key, the current value, and
 * the new value, and returns the value to store.
 * @return [destination] after all pairs have been added.
 */
@IgnorableReturnValue
public inline fun <T, K, D : MutableMap<K, T>> Iterable<T>.associateByTo(destination: D, keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): D {
    for (element in this) {
        val key = keySelector(element)
        destination.putOrChange(key, { element }, { resolve(key, it, element) })
    }
    return destination
}

/**
 * Returns a map of key–value pairs produced from each element of this iterable, resolving duplicate keys with [resolve].
 *
 * Pairs are added in iteration order into a new [LinkedHashMap]. When a key already exists, [resolve] chooses the
 * stored value.
 *
 * @param T The element type of this iterable.
 * @param K The key type of the result map.
 * @param V The value type of the result map.
 * @receiver This iterable whose elements are associated into the result map.
 * @param transform Called with each element; returns the key–value pair to insert.
 * @param resolve Called when a generated key already exists; receives the key, the current value, and the new value,
 * and returns the value to store.
 * @return A new map containing the generated key–value pairs.
 */
public inline fun <T, K, V> Iterable<T>.associate(transform: (T) -> Pair<K, V>, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
    associateTo(LinkedHashMap(), transform, resolve)

/**
 * Returns a map keyed by [keySelector] with values from [valueTransform], resolving duplicate keys with [resolve].
 *
 * Pairs are added in iteration order into a new [LinkedHashMap].
 *
 * @param T The element type of this iterable.
 * @param K The key type of the result map.
 * @param V The value type of the result map.
 * @receiver This iterable whose elements are associated into the result map.
 * @param keySelector Called with each element; returns the key for the generated pair.
 * @param valueTransform Called with each element; returns the value for the generated pair.
 * @param resolve Called when a generated key already exists; receives the key, the current value, and the new value,
 * and returns the value to store.
 * @return A new map containing the generated key–value pairs.
 */
public inline fun <T, K, V> Iterable<T>.associateBy(keySelector: (T) -> K, valueTransform: (T) -> V, resolve: (key: K, currentValue: V, newValue: V) -> V): Map<K, V> =
    associateByTo(LinkedHashMap(), keySelector, valueTransform, resolve)

/**
 * Returns a map keyed by [keySelector] with elements of this iterable as values, resolving duplicate keys with [resolve].
 *
 * Pairs are added in iteration order into a new [LinkedHashMap].
 *
 * @param T The element type of this iterable.
 * @param K The key type of the result map.
 * @receiver This iterable whose elements are associated into the result map.
 * @param keySelector Called with each element; returns the key for the generated pair.
 * @param resolve Called when a generated key already exists; receives the key, the current value, and the new value,
 * and returns the value to store.
 * @return A new map containing the generated key–value pairs.
 */
public inline fun <T, K> Iterable<T>.associateBy(keySelector: (T) -> K, resolve: (key: K, currentValue: T, newValue: T) -> T): Map<K, T> =
    associateByTo(LinkedHashMap(), keySelector, resolve)

/**
 * Populates [destination] with entries that keep the keys of this map and transform values with [transform].
 *
 * Duplicate keys in [destination] are resolved with [resolve]. Pairs are added in iteration order.
 *
 * @param K The key type of this map.
 * @param V The value type of entries in this map.
 * @param W The transformed value type stored in [destination].
 * @param D The destination map type.
 * @receiver This map whose values are transformed into [destination].
 * @param destination The mutable map that receives transformed entries.
 * @param transform Called with each entry of this map; its result becomes the value candidate for the entry's key.
 * @param resolve Called when a key already exists in [destination]; receives the key, the current value, and the new
 * transformed value, and returns the value to store.
 * @return [destination] after all transformed entries have been added.
 */
@IgnorableReturnValue
public inline fun <K, V, W, D : MutableMap<K, W>> Map<out K, V>.mapValuesTo(destination: D, transform: (Map.Entry<K, V>) -> W, resolve: (key: K, currentValue: W, newValue: W) -> W): D =
    entries.associateByTo(destination, { it.key }, transform, resolve)

/**
 * Populates [destination] with entries that transform keys with [transform] while keeping values from this map.
 *
 * Duplicate keys in [destination] are resolved with [resolve]. Pairs are added in iteration order.
 *
 * @param K The key type of this map.
 * @param V The value type of entries in this map.
 * @param L The transformed key type stored in [destination].
 * @param D The destination map type.
 * @receiver This map whose keys are transformed into [destination].
 * @param destination The mutable map that receives transformed entries.
 * @param transform Called with each entry of this map; its result becomes the key for the generated pair.
 * @param resolve Called when a transformed key already exists in [destination]; receives the key, the current value,
 * and the new value, and returns the value to store.
 * @return [destination] after all transformed entries have been added.
 */
@IgnorableReturnValue
public inline fun <K, V, L, D : MutableMap<L, V>> Map<out K, V>.mapKeysTo(destination: D, transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): D =
    entries.associateByTo(destination, transform, { it.value }, resolve)

/**
 * Returns a new map with keys transformed by [transform] and values unchanged, resolving duplicate keys with [resolve].
 *
 * Pairs are added in iteration order into a new [LinkedHashMap].
 *
 * @param K The key type of this map.
 * @param V The value type of entries in this map.
 * @param L The transformed key type of the result map.
 * @receiver This map whose keys are transformed.
 * @param transform Called with each entry of this map; its result becomes the key for the generated pair.
 * @param resolve Called when a transformed key already exists; receives the key, the current value, and the new value,
 * and returns the value to store.
 * @return A new map with transformed keys and the same values.
 */
public inline fun <K, V, L> Map<out K, V>.mapKeys(transform: (Map.Entry<K, V>) -> L, resolve: (key: L, currentValue: V, newValue: V) -> V): Map<L, V> =
    mapKeysTo(LinkedHashMap(size), transform, resolve)

/**
 * Accumulates a value by applying [operation] to [initial] and each entry of this map in iteration order.
 *
 * Returns [initial] unchanged if this map is empty.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @param R The accumulator and result type.
 * @receiver This map whose entries are folded.
 * @param initial The starting accumulator value.
 * @param operation Called with the current accumulator and each entry; returns the next accumulator value.
 * @return The final accumulator value after all entries have been processed, or [initial] if this map is empty.
 */
public inline fun <K, V, R> Map<out K, V>.fold(initial: R, operation: (acc: R, Map.Entry<K, V>) -> R): R {
    var accumulator = initial
    for (element in this) accumulator = operation(accumulator, element)
    return accumulator
}

/**
 * Transforms each entry of this map with [transform] and reduces the results with [operation].
 *
 * The first entry is transformed to the initial accumulator; each subsequent entry is transformed and combined with
 * [operation]. Iteration order follows this map's entry iterator.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @param T The type produced by [transform] for each entry; must be a subtype of [R].
 * @param R The accumulator and result type.
 * @receiver This map whose entries are transformed and reduced.
 * @param transform Called with each entry; its result becomes an element passed to [operation].
 * @param operation Called with the current accumulator and each transformed element; returns the next accumulator value.
 * @return The final reduced value.
 * @throws UnsupportedOperationException if this map is empty.
 */
public inline fun <K, V, T: R, R> Map<out K, V>.mapReduce(transform: (Map.Entry<K, V>) -> T, operation: (acc: R, T) -> R): R {
    val iterator = this.iterator()
    if (!iterator.hasNext()) throw UnsupportedOperationException("Empty collection can't be reduced.")
    var accumulator: R = transform(iterator.next())
    for (element in iterator) accumulator = operation(accumulator, transform(element))
    return accumulator
}

/**
 * Transforms each entry of this map with [transform] and reduces the results with [operation], or returns `null` if empty.
 *
 * Same as [mapReduce], but returns `null` instead of throwing when this map has no entries.
 *
 * @param K The key type of this map.
 * @param V The value type of this map.
 * @param T The type produced by [transform] for each entry; must be a subtype of [R].
 * @param R The accumulator and result type.
 * @receiver This map whose entries are transformed and reduced.
 * @param transform Called with each entry; its result becomes an element passed to [operation].
 * @param operation Called with the current accumulator and each transformed element; returns the next accumulator value.
 * @return The final reduced value, or `null` if this map is empty.
 */
public inline fun <K, V, T: R, R> Map<out K, V>.mapReduceOrNull(transform: (Map.Entry<K, V>) -> T, operation: (acc: R, T) -> R): R? {
    val iterator = this.iterator()
    if (!iterator.hasNext()) return null
    var accumulator: R = transform(iterator.next())
    for (element in iterator) accumulator = operation(accumulator, transform(element))
    return accumulator
}

/**
 * Folds over the union of [map1] and [map2], applying different [operation] variants depending on key membership.
 *
 * Entries unique to [map2] are processed with [operation2], then entries from [map1] are processed with [operation1]
 * when the key is absent from [map2], or with [operationMerge] when the key appears in both maps.
 *
 * @param K The key type of both maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param R The accumulator and result type.
 * @param map1 The first map to fold over.
 * @param map2 The second map to fold over.
 * @param initial The starting accumulator value.
 * @param operation1 Called with the accumulator and each entry of [map1] whose key is absent from [map2].
 * @param operation2 Called with the accumulator and each entry of [map2] whose key is absent from [map1].
 * @param operationMerge Called with the accumulator, a common key, and the corresponding values from [map1] and [map2].
 * @return The final accumulator value.
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
    for (element in map2) if (element.key !in map1) accumulator = operation2(accumulator, element)
    for (element in map1) accumulator =
        if (element.key !in map2) operation1(accumulator, element)
        else operationMerge(accumulator, element.key, element.value, map2[element.key]!!)
    return accumulator
}

/**
 * Returns `true` if any entry across the union of [map1] and [map2] satisfies the corresponding predicate.
 *
 * Entries unique to [map2] are tested with [operation2], entries unique to [map1] with [operation1], and keys present
 * in both maps with [operationMerge].
 *
 * @param K The key type of both maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param map1 The first map to test.
 * @param map2 The second map to test.
 * @param operation1 Called with each entry of [map1] whose key is absent from [map2]; should return `true` to short-circuit with `true`.
 * @param operation2 Called with each entry of [map2] whose key is absent from [map1]; should return `true` to short-circuit with `true`.
 * @param operationMerge Called with a common key and the corresponding values from [map1] and [map2]; should return `true` to short-circuit with `true`.
 * @return `true` if any tested entry or key group satisfies its predicate, `false` otherwise.
 */
public inline fun <K, V1, V2> mergingAny(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Boolean,
    operation2: (Map.Entry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map2) if (element.key !in map1 && operation2(element)) return true
    for (element in map1)
        if (element.key !in map2) { if(operation1(element)) return true }
        else if(operationMerge(element.key, element.value, map2[element.key]!!)) return true
    return false
}

/**
 * Returns `true` if every entry across the union of [map1] and [map2] satisfies the corresponding predicate.
 *
 * Entries unique to [map2] are tested with [operation2], entries unique to [map1] with [operation1], and keys present
 * in both maps with [operationMerge].
 *
 * @param K The key type of both maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param map1 The first map to test.
 * @param map2 The second map to test.
 * @param operation1 Called with each entry of [map1] whose key is absent from [map2]; should return `true` for the entry to pass.
 * @param operation2 Called with each entry of [map2] whose key is absent from [map1]; should return `true` for the entry to pass.
 * @param operationMerge Called with a common key and the corresponding values from [map1] and [map2]; should return `true` for the key group to pass.
 * @return `true` if all tested entries and key groups satisfy their predicates, `false` otherwise.
 */
public inline fun <K, V1, V2> mergingAll(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Boolean,
    operation2: (Map.Entry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map2) if (element.key !in map1 && !operation2(element)) return false
    for (element in map1)
        if (element.key !in map2) { if (!operation1(element)) return false }
        else if(!operationMerge(element.key, element.value, map2[element.key]!!)) return false
    return true
}

/**
 * Returns `true` if no entry across the union of [map1] and [map2] satisfies the corresponding predicate.
 *
 * Entries unique to [map2] are tested with [operation2], entries unique to [map1] with [operation1], and keys present
 * in both maps with [operationMerge].
 *
 * @param K The key type of both maps.
 * @param V1 The value type of entries in [map1].
 * @param V2 The value type of entries in [map2].
 * @param map1 The first map to test.
 * @param map2 The second map to test.
 * @param operation1 Called with each entry of [map1] whose key is absent from [map2]; should return `false` for the entry to pass.
 * @param operation2 Called with each entry of [map2] whose key is absent from [map1]; should return `false` for the entry to pass.
 * @param operationMerge Called with a common key and the corresponding values from [map1] and [map2]; should return `false` for the key group to pass.
 * @return `true` if all tested entries and key groups fail their predicates, `false` otherwise.
 */
public inline fun <K, V1, V2> mergingNone(
    map1: Map<out K, V1>,
    map2: Map<out K, V2>,
    operation1: (Map.Entry<K, V1>) -> Boolean,
    operation2: (Map.Entry<K, V2>) -> Boolean,
    operationMerge: (key: K, value1: V1, value2: V2) -> Boolean
): Boolean {
    for (element in map2) if (element.key !in map1 && operation2(element)) return false
    for (element in map1)
        if (element.key !in map2) { if(operation1(element)) return false }
        else if(operationMerge(element.key, element.value, map2[element.key]!!)) return false
    return true
}