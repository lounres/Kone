/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMapNode
import dev.lounres.kone.collections.KoneMapWithContext
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


internal class KoneSingletonMap<Key, KeyContext: Equality<Key>, Value>(
    val singleKey: Key,
    val singleValue: Value,
    override val keyContext: KeyContext,
) : KoneMapWithContext<Key, KeyContext, Value> {
    private val singleNode = Node(singleKey, singleValue)
    
    override val size: UInt get() = 1u
    override val nodesView: KoneSet<KoneMapNode<Key, Value>> =
        KoneSingletonNoddedSet(
            singleElement = singleNode,
            elementContext = absoluteEquality(),
        )
    override val keysView: KoneSet<Key> =
        KoneSingletonNoddedSet(
            singleElement = singleKey,
            elementContext = keyContext
        )
    override val valuesView: KoneIterable<Value> = KoneSingletonSettableLinearIterable(singleValue)
    override val entriesView: KoneIterable<KoneMapEntry<Key, Value>> = KoneSingletonSettableLinearIterable(KoneMapEntry(singleKey, singleValue))
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? = if (keyContext { key eq singleKey }) singleNode else null
    
    override fun toString(): String = "{$singleKey=$singleValue}"
    override fun hashCode(): Int = singleKey.hashCode() xor singleValue.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (other.size != 1u) return false

        val (otherKey, otherValue) = other.entriesView.first()

        return singleKey == otherKey && singleValue == otherValue
    }
    
    private class Node<Key, Value>(
        override val key: Key,
        override val value: Value,
    ) : KoneMapNode<Key, Value> {
        override val isDetached: Boolean get() = false
    }
}