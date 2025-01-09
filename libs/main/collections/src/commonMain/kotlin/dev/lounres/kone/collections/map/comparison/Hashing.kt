/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.map.comparison

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.map.iterator
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.copyTo
import dev.lounres.kone.comparison.*
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


internal open class KoneMapEntryHashing<Key, Value>(open val keyContext: Hashing<Key>, open val valueContext: Hashing<Value>) : Hashing<KoneMapEntry<Key, Value>> {
    override fun KoneMapEntry<Key, Value>.equalsTo(other: KoneMapEntry<Key, Value>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
    override fun KoneMapEntry<Key, Value>.hash(): Int = keyContext { key.hash() } xor valueContext { value.hash() }
}

internal open class KoneMapEntryReifiedHashing<Key, Value>(
    override val keyContext: ReifiedHashing<Key>,
    override var valueContext: ReifiedHashing<Value>,
) : KoneMapEntryHashing<Key, Value>(
    keyContext = keyContext,
    valueContext = valueContext,
), ReifiedHashing<KoneMapEntry<Key, Value>> {
    override fun contains(element: Any?): Boolean =
        element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext
    override fun reifyMaybe(element: Any?): Maybe<KoneMapEntry<Key, Value>> =
        if (element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext) Some(element as KoneMapEntry<Key, Value>)
        else None
    override fun reifyOrNull(element: Any?): KoneMapEntry<Key, Value>? =
        if (element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext)element as KoneMapEntry<Key, Value>
        else null
    override fun reify(element: Any?): KoneMapEntry<Key, Value> =
        if (element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext) element as KoneMapEntry<Key, Value>
        else reificationException()
}

public fun <Key, Value> koneMapEntryHashing(keyContext: Hashing<Key>, valueContext: Hashing<Value>): Hashing<KoneMapEntry<Key, Value>> =
    KoneMapEntryHashing(keyContext, valueContext)

public fun <Key, Value> koneMapEntryReifiedHashing(keyContext: ReifiedHashing<Key>, valueContext: ReifiedHashing<Value>): ReifiedHashing<KoneMapEntry<Key, Value>> =
    KoneMapEntryReifiedHashing(keyContext, valueContext)

internal open class KoneMapHashing<Key, Value>(open val keyContext: Hashing<Key>, open val valueContext: Hashing<Value>) : Hashing<KoneMap<out Key, Value>> {
    override fun KoneMap<out Key, Value>.equalsTo(other: KoneMap<out Key, Value>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false
        
        val thisCopied: KoneMap<Key, Value> = this.copyTo(koneMutableMapOf(keyContext = keyContext))
        if (this.size != thisCopied.size) return false
        val otherCopied: KoneMap<Key, Value> = other.copyTo(koneMutableMapOf(keyContext = keyContext))
        if (other.size != otherCopied.size) return false
        for ((key, value) in thisCopied) {
            val otherValue = otherCopied.getMaybe(key).orElse { return false }
            if (valueContext { value neq otherValue }) return false
        }

        return true
    }

    override fun KoneMap<out Key, Value>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) {
            val (key, value) = thisIterator.getAndMoveNext()
            hash += keyContext { key.hash() } xor valueContext { value.hash() }
        }
        return hash
    }
}

internal class KoneMapReifiedHashing<Key, Value>(
    override val keyContext: ReifiedHashing<Key>,
    override val valueContext: ReifiedHashing<Value>
) : KoneMapHashing<Key, Value>(
    keyContext = keyContext,
    valueContext = valueContext,
), ReifiedHashing<KoneMap<out Key, Value>> {
    override fun contains(element: Any?): Boolean =
        element is KoneMap<*, *> && element.nodesView.all { it.key in keyContext && it.value in valueContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneMap<Key, Value>> =
        if (element is KoneMap<*, *> && element.nodesView.all { it.key in keyContext && it.value in valueContext }) Some(element as KoneMap<Key, Value>)
        else None
    override fun reifyOrNull(element: Any?): KoneMap<Key, Value>? =
        if (element is KoneMap<*, *> && element.nodesView.all { it.key in keyContext && it.value in valueContext }) element as KoneMap<Key, Value>
        else null
    override fun reify(element: Any?): KoneMap<Key, Value> =
        if (element is KoneMap<*, *> && element.nodesView.all { it.key in keyContext && it.value in valueContext }) element as KoneMap<Key, Value>
        else reificationException()
}

public fun <Key, Value> koneMapHashing(keyContext: Hashing<Key>, valueContext: Hashing<Value>): Hashing<KoneMap<out Key, Value>> =
    KoneMapHashing(keyContext = keyContext, valueContext = valueContext)

public fun <Key, Value> koneMapReifiedHashing(keyContext: ReifiedHashing<Key>, valueContext: ReifiedHashing<Value>): ReifiedHashing<KoneMap<out Key, Value>> =
    KoneMapReifiedHashing(keyContext = keyContext, valueContext = valueContext)