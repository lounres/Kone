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


internal class KoneListEquality<Element>(val elementContext: Equality<Element>) : Equality<KoneList<Element>> {
    override fun KoneList<Element>.equalsTo(other: KoneList<Element>): Boolean {
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

public fun <Element> koneListEquality(elementContext: Equality<Element>): Equality<KoneList<Element>> =
    if (elementContext is Hashing<Element>) KoneListHashing(elementContext)
    else KoneListEquality(elementContext)

internal class KoneSetEquality<Element>(val elementContext: Equality<Element>) : Equality<KoneSet<Element>> {
    override fun KoneSet<Element>.equalsTo(other: KoneSet<Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() !in other }) return false
        }

        return true
    }
}

public fun <Element> koneSetEquality(elementContext: Equality<Element>): Equality<KoneSet<Element>> =
    if (elementContext is Hashing<Element>) KoneSetHashing(elementContext)
    else KoneSetEquality(elementContext)

internal open class KoneMapEntryEquality<Key, Value>(val keyContext: Equality<Key>, var valueContext: Equality<Value>) : Equality<KoneMapEntry<Key, Value>> {
    override fun KoneMapEntry<Key, Value>.equalsTo(other: KoneMapEntry<Key, Value>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
}

public fun <Key, Value> koneMapEntryEquality(keyContext: Equality<Key>, valueContext: Equality<Value>): Equality<KoneMapEntry<Key, Value>> =
    if (keyContext is Hashing<Key> && valueContext is Hashing<Value>) KoneMapEntryHashing(keyContext, valueContext)
    else KoneMapEntryEquality(keyContext, valueContext)

internal class KoneMapEquality<Key, Value>(val keyContext: Equality<Key>, val valueContext: Equality<Value>) : Equality<KoneMap<Key, Value>> {
    override fun KoneMap<Key, Value>.equalsTo(other: KoneMap<Key, Value>): Boolean {
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

public fun <Key, Value> koneMapEquality(keyContext: Equality<Key>, valueContext: Equality<Value>): Equality<KoneMap<Key, Value>> =
    if (keyContext is Hashing<Key> && valueContext is Hashing<Value>) KoneMapEquality(keyContext = keyContext, valueContext = valueContext)
    else KoneMapEquality(keyContext = keyContext, valueContext = valueContext)