/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.noMatchingKeyException
import dev.lounres.kone.collections.utils.forEach
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.transformNotNullMaybe
import kotlin.jvm.JvmName


public fun <Key, Value> KoneMapNode<Key, Value>.toKoneMapEntry(): KoneMapEntry<Key, Value> = KoneMapEntry(key = key, value = value)

public fun KoneMap<*, *>.isEmpty(): Boolean = size == 0u
public fun KoneMap<*, *>.isNotEmpty(): Boolean = !isEmpty()

public operator fun <Key> KoneMap<in Key, *>.contains(key: Key): Boolean = key in keysView
public fun <Key> KoneMap<in Key, *>.containsKey(key: Key): Boolean = key in keysView
// FIXME: Remove if KT-73923 will be closed
public operator fun <Key> KoneReifiedMap<Key, *>.contains(key: Key): Boolean = key in keysView
// FIXME: Remove if KT-73923 will be closed
public fun <Key> KoneReifiedMap<Key, *>.containsKey(key: Key): Boolean = key in keysView

public fun <Key, Value> KoneMap<Key, Value>.getNode(key: Key): KoneMapNode<Key, Value> = getNodeOrNull(key) ?: noMatchingKeyException(key)
public fun <Key, Value> KoneMutableMap<Key, Value>.getNode(key: Key): KoneMutableMapNode<Key, Value> = getNodeOrNull(key) ?: noMatchingKeyException(key)
public operator fun <Key, Value> KoneMap<in Key, Value>.get(key: Key): Value = getNode(key).value
public fun <Key, Value> KoneMap<in Key, Value>.getOrNull(key: Key): Value? = getNodeOrNull(key)?.value
public fun <Key, Value> KoneMap<in Key, Value>.getMaybe(key: Key): Maybe<Value> = getNodeOrNull(key).transformNotNullMaybe { it.value }

public fun <Key, Value> KoneMap<in Key, Value>.getOrDefault(key: Key, default: Value): Value = getNodeOrNull(key).let { node -> if (node == null) default else node.value }
public inline fun <Key, Value> KoneMap<in Key, Value>.getOrElse(key: Key, default: () -> Value): Value = getNodeOrNull(key).let { node -> if (node == null) default() else node.value }
public inline fun <Key, Value> KoneMutableMap<in Key, Value>.getOrSet(key: Key, default: () -> Value): Value =
    getNodeOrNull(key).let { node -> if (node == null) default().also { this[key] = it } else node.value }

public fun <Key, Value> KoneMutableMap<Key, Value>.set(entry: KoneMapEntry<Key, Value>): KoneMutableMapNode<Key, Value> = set(entry.key, entry.value)
public fun <Key, Value> KoneMutableMap<Key, Value>.set(node: KoneMapNode<Key, Value>): KoneMutableMapNode<Key, Value> = set(node.key, node.value)

@JvmName("setAllEntriesFrom")
public fun <Key, Value> KoneMutableMap<Key, Value>.setAllFrom(entries: KoneIterable<KoneMapEntry<Key, Value>>) { entries.forEach { set(it) } }
@JvmName("setAllNodesFrom")
public fun <Key, Value> KoneMutableMap<Key, Value>.setAllFrom(nodes: KoneIterable<KoneMapNode<Key, Value>>) { nodes.forEach { set(it) } }
public fun <Key, Value> KoneMutableMap<Key, Value>.setAllFrom(map: KoneMap<out Key, Value>) { map.nodesView.forEach { set(it) } }

public fun <Key> KoneMutableMap<in Key, *>.remove(key: Key) { getNode(key).remove() }

public operator fun <Key, Value> KoneMap<out Key, Value>.iterator(): KoneIterator<KoneMapEntry<Key, Value>> = entriesView.iterator()