/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.iterable.KoneIterable
import dev.lounres.kone.collections.iterable.contains
import dev.lounres.kone.collections.iterator.KoneRemovableIterator
import dev.lounres.kone.collections.iterator.next
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.set.empty.KoneEmptyNoddedReifiedSet
import dev.lounres.kone.collections.set.implementations.*
import dev.lounres.kone.collections.set.singleton.KoneSingletonNoddedReifiedSet
import dev.lounres.kone.collections.set.singleton.KoneSingletonNoddedSet
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


// TODO: Add builders for nodded sets and linked sets

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSet.Companion.empty(): KoneSet<Element> = KoneEmptyNoddedReifiedSet as KoneSet<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneReifiedSet.Companion.empty(): KoneReifiedSet<Element> = KoneEmptyNoddedReifiedSet as KoneReifiedSet<Element>

@Suppress("UNUSED_PARAMETER")
public fun <Element> KoneSet.Companion.of(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSet.empty()

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneSet.Companion.contextualOf(): KoneSet<Element> =
//    KoneSet.of(
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

@Suppress("UNUSED_PARAMETER")
public inline fun <reified Element> KoneReifiedSet.Companion.of(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneReifiedSet.empty()

@Suppress("UNUSED_PARAMETER")
public fun <Element> KoneReifiedSet.Companion.of(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneReifiedSet.empty()

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneReifiedSet.Companion.contextualOf(): KoneReifiedSet<Element> =
//    KoneReifiedSet.of(
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

@Suppress("unused")
public fun <Element> KoneSet.Companion.of(
    element: Element,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSingletonNoddedSet(element, elementEquality)

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneSet.Companion.contextualOf(
//    element: Element,
//): KoneSet<Element> =
//    KoneSet.of(
//        element = element,
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

@Suppress("unused")
public inline fun <reified Element> KoneReifiedSet.Companion.of(
    element: Element,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneSingletonNoddedReifiedSet(element, Reification.defaultFor(), elementEquality)

@Suppress("unused")
public fun <Element> KoneReifiedSet.Companion.of(
    element: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, elementReification = elementReification, elementEquality = elementEquality)

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneReifiedSet.Companion.contextualOf(
//    element: Element,
//): KoneReifiedSet<Element> =
//    KoneReifiedSet.of(
//        element = element,
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public fun <Element> KoneSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> =
    if (elementHashing != null)
        KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in elements) if (elementEquality { element !in backingList }) backingList.add(element)
        KoneListBackedSet(elementEquality, backingList)
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneSet.Companion.contextualOf(
//    vararg elements: Element,
//): KoneSet<Element> =
//    KoneSet.of(
//        elements = elements,
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public inline fun <reified Element> KoneReifiedSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneReifiedSet.of(
    elements = elements,
    elementReification = Reification.defaultFor(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public fun <Element> KoneReifiedSet.Companion.of(
    vararg elements: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    if (elementHashing != null)
        KoneHashResizableReifiedSet(
            elementReification = elementReification,
            elementEquality = elementEquality,
            elementHashing = elementHashing
        ).apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in elements) if (elementEquality { element !in backingList }) backingList.add(element)
        KoneListBackedReifiedSet(elementReification, elementEquality, backingList)
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneReifiedSet.Companion.contextualOf(
//    vararg elements: Element,
//): KoneReifiedSet<Element> =
//    KoneReifiedSet.of(
//        elements = elements,
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public fun <Element> KoneMutableSet.Companion.of(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableSet(elementEquality = elementEquality)

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneMutableSet.Companion.contextualOf(): KoneMutableSet<Element> =
//    KoneMutableSet.of(
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public inline fun <reified Element> KoneMutableReifiedSet.Companion.of(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> = KoneMutableReifiedSet.of(
    elementReification = Reification.defaultFor(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public fun <Element> KoneMutableReifiedSet.Companion.of(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality)

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneMutableReifiedSet.Companion.contextualOf(): KoneMutableReifiedSet<Element> =
//    KoneMutableReifiedSet.of(
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public fun <Element> KoneMutableSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null)
        KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayResizableLinkedList<Element>()
        for (element in elements) if (elementEquality { element !in backingList }) backingList.add(element)
        KoneListBackedMutableSet(elementEquality, backingList)
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneMutableSet.Companion.contextualOf(
//    vararg elements: Element,
//): KoneMutableSet<Element> =
//    KoneMutableSet.of(
//        elements = elements,
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public inline fun <reified Element> KoneMutableReifiedSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    KoneMutableReifiedSet.of(
        elements = elements,
        elementReification = Reification.defaultFor(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public fun <Element> KoneMutableReifiedSet.Companion.of(
    vararg elements: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null)
        KoneHashResizableReifiedSet(
            elementReification = elementReification,
            elementEquality = elementEquality,
            elementHashing = elementHashing
        ).apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayResizableLinkedList<Element>()
        for (element in elements) if (elementEquality { element !in backingList }) backingList.add(element)
        KoneListBackedMutableReifiedSet(
            elementReification = elementReification,
            elementEquality = elementEquality,
            backingList = backingList,
        )
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneMutableReifiedSet.Companion.contextualOf(
//    vararg elements: Element,
//): KoneMutableReifiedSet<Element> =
//    KoneMutableReifiedSet.of(
//        elements = elements,
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public fun <Element> KoneIterable<Element>.toKoneMutableSet(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null)
        KoneHashResizableSet(elementEquality = elementEquality,  elementHashing = elementHashing)
            .apply { addAllFrom(this@toKoneMutableSet) }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in this) if (elementEquality { element !in backingList }) backingList.add(element)
        KoneListBackedMutableSet(elementEquality, backingList)
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneIterable<Element>.toKoneContextualMutableSet(): KoneMutableSet<Element> =
//    toKoneMutableSet(
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public inline fun <reified Element> KoneIterable<Element>.toKoneMutableReifiedSet(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    toKoneMutableReifiedSet(
        elementReification = Reification.defaultFor(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public fun <Element> KoneIterable<Element>.toKoneMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null)
        KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
            .apply { addAllFrom(this@toKoneMutableReifiedSet) }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in this) if (elementEquality { element !in backingList }) backingList.add(element)
        KoneListBackedMutableReifiedSet(elementReification, elementEquality, backingList)
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneIterable<Element>.toKoneContextualMutableReifiedSet(): KoneMutableReifiedSet<Element> =
//    toKoneMutableReifiedSet(
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public fun <Element> KoneIterable<Element>.toKoneSet(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> =
    when (size) {
        0u -> KoneSet.empty()
        1u -> KoneSingletonNoddedSet(singleElement = iterator().getNext(), elementEquality = elementEquality)
        else -> this.toKoneMutableSet(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneIterable<Element>.toKoneContextualSet(): KoneSet<Element> =
//    toKoneSet(
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

public inline fun <reified Element> KoneIterable<Element>.toKoneReifiedSet(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    toKoneReifiedSet(
        elementReification = Reification.defaultFor(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public fun <Element> KoneIterable<Element>.toKoneReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    when (size) {
        0u -> KoneReifiedSet.empty()
        1u -> KoneSingletonNoddedReifiedSet(
            singleElement = iterator().getNext(),
            elementReification = elementReification,
            elementEquality = elementEquality,
        )
        else -> this.toKoneMutableReifiedSet(
            elementReification = elementReification,
            elementEquality = elementEquality,
            elementHashing = elementHashing,
            elementOrder = elementOrder,
        )
    }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Element> KoneIterable<Element>.toKoneContextualReifiedSet(): KoneReifiedSet<Element> =
//    toKoneReifiedSet(
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//    )

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneSetBuilder<Element> @PublishedApi internal constructor(result: KoneMutableSet<Element>) : KoneMutableSet<Element> {
    private var result: KoneMutableSet<Element>? = result
    
    override val size: UInt get() {
        val result = result ?: error("This KoneSet builder is already used")
        return result.size
    }
    
    override fun contains(element: Element): Boolean {
        val result = result ?: error("This KoneSet builder is already used")
        return element in result
    }
    
    override fun add(element: Element) {
        val result = result ?: error("This KoneSet builder is already used")
        result.add(element)
    }
    
    override fun remove(element: Element) {
        val result = result ?: error("This KoneSet builder is already used")
        result.remove(element)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneSet builder is already used")
        result.removeAll()
    }
    
    override fun iterator(): KoneRemovableIterator<Element> {
        val result = result ?: error("This KoneSet builder is already used")
        return result.iterator()
    }
    
    public operator fun Element.unaryPlus() {
        val result = result ?: error("This KoneSet builder is already used")
        result.add(this)
    }
    
    public operator fun Element.unaryMinus() {
        val result = result ?: error("This KoneSet builder is already used")
        result.remove(this)
    }
    
    @PublishedApi
    internal fun build(): KoneSet<Element> {
        val result = result ?: error("This KoneSet builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneReifiedSetBuilder<Element> @PublishedApi internal constructor(result: KoneMutableReifiedSet<Element>) : KoneMutableReifiedSet<Element> {
    private var result: KoneMutableReifiedSet<Element>? = result
    
    override val size: UInt get() {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        return result.size
    }
    
    override fun contains(element: Element): Boolean {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        return element in result
    }
    
    override fun add(element: Element) {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        result.add(element)
    }
    
    override fun remove(element: Element) {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        result.remove(element)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        result.removeAll()
    }
    
    override fun iterator(): KoneRemovableIterator<Element> {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        return result.iterator()
    }
    
    public operator fun Element.unaryPlus() {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        result.add(this)
    }
    
    public operator fun Element.unaryMinus() {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        result.remove(this)
    }
    
    @PublishedApi
    internal fun build(): KoneReifiedSet<Element> {
        val result = result ?: error("This KoneReifiedSet builder is already used")
        return result.also { this.result = null }
    }
}

public inline fun <Element> KoneSet.Companion.build(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    builderAction: KoneSetBuilder<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return KoneSetBuilder(result).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Element> KoneSet.Companion.buildContextual(
//    builderAction: KoneSetBuilder<Element>.() -> Unit,
//): KoneSet<Element> =
//    KoneSet.build(
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//        builderAction = builderAction
//    )

public inline fun <reified Element> KoneReifiedSet.Companion.build(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    KoneReifiedSet.build(
        elementReification = Reification.defaultFor(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
        builderAction = builderAction
    )

public inline fun <Element> KoneReifiedSet.Companion.build(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return KoneReifiedSetBuilder(result).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Element> KoneReifiedSet.Companion.buildContextual(
//    builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
//): KoneReifiedSet<Element> =
//    KoneReifiedSet.build(
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//        builderAction = builderAction,
//    )

public inline fun <Element> KoneSet.Companion.build(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    builderAction: KoneSetBuilder<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return KoneSetBuilder(result).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Element> KoneSet.Companion.buildContextual(
//    initialCapacity: UInt,
//    builderAction: KoneSetBuilder<Element>.() -> Unit,
//): KoneSet<Element> =
//    KoneSet.build(
//        initialCapacity = initialCapacity,
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//        builderAction = builderAction,
//    )

public inline fun <reified Element> KoneReifiedSet.Companion.build(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    KoneReifiedSet.build(
        initialCapacity = initialCapacity,
        elementReification = Reification.defaultFor(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
        builderAction = builderAction,
    )

public inline fun <Element> KoneReifiedSet.Companion.build(
    initialCapacity: UInt,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    builderAction: KoneReifiedSetBuilder<Element>.() -> Unit
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return KoneReifiedSetBuilder(result).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Element> KoneReifiedSet.Companion.buildContextual(
//    initialCapacity: UInt,
//    builderAction: KoneReifiedSetBuilder<Element>.() -> Unit
//): KoneReifiedSet<Element> =
//    KoneReifiedSet.build(
//        initialCapacity = initialCapacity,
//        elementReification = Reification.getFor(),
//        elementEquality = Equality.getFor(),
//        elementHashing = Hashing.getForOrNull(),
//        elementOrder = Order.getForOrNull(),
//        builderAction = builderAction,
//    )