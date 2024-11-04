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


// TODO: Apply the class
internal class SingletonMap<K, KC: Equality<K>, V>(
    val singleKey: K,
    val singleValue: V,
    override val keyContext: KC,
) : KoneMapWithContext<K, KC, V> {
    private val singleNode = Node(singleKey, singleValue)
    
    override val size: UInt get() = 1u
    override val nodesView: KoneSet<KoneMapNode<K, V>> =
        SingletonSet(
            singleElement = singleNode,
            elementContext = absoluteEquality(),
        )
    override val keysView: KoneSet<K> =
        SingletonSet(
            singleElement = singleKey,
            elementContext = keyContext
        )
    override val valuesView: KoneIterable<V> = SingletonList(singleValue)
    override val entriesView: KoneIterable<KoneMapEntry<K, V>> = SingletonList(KoneMapEntry(singleKey, singleValue))
    
    override fun getNodeOrNull(key: K): KoneMapNode<K, V>? = if (keyContext { key eq singleKey }) singleNode else null
    
    override fun toString(): String = "{$singleKey=$singleValue}"
    override fun hashCode(): Int = singleKey.hashCode() xor singleValue.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (other.size != 1u) return false

        val (otherKey, otherValue) = other.entriesView.first()

        return singleKey == otherKey && singleValue == otherValue
    }
    
    private class Node<K, V>(
        override val key: K,
        override val value: V,
    ) : KoneMapNode<K, V>
}