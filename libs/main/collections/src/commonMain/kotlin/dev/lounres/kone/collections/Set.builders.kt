/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneEmptyNoddedReifiedSet
import dev.lounres.kone.collections.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.implementations.KoneListBackedSet
import dev.lounres.kone.collections.implementations.KoneListBackedMutableSet
import dev.lounres.kone.collections.implementations.KoneHashResizableSet
import dev.lounres.kone.collections.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.implementations.KoneHashResizableReifiedSet
import dev.lounres.kone.collections.implementations.KoneListBackedMutableReifiedSet
import dev.lounres.kone.collections.implementations.KoneListBackedReifiedSet
import dev.lounres.kone.collections.implementations.KoneSingletonNoddedReifiedSet
import dev.lounres.kone.collections.implementations.KoneSingletonNoddedSet
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultReifiedEquality
import dev.lounres.kone.context.invoke
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders for nodded sets and linked set

@Suppress("UNCHECKED_CAST")
public fun <Element> emptyKoneSet(): KoneSet<Element> = KoneEmptyNoddedReifiedSet as KoneSet<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> emptyKoneReifiedSet(): KoneReifiedSet<Element> = KoneEmptyNoddedReifiedSet as KoneReifiedSet<Element>

@Suppress("UNUSED_PARAMETER")
public fun <Element> koneSetOf(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> = emptyKoneSet()

public inline fun <reified Element> koneReifiedSetOf(): KoneReifiedSet<Element> = koneReifiedSetOf(elementContext = defaultReifiedEquality())

@Suppress("UNUSED_PARAMETER")
public fun <Element> koneReifiedSetOf(elementContext: ReifiedEquality<Element>): KoneReifiedSet<Element> = emptyKoneReifiedSet()

public fun <Element> koneSetOf(element: Element, elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    KoneSingletonNoddedSet(element, elementContext)

public inline fun <reified Element> koneReifiedSetOf(element: Element): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, defaultReifiedEquality())

public fun <Element> koneReifiedSetOf(element: Element, elementContext: ReifiedEquality<Element>): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, elementContext)

public fun <Element> koneSetOf(vararg elements: Element, elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    if (elementContext is Hashing<Element>)
        KoneHashResizableSet(elementContext = elementContext)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedSet(elementContext, backingList)
    }

public inline fun <reified Element> koneReifiedSetOf(vararg elements: Element): KoneReifiedSet<Element> = koneReifiedSetOf(elements = elements, elementContext = defaultReifiedEquality())

public fun <Element> koneReifiedSetOf(vararg elements: Element, elementContext: ReifiedEquality<Element>): KoneReifiedSet<Element> =
    if (elementContext is ReifiedHashing<Element>)
        KoneHashResizableReifiedSet(elementContext = elementContext)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedReifiedSet(elementContext, backingList)
    }

public fun <Element> koneMutableSetOf(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>) KoneHashResizableSet(elementContext = elementContext)
    else KoneListBackedMutableSet(elementContext = elementContext)

public fun <Element> koneMutableReifiedSetOf(elementContext: ReifiedEquality<Element>): KoneMutableReifiedSet<Element> =
    if (elementContext is ReifiedHashing<Element>) KoneHashResizableReifiedSet(elementContext = elementContext)
    else KoneListBackedMutableReifiedSet(elementContext = elementContext)

public fun <Element> koneMutableSetOf(vararg elements: Element, elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>)
        KoneHashResizableSet(elementContext = elementContext)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayResizableLinkedList<Element>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedMutableSet(elementContext, backingList)
    }

public inline fun <reified Element> koneMutableReifiedSetOf(vararg elements: Element): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(elements = elements, elementContext = defaultReifiedEquality())

public fun <Element> koneMutableReifiedSetOf(vararg elements: Element, elementContext: ReifiedEquality<Element>): KoneMutableReifiedSet<Element> =
    if (elementContext is ReifiedHashing<Element>)
        KoneHashResizableReifiedSet(elementContext = elementContext)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayResizableLinkedList<Element>()
        for (element in elements) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedMutableReifiedSet(elementContext, backingList)
    }

public inline fun <reified Element> KoneIterable<Element>.toKoneMutableReifiedSet(): KoneMutableReifiedSet<Element> =
    toKoneMutableReifiedSet(elementContext = defaultReifiedEquality())

public fun <Element> KoneIterable<Element>.toKoneMutableReifiedSet(elementContext: ReifiedEquality<Element>): KoneMutableReifiedSet<Element> =
    if (elementContext is ReifiedHashing<Element>)
        KoneHashResizableReifiedSet(elementContext = elementContext)
            .apply { addAllFrom(this@toKoneMutableReifiedSet) }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedMutableReifiedSet(elementContext, backingList)
    }

public fun <Element> KoneIterable<Element>.toKoneMutableSet(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
    if (elementContext is Hashing<Element>)
        KoneHashResizableSet(elementContext = elementContext)
            .apply { addAllFrom(this@toKoneMutableSet) }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
        KoneListBackedMutableSet(elementContext, backingList)
    }

public fun <Element> KoneIterable<Element>.toKoneSet(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
    when {
        size == 0u -> emptyKoneSet()
        size == 1u -> KoneSingletonNoddedSet(singleElement = iterator().getNext(), elementContext = elementContext)
        else -> this.toKoneMutableSet(elementContext = elementContext)
    }

public inline fun <reified Element> KoneIterable<Element>.toKoneReifiedSet(): KoneReifiedSet<Element> =
    toKoneReifiedSet(defaultReifiedEquality())

public fun <Element> KoneIterable<Element>.toKoneReifiedSet(elementContext: ReifiedEquality<Element>): KoneReifiedSet<Element> =
    when {
        size == 0u -> emptyKoneReifiedSet()
        size == 1u -> KoneSingletonNoddedReifiedSet(singleElement = iterator().getNext(), elementContext = elementContext)
        else -> this.toKoneMutableReifiedSet(elementContext = elementContext)
    }

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneSet(elementContext: Equality<Element> = defaultEquality(), @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<Element>) KoneHashResizableSet(elementContext = elementContext)
        else KoneListBackedMutableSet(elementContext = elementContext, backingList = KoneArrayGrowableList<Element>())
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <reified Element> buildKoneReifiedSet(@BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit): KoneReifiedSet<Element> =
    buildKoneReifiedSet(defaultReifiedEquality(), builderAction)

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneReifiedSet(elementContext: ReifiedEquality<Element>, @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is ReifiedHashing<Element>) KoneHashResizableReifiedSet(elementContext = elementContext)
        else KoneListBackedMutableReifiedSet(elementContext = elementContext, backingList = KoneArrayGrowableList<Element>())
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneSet(elementContext: Equality<Element> = defaultEquality(), initialCapacity: UInt, @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is Hashing<Element>) KoneHashResizableSet(elementContext = elementContext)
        else KoneListBackedMutableSet(elementContext = elementContext, backingList = KoneArrayGrowableList<Element>(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <reified Element> buildKoneReifiedSet(initialCapacity: UInt, @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit): KoneReifiedSet<Element> =
    buildKoneReifiedSet(defaultReifiedEquality(), initialCapacity, builderAction)

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> buildKoneReifiedSet(elementContext: ReifiedEquality<Element>, initialCapacity: UInt, @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementContext is ReifiedHashing<Element>) KoneHashResizableReifiedSet(elementContext = elementContext)
        else KoneListBackedMutableReifiedSet(elementContext = elementContext, backingList = KoneArrayGrowableList<Element>(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}