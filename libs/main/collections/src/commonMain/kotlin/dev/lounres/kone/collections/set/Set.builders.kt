/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

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
import dev.lounres.kone.collections.iterables.contains
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.loadEqualityFor
import dev.lounres.kone.comparison.loadHashingForOrNull
import dev.lounres.kone.comparison.loadOrderForOrNull
import dev.lounres.kone.comparison.loadReificationFor
import dev.lounres.kone.context
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders for nodded sets and linked set

@Suppress("UNCHECKED_CAST")
public fun <Element> emptyKoneSet(): KoneSet<Element> = KoneEmptyNoddedReifiedSet as KoneSet<Element>

@Suppress("UNCHECKED_CAST")
public fun <Element> emptyKoneReifiedSet(): KoneReifiedSet<Element> = KoneEmptyNoddedReifiedSet as KoneReifiedSet<Element>

@Suppress("UNUSED_PARAMETER")
public fun <Element> koneSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = emptyKoneSet()

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualSetOf(
    elementType: SuppliedType<Element>,
): KoneSet<Element> =
    koneSetOf(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

@Suppress("UNUSED_PARAMETER")
public inline fun <reified Element> koneReifiedSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = emptyKoneReifiedSet()

@Suppress("UNUSED_PARAMETER")
public fun <Element> koneReifiedSetOf(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = emptyKoneReifiedSet()

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

@Suppress("unused")
public fun <Element> koneSetOf(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneSet<Element> = KoneSingletonNoddedSet(element, elementEquality)

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

@Suppress("unused")
public inline fun <reified Element> koneReifiedSetOf(
    element: Element,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> = KoneSingletonNoddedReifiedSet(element, Reification(), elementEquality)

@Suppress("unused")
public fun <Element> koneReifiedSetOf(
    element: Element,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneReifiedSet<Element> =
    KoneSingletonNoddedReifiedSet(element, elementReification = elementReification, elementEquality = elementEquality)

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

public fun <Element> koneMutableSetOf(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableSet<Element> =
    if (elementHashing != null) KoneHashResizableSet(elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableSet(elementEquality = elementEquality)

context(koneContextRegistry: KoneContextRegistry)
public fun <Element> koneContextualMutableSetOf(
    elementType: SuppliedType<Element>,
): KoneMutableSet<Element> =
    koneMutableSetOf(
        elementEquality = koneContextRegistry.loadEqualityFor(elementType),
        elementHashing = koneContextRegistry.loadHashingForOrNull(elementType),
        elementOrder = koneContextRegistry.loadOrderForOrNull(elementType),
    )

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

public fun <Element> koneMutableReifiedSetOf(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element>? = null,
    elementOrder: Order<Element>? = null,
): KoneMutableReifiedSet<Element> =
    if (elementHashing != null) KoneHashResizableReifiedSet(elementReification = elementReification, elementEquality = elementEquality, elementHashing = elementHashing)
    else KoneListBackedMutableReifiedSet(elementReification = elementReification, elementEquality = elementEquality)

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

@OptIn(ExperimentalTypeInference::class)
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

context(koneContextRegistry: KoneContextRegistry)
@OptIn(ExperimentalTypeInference::class)
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

context(koneContextRegistry: KoneContextRegistry)
@OptIn(ExperimentalTypeInference::class)
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

context(koneContextRegistry: KoneContextRegistry)
@OptIn(ExperimentalTypeInference::class)
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

context(koneContextRegistry: KoneContextRegistry)
@OptIn(ExperimentalTypeInference::class)
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