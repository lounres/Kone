/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneArrayResizableList
import dev.lounres.kone.collections.implementations.KoneEmptyNoddedSet
import dev.lounres.kone.collections.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.implementations.KoneListBackedSet
import dev.lounres.kone.collections.implementations.KoneMutableListBackedSet
import dev.lounres.kone.collections.implementations.KoneResizableHashSet
import dev.lounres.kone.collections.implementations.KoneResizableLinkedArrayList
import dev.lounres.kone.collections.implementations.KoneSingletonNoddedSet
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.context.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders for nodded sets and linked set

@Suppress("UNCHECKED_CAST")
public fun <Element> emptyKoneSet(): KoneSet<Element> = KoneEmptyNoddedSet as KoneSet<Element>

@Suppress("UNUSED_PARAMETER")
public fun <Element> koneSetOf(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> = emptyKoneSet()

@Suppress("UNUSED_PARAMETER")
public fun <Element> koneSetOf(element: Element, elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    KoneSingletonNoddedSet(element, elementContext)

public fun <Element> koneSetOf(vararg elements: Element, elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    if (elementContext is Hashing<Element>) KoneResizableHashSet(elementContext = elementContext)
        .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedSet(elementContext, backingList)
    }

public fun <Element> koneMutableSetOf(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>) KoneResizableHashSet(elementContext = elementContext)
    else KoneMutableListBackedSet(elementContext = elementContext)

public fun <Element> koneMutableSetOf(vararg elements: Element, elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>) KoneResizableHashSet(elementContext = elementContext)
        .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneResizableLinkedArrayList<Element>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneMutableListBackedSet(elementContext, backingList)
    }

public fun <Element> Iterable<Element>.toKoneMutableSet(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> {
    if (this is Collection<Element>) return this.toKoneMutableSet(elementContext = elementContext)
    
    val result = koneMutableSetOf<Element>(elementContext = elementContext)
    for (element in this) result.add(element)
    return result
}

public fun <Element> Collection<Element>.toKoneMutableSet(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>)
        KoneResizableHashSet(elementContext = elementContext).apply {
            for (element in this@toKoneMutableSet) add(element)
        }
    else {
        val backingList = KoneArrayResizableList<Element>()
        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
        KoneMutableListBackedSet(elementContext, KoneResizableLinkedArrayList<Element>().apply { addAllFrom(backingList) })
    }

public fun <Element> KoneIterable<Element>.toKoneMutableSet(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>)
        KoneResizableHashSet(elementContext = elementContext).apply { addAllFrom(this@toKoneMutableSet) }
    else {
        val backingList = KoneArrayResizableList<Element>()
        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
        KoneMutableListBackedSet(elementContext, backingList)
    }

public fun <Element> Iterable<Element>.toKoneSet(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    if (this is Collection<Element>) this.toKoneSet(elementContext = elementContext)
    else this.toKoneMutableSet(elementContext = elementContext)

public fun <Element> Collection<Element>.toKoneSet(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    if (size == 0) emptyKoneSet()
    else this.toKoneMutableSet(elementContext = elementContext)

public fun <Element> KoneIterable<Element>.toKoneSet(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    when {
        size == 0u -> emptyKoneSet()
        size == 1u -> KoneSingletonNoddedSet(singleElement = iterator().getNext(), elementContext = elementContext)
        else -> this.toKoneMutableSet(elementContext = elementContext)
    }

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneSet(elementContext: Equality<Element> = defaultEquality(), @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<Element>) KoneResizableHashSet(elementContext = elementContext)
        else KoneMutableListBackedSet(elementContext = elementContext,backingList = KoneArrayGrowableList<Element>())
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneSet(elementContext: Equality<Element> = defaultEquality(), initialCapacity: UInt, @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<Element>) KoneResizableHashSet(elementContext = elementContext)
        else KoneMutableListBackedSet(elementContext = elementContext, backingList = KoneArrayGrowableList<Element>(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}