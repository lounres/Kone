/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMapNode
import dev.lounres.kone.collections.KoneReifiedMap
import dev.lounres.kone.collections.KoneReifiedSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.absoluteReifiedEquality
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.context.invoke


internal open class KoneSingletonMap<Key, KeyContext: Equality<Key>, Value>(
    val singleKey: Key,
    val singleValue: Value,
    val keyContext: KeyContext,
) : KoneMap<Key, Value> {
    private val singleNode = Node(singleKey, singleValue)
    
    override val size: UInt get() = 1u
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> =
        KoneSingletonNoddedReifiedSet(
            singleElement = singleNode,
            elementContext = absoluteReifiedEquality(),
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

internal open class KoneSingletonReifiedMap<Key, KeyContext: ReifiedEquality<Key>, Value>(
    singleKey: Key,
    singleValue: Value,
    keyContext: KeyContext,
) : KoneSingletonMap<Key, KeyContext, Value>(
    singleKey = singleKey,
    singleValue = singleValue,
    keyContext = keyContext,
), KoneReifiedMap<Key, Value> {
    private val singleNode = Node(singleKey, singleValue)
    
    override val size: UInt get() = 1u
    override val nodesView: KoneReifiedSet<KoneMapNode<Key, Value>> =
        KoneSingletonNoddedReifiedSet(
            singleElement = singleNode,
            elementContext = absoluteReifiedEquality(),
        )
    override val keysView: KoneReifiedSet<Key> =
        KoneSingletonNoddedReifiedSet(
            singleElement = singleKey,
            elementContext = keyContext
        )
    
    override fun getNodeOrNull(key: Key): KoneMapNode<Key, Value>? =
        if (key in keyContext && keyContext { key eq singleKey }) singleNode else null
    
    private class Node<Key, Value>(
        override val key: Key,
        override val value: Value,
    ) : KoneMapNode<Key, Value> {
        override val isDetached: Boolean get() = false
    }
}