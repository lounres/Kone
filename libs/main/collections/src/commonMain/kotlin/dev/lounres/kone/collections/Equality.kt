/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.orElse


internal class KoneListEquality<E>(val elementContext: Equality<E>) : Equality<KoneList<E>> {
    override fun KoneList<E>.equalsTo(other: KoneList<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }
}

public fun <E> koneListEquality(elementContext: Equality<E>): Equality<KoneList<E>> =
    if (elementContext is Hashing<E>) KoneListHashing(elementContext)
    else KoneListEquality(elementContext)

internal class KoneSetEquality<E>(val elementContext: Equality<E>) : Equality<KoneSet<E>> {
    override fun KoneSet<E>.equalsTo(other: KoneSet<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }
}

public fun <E> koneSetEquality(elementContext: Equality<E>): Equality<KoneSet<E>> =
    if (elementContext is Hashing<E>) KoneSetHashing(elementContext)
    else KoneSetEquality(elementContext)

internal open class KoneMapEntryEquality<K, V>(val keyContext: Equality<K>, var valueContext: Equality<V>) : Equality<KoneMapEntry<K, V>> {
    override fun KoneMapEntry<K, V>.equalsTo(other: KoneMapEntry<K, V>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
}

public fun <K, V> koneMapEntryEquality(keyContext: Equality<K>, valueContext: Equality<V>): Equality<KoneMapEntry<K, V>> =
    if (keyContext is Hashing<K> && valueContext is Hashing<V>) KoneMapEntryHashing(keyContext, valueContext)
    else KoneMapEntryEquality(keyContext, valueContext)

internal class KoneMapEquality<K, V>(val keyContext: Equality<K>, val valueContext: Equality<V>) : Equality<KoneMap<K, V>> {
    override fun KoneMap<K, V>.equalsTo(other: KoneMap<K, V>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            val otherValue = keyContext { other.getMaybe(key) }.orElse { return false }
            if (valueContext { otherValue neq value }) return false
        }

        return true
    }
}

public fun <K, V> koneMapEquality(keyContext: Equality<K>, valueContext: Equality<V>): Equality<KoneMap<K, V>> =
    if (keyContext is Hashing<K> && valueContext is Hashing<V>) KoneMapEquality(keyContext = keyContext, valueContext = valueContext)
    else KoneMapEquality(keyContext = keyContext, valueContext = valueContext)