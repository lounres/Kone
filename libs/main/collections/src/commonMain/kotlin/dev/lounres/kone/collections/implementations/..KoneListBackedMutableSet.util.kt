/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.producers.KoneResizableMutableListProducer
import dev.lounres.kone.collections.serializers.KoneIterableDescriptor
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element, ElementContext: Equality<Element>> KoneListBackedMutableSet(
    elementContext: ElementContext,
): KoneListBackedMutableSet<Element, ElementContext> = KoneListBackedMutableSet(elementContext, KoneArrayResizableLinkedList())

public fun <Element, ElementContext: Equality<Element>> KoneListBackedMutableSet(
    elementContext: ElementContext,
    listProducer: KoneResizableMutableListProducer,
): KoneListBackedMutableSet<Element, ElementContext> = KoneListBackedMutableSet(elementContext, listProducer.produce())

public fun <Element, ElementContext: Equality<Element>> KoneListBackedMutableSet(
    elementContext: ElementContext,
    listProducer: KoneGrowableMutableListProducer,
): KoneListBackedMutableSet<Element, ElementContext> = KoneListBackedMutableSet(elementContext, listProducer.produce())

public fun <Element, ElementContext: Equality<Element>> KoneListBackedMutableSet(
    elementContext: ElementContext,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element, ElementContext> = KoneListBackedMutableSet(elementContext, KoneArrayResizableLinkedList(size, builder))

public fun <Element, ElementContext: Equality<Element>> KoneListBackedMutableSet(
    elementContext: ElementContext,
    size: UInt,
    listProducer: KoneResizableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element, ElementContext> = KoneListBackedMutableSet(elementContext, listProducer.produceBy(size, builder))

public fun <Element, ElementContext: Equality<Element>> KoneListBackedMutableSet(
    elementContext: ElementContext,
    size: UInt,
    listProducer: KoneGrowableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element, ElementContext> = KoneListBackedMutableSet(elementContext, listProducer.produceBy(size, builder))

public fun <Element, ElementContext: ReifiedEquality<Element>> KoneListBackedMutableReifiedSet(
    elementContext: ElementContext,
): KoneListBackedMutableReifiedSet<Element, ElementContext> = KoneListBackedMutableReifiedSet(elementContext, KoneArrayResizableLinkedList())

public fun <Element, ElementContext: ReifiedEquality<Element>> KoneListBackedMutableReifiedSet(
    elementContext: ElementContext,
    listProducer: KoneResizableMutableListProducer,
): KoneListBackedMutableReifiedSet<Element, ElementContext> = KoneListBackedMutableReifiedSet(elementContext, listProducer.produce())

public fun <Element, ElementContext: ReifiedEquality<Element>> KoneListBackedMutableReifiedSet(
    elementContext: ElementContext,
    listProducer: KoneGrowableMutableListProducer,
): KoneListBackedMutableReifiedSet<Element, ElementContext> = KoneListBackedMutableReifiedSet(elementContext, listProducer.produce())

public fun <Element, ElementContext: ReifiedEquality<Element>> KoneListBackedMutableReifiedSet(
    elementContext: ElementContext,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element, ElementContext> = KoneListBackedMutableReifiedSet(elementContext, KoneArrayResizableLinkedList(size, builder))

public fun <Element, ElementContext: ReifiedEquality<Element>> KoneListBackedMutableReifiedSet(
    elementContext: ElementContext,
    size: UInt,
    listProducer: KoneResizableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element, ElementContext> = KoneListBackedMutableReifiedSet(elementContext, listProducer.produceBy(size, builder))

public fun <Element, ElementContext: ReifiedEquality<Element>> KoneListBackedMutableReifiedSet(
    elementContext: ElementContext,
    size: UInt,
    listProducer: KoneGrowableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element, ElementContext> = KoneListBackedMutableReifiedSet(elementContext, listProducer.produceBy(size, builder))

internal class KoneMutableListBackedSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneIterableDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneMutableListBackedSet",
        elementDescriptor = elementDescriptor,
    )

//internal class KoneMutableListBackedSetSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneMutableListBackedSet<E, EC>>(), DeserializationStrategy<KoneMutableListBackedSet<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneMutableListBackedSetDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneMutableListBackedSet<E, EC> =
//        KoneMutableListBackedSet(
//            elementContext,
//            KoneFixedCapacityArrayList(size, elementContext)
//                .apply {
//                    (0u..<size).forEach {
//                        val element = initializer(it)
//                        if (element !in this ) add(element)
//                    }
//                }.toKoneMutableIterableList(elementContext)
//        )
//}
//
//internal class KoneMutableListBackedSetWithContextSerializer<E, EC: Equality<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneMutableListBackedSet<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneMutableListBackedSet",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneMutableListBackedSet<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneMutableListBackedSet<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneMutableListBackedSet<E, EC> =
//        KoneMutableListBackedSet(
//            elementContext,
//            KoneFixedCapacityArrayList(elementList.size, elementContext)
//                .apply {
//                    elementList.indices.forEach {
//                        val element = elementList[it]
//                        if (element !in this ) add(element)
//                    }
//                }.toKoneMutableIterableList(elementContext)
//        )
//}