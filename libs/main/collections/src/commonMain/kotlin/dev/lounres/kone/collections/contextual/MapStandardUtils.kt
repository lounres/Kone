/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.KoneIterator
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.isSome
import dev.lounres.kone.option.orDefault
import dev.lounres.kone.option.orElse
import dev.lounres.kone.option.orThrow


public fun KoneContextualMap<*, *, *>.isEmpty(): Boolean = size == 0u
public fun KoneContextualMap<*, *, *>.isNotEmpty(): Boolean = !isEmpty()

context(KE)
public operator fun <K, KE: Equality<K>> KoneContextualMap<in K, KE, *>.contains(key: K): Boolean = containsKey(key)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMap<in K, KE, V>.getOrNull(key: K): V? = getMaybe(key).orDefault(null)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMap<in K, KE, V>.getOrDefault(key: K, default: V): V = getMaybe(key).orDefault(default)

context(KE)
public inline fun <K, KE: Equality<K>, V> KoneContextualMap<in K, KE, V>.getOrElse(key: K, default: () -> V): V = getMaybe(key).orElse(default)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, in V>.set(entry: KoneMapEntry<K, V>) {
    set(entry.key, entry.value)
}

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, V>.getOrNullAndSet(key: K, value: V): V? = getMaybeAndSet(key, value).orDefault(null)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, V>.getOrDefaultAndSet(key: K, value: V, default: V): V = getMaybeAndSet(key, value).orDefault(default)

context(KE)
public inline fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, V>.getOrElseAndSet(key: K, value: V, default: () -> V): V = getMaybeAndSet(key, value).orElse(default)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, V>.getOrNullAndRemove(key: K): V? = getMaybeAndRemove(key).orDefault(null)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, V>.getOrDefaultAndRemove(key: K, default: V): V = getMaybeAndRemove(key).orDefault(default)

context(KE)
public inline fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, V>.getOrElseAndRemove(key: K, default: () -> V): V = getMaybeAndRemove(key).orElse(default)

context(KE)
public fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, *>.definitelyRemove(key: K): Boolean = getMaybeAndRemove(key).isSome()

context(KE)
public inline fun <K, KE: Equality<K>, V> KoneContextualMutableMap<in K, KE, *>.definitelyRemoveThrowing(key: K, error: () -> Throwable) {
    getMaybeAndRemove(key).orThrow(error)
}

public operator fun <K, V> KoneContextualMap<out K, *, V>.iterator(): KoneIterator<KoneMapEntry<K, V>> = entries.iterator()