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
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.eq
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import dev.lounres.kone.option.orElse


internal open class KoneListHashing<Element>(open val elementContext: Hashing<Element>) : Hashing<KoneList<Element>> {
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

internal class KoneListReifiedHashing<Element>(
    override val elementContext: ReifiedHashing<Element>
) : KoneListHashing<Element>(elementContext), ReifiedHashing<KoneList<Element>> {
    override fun contains(element: Any?): Boolean = element is KoneList<*> && element.all { it in elementContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneList<Element>> =
        when {
            element !is KoneList<*> -> None
            element.any { it !in elementContext } -> None
            else -> Some(elementContext as KoneList<Element>)
        }
    override fun reifyOrNull(element: Any?): KoneList<Element>? =
        when {
            element !is KoneList<*> -> null
            element.any { it !in elementContext } -> null
            else -> elementContext as KoneList<Element>
        }
    override fun reify(element: Any?): KoneList<Element> =
        when {
            element !is KoneList<*> -> reificationException()
            element.any { it !in elementContext } -> reificationException()
            else -> elementContext as KoneList<Element>
        }
}

public fun <Element> koneListHashing(elementContext: Hashing<Element>): Hashing<KoneList<Element>> =
    KoneListHashing(elementContext)

public fun <Element> koneListReifiedHashing(elementContext: ReifiedHashing<Element>): ReifiedHashing<KoneList<Element>> =
    KoneListReifiedHashing(elementContext)

internal open class KoneSetHashing<Element>(open val elementContext: Hashing<Element>) : Hashing<KoneSet<out Element>> {
    override fun KoneSet<out Element>.equalsTo(other: KoneSet<out Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisCopied = this.toKoneSet(elementContext)
        if (thisCopied.size != this.size) return false
        val otherCopied = other.toKoneSet(elementContext)
        if (otherCopied.size != other.size) return false
        for (element in thisCopied) if (elementContext { element !in otherCopied }) return false

        return true
    }

    override fun KoneSet<out Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) elementContext {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

internal class KoneSetReifiedHashing<Element>(
    override val elementContext: ReifiedHashing<Element>
) : KoneSetHashing<Element>(
    elementContext = elementContext
), ReifiedHashing<KoneSet<out Element>> {
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

public fun <Element> koneSetHashing(elementContext: Hashing<Element>): Hashing<KoneSet<out Element>> =
    KoneSetHashing(elementContext)

public fun <Element> koneSetReifiedHashing(elementContext: ReifiedHashing<Element>): ReifiedHashing<KoneSet<out Element>> =
    KoneSetReifiedHashing(elementContext)

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