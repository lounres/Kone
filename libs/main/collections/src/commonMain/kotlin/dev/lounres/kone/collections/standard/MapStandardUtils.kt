/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.option.isSome
import dev.lounres.kone.option.orDefault
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public infix fun <K, V> K.mapsTo(value: V): KoneMap.Entry<K, V> = KoneMap.Entry(this, value)

public fun KoneMap<*, *>.isEmpty(): Boolean = size == 0u
public fun KoneMap<*, *>.isNotEmpty(): Boolean = !isEmpty()

public operator fun <K> KoneMap<K, *>.contains(key: K): Boolean = containsKey(key)

public fun <K, V> KoneMap<K, V>.getOrNull(key: K): V? = getMaybe(key).orDefault(null)

public fun <K, V> KoneMap<K, V>.getOrDefault(key: K, default: V): V = getMaybe(key).orDefault(default)

public inline fun <K, V> KoneMap<K, V>.getOrElse(key: K, default: () -> V): V = getMaybe(key).orElse(default)

public fun <K, V> KoneMutableMap<in K, in V>.set(entry: KoneMap.Entry<K, V>) {
    set(entry.key, entry.value)
}

public fun <K, V> KoneMutableMap<K, V>.getOrNullAndSet(key: K, value: V): V? = getMaybeAndSet(key, value).orDefault(null)

public fun <K, V> KoneMutableMap<K, V>.getOrDefaultAndSet(key: K, value: V, default: V): V = getMaybeAndSet(key, value).orDefault(default)

public inline fun <K, V> KoneMutableMap<K, V>.getOrElseAndSet(key: K, value: V, default: () -> V): V = getMaybeAndSet(key, value).orElse(default)

public fun <K, V> KoneMutableMap<K, V>.getOrNullAndRemove(key: K): V? = getOptionalAndRemove(key).orDefault(null)

public fun <K, V> KoneMutableMap<K, V>.getOrDefaultAndRemove(key: K, default: V): V = getOptionalAndRemove(key).orDefault(default)

public inline fun <K, V> KoneMutableMap<K, V>.getOrElseAndRemove(key: K, default: () -> V): V = getOptionalAndRemove(key).orElse(default)

public fun <K, V> KoneMutableMap<K, V>.definitelyRemove(key: K): Boolean = getOptionalAndRemove(key).isSome()

public inline fun <K, V> KoneMutableMap<K, V>.definitelyRemoveThrowing(key: K, error: () -> Throwable) {
    getOptionalAndRemove(key).orThrow(error)
}

public operator fun <K, V> KoneMap<out K, V>.iterator(): KoneIterator<KoneMap.Entry<K, V>> = entries.iterator()