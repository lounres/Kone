/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.set.serializers.KoneSetImplementationDescriptor
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultFor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public fun <Element> KoneHashResizableSet(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element> = Hashing.defaultFor()
): KoneHashResizableSet<Element> =
    KoneHashResizableSet(size = 0u, elementEquality = elementEquality, elementHashing = elementHashing)

public fun <Element> KoneHashResizableReifiedSet(
    elementReification: Reification<Element>,
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element> = Hashing.defaultFor(),
): KoneHashResizableReifiedSet<Element> =
    KoneHashResizableReifiedSet(size = 0u, elementReification = elementReification, elementEquality = elementEquality,  elementHashing = elementHashing)

public inline fun <reified Element> KoneHashResizableReifiedSet(
    elementEquality: Equality<Element> = Equality.defaultFor(),
    elementHashing: Hashing<Element> = Hashing.defaultFor(),
): KoneHashResizableReifiedSet<Element> =
    KoneHashResizableReifiedSet(size = 0u, elementReification = Reification.defaultFor(), elementEquality = elementEquality,  elementHashing = elementHashing)

public open class KoneHashResizableSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementEquality: Equality<Element>,
    protected val elementHashing: Hashing<Element>,
): KoneIterableSerializerTemplate<Element, KoneHashResizableSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneHashResizableSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneHashResizableSet<Element> =
        KoneHashResizableSet(
            elementEquality,
            elementHashing,
        ).apply {
            (0u..<size).forEach { index -> add(initializer(index)) }
        }
}

public fun <Element> KoneHashResizableSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementEquality: Equality<Element>,
    elementHashing: Hashing<Element>,
): KSerializer<KoneHashResizableSet<Element>> =
    KoneHashResizableSetSerializer(
        elementSerializer = elementSerializer,
        elementEquality = elementEquality,
        elementHashing = elementHashing,
    )

public open class KoneHashResizableReifiedSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementReification: Reification<Element>,
    protected val elementEquality: Equality<Element>,
    protected val elementHashing: Hashing<Element>,
): KoneIterableSerializerTemplate<Element, KoneHashResizableReifiedSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneHashResizableReifiedSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneHashResizableReifiedSet<Element> =
        KoneHashResizableReifiedSet(
            elementReification,
            elementEquality,
            elementHashing,
        ).apply {
            (0u..<size).forEach { index -> add(initializer(index)) }
        }
}

public fun <Element> KoneHashResizableReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
    elementHashing: Hashing<Element>,
): KSerializer<KoneHashResizableReifiedSet<Element>> =
    KoneHashResizableReifiedSetSerializer(
        elementSerializer = elementSerializer,
        elementReification = elementReification,
        elementEquality = elementEquality,
        elementHashing = elementHashing,
    )