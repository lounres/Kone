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
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.KoneMutableListIterator
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.build
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
@Deprecated("", replaceWith = ReplaceWith("KoneSet.empty()", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.empty"))
public fun <Element> emptyKoneSet(): KoneSet<Element> = KoneEmptyNoddedReifiedSet as KoneSet<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneSet.Companion.empty(): KoneSet<Element> = KoneEmptyNoddedReifiedSet as KoneSet<Element>

@Suppress("UNCHECKED_CAST")
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.empty()", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.empty"))
public fun <Element> emptyKoneReifiedSet(): KoneReifiedSet<Element> = KoneEmptyNoddedReifiedSet as KoneReifiedSet<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> KoneReifiedSet.Companion.empty(): KoneReifiedSet<Element> = KoneEmptyNoddedReifiedSet as KoneReifiedSet<Element>

@Suppress("UNUSED_PARAMETER")
@Deprecated("", replaceWith = ReplaceWith("KoneSet.of(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = emptyKoneSet()

@Suppress("UNUSED_PARAMETER")
public fun <Element> KoneSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = emptyKoneSet()

@Deprecated("", replaceWith = ReplaceWith("KoneSet.contextualOf(elementType = elementType)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualSetOf(
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("UNUSED_PARAMETER")
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.of(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.of"))
public inline fun <reified Element> koneReifiedSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = emptyKoneReifiedSet()

@Suppress("UNUSED_PARAMETER")
public inline fun <reified Element> KoneReifiedSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = emptyKoneReifiedSet()

@Suppress("UNUSED_PARAMETER")
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.of(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneReifiedSetOf(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = emptyKoneReifiedSet()

@Suppress("UNUSED_PARAMETER")
public fun <Element> KoneReifiedSet.Companion.of(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = emptyKoneReifiedSet()

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.contextualOf(elementType = elementType)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualReifiedSetOf(
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    koneReifiedSetOf(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneReifiedSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    koneReifiedSetOf(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("unused")
@Deprecated("", replaceWith = ReplaceWith("KoneSet.of(element, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneSetOf(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSingletonNoddedSet(element, elementEquality)

@Suppress("unused")
public fun <Element> KoneSet.Companion.of(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSingletonNoddedSet(element, elementEquality)

@Deprecated("", replaceWith = ReplaceWith("KoneSet.contextualOf(element, elementType = elementType)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualSetOf(
    element: Element,
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        element = element,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneSet.Companion.contextualOf(
    element: Element,
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        element = element,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("unused")
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.of(element, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.of"))
public inline fun <reified Element> koneReifiedSetOf(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneSingletonNoddedReifiedSet(element, Reification(), elementEquality)

@Suppress("unused")
public inline fun <reified Element> KoneReifiedSet.Companion.of(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneSingletonNoddedReifiedSet(element, Reification(), elementEquality)

@Suppress("unused")
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.of(element, elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneReifiedSetOf(
    element: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, elementReification = elementReification, elementEquality = elementEquality)

@Suppress("unused")
public fun <Element> KoneReifiedSet.Companion.of(
    element: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, elementReification = elementReification, elementEquality = elementEquality)

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.contextualOf(element, elementType = elementType)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualReifiedSetOf(
    element: Element,
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    koneReifiedSetOf(
        element = element,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneReifiedSet.Companion.contextualOf(
    element: Element,
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    koneReifiedSetOf(
        element = element,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneSet.of(*elements, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneSetOf(
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

@Deprecated("", replaceWith = ReplaceWith("KoneSet.contextualOf(*elements, elementType = elementType)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualSetOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        elements = elements,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        elements = elements,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.of(*elements, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.of"))
public inline fun <reified Element> koneReifiedSetOf(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = koneReifiedSetOf(
    elements = elements,
    elementReification = Reification(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public inline fun <reified Element> KoneReifiedSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = koneReifiedSetOf(
    elements = elements,
    elementReification = Reification(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.of(*elements, elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneReifiedSetOf(
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

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.contextualOf(*elements, elementType = elementType)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualReifiedSetOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    koneReifiedSetOf(
        elements = elements,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneReifiedSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneReifiedSet<Element> =
    koneReifiedSetOf(
        elements = elements,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableSet.of(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneMutableSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneMutableSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableSet(elementEquality = elementEquality)

public fun <Element> KoneMutableSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableSet(elementEquality = elementEquality)

@Deprecated("", replaceWith = ReplaceWith("KoneMutableSet.contextualOf(elementType = elementType)", "dev.lounres.kone.collections.set.KoneMutableSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualMutableSetOf(
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    koneMutableSetOf(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    koneMutableSetOf(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedSet.of(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneMutableReifiedSet", "dev.lounres.kone.collections.set.of"))
public inline fun <reified Element> koneMutableReifiedSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> = koneMutableReifiedSetOf(
    elementReification = Reification(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public inline fun <reified Element> KoneMutableReifiedSet.Companion.of(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> = koneMutableReifiedSetOf(
    elementReification = Reification(),
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedSet.of(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneMutableReifiedSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneMutableReifiedSetOf(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality)

public fun <Element> KoneMutableReifiedSet.Companion.of(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality)

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedSet.contextualOf(elementType = elementType)", "dev.lounres.kone.collections.set.KoneMutableReifiedSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualMutableReifiedSetOf(
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableReifiedSet.Companion.contextualOf(
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableSet.of(*elements, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneMutableSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneMutableSetOf(
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

@Deprecated("", replaceWith = ReplaceWith("KoneMutableSet.contextualOf(*elements, elementType = elementType)", "dev.lounres.kone.collections.set.KoneMutableSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualMutableSetOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    koneMutableSetOf(
        elements = elements,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    koneMutableSetOf(
        elements = elements,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedSet.of(*elements, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneMutableReifiedSet", "dev.lounres.kone.collections.set.of"))
public inline fun <reified Element> koneMutableReifiedSetOf(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(
        elements = elements,
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

public inline fun <reified Element> KoneMutableReifiedSet.Companion.of(
    vararg elements: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(
        elements = elements,
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedSet.of(*elements, elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder)", "dev.lounres.kone.collections.set.KoneMutableReifiedSet", "dev.lounres.kone.collections.set.of"))
public fun <Element> koneMutableReifiedSetOf(
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

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedSet.contextualOf(*elements, elementType = elementType)", "dev.lounres.kone.collections.set.KoneMutableReifiedSet", "dev.lounres.kone.collections.set.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualMutableReifiedSetOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(
        elements = elements,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> KoneMutableReifiedSet.Companion.contextualOf(
    vararg elements: Element,
    elementType: SuppliedType<Element>,
): KoneMutableReifiedSet<Element> =
    koneMutableReifiedSetOf(
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
        size == 0u -> emptyKoneSet()
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
        size == 0u -> emptyKoneReifiedSet()
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
        if (result == null) error("This KoneList builder is already used")
        return result.size
    }
    
    override fun contains(element: Element): Boolean {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return element in result
    }
    
    override fun add(element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(element)
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun remove(element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.remove(element)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun iterator(): KoneRemovableIterator<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.iterator()
    }
    
    public operator fun Element.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(this)
    }
    
    public operator fun KoneIterable<Element>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneSet<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneReifiedSetBuilder<Element> @PublishedApi internal constructor(result: KoneMutableReifiedSet<Element>) : KoneMutableReifiedSet<Element> {
    private var result: KoneMutableReifiedSet<Element>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.size
    }
    
    override fun contains(element: Element): Boolean {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return element in result
    }
    
    override fun add(element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(element)
    }
    
    override fun addSeveral(number: UInt, builder: (UInt) -> Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addSeveral(number, builder)
    }
    
    override fun remove(element: Element) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.remove(element)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Element) -> Boolean) {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun iterator(): KoneRemovableIterator<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.iterator()
    }
    
    public operator fun Element.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.add(this)
    }
    
    public operator fun KoneIterable<Element>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.addAllFrom(this)
    }
    
    public operator fun Element.unaryMinus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.remove(this)
    }
    
    public operator fun KoneIterable<Element>.unaryMinus() {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        result.removeAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneReifiedSet<Element> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("", replaceWith = ReplaceWith("KoneSet.build(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder, builderAction)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.build"))
public inline fun <Element> buildKoneSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <Element> KoneSet.Companion.build(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return result.apply(builderAction)
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
@Deprecated("", replaceWith = ReplaceWith("KoneSet.buildContextual(elementType = elementType, builderAction)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.buildContextual"))
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> buildKoneContextualSet(
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> =
    buildKoneSet(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneSet.Companion.buildContextual(
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> =
    buildKoneSet(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.build(elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder, builderAction)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.build"))
public inline fun <reified Element> buildKoneReifiedSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
        builderAction = builderAction
    )

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <reified Element> KoneReifiedSet.Companion.build(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.build(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder, builderAction)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.build"))
public inline fun <Element> buildKoneReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <Element> KoneReifiedSet.Companion.build(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList())
    return result.apply(builderAction)
}

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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.buildContextual(elementType = elementType, builderAction)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.buildContextual"))
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> buildKoneContextualReifiedSet(
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneReifiedSet.Companion.buildContextual(
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

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
@Deprecated("", replaceWith = ReplaceWith("KoneSet.build(initialCapacity = initialCapacity, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder, builderAction)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.build"))
public inline fun <Element> buildKoneSet(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <Element> KoneSet.Companion.build(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableSet(elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}

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
@Deprecated("", replaceWith = ReplaceWith("KoneSet.buildContextual(initialCapacity = initialCapacity, elementType = elementType, builderAction)", "dev.lounres.kone.collections.set.KoneSet", "dev.lounres.kone.collections.set.buildContextual"))
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> buildKoneContextualSet(
    initialCapacity: UInt,
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> =
    buildKoneSet(
        initialCapacity = initialCapacity,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneSet.Companion.buildContextual(
    initialCapacity: UInt,
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableSet<Element>.() -> Unit,
): KoneSet<Element> =
    buildKoneSet(
        initialCapacity = initialCapacity,
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.build(initialCapacity = initialCapacity, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder, builderAction)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.build"))
public inline fun <reified Element> buildKoneReifiedSet(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        initialCapacity = initialCapacity,
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <reified Element> KoneReifiedSet.Companion.build(
    initialCapacity: UInt,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit,
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        initialCapacity = initialCapacity,
        elementReification = Reification(),
        elementEquality = elementEquality,
        elementHashing = elementHashing,
        elementOrder = elementOrder,
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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.build(initialCapacity = initialCapacity, elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing, elementOrder = elementOrder, builderAction)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.build"))
public inline fun <Element> buildKoneReifiedSet(
    initialCapacity: UInt,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildOld")
public inline fun <Element> KoneReifiedSet.Companion.build(
    initialCapacity: UInt,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit
): KoneReifiedSet<Element> {
    contract { callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) }
    // TODO: Insert growable hash set implementation for hashing element context
    val result =
        if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
        else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, backingList = KoneArrayGrowableList(initialCapacity = initialCapacity))
    return result.apply(builderAction)
}

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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedSet.buildContextual(initialCapacity = initialCapacity, elementType = elementType, builderAction)", "dev.lounres.kone.collections.set.KoneReifiedSet", "dev.lounres.kone.collections.set.buildContextual"))
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> buildKoneContextualReifiedSet(
    initialCapacity: UInt,
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        initialCapacity = initialCapacity,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

@OptIn(ExperimentalTypeInference::class)
@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> KoneReifiedSet.Companion.buildContextual(
    initialCapacity: UInt,
    elementType: SuppliedType<Element>,
    @BuilderInference builderAction: KoneMutableReifiedSet<Element>.() -> Unit
): KoneReifiedSet<Element> =
    buildKoneReifiedSet(
        initialCapacity = initialCapacity,
        elementReification = koneContextRegistry.loadReificationFor(elementType),
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
        builderAction = builderAction,
    )

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