/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.orElse


internal class KoneContextualIterableListEquality<E>(val elementEquality: Equality<E>) : Equality<KoneContextualIterableList<E, *>> {
    override fun KoneContextualIterableList<E, *>.equalsTo(other: KoneContextualIterableList<E, *>): Boolean {
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

public fun <E, EE: Equality<E>> koneContextualIterableListEquality(elementEquality: EE): Equality<KoneContextualIterableList<E, EE>> =
    KoneContextualIterableListEquality(elementEquality)

internal class KoneContextualIterableSetEquality<E, EE: Equality<E>>(val elementsEquality: EE) : Equality<KoneContextualIterableSet<E, EE>> {
    override fun KoneContextualIterableSet<E, EE>.equalsTo(other: KoneContextualIterableSet<E, EE>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementsEquality { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }
}

public fun <E, EE: Equality<E>> koneContextualIterableSetEquality(elementEquality: EE): Equality<KoneContextualIterableSet<E, EE>> =
    KoneContextualIterableSetEquality(elementEquality)

internal class KoneContextualMapEquality<K, KE: Equality<K>, V>(val keyEquality: KE, val valueEquality: Equality<V>) : Equality<KoneContextualMap<K, KE, V>> {
    override fun KoneContextualMap<K, KE, V>.equalsTo(other: KoneContextualMap<K, KE, V>): Boolean {
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

public fun <K, KE: Equality<K>, V> koneContextualMapEquality(keyEquality: KE, valueEquality: Equality<V>): Equality<KoneContextualMap<K, KE, V>> =
    KoneContextualMapEquality(keyEquality = keyEquality, valueEquality = valueEquality)