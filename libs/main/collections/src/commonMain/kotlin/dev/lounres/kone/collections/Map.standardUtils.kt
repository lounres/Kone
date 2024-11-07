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

public operator fun <Key> KoneMap<in Key, *>.contains(key: Key): Boolean = key in keysView
public fun <Key> KoneMap<in Key, *>.containsKey(key: Key): Boolean = key in keysView

public fun <Key, Value> KoneMap<Key, Value>.getNode(key: Key): KoneMapNode<Key, Value> = getNodeOrNull(key) ?: noMatchingKeyException(key)
public fun <Key, Value> KoneMutableMap<Key, Value>.getNode(key: Key): KoneMutableMapNode<Key, Value> = getNodeOrNull(key) ?: noMatchingKeyException(key)
public operator fun <Key, Value> KoneMap<in Key, Value>.get(key: Key): Value = getNode(key).value
public fun <Key, Value> KoneMap<in Key, Value>.getOrNull(key: Key): Value? = getNodeOrNull(key)?.value
public fun <Key, Value> KoneMap<in Key, Value>.getMaybe(key: Key): Option<Value> = getNodeOrNull(key).transformNotNullMaybe { it.value }

public fun <Key, Value> KoneMap<in Key, Value>.getOrDefault(key: Key, default: Value): Value = getNodeOrNull(key).let { node -> if (node == null) default else node.value }
public inline fun <Key, Value> KoneMap<in Key, Value>.getOrElse(key: Key, default: () -> Value): Value = getNodeOrNull(key).let { node -> if (node == null) default() else node.value }
public inline fun <Key, Value> KoneMutableMap<in Key, Value>.getOrSet(key: Key, default: () -> Value): Value =
    getNodeOrNull(key).let { node -> if (node == null) default().also { this[key] = it } else node.value }

public fun <Key, Value> KoneMutableMap<Key, Value>.set(entry: KoneMapEntry<Key, Value>): KoneMutableMapNode<Key, Value> = set(entry.key, entry.value)
public fun <Key, Value> KoneMutableMap<Key, Value>.set(node: KoneMapNode<Key, Value>): KoneMutableMapNode<Key, Value> = set(node.key, node.value)

public fun <Key, Value> KoneMutableMap<Key, Value>.setAllFrom(entries: KoneIterable<KoneMapEntry<Key, Value>>) { entries.forEach { set(it) } }
public fun <Key, Value> KoneMutableMap<Key, Value>.setAllFrom(nodes: KoneIterable<KoneMapNode<Key, Value>>) { nodes.forEach { set(it) } }

public fun <Key> KoneMutableMap<in Key, *>.remove(key: Key) { getNode(key).remove() }

public val <Key, Value> KoneMap<Key, Value>.nodes: KoneSet<KoneMapNode<Key, Value>>
    get() = nodesView.toKoneSet(absoluteEquality())
public val <Key, Value> KoneMutableMap<Key, Value>.nodes: KoneSet<KoneMutableMapNode<Key, Value>>
    get() = nodesView.toKoneSet(absoluteEquality())
public val <Key> KoneMapWithContext<Key, Equality<Key>, *>.keys: KoneSet<Key>
    get() = keysView.toKoneSet(keyContext)
public val <Value> KoneMapWithContext<*, *, Value>.values: KoneIterable<Value>
    get() = valuesView.toKoneList()
public val <Key, Value> KoneMapWithContext<Key, Equality<Key>, Value>.entries: KoneIterable<KoneMapEntry<Key, Value>>
    get() = entriesView.toKoneList()

public operator fun <Key, Value> KoneMap<out Key, Value>.iterator(): KoneIterator<KoneMapEntry<Key, Value>> = entriesView.iterator()