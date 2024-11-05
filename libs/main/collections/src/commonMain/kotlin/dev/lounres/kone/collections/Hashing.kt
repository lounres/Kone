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


internal class KoneListHashing<Element>(val elementContext: Hashing<Element>) : Hashing<KoneList<Element>> {
    override fun KoneList<Element>.equalsTo(other: KoneList<Element>): Boolean {
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

    override fun KoneList<Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 1
        while (thisIterator.hasNext()) elementContext {
            hash = 31 * hash + thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <Element> koneIterableListHashing(elementContext: Hashing<Element>): Hashing<KoneList<Element>> =
    KoneListHashing(elementContext)

internal class KoneSetHashing<Element>(val elementContext: Hashing<Element>) : Hashing<KoneSet<Element>> {
    override fun KoneSet<Element>.equalsTo(other: KoneSet<Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }

    override fun KoneSet<Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) elementContext {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <Element> koneIterableSetHashing(elementContext: Hashing<Element>): Hashing<KoneSet<Element>> =
    KoneSetHashing(elementContext)

internal open class KoneMapEntryHashing<K, V>(val keyContext: Hashing<K>, var valueContext: Hashing<V>) : Hashing<KoneMapEntry<K, V>> {
    override fun KoneMapEntry<K, V>.equalsTo(other: KoneMapEntry<K, V>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
    override fun KoneMapEntry<K, V>.hash(): Int = keyContext { key.hash() } xor valueContext { value.hash() }
}

public fun <Key, Value> koneMapEntryHashing(keyContext: Hashing<Key>, valueContext: Hashing<Value>): Hashing<KoneMapEntry<Key, Value>> =
    KoneMapEntryHashing(keyContext, valueContext)

internal class KoneMapHashing<Key, Value>(val keyContext: Hashing<Key>, val valueContext: Hashing<Value>) : Hashing<KoneMap<Key, Value>> {
    override fun KoneMap<Key, Value>.equalsTo(other: KoneMap<Key, Value>): Boolean {
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

    override fun KoneMap<Key, Value>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            hash += keyContext { key.hash() } xor valueContext { value.hash() }
        }
        return hash
    }
}

public fun <Key, KE: Hashing<Key>, Value> koneMapHashing(keyContext: KE, valueContext: Hashing<Value>): Equality<KoneMap<Key, Value>> =
    KoneMapHashing(keyContext = keyContext, valueContext = valueContext)