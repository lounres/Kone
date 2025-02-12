/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedList
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Reification


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
): KoneListBackedMutableLinkedNoddedSet<Element,> = KoneListBackedMutableLinkedNoddedSet(elementEquality, listProducer.produce())

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

//internal class KoneMutableListBackedNoddedSetDescriptor(elementDescriptor: SerialDescriptor):
//    KoneIterableDescriptor(
//        serialName = "dev.lounres.kone.collections.implementations.KoneMutableListBackedNoddedSet",
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