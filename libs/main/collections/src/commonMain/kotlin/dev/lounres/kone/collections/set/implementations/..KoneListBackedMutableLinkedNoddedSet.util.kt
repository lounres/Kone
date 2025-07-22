/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.DelicateListBackedCollectionsBuilderAPI
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedList
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.collections.list.toKoneMutableNoddedList
import dev.lounres.kone.collections.set.serializers.KoneSetImplementationDescriptor
import dev.lounres.kone.collections.utils.none
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneListBackedMutableLinkedNoddedSet(
    elementEquality: Equality<Element>,
): KoneListBackedMutableLinkedNoddedSet<Element> = KoneListBackedMutableLinkedNoddedSet(elementEquality, KoneArrayResizableLinkedNoddedList())

public fun <Element> KoneListBackedMutableLinkedNoddedSet(
    elementEquality: Equality<Element>,
    listProducer: KoneResizableMutableNoddedListProducer,
): KoneListBackedMutableLinkedNoddedSet<Element> = KoneListBackedMutableLinkedNoddedSet(elementEquality, listProducer.produce())

public fun <Element> KoneListBackedMutableLinkedNoddedSet(
    elementEquality: Equality<Element>,
    listProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableLinkedNoddedSet<Element> = KoneListBackedMutableLinkedNoddedSet(elementEquality, listProducer.produce())

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableLinkedNoddedSet(
    elementEquality: Equality<Element>,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableLinkedNoddedSet<Element> = KoneListBackedMutableLinkedNoddedSet(
    elementEquality,
    KoneArrayResizableLinkedNoddedList(size) { KoneListBackedMutableLinkedNoddedSet.Node(builder(it)) }.also {
        val iterator = it.iterator()
        while (iterator.hasNext()) {
            iterator.getNext().listNode = iterator.getNextNode()
            iterator.moveNext()
        }
    }
)

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableLinkedNoddedSet(
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneResizableMutableNoddedListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableLinkedNoddedSet<Element> = KoneListBackedMutableLinkedNoddedSet(
    elementEquality,
    listProducer.produceBy(size) { KoneListBackedMutableLinkedNoddedSet.Node(builder(it)) }.also {
        val iterator = it.iterator()
        while (iterator.hasNext()) {
            iterator.getNext().listNode = iterator.getNextNode()
            iterator.moveNext()
        }
    }
)

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableLinkedNoddedSet(
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneGrowableMutableNoddedListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableLinkedNoddedSet<Element> = KoneListBackedMutableLinkedNoddedSet(
    elementEquality,
    listProducer.produceBy(size) { KoneListBackedMutableLinkedNoddedSet.Node(builder(it)) }.also {
        val iterator = it.iterator()
        while (iterator.hasNext()) {
            iterator.getNext().listNode = iterator.getNextNode()
            iterator.moveNext()
        }
    }
)

public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
): KoneListBackedMutableLinkedNoddedReifiedSet<Element> =
    KoneListBackedMutableLinkedNoddedReifiedSet(elementReification, elementEquality, KoneArrayResizableLinkedNoddedList())

public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    listProducer: KoneResizableMutableNoddedListProducer,
): KoneListBackedMutableLinkedNoddedReifiedSet<Element> =
    KoneListBackedMutableLinkedNoddedReifiedSet(elementReification, elementEquality, listProducer.produce())

public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    listProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableLinkedNoddedReifiedSet<Element> = KoneListBackedMutableLinkedNoddedReifiedSet(elementReification, elementEquality, listProducer.produce())

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableLinkedNoddedReifiedSet<Element> = KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification,
    elementEquality,
    KoneArrayResizableLinkedNoddedList(size) { KoneListBackedMutableLinkedNoddedSet.Node(builder(it)) }.also {
        val iterator = it.iterator()
        while (iterator.hasNext()) {
            iterator.getNext().listNode = iterator.getNextNode()
            iterator.moveNext()
        }
    }
)

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneResizableMutableNoddedListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableLinkedNoddedReifiedSet<Element> = KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification,
    elementEquality,
    listProducer.produceBy(size) { KoneListBackedMutableLinkedNoddedSet.Node(builder(it)) }.also {
        val iterator = it.iterator()
        while (iterator.hasNext()) {
            iterator.getNext().listNode = iterator.getNextNode()
            iterator.moveNext()
        }
    }
)

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneGrowableMutableNoddedListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableLinkedNoddedReifiedSet<Element> = KoneListBackedMutableLinkedNoddedReifiedSet(
    elementReification,
    elementEquality,
    listProducer.produceBy(size) { KoneListBackedMutableLinkedNoddedSet.Node(builder(it)) }.also {
        val iterator = it.iterator()
        while (iterator.hasNext()) {
            iterator.getNext().listNode = iterator.getNextNode()
            iterator.moveNext()
        }
    }
)

// TODO: Think about adding custom list producer argument to the serializers classes

public open class KoneListBackedMutableLinkedNoddedSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementEquality: Equality<Element>,
): KoneIterableSerializerTemplate<Element, KoneListBackedMutableLinkedNoddedSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneListBackedMutableLinkedNoddedSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneListBackedMutableLinkedNoddedSet<Element> =
        KoneListBackedMutableLinkedNoddedSet(
            elementEquality,
            KoneArrayFixedCapacityList<KoneListBackedMutableLinkedNoddedSet.Node<Element>>(size)
                .apply {
                    (0u..<size).forEach { index ->
                        val element = initializer(index)
                        if (this.none { elementEquality { element eq it.element } })
                            add(KoneListBackedMutableLinkedNoddedSet.Node(element))
                    }
                }.toKoneMutableNoddedList()
        )
}

public fun <Element> KoneListBackedMutableLinkedNoddedSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementEquality: Equality<Element>,
): KSerializer<KoneListBackedMutableLinkedNoddedSet<Element>> =
    KoneListBackedMutableLinkedNoddedSetSerializer(
        elementSerializer = elementSerializer,
        elementEquality = elementEquality,
    )

public open class KoneListBackedMutableLinkedNoddedReifiedSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementReification: Reification<Element>,
    protected val elementEquality: Equality<Element>,
): KoneIterableSerializerTemplate<Element, KoneListBackedMutableLinkedNoddedReifiedSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneListBackedMutableLinkedNoddedReifiedSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneListBackedMutableLinkedNoddedReifiedSet<Element> =
        KoneListBackedMutableLinkedNoddedReifiedSet(
            elementReification,
            elementEquality,
            KoneArrayFixedCapacityList<KoneListBackedMutableLinkedNoddedSet.Node<Element>>(size)
                .apply {
                    (0u..<size).forEach { index ->
                        val element = initializer(index)
                        if (this.none { elementEquality { element eq it.element } })
                            add(KoneListBackedMutableLinkedNoddedSet.Node(element))
                    }
                }.toKoneMutableNoddedList()
        )
}

public fun <Element> KoneListBackedMutableLinkedNoddedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
): KSerializer<KoneListBackedMutableLinkedNoddedReifiedSet<Element>> =
    KoneListBackedMutableLinkedNoddedReifiedSetSerializer(
        elementSerializer = elementSerializer,
        elementReification = elementReification,
        elementEquality = elementEquality,
    )