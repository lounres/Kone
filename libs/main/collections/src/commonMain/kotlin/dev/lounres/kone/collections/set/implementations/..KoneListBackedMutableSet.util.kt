/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedList
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableListProducer
import dev.lounres.kone.collections.list.producers.KoneResizableMutableListProducer
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification


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

public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, KoneArrayResizableLinkedList(size, builder))

public fun <Element> KoneListBackedMutableSet(
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneResizableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableSet<Element> = KoneListBackedMutableSet(elementEquality, listProducer.produceBy(size, builder))

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

public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, KoneArrayResizableLinkedList(size, builder))

public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneResizableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, listProducer.produceBy(size, builder))

public fun <Element> KoneListBackedMutableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    size: UInt,
    listProducer: KoneGrowableMutableListProducer,
    builder: (UInt) -> Element
): KoneListBackedMutableReifiedSet<Element> = KoneListBackedMutableReifiedSet(elementReification, elementEquality, listProducer.produceBy(size, builder))

//internal class KoneMutableListBackedSetDescriptor(elementDescriptor: SerialDescriptor):
//    KoneIterableDescriptor(
//        serialName = "dev.lounres.kone.collections.implementations.KoneMutableListBackedSet",
//        elementDescriptor = elementDescriptor,
//    )

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