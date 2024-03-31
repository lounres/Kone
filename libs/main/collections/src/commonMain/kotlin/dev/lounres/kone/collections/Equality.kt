/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.orElse


internal class KoneIterableListEquality<E>(val elementEquality: Equality<E>) : Equality<KoneIterableList<E>> {
    override fun KoneIterableList<E>.equalsTo(other: KoneIterableList<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementEquality { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }
}

public fun <E, EE: Equality<E>> koneIterableListEquality(elementEquality: EE): Equality<KoneIterableList<E>> =
    KoneIterableListEquality(elementEquality)

internal class KoneIterableSetEquality<E, EE: Equality<E>>(val elementsEquality: EE) : Equality<KoneIterableSet<E>> {
    override fun KoneIterableSet<E>.equalsTo(other: KoneIterableSet<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementsEquality { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }
}

public fun <E, EE: Equality<E>> koneIterableSetEquality(elementEquality: EE): Equality<KoneIterableSet<E>> =
    KoneIterableSetEquality(elementEquality)

internal class KoneMapEquality<K, KE: Equality<K>, V>(val keyEquality: KE, val valueEquality: Equality<V>) : Equality<KoneMap<K, V>> {
    override fun KoneMap<K, V>.equalsTo(other: KoneMap<K, V>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            val otherValue = keyEquality { other.getMaybe(key) }.orElse { return false }
            if (valueEquality { otherValue neq value }) return false
        }

        return true
    }
}

public fun <K, KE: Equality<K>, V> koneMapEquality(keyEquality: KE, valueEquality: Equality<V>): Equality<KoneMap<K, V>> =
    KoneMapEquality(keyEquality = keyEquality, valueEquality = valueEquality)