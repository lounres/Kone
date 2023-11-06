/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.option.isSome
import dev.lounres.kone.option.orDefault
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public infix fun <K, V> K.mapsTo(value: V): KoneMap.Entry<K, V> = KoneMap.Entry(this, value)

public operator fun <K> KoneMap<K, *>.contains(key: K): Boolean = containsKey(key)

public fun <K, V> KoneMap<K, V>.getOrNull(key: K): V? = getOptional(key).orDefault(null)

public fun <K, V> KoneMap<K, V>.getOrDefault(key: K, default: V): V = getOptional(key).orDefault(default)

public fun <K, V> KoneMap<K, V>.getOrElse(key: K, default: () -> V): V = getOptional(key).orElse(default)

public fun <K, V> KoneMutableMap<K, V>.getOrNullAndSet(key: K, value: V): V? = getOptionalAndSet(key, value).orDefault(null)

public fun <K, V> KoneMutableMap<K, V>.getOrDefaultAndSet(key: K, value: V, default: V): V = getOptionalAndSet(key, value).orDefault(default)

public fun <K, V> KoneMutableMap<K, V>.getOrElseAndSet(key: K, value: V, default: () -> V): V = getOptionalAndSet(key, value).orElse(default)

public fun <K, V> KoneMutableMap<K, V>.getOrNullAndRemove(key: K): V? = getOptionalAndRemove(key).orDefault(null)

public fun <K, V> KoneMutableMap<K, V>.getOrDefaultAndRemove(key: K, default: V): V = getOptionalAndRemove(key).orDefault(default)

public fun <K, V> KoneMutableMap<K, V>.getOrElseAndRemove(key: K, default: () -> V): V = getOptionalAndRemove(key).orElse(default)

public fun <K, V> KoneMutableMap<K, V>.definitelyRemove(key: K): Boolean = getOptionalAndRemove(key).isSome()

public inline fun <K, V> KoneMutableMap<K, V>.definitelyRemoveThrowing(key: K, error: () -> Throwable) {
    getOptionalAndRemove(key).orThrow(error)
}