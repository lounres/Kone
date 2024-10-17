/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.isSome
import dev.lounres.kone.option.orDefault
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public fun KoneMap<*, *>.isEmpty(): Boolean = size == 0u
public fun KoneMap<*, *>.isNotEmpty(): Boolean = !isEmpty()

public operator fun <K> KoneMap<in K, *>.contains(key: K): Boolean = containsKey(key)

public fun <K, V> KoneMap<in K, V>.getOrNull(key: K): V? = getMaybe(key).orDefault(null)

public fun <K, V> KoneMap<in K, V>.getOrDefault(key: K, default: V): V = getMaybe(key).orDefault(default)

public inline fun <K, V> KoneMap<in K, V>.getOrElse(key: K, default: () -> V): V = getMaybe(key).orElse(default)

public fun <K, V> KoneMutableMap<in K, in V>.set(entry: KoneMapEntry<K, V>) {
    set(entry.key, entry.value)
}

public fun <K, V> KoneMutableMap<in K, V>.getOrNullAndSet(key: K, value: V): V? = getMaybeAndSet(key, value).orDefault(null)

public fun <K, V> KoneMutableMap<in K, V>.getOrDefaultAndSet(key: K, value: V, default: V): V = getMaybeAndSet(key, value).orDefault(default)

public inline fun <K, V> KoneMutableMap<in K, V>.getOrElseAndSet(key: K, value: V, default: () -> V): V = getMaybeAndSet(key, value).orElse(default)

public fun <K, V> KoneMutableMap<in K, out V>.getOrNullAndRemove(key: K): V? = getMaybeAndRemove(key).orDefault(null)

public fun <K, V> KoneMutableMap<in K, V>.getOrDefaultAndRemove(key: K, default: V): V = getMaybeAndRemove(key).orDefault(default)

public inline fun <K, V> KoneMutableMap<in K, V>.getOrElseAndRemove(key: K, default: () -> V): V = getMaybeAndRemove(key).orElse(default)

public fun <K> KoneMutableMap<in K, *>.definitelyRemove(key: K): Boolean = getMaybeAndRemove(key).isSome()

public inline fun <K> KoneMutableMap<in K, *>.definitelyRemoveThrowing(key: K, error: () -> Throwable) {
    getMaybeAndRemove(key).orThrow(error)
}

public val <K> KoneMapWithContext<K, Equality<K>, *, *>.keys: KoneIterableSet<K>
    get() = keysView.toKoneMutableIterableSet(keyContext)
public val <V> KoneMapWithContext<*, *, V, Equality<V>>.values: KoneIterableCollection<V>
    get() = valuesView.toKoneMutableIterableList(valueContext)
public val <K, V> KoneMapWithContext<K, Equality<K>, V, Equality<V>>.entries: KoneIterableSet<KoneMapEntry<K, V>>
    get() = entriesView.toKoneMutableIterableSet(koneMapEntryEquality(keyContext, valueContext))

public operator fun <K, V> KoneMap<out K, V>.iterator(): KoneIterator<KoneMapEntry<K, V>> = entriesView.iterator()