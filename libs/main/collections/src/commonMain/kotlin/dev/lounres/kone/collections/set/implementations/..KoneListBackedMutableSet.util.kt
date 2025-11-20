/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.DelicateListBackedCollectionsBuilderAPI
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableListProducer
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.collections.list.toKoneMutableNoddedList
import dev.lounres.kone.collections.set.serializers.KoneSetImplementationDescriptor
import dev.lounres.kone.collections.utils.none
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, KoneArrayResizableLinkedList())

public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    listProducer: KoneResizableMutableListProducer,
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, listProducer.produce())

public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    listProducer: KoneGrowableMutableListProducer,
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, listProducer.produce())

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, KoneArrayResizableLinkedList.generate(size, builder))

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneResizableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, listProducer.produceBy(size, builder))

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneGrowableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, listProducer.produceBy(size, builder))

public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, KoneArrayResizableLinkedList())

public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    listProducer: KoneResizableMutableListProducer,
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, listProducer.produce())

public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    listProducer: KoneGrowableMutableListProducer,
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, listProducer.produce())

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, KoneArrayResizableLinkedList.generate(size, builder))

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneResizableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, listProducer.produceBy(size, builder))

@DelicateListBackedCollectionsBuilderAPI
public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneGrowableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, listProducer.produceBy(size, builder))

// TODO: Think about adding custom list producer argument to the serializers classes

public open class KoneListBackedMutableSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementEquality: Equality<Element>,
): KoneIterableSerializerTemplate<Element, KoneListBackedMutableSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneListBackedMutableSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneListBackedMutableSet<Element> =
        KoneListBackedMutableSet(
            elementEquality,
            KoneArrayFixedCapacityList<Element>(size)
                .apply {
                    (0u..<size).forEach { index ->
                        val element = initializer(index)
                        if (this.none { elementEquality { element eq it } })
                            add(element)
                    }
                }.toKoneMutableNoddedList()
        )
}

public fun <Element> KoneListBackedMutableSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementEquality: Equality<Element>,
): KSerializer<KoneListBackedMutableSet<Element>> =
    KoneListBackedMutableSetSerializer(
        elementSerializer = elementSerializer,
        elementEquality = elementEquality,
    )

public open class KoneListBackedMutableReifiedSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementReification: Reification<Element>,
    protected val elementEquality: Equality<Element>,
): KoneIterableSerializerTemplate<Element, KoneListBackedMutableReifiedSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneListBackedMutableReifiedSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneListBackedMutableReifiedSet<Element> =
        KoneListBackedMutableReifiedSet(
            elementReification,
            elementEquality,
            KoneArrayFixedCapacityList<Element>(size)
                .apply {
                    (0u..<size).forEach { index ->
                        val element = initializer(index)
                        if (this.none { elementEquality { element eq it } })
                            add(element)
                    }
                }.toKoneMutableNoddedList()
        )
}

public fun <Element> KoneListBackedMutableReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
): KSerializer<KoneListBackedMutableReifiedSet<Element>> =
    KoneListBackedMutableReifiedSetSerializer(
        elementSerializer = elementSerializer,
        elementReification = elementReification,
        elementEquality = elementEquality,
    )