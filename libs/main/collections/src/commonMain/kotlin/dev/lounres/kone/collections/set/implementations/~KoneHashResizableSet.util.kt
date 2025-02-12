/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing


public fun <Element> KoneHashResizableSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element> = defaultHashing()
): KoneHashResizableSet<Element> =
    KoneHashResizableSet(size = 0u, elementEquality = elementEquality, elementHashing = elementHashing)

public fun <Element> KoneHashResizableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element> = defaultHashing(),
): KoneHashResizableReifiedSet<Element> =
    KoneHashResizableReifiedSet(size = 0u, elementReification = elementReification, elementEquality = elementEquality,  elementHashing = elementHashing)

public inline fun <reified Element> KoneHashResizableReifiedSet(
    elementEquality: Equality<Element> = defaultEquality(),
    elementHashing: Hashing<Element> = defaultHashing(),
): KoneHashResizableReifiedSet<Element> =
    KoneHashResizableReifiedSet(size = 0u, elementReification = Reification(), elementEquality = elementEquality,  elementHashing = elementHashing)

//internal class KoneHashResizableSetDescriptor(elementDescriptor: SerialDescriptor):
//    KoneIterableDescriptor(
//        serialName = "dev.lounres.kone.collections.set.implementations.KoneHashResizableSet<data>",
//        elementDescriptor = elementDescriptor,
//    )

//internal class KoneHashResizableSetSerializer<E, EC: Hashing<E>>(
//    override val elementSerializer: KSerializer<E>,
//    public val elementContext: EC,
//): KoneIterableCollectionSerializerTemplate<E, KoneHashResizableSet<E, EC>>(), DeserializationStrategy<KoneHashResizableSet<E, EC>> {
//    override val descriptor: SerialDescriptor = KoneHashResizableSetDescriptor(elementSerializer.descriptor)
//    override fun buildCollection(size: UInt, initializer: (UInt) -> E): KoneHashResizableSet<E, EC> =
//        KoneHashResizableSet(elementContext).apply {
//            (0u..<size).forEach { add(initializer(it)) }
//        }
//}
//
//internal class KoneHashResizableSetWithContextSerializer<E, EC: Hashing<E>>(
//    override val elementSerializer: KSerializer<E>,
//    override val elementContextSerializer: KSerializer<EC>,
//): KoneIterableCollectionWithContextSerializerTemplate<E, EC, KoneHashResizableSet<E, EC>>(
//    collectionSerialName = "dev.lounres.kone.collections.set.implementations.KoneHashResizableSet",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneHashResizableSet<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneHashResizableSet<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneHashResizableSet<E, EC> =
//        KoneHashResizableSet(elementContext).apply {
//            addAllFrom(elementList)
//        }
//}