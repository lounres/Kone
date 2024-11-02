/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.transformNotNullMaybe


public fun KoneMap<*, *>.isEmpty(): Boolean = size == 0u
public fun KoneMap<*, *>.isNotEmpty(): Boolean = !isEmpty()

public operator fun <K> KoneMap<in K, *>.contains(key: K): Boolean = key in keysView
public fun <K> KoneMap<in K, *>.containsKey(key: K): Boolean = key in keysView
public fun <V> KoneMap<*, V>.containsValue(value: V): Boolean = value in valuesView

public fun <K, V> KoneMap<K, V>.getNode(key: K): KoneMapNode<K, V> = getNodeOrNull(key) ?: noMatchingKeyException(key)
public fun <K, V> KoneMutableMap<K, V>.getNode(key: K): KoneMutableMapNode<K, V> = getNodeOrNull(key) ?: noMatchingKeyException(key)
public operator fun <K, V> KoneMap<in K, V>.get(key: K): V = getNode(key).value
public fun <K, V> KoneMap<in K, V>.getOrNull(key: K): V? = getNodeOrNull(key)?.value
public fun <K, V> KoneMap<in K, V>.getMaybe(key: K): Option<V> = getNodeOrNull(key).transformNotNullMaybe { it.value }

public fun <K, V> KoneMap<in K, V>.getOrDefault(key: K, default: V): V = getNodeOrNull(key).let { node -> if (node == null) default else node.value }
public inline fun <K, V> KoneMap<in K, V>.getOrElse(key: K, default: () -> V): V = getNodeOrNull(key).let { node -> if (node == null) default() else node.value }

public fun <K, V> KoneMutableMap<K, V>.set(entry: KoneMapEntry<K, V>): KoneMutableMapNode<K, V> = set(entry.key, entry.value)
public fun <K, V> KoneMutableMap<K, V>.set(node: KoneMapNode<K, V>): KoneMutableMapNode<K, V> = set(node.key, node.value)

public fun <K> KoneMutableMap<in K, *>.remove(key: K) { getNode(key).remove() }
public fun <K> KoneMutableMap<in K, *>.removeAll() { nodes.forEach { it.remove() } }

public val <K, V> KoneMap<K, V>.nodes: KoneIterableSet<KoneMapNode<K, V>>
    get() = nodesView.toKoneMutableIterableSet(absoluteEquality())
public val <K, V> KoneMutableMap<K, V>.nodes: KoneIterableSet<KoneMutableMapNode<K, V>>
    get() = nodesView.toKoneMutableIterableSet(absoluteEquality())
public val <K> KoneMapWithContext<K, Equality<K>, *, *>.keys: KoneIterableSet<K>
    get() = keysView.toKoneMutableIterableSet(keyContext)
public val <V> KoneMapWithContext<*, *, V, Equality<V>>.values: KoneIterableCollection<V>
    get() = valuesView.toKoneMutableIterableList(valueContext)
public val <K, V> KoneMapWithContext<K, Equality<K>, V, Equality<V>>.entries: KoneIterableSet<KoneMapEntry<K, V>>
    get() = entriesView.toKoneMutableIterableSet(koneMapEntryEquality(keyContext, valueContext))

public operator fun <K, V> KoneMap<out K, V>.iterator(): KoneIterator<KoneMapEntry<K, V>> = entriesView.iterator()