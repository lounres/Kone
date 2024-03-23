/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.orElse


internal class KoneContextualIterableListHashing<E, EH: Hashing<E>>(val elementHashing: EH) : Hashing<KoneContextualIterableList<E, EH>> {
    override fun KoneContextualIterableList<E, EH>.equalsTo(other: KoneContextualIterableList<E, EH>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementHashing { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }

    override fun KoneContextualIterableList<E, EH>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 1
        while (thisIterator.hasNext()) elementHashing {
            hash = 31 * hash + thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <E, EH: Hashing<E>> koneContextualIterableListHashing(elementHashing: EH): Hashing<KoneContextualIterableList<E, EH>> =
    KoneContextualIterableListHashing(elementHashing)

internal class KoneContextualIterableSetHashing<E, EH: Hashing<E>>(val elementHashing: EH) : Hashing<KoneContextualIterableSet<E, EH>> {
    override fun KoneContextualIterableSet<E, EH>.equalsTo(other: KoneContextualIterableSet<E, EH>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementHashing { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }

    override fun KoneContextualIterableSet<E, EH>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) elementHashing {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <E, EH: Hashing<E>> koneContextualIterableSetHashing(elementHashing: EH): Hashing<KoneContextualIterableSet<E, EH>> =
    KoneContextualIterableSetHashing(elementHashing)

internal class KoneContextualMapHashing<K, KE: Hashing<K>, V>(val keyHashing: KE, val valueHashing: Hashing<V>) : Hashing<KoneContextualMap<K, KE, V>> {
    override fun KoneContextualMap<K, KE, V>.equalsTo(other: KoneContextualMap<K, KE, V>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            val otherValue = keyHashing { other.getMaybe(key) }.orElse { return false }
            if (valueHashing { otherValue neq value }) return false
        }

        return true
    }

    override fun KoneContextualMap<K, KE, V>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            hash += keyHashing { key.hash() } xor valueHashing { value.hash() }
        }
        return hash
    }
}

public fun <K, KE: Hashing<K>, V> koneContextualMapHashing(keyEquality: KE, valueEquality: Hashing<V>): Equality<KoneContextualMap<K, KE, V>> =
    KoneContextualMapHashing(keyHashing = keyEquality, valueHashing = valueEquality)