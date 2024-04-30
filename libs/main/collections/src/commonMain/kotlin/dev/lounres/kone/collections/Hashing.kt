/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.orElse


internal class KoneIterableListHashing<E>(val elementContext: Hashing<E>) : Hashing<KoneIterableList<E>> {
    override fun KoneIterableList<E>.equalsTo(other: KoneIterableList<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }

    override fun KoneIterableList<E>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 1
        while (thisIterator.hasNext()) elementContext {
            hash = 31 * hash + thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <E> koneIterableListHashing(elementContext: Hashing<E>): Hashing<KoneIterableList<E>> =
    KoneIterableListHashing(elementContext)

internal class KoneIterableSetHashing<E, EH: Hashing<E>>(val elementContext: EH) : Hashing<KoneIterableSet<E>> {
    override fun KoneIterableSet<E>.equalsTo(other: KoneIterableSet<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }

    override fun KoneIterableSet<E>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) elementContext {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <E> koneIterableSetHashing(elementContext: Hashing<E>): Hashing<KoneIterableSet<E>> =
    KoneIterableSetHashing(elementContext)

internal open class KoneMapEntryHashing<K, V>(val keyContext: Hashing<K>, var valueContext: Hashing<V>) : Hashing<KoneMapEntry<K, V>> {
    override fun KoneMapEntry<K, V>.equalsTo(other: KoneMapEntry<K, V>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
    override fun KoneMapEntry<K, V>.hash(): Int = keyContext { key.hash() } xor valueContext { value.hash() }
}

public fun <K, V> koneMapEntryHashing(keyContext: Hashing<K>, valueContext: Hashing<V>): Hashing<KoneMapEntry<K, V>> =
    KoneMapEntryHashing(keyContext, valueContext)

internal class KoneMapHashing<K, V>(val keyContext: Hashing<K>, val valueContext: Hashing<V>) : Hashing<KoneMap<K, V>> {
    override fun KoneMap<K, V>.equalsTo(other: KoneMap<K, V>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            val otherValue = keyContext { other.getMaybe(key) }.orElse { return false }
            if (valueContext { otherValue neq value }) return false
        }

        return true
    }

    override fun KoneMap<K, V>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            hash += keyContext { key.hash() } xor valueContext { value.hash() }
        }
        return hash
    }
}

public fun <K, KE: Hashing<K>, V> koneMapHashing(keyContext: KE, valueContext: Hashing<V>): Equality<KoneMap<K, V>> =
    KoneMapHashing(keyContext = keyContext, valueContext = valueContext)