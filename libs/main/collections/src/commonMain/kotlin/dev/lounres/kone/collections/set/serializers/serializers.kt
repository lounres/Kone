/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.serializers

import dev.lounres.kone.collections.iterables.serializers.KoneIterableDescriptor
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializationStrategyTemplate
import dev.lounres.kone.collections.set.KoneLinkedNoddedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedNoddedSet
import dev.lounres.kone.collections.set.KoneLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneLinkedSet
import dev.lounres.kone.collections.set.KoneMutableLinkedNoddedReifiedSet
import dev.lounres.kone.collections.set.KoneMutableLinkedNoddedSet
import dev.lounres.kone.collections.set.KoneMutableLinkedReifiedSet
import dev.lounres.kone.collections.set.KoneMutableLinkedSet
import dev.lounres.kone.collections.set.KoneMutableNoddedReifiedSet
import dev.lounres.kone.collections.set.KoneMutableNoddedSet
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.KoneNoddedReifiedSet
import dev.lounres.kone.collections.set.KoneNoddedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.implementations.KoneHashResizableReifiedSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneHashResizableSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableLinkedNoddedReifiedSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableLinkedNoddedSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableReifiedSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneListBackedMutableSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneListBackedReifiedSetSerializer
import dev.lounres.kone.collections.set.implementations.KoneListBackedSetSerializer
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder


internal fun KoneSetImplementationDescriptor(
    implementationName: String,
    elementDescriptor: SerialDescriptor,
) : SerialDescriptor = KoneIterableDescriptor(
    serialName = "dev.lounres.kone.collections.set.implementations.$implementationName",
    elementDescriptor = elementDescriptor,
)

internal fun KoneSetDeclarationDescriptor(
    declarationName: String,
    elementDescriptor: SerialDescriptor,
) : SerialDescriptor = KoneIterableDescriptor(
    serialName = "dev.lounres.kone.collections.set.$declarationName",
    elementDescriptor = elementDescriptor,
)

// region Default serializers for common interfaces

public open class DefaultKoneSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneSet<E>>(), KSerializer<KoneSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneSet<E>> =
        if (elementHashing != null) KoneHashResizableSetSerializer(elementSerializer, elementEquality, elementHashing)
        else KoneListBackedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneSet<E>> = DefaultKoneSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableSet<E>>(), KSerializer<KoneMutableSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableSet<E>> =
        if (elementHashing != null) KoneHashResizableSetSerializer(elementSerializer, elementEquality, elementHashing)
        else KoneListBackedMutableSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableSet<E>> = DefaultKoneMutableSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneNoddedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneNoddedSet<E>>(), KSerializer<KoneNoddedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneNoddedSet<E>> =
        KoneListBackedMutableLinkedNoddedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneNoddedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneNoddedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneNoddedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneNoddedSet<E>> = DefaultKoneNoddedSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableNoddedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableNoddedSet<E>>(), KSerializer<KoneMutableNoddedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableNoddedSet<E>> =
        KoneListBackedMutableLinkedNoddedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableNoddedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableNoddedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableNoddedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableNoddedSet<E>> = DefaultKoneMutableNoddedSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneLinkedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneLinkedSet<E>>(), KSerializer<KoneLinkedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneLinkedSet<E>> =
        KoneListBackedMutableLinkedNoddedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneLinkedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneLinkedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneLinkedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneLinkedSet<E>> = DefaultKoneLinkedSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableLinkedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableLinkedSet<E>>(), KSerializer<KoneMutableLinkedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableLinkedSet<E>> =
        KoneListBackedMutableLinkedNoddedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableLinkedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableLinkedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableLinkedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableLinkedSet<E>> = DefaultKoneMutableLinkedSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneLinkedNoddedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneLinkedNoddedSet<E>>(), KSerializer<KoneLinkedNoddedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneLinkedNoddedSet<E>> =
        KoneListBackedMutableLinkedNoddedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneLinkedNoddedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneLinkedNoddedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneLinkedNoddedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneLinkedNoddedSet<E>> = DefaultKoneLinkedNoddedSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableLinkedNoddedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableLinkedNoddedSet<E>>(), KSerializer<KoneMutableLinkedNoddedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableLinkedNoddedSet<E>> =
        KoneListBackedMutableLinkedNoddedSetSerializer(elementSerializer, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableLinkedNoddedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableLinkedNoddedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableLinkedNoddedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableLinkedNoddedSet<E>> = DefaultKoneMutableLinkedNoddedSetSerializer(
    elementSerializer = elementSerializer,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneReifiedSet<E>>(), KSerializer<KoneReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneReifiedSet<E>> =
        if (elementHashing != null) KoneHashResizableReifiedSetSerializer(elementSerializer, elementReification, elementEquality, elementHashing)
        else KoneListBackedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneReifiedSet<E>> = DefaultKoneReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableReifiedSet<E>>(), KSerializer<KoneMutableReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableReifiedSet<E>> =
        if (elementHashing != null) KoneHashResizableReifiedSetSerializer(elementSerializer, elementReification, elementEquality, elementHashing)
        else KoneListBackedMutableReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableReifiedSet<E>> = DefaultKoneMutableReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneNoddedReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneNoddedReifiedSet<E>>(), KSerializer<KoneNoddedReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneNoddedReifiedSet<E>> =
        KoneListBackedMutableLinkedNoddedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneNoddedReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneNoddedReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneNoddedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneNoddedReifiedSet<E>> = DefaultKoneNoddedReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableNoddedReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableNoddedReifiedSet<E>>(), KSerializer<KoneMutableNoddedReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableNoddedReifiedSet<E>> =
        KoneListBackedMutableLinkedNoddedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableNoddedReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableNoddedReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableNoddedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableNoddedReifiedSet<E>> = DefaultKoneMutableNoddedReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneLinkedReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneLinkedReifiedSet<E>>(), KSerializer<KoneLinkedReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneLinkedReifiedSet<E>> =
        KoneListBackedMutableLinkedNoddedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneLinkedReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneLinkedReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneLinkedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneLinkedReifiedSet<E>> = DefaultKoneLinkedReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableLinkedReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableLinkedReifiedSet<E>>(), KSerializer<KoneMutableLinkedReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableLinkedReifiedSet<E>> =
        KoneListBackedMutableLinkedNoddedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableLinkedReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableLinkedReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableLinkedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableLinkedReifiedSet<E>> = DefaultKoneMutableLinkedReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneLinkedNoddedReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneLinkedNoddedReifiedSet<E>>(), KSerializer<KoneLinkedNoddedReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneLinkedNoddedReifiedSet<E>> =
        KoneListBackedMutableLinkedNoddedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneLinkedNoddedReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneLinkedNoddedReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneLinkedNoddedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneLinkedNoddedReifiedSet<E>> = DefaultKoneLinkedNoddedReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

public open class DefaultKoneMutableLinkedNoddedReifiedSetSerializer<E>(
    final override val elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableLinkedNoddedReifiedSet<E>>(), KSerializer<KoneMutableLinkedNoddedReifiedSet<E>> {
    private val actualDeserializationStrategy: DeserializationStrategy<KoneMutableLinkedNoddedReifiedSet<E>> =
        KoneListBackedMutableLinkedNoddedReifiedSetSerializer(elementSerializer, elementReification, elementEquality)
    
    final override val descriptor: SerialDescriptor = KoneSetDeclarationDescriptor("KoneMutableLinkedNoddedReifiedSet", elementSerializer.descriptor)
    final override fun deserialize(decoder: Decoder): KoneMutableLinkedNoddedReifiedSet<E> = actualDeserializationStrategy.deserialize(decoder)
}

public fun <E> KoneMutableLinkedNoddedReifiedSet.Companion.serializer(
    elementSerializer: KSerializer<E>,
    elementReification: Reification<E>,
    elementEquality: Equality<E>,
    elementHashing: Hashing<E>? = null,
    elementOrder: Order<E>? = null,
): KSerializer<KoneMutableLinkedNoddedReifiedSet<E>> = DefaultKoneMutableLinkedNoddedReifiedSetSerializer(
    elementSerializer = elementSerializer,
    elementReification = elementReification,
    elementEquality = elementEquality,
    elementHashing = elementHashing,
    elementOrder = elementOrder,
)

// endregion