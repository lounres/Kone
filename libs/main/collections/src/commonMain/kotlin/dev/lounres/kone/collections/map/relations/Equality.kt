/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.map.relations

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.map.iterator
import dev.lounres.kone.collections.utils.copyTo
import dev.lounres.kone.context
import dev.lounres.kone.option.orElse
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.eq
import dev.lounres.kone.relations.neq


internal class KoneMapEntryEquality<Key, Value>(val keyEquality: Equality<Key>, val valueEquality: Equality<Value>) : Equality<KoneMapEntry<Key, Value>> {
    override fun KoneMapEntry<Key, Value>.equalsTo(other: KoneMapEntry<Key, Value>): Boolean =
        context(keyEquality) { this.key eq other.key } && context(valueEquality) { this.value eq other.value }
}

public fun <Key, Value> koneMapEntryEquality(keyEquality: Equality<Key>, valueEquality: Equality<Value>): Equality<KoneMapEntry<Key, Value>> =
    KoneMapEntryEquality(keyEquality, valueEquality)

internal open class KoneMapEquality<Key, Value>(open val keyEquality: Equality<Key>, open val valueEquality: Equality<Value>) : Equality<KoneMap<out Key, Value>> {
    override fun KoneMap<out Key, Value>.equalsTo(other: KoneMap<out Key, Value>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        
        val thisCopied: KoneMap<Key, Value> = this.copyTo(koneMutableMapOf(keyEquality = keyEquality))
        if (this.size != thisCopied.size) return false
        val otherCopied: KoneMap<Key, Value> = other.copyTo(koneMutableMapOf(keyEquality = keyEquality))
        if (other.size != otherCopied.size) return false
        for ((key, value) in thisCopied) {
            val otherValue = otherCopied.getMaybe(key).orElse { return false }
            if (context(valueEquality) { value neq otherValue }) return false
        }
        
        return true
    }
}

public fun <Key, Value> koneMapEquality(keyEquality: Equality<Key>, valueEquality: Equality<Value>): Equality<KoneMap<out Key, Value>> =
    KoneMapEquality(keyEquality, valueEquality)