/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.serializers.KoneCollectionDescriptor
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.comparison.defaultReifiedHashing
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element, ElementContext: Hashing<Element>> KoneHashResizableSet(elementContext: ElementContext): KoneHashResizableSet<Element, ElementContext> =
    KoneHashResizableSet(size = 0u, elementContext = elementContext)

public fun <Element> KoneHashResizableSet(): KoneHashResizableSet<Element, Hashing<Element>> =
    KoneHashResizableSet(size = 0u, elementContext = defaultHashing())

public fun <Element, ElementContext: ReifiedHashing<Element>> KoneHashResizableReifiedSet(elementContext: ElementContext): KoneHashResizableReifiedSet<Element, ElementContext> =
    KoneHashResizableReifiedSet(size = 0u, elementContext = elementContext)

public inline fun <reified Element> KoneHashResizableReifiedSet(): KoneHashResizableReifiedSet<Element, ReifiedHashing<Element>> =
    KoneHashResizableReifiedSet(size = 0u, elementContext = defaultReifiedHashing())

internal class KoneHashResizableSetDescriptor(elementDescriptor: SerialDescriptor):
    KoneCollectionDescriptor(
        serialName = "dev.lounres.kone.collections.implementations.KoneHashResizableSet<data>",
        elementDescriptor = elementDescriptor,
    )

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
//    collectionSerialName = "dev.lounres.kone.collections.implementations.KoneHashResizableSet",
//    elementDescriptor = elementSerializer.descriptor,
//), DeserializationStrategy<KoneHashResizableSet<E, EC>> {
//    override val elementCollectionSerializer: SerializationStrategy<KoneHashResizableSet<E, EC>> =
//        DefaultKoneIterableCollectionSerializer(elementSerializer)
//    override fun result(elementList: KoneIterableList<E>, elementContext: EC): KoneHashResizableSet<E, EC> =
//        KoneHashResizableSet(elementContext).apply {
//            addAllFrom(elementList)
//        }
//}