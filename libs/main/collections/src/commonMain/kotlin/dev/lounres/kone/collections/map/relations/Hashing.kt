/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.map.relations

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMapEntry
import dev.lounres.kone.collections.map.iterator
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash


internal open class KoneMapEntryHashing<Key, Value>(open val keyHashing: Hashing<Key>, open val valueHashing: Hashing<Value>) : Hashing<KoneMapEntry<Key, Value>> {
    override fun KoneMapEntry<Key, Value>.hash(): Int = context(keyHashing) { key.hash() } * 31 + context(valueHashing) { value.hash() }
}

public fun <Key, Value> koneMapEntryHashing(keyHashing: Hashing<Key>, valueHashing: Hashing<Value>): Hashing<KoneMapEntry<Key, Value>> =
    KoneMapEntryHashing(keyHashing, valueHashing)

internal open class KoneMapHashing<Key, Value>(open val keyHashing: Hashing<Key>, open val valueHashing: Hashing<Value>) : Hashing<KoneMap<out Key, Value>> {
    override fun KoneMap<out Key, Value>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) {
            val entry = thisIterator.getAndMoveNext()
            hash += context(keyHashing) { entry.key.hash() } xor context(valueHashing) { entry.value.hash() }
        }
        return hash
    }
}

public fun <Key, Value> KoneMap.Companion.hashing(keyHashing: Hashing<Key>, valueHashing: Hashing<Value>): Hashing<KoneMap<out Key, Value>> =
    KoneMapHashing(keyHashing = keyHashing, valueHashing = valueHashing)