/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.comparison

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.getMaybe
import dev.lounres.kone.collections.iterator
import dev.lounres.kone.collections.koneMutableMapOf
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.toKoneSet
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.collections.utils.copyTo
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


internal open class KoneListEquality<Element>(open val elementContext: Equality<Element>) : Equality<KoneList<Element>> {
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

internal class KoneListReifiedEquality<Element>(
    override val elementContext: ReifiedEquality<Element>
) : KoneListEquality<Element>(
    elementContext = elementContext,
), ReifiedEquality<KoneList<Element>> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneList<Element>> =
        if (element is KoneList<*> && element.all { it in elementContext }) Some(element as KoneList<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneList<Element>? =
        if (element is KoneList<*> && element.all { it in elementContext }) element as KoneList<Element>
        else null
    override fun reify(element: Any?): KoneList<Element> =
        if (element is KoneList<*> && element.all { it in elementContext }) element as KoneList<Element>
        else reificationException()
}

public fun <Element> koneListEquality(elementContext: Equality<Element>): Equality<KoneList<Element>> =
    if (elementContext is Hashing<Element>) KoneListHashing(elementContext)
    else KoneListEquality(elementContext)

public fun <Element> koneListReifiedEquality(elementContext: ReifiedEquality<Element>): ReifiedEquality<KoneList<Element>> =
    if (elementContext is ReifiedHashing<Element>) KoneListReifiedHashing(elementContext)
    else KoneListReifiedEquality(elementContext)

internal open class KoneSetEquality<Element>(open val elementContext: Equality<Element>) : Equality<KoneSet<out Element>> {
    override fun KoneSet<out Element>.equalsTo(other: KoneSet<out Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        
        val thisCopied = this.toKoneSet(elementContext)
        if (thisCopied.size != this.size) return false
        val otherCopied = other.toKoneSet(elementContext)
        if (otherCopied.size != other.size) return false
        for (element in thisCopied) if (elementContext { element !in otherCopied }) return false
        
        return true
    }
}

internal class KoneSetReifiedEquality<Element>(
    override val elementContext: ReifiedEquality<Element>
) : KoneSetEquality<Element>(
    elementContext = elementContext,
), ReifiedEquality<KoneSet<out Element>> {
    override fun contains(element: Any?): Boolean = element is KoneList<*> && element.all { it in elementContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneSet<out Element>> =
        when {
            element !is KoneSet<*> -> None
            element.any { it !in elementContext } -> None
            else -> Some(elementContext as KoneSet<out Element>)
        }
    override fun reifyOrNull(element: Any?): KoneSet<out Element>? =
        when {
            element !is KoneSet<*> -> null
            element.any { it !in elementContext } -> null
            else -> elementContext as KoneSet<out Element>
        }
    override fun reify(element: Any?): KoneSet<out Element> =
        when {
            element !is KoneSet<*> -> reificationException()
            element.any { it !in elementContext } -> reificationException()
            else -> elementContext as KoneSet<out Element>
        }
}

public fun <Element> koneSetEquality(elementContext: Equality<Element>): Equality<KoneSet<out Element>> =
    if (elementContext is Hashing<Element>) KoneSetHashing(elementContext)
    else KoneSetEquality(elementContext)

public fun <Element> koneReifiedSetEquality(elementContext: ReifiedEquality<Element>): ReifiedEquality<KoneSet<out Element>> =
    if (elementContext is ReifiedHashing<Element>) KoneSetReifiedHashing(elementContext)
    else KoneSetReifiedEquality(elementContext)

internal open class KoneMapEntryEquality<Key, Value>(open val keyContext: Equality<Key>, open val valueContext: Equality<Value>) : Equality<KoneMapEntry<Key, Value>> {
    override fun KoneMapEntry<Key, Value>.equalsTo(other: KoneMapEntry<Key, Value>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
}

internal class KoneMapEntryReifiedEquality<Key, Value>(
    override val keyContext: ReifiedEquality<Key>,
    override val valueContext: ReifiedEquality<Value>,
) : KoneMapEntryEquality<Key, Value>(
    keyContext = keyContext,
    valueContext = valueContext,
), ReifiedEquality<KoneMapEntry<Key, Value>> {
    override fun contains(element: Any?): Boolean =
        element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext
    override fun reifyMaybe(element: Any?): Maybe<KoneMapEntry<Key, Value>> =
        if (element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext) Some(element as KoneMapEntry<Key, Value>)
        else None
    override fun reifyOrNull(element: Any?): KoneMapEntry<Key, Value>? =
        if (element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext) element as KoneMapEntry<Key, Value>
        else null
    override fun reify(element: Any?): KoneMapEntry<Key, Value> =
        if (element is KoneMapEntry<*, *> && element.key in keyContext && element.value in valueContext) element as KoneMapEntry<Key, Value>
        else reificationException()
    
}

public fun <Key, Value> koneMapEntryEquality(keyContext: Equality<Key>, valueContext: Equality<Value>): Equality<KoneMapEntry<Key, Value>> =
    if (keyContext is Hashing<Key> && valueContext is Hashing<Value>) KoneMapEntryHashing(keyContext, valueContext)
    else KoneMapEntryEquality(keyContext, valueContext)

public fun <Key, Value> koneMapEntryReifiedEquality(keyContext: ReifiedEquality<Key>, valueContext: ReifiedEquality<Value>): ReifiedEquality<KoneMapEntry<Key, Value>> =
    if (keyContext is ReifiedHashing<Key> && valueContext is ReifiedHashing<Value>) KoneMapEntryReifiedHashing(keyContext, valueContext)
    else KoneMapEntryReifiedEquality(keyContext, valueContext)

internal open class KoneMapEquality<Key, Value>(open val keyContext: Equality<Key>, open val valueContext: Equality<Value>) : Equality<KoneMap<out Key, Value>> {
    override fun KoneMap<out Key, Value>.equalsTo(other: KoneMap<out Key, Value>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        
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
}

internal class KoneMapReifiedEquality<Key, Value>(
    override val keyContext: ReifiedEquality<Key>,
    override val valueContext: ReifiedEquality<Value>,
) : KoneMapEquality<Key, Value>(
    keyContext = keyContext,
    valueContext = valueContext,
), ReifiedEquality<KoneMap<out Key, Value>> {
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

public fun <Key, Value> koneMapEquality(keyContext: Equality<Key>, valueContext: Equality<Value>): Equality<KoneMap<out Key, Value>> =
    if (keyContext is Hashing<Key> && valueContext is Hashing<Value>) KoneMapHashing(keyContext = keyContext, valueContext = valueContext)
    else KoneMapEquality(keyContext = keyContext, valueContext = valueContext)

public fun <Key, Value> koneMapReifiedEquality(keyContext: ReifiedEquality<Key>, valueContext: ReifiedEquality<Value>): ReifiedEquality<KoneMap<out Key, Value>> =
    if (keyContext is ReifiedHashing<Key> && valueContext is ReifiedHashing<Value>) KoneMapReifiedHashing(keyContext = keyContext, valueContext = valueContext)
    else KoneMapReifiedEquality(keyContext = keyContext, valueContext = valueContext)