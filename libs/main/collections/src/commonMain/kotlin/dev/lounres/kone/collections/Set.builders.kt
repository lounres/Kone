/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.EmptyKoneSet
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import dev.lounres.kone.collections.implementations.KoneListBackedSet
import dev.lounres.kone.collections.implementations.KoneMutableListBackedSet
import dev.lounres.kone.collections.implementations.KoneResizableArrayList
import dev.lounres.kone.collections.implementations.KoneResizableHashSet
import dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.invoke
import dev.lounres.kone.repeat
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add converters for `KoneLinkedSet`

@Suppress("UNCHECKED_CAST")
public fun <E> emptyKoneSet(): KoneSet<E> = EmptyKoneSet as KoneSet<E>

@Suppress("UNUSED_PARAMETER")
public fun <E> koneSetOf(elementContext: Equality<E> = defaultEquality()): KoneSet<E> = emptyKoneSet()

// TODO: Add single-element implementations

public fun <E> koneSetOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneSet<E> =
    if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
        .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneGrowableArrayList<E>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedSet(elementContext, backingList)
    }

public fun <E> koneMutableSetOf(elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> =
    if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
    else KoneMutableListBackedSet(elementContext = elementContext)

public fun <E> koneMutableSetOf(vararg elements: E, elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> =
    if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
        .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneResizableLinkedArrayList<E>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneMutableListBackedSet(elementContext, backingList)
    }

public fun <E> Iterable<E>.toKoneMutableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> {
    if (this is Collection<E>) return this.toKoneMutableSet(elementContext = elementContext)
    
    val result = koneMutableSetOf<E>(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <E> KoneIterable<E>.toKoneMutableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> {
    if (this is KoneList<E>) return this.toKoneMutableSet(elementContext = elementContext)
    if (this is KoneSet<E>) return this.toKoneMutableSet(elementContext = elementContext)

    val result = koneMutableSetOf<E>(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <E> Collection<E>.toKoneMutableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply {
            for (element in this@toKoneMutableSet) add(element)
        }
    else {
        val backingList = KoneResizableArrayList<E>()
        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
        KoneMutableListBackedSet(elementContext, KoneResizableLinkedArrayList<E>().apply { addAllFrom(backingList) })
    }

public fun <E> KoneList<E>.toKoneMutableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply {
            repeat(this@toKoneMutableSet.size) { this.add(this@toKoneMutableSet[it]) }
        }
    else {
        val backingList = KoneResizableArrayList<E>()
        for (index in indices) {
            val element = this[index]
            if (elementContext { element !in backingList }) backingList.add(element)
        }
        KoneMutableListBackedSet(elementContext, backingList)
    }

public fun <E> KoneSet<E>.toKoneMutableSet(elementContext: Equality<E> = defaultEquality()): KoneMutableSet<E> =
    if (elementContext is Hashing<E>)
        KoneResizableHashSet(elementContext = elementContext).apply { addAllFrom(this@toKoneMutableSet) }
    else {
        val backingList = KoneResizableArrayList<E>()
        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
        KoneMutableListBackedSet(elementContext, backingList)
    }

// TODO: Replace inner implementation of `toKoneSet`
//  via KoneGrowableArrayList and KoneResizableHashSet
//  with KoneFixedCapacityList and fixed capacity analogue of KoneResizableHashSet
public fun <E> KoneIterable<E>.toKoneSet(elementContext: Equality<E> = defaultEquality()): KoneSet<E> =
    when {
        this is KoneList<E> -> this.toKoneSet(elementContext = elementContext)
        this is KoneSet<E> -> this.toKoneSet(elementContext = elementContext)
        else -> this.toKoneMutableSet(elementContext = elementContext)
    }

public fun <E> Iterable<E>.toKoneSet(elementContext: Equality<E> = defaultEquality()): KoneSet<E> =
    if (this is Collection<E>) this.toKoneSet(elementContext = elementContext)
    else this.toKoneMutableSet(elementContext = elementContext)

public fun <E> Collection<E>.toKoneSet(elementContext: Equality<E> = defaultEquality()): KoneSet<E> =
    if (size == 0) emptyKoneSet()
    else this.toKoneMutableSet(elementContext = elementContext)

public fun <E> KoneList<E>.toKoneSet(elementContext: Equality<E> = defaultEquality()): KoneSet<E> =
    if (size == 0u) emptyKoneSet()
    else this.toKoneMutableSet(elementContext = elementContext)

public fun <E> KoneSet<E>.toKoneSet(elementContext: Equality<E> = defaultEquality()): KoneSet<E> =
    if (size == 0u) emptyKoneSet()
    else this.toKoneMutableSet(elementContext = elementContext)

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneSet(elementContext: Equality<E> = defaultEquality(), @BuilderInference builderAction: KoneMutableSet<E>.() -> Unit): KoneSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
        else KoneMutableListBackedSet(elementContext = elementContext,backingList = KoneGrowableArrayList<E>())
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <E> buildKoneSet(elementContext: Equality<E> = defaultEquality(), initialCapacity: UInt, @BuilderInference builderAction: KoneMutableSet<E>.() -> Unit): KoneSet<E> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<E>) KoneResizableHashSet(elementContext = elementContext)
        else KoneMutableListBackedSet(elementContext = elementContext, backingList = KoneGrowableArrayList<E>(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}