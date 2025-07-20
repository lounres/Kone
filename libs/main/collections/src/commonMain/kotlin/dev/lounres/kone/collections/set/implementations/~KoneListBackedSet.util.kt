/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.implementations

import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializerTemplate
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.set.serializers.KoneSetImplementationDescriptor
import dev.lounres.kone.collections.utils.none
import dev.lounres.kone.collections.utils.toOptimizedList
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.eq
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


public open class KoneListBackedSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementEquality: Equality<Element>,
): KoneIterableSerializerTemplate<Element, KoneListBackedSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneListBackedSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneListBackedSet<Element> =
        KoneListBackedSet(
            elementEquality,
            KoneArrayFixedCapacityList<Element>(size)
                .apply {
                    (0u..<size).forEach { index ->
                        val element = initializer(index)
                        if (this.none { elementEquality { element eq it } }) add(element)
                    }
                }.toOptimizedList()
        )
}

public fun <Element> KoneListBackedSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementEquality: Equality<Element>,
): KSerializer<KoneListBackedSet<Element>> =
    KoneListBackedSetSerializer(
        elementSerializer = elementSerializer,
        elementEquality = elementEquality,
    )

public open class KoneListBackedReifiedSetSerializer<Element>(
    final override val elementSerializer: KSerializer<Element>,
    protected val elementReification: Reification<Element>,
    protected val elementEquality: Equality<Element>,
): KoneIterableSerializerTemplate<Element, KoneListBackedReifiedSet<Element>>() {
    final override val descriptor: SerialDescriptor =
        KoneSetImplementationDescriptor(
            "KoneListBackedReifiedSet",
            elementSerializer.descriptor,
        )
    final override fun buildCollection(size: UInt, initializer: (UInt) -> Element): KoneListBackedReifiedSet<Element> =
        KoneListBackedReifiedSet(
            elementReification,
            elementEquality,
            KoneArrayFixedCapacityList<Element>(size)
                .apply {
                    (0u..<size).forEach { index ->
                        val element = initializer(index)
                        if (this.none { elementEquality { element eq it } }) add(element)
                    }
                }.toOptimizedList()
        )
}

public fun <Element> KoneListBackedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<Element>,
    elementReification: Reification<Element>,
    elementEquality: Equality<Element>,
): KSerializer<KoneListBackedReifiedSet<Element>> =
    KoneListBackedReifiedSetSerializer(
        elementSerializer = elementSerializer,
        elementReification = elementReification,
        elementEquality = elementEquality,
    )