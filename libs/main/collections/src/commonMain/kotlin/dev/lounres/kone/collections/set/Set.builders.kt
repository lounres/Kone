/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.set.empty.KoneEmptyNoddedReifiedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableSet
import dev.lounres.kone.collections.set.implementations.KoneHashResizableSet
import dev.lounres.kone.collections.set.implementations.KoneHashResizableReifiedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableReifiedSet
import dev.lounres.kone.collections.set.implementations.KoneListBackedReifiedSet
import dev.lounres.kone.collections.set.singleton.KoneSingletonNoddedReifiedSet
import dev.lounres.kone.collections.set.singleton.KoneSingletonNoddedSet
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.KoneRemovableIterator
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.loadEqualityFor
import dev.lounres.kone.relations.loadHashingForOrNull
import dev.lounres.kone.relations.loadOrderForOrNull
import dev.lounres.kone.relations.loadReificationFor
import dev.lounres.kone.context
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName


// TODO: Add builders for nodded sets and linked sets

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSet.Companion.empty(): KoneSet<Element> = KoneEmptyNoddedReifiedSet as KoneSet<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneReifiedSet.Companion.empty(): KoneReifiedSet<Element> = KoneEmptyNoddedReifiedSet as KoneReifiedSet<Element>

@Suppress("UNUSED_PARAMETER")
public fun <Element> KoneSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSet.empty()

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    KoneSet.of(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("UNUSED_PARAMETER")
public inline fun <reified Element> KoneReifiedSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneReifiedSet.empty()

@Suppress("UNUSED_PARAMETER")
public fun <Element> KoneReifiedSet.Companion.of(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneReifiedSet.empty()

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneReifiedSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    KoneReifiedSet.of(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("unused")
public fun <Element> KoneSet.Companion.of(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSingletonNoddedSet(element, elementEquality)

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneSet.Companion.contextualOf(
    element: Element,
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    KoneSet.of(
        element = element,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("unused")
public inline fun <reified Element> KoneReifiedSet.Companion.of(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneSingletonNoddedReifiedSet(element, Reification(), elementEquality)

@Suppress("unused")
public fun <Element> KoneReifiedSet.Companion.of(
    element: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, elementReification = elementReification, elementEquality = elementEquality)

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneReifiedSet.Companion.contextualOf(
    element: Element,
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    KoneReifiedSet.of(
        element = element,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public fun <Element> KoneSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> =
    if (elementHashing != null)
        KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in elements) if (context(elementEquality) { element !in backingList }) backingList.add(element)
        KoneListBackedSet(elementEquality, backingList)
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    KoneSet.of(
        elements = elements,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public inline fun <reified Element> KoneReifiedSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneReifiedSet.of(
    elements = elements,
    elementReification = Reification(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public fun <Element> KoneReifiedSet.Companion.of(
    vararg elements: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
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
        for (element in elements) if (context(elementEquality) { element !in backingList }) backingList.add(element)
        KoneListBackedReifiedSet(elementReification, elementEquality, backingList)
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneReifiedSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    KoneReifiedSet.of(
        elements = elements,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public fun <Element> KoneMutableSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableSet(elementEquality = elementEquality)

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    KoneMutableSet.of(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public inline fun <reified Element> KoneMutableReifiedSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> = KoneMutableReifiedSet.of(
    elementReification = Reification(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public fun <Element> KoneMutableReifiedSet.Companion.of(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality)

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableReifiedSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    KoneMutableReifiedSet.of(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public fun <Element> KoneMutableSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null)
        KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
            .apply { addSeveral(elements.size.toUInt()) { elements[it.toInt()] } }
    else {
        val backingList = KoneArrayResizableLinkedList<Element>()
        for (element in elements) if (context(elementEquality) { element !in backingList }) backingList.add(element)
        KoneListBackedMutableSet(elementEquality, backingList)
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    KoneMutableSet.of(
        elements = elements,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public inline fun <reified Element> KoneMutableReifiedSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    KoneMutableReifiedSet.of(
        elements = elements,
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public fun <Element> KoneMutableReifiedSet.Companion.of(
    vararg elements: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
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
        for (element in elements) if (context(elementEquality) { element !in backingList }) backingList.add(element)
        KoneListBackedMutableReifiedSet(
            elementReification = elementReification,
            elementEquality = elementEquality,
            backingList = backingList,
        )
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableReifiedSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    KoneMutableReifiedSet.of(
        elements = elements,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public fun <Element> KoneIterable<Element>.toKoneMutableSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null)
        KoneHashResizableSet(elementEquality = elementEquality,  elementHashing = elementHashing)
            .apply { addAllFrom(this@toKoneMutableSet) }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in this) if (context(elementEquality) { element !in backingList }) backingList.add(element)
        KoneListBackedMutableSet(elementEquality, backingList)
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneIterable<Element>.toKoneContextualMutableSet(
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    toKoneMutableSet(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public inline fun <reified Element> KoneIterable<Element>.toKoneMutableReifiedSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    toKoneMutableReifiedSet(
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public fun <Element> KoneIterable<Element>.toKoneMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null)
        KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
            .apply { addAllFrom(this@toKoneMutableReifiedSet) }
    else {
        val backingList = KoneArrayGrowableList<Element>()
        for (element in this) if (context(elementEquality) { element !in backingList }) backingList.add(element)
        KoneListBackedMutableReifiedSet(elementReification, elementEquality, backingList)
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneIterable<Element>.toKoneContextualMutableReifiedSet(
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    toKoneMutableReifiedSet(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public fun <Element> KoneIterable<Element>.toKoneSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> =
    when {
        size == 0u -> KoneSet.empty()
        size == 1u -> KoneSingletonNoddedSet(singleElement = iterator().getNext(), elementEquality = elementEquality)
        else -> this.toKoneMutableSet(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneIterable<Element>.toKoneContextualSet(
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    toKoneSet(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

public inline fun <reified Element> KoneIterable<Element>.toKoneReifiedSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    toKoneReifiedSet(
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public fun <Element> KoneIterable<Element>.toKoneReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    when {
        size == 0u -> KoneReifiedSet.empty()
        size == 1u -> KoneSingletonNoddedReifiedSet(
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

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneIterable<Element>.toKoneContextualReifiedSet(
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    toKoneReifiedSet(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneSetBuilder<Element> @PublishedApi internal constructor(result: KoneMutableSet<Element>) : KoneMutableSet<Element> {
    private var result: KoneMutableSet<Element>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        return result.size
    }
    
    override fun contains(element: Element): Boolean {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        return element in result
    }
    
    override fun add(element: Element) {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.add(element)
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun remove(element: Element) {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.remove(element)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun iterator(): KoneRemovableIterator<Element> {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        return result.iterator()
    }
    
    public operator fun Element.unaryPlus() {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.add(this)
    }
    
    public operator fun KoneIterable<Element>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.addAllFrom(this)
    }
    
    public operator fun Element.unaryMinus() {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.remove(this)
    }
    
    public operator fun KoneIterable<Element>.unaryMinus() {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        result.removeAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneSet<Element> {
        val result = result
        if (result == null) error("This KoneSet builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneReifiedSetBuilder<Element> @PublishedApi internal constructor(result: KoneMutableReifiedSet<Element>) : KoneMutableReifiedSet<Element> {
    private var result: KoneMutableReifiedSet<Element>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        return result.size
    }
    
    override fun contains(element: Element): Boolean {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        return element in result
    }
    
    override fun add(element: Element) {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.add(element)
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun remove(element: Element) {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.remove(element)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun iterator(): KoneRemovableIterator<Element> {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        return result.iterator()
    }
    
    public operator fun Element.unaryPlus() {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.add(this)
    }
    
    public operator fun KoneIterable<Element>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.addAllFrom(this)
    }
    
    public operator fun Element.unaryMinus() {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.remove(this)
    }
    
    public operator fun KoneIterable<Element>.unaryMinus() {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        result.removeAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneReifiedSet<Element> {
        val result = result
        if (result == null) error("This KoneReifiedSet builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneSet.Companion.build(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneSetBuilder<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return KoneSetBuilder(result).apply(builderAction).build()
}

@OptIn(ExperimentalTypeInference::class)
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneSet.Companion.buildContextual(
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneSetBuilder<Element>.() -> Unit,
): KoneSet<Element> =
    KoneSet.build(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction
    )

@OptIn(ExperimentalTypeInference::class)
public inline fun <reified Element> KoneReifiedSet.Companion.build(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    KoneReifiedSet.build(
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
        builderAction = builderAction
    )

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneReifiedSet.Companion.build(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return KoneReifiedSetBuilder(result).apply(builderAction).build()
}

@OptIn(ExperimentalTypeInference::class)
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneReifiedSet.Companion.buildContextual(
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    KoneReifiedSet.build(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneSet.Companion.build(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneSetBuilder<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return KoneSetBuilder(result).apply(builderAction).build()
}

@OptIn(ExperimentalTypeInference::class)
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneSet.Companion.buildContextual(
    initialCapacity: UInt,
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneSetBuilder<Element>.() -> Unit,
): KoneSet<Element> =
    KoneSet.build(
        initialCapacity = initialCapacity,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
public inline fun <reified Element> KoneReifiedSet.Companion.build(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneReifiedSetBuilder<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    KoneReifiedSet.build(
        initialCapacity = initialCapacity,
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
public inline fun <Element> KoneReifiedSet.Companion.build(
    initialCapacity: UInt,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneReifiedSetBuilder<Element>.() -> Unit
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return KoneReifiedSetBuilder(result).apply(builderAction).build()
}

@OptIn(ExperimentalTypeInference::class)
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneReifiedSet.Companion.buildContextual(
    initialCapacity: UInt,
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneReifiedSetBuilder<Element>.() -> Unit
): KoneReifiedSet<Element> =
    KoneReifiedSet.build(
        initialCapacity = initialCapacity,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )