/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


// TODO: Apply the class
internal class SingletonMap<K, KC: Equality<K>, V, VC: Equality<V>>(
    val singleKey: K,
    val singleValue: V,
    override val keyContext: KC,
    override val valueContext: VC,
) : KoneMapWithContext<K, KC, V, VC> {
    override val size: UInt = 1u
    override val keys: KoneIterableSet<K> =
        SingletonSet(
            singleElement = singleKey,
            elementContext = keyContext
        )
    override val values: KoneIterableCollection<V> =
        SingletonList(
            singleElement = singleValue,
            elementContext = valueContext
        )
    override val entries: KoneIterableSet<KoneMapEntry<K, V>> =
        SingletonSet(
            singleElement = KoneMapEntry(singleKey, singleValue),
            elementContext = KoneMapEntryEquality(keyContext, valueContext)
        )

    override fun containsKey(key: K): Boolean = keyContext { singleKey eq key }

    override fun containsValue(value: V): Boolean = valueContext { singleValue eq value }

    override fun get(key: K): V =
        if (keyContext { singleKey eq key }) singleValue
        else noMatchingKeyException(key)

    override fun getMaybe(key: K): Option<V> =
        if (keyContext { singleKey eq key }) Some(singleValue)
        else None

    override fun toString(): String = "{$singleKey=$singleValue}"
    override fun hashCode(): Int = singleKey.hashCode() xor singleValue.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneMap<*, *>) return false
        if (other.size != 1u) return false

        val (otherKey, otherValue) = other.entries.first()

        return singleKey == otherKey && singleValue == otherValue
    }
}