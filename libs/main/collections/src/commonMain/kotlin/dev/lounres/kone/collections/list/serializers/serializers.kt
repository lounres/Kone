/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("FunctionName")

package dev.lounres.kone.collections.list.serializers

import dev.lounres.kone.collections.iterables.serializers.KoneIterableDescriptor
import dev.lounres.kone.collections.iterables.serializers.KoneIterableSerializationStrategyTemplate
import dev.lounres.kone.collections.list.*
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableListSerializer
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableNoddedListSerializer
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableListSerializer
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableNoddedListSerializer
import dev.lounres.kone.collections.list.implementations.KoneArraySettableListSerializer
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedListSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder


internal fun KoneListImplementationDescriptor(
    implementationName: String,
    elementSerializer: KSerializer<*>,
) : SerialDescriptor = KoneIterableDescriptor(
    serialName = "dev.lounres.kone.collections.list.implementations.$implementationName",
    elementSerializer = elementSerializer,
)

internal fun KoneListDeclarationDescriptor(
    declarationName: String,
    elementSerializer: KSerializer<*>,
) : SerialDescriptor = KoneIterableDescriptor(
    serialName = "dev.lounres.kone.collections.list.$declarationName",
    elementSerializer = elementSerializer,
)

// region Default serializers for common interfaces

internal class DefaultKoneListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneList<E>>(), KSerializer<KoneList<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableList<E>>(), KSerializer<KoneSettableList<E>> {
    private val actualSerializer = KoneArraySettableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneSettableList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneSettableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableList<E>>(), KSerializer<KoneMutableList<E>> {
    private val actualSerializer = KoneArrayResizableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneMutableList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneMutableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneGrowableMutableListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneGrowableMutableList<E>>(), KSerializer<KoneGrowableMutableList<E>> {
    private val actualSerializer = KoneArrayGrowableListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneGrowableMutableList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneGrowableMutableList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneNoddedList<E>>(), KSerializer<KoneNoddedList<E>> {
    private val actualSerializer = KoneArraySettableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneNoddedList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneNoddedList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneSettableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneSettableNoddedList<E>>(), KSerializer<KoneSettableNoddedList<E>> {
    private val actualSerializer = KoneArraySettableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneSettableNoddedList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneSettableNoddedList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneMutableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneMutableNoddedList<E>>(), KSerializer<KoneMutableNoddedList<E>> {
    private val actualSerializer = KoneArrayResizableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneMutableNoddedList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneMutableNoddedList<E> = actualSerializer.deserialize(decoder)
}

internal class DefaultKoneGrowableMutableNoddedListSerializer<E>(
    override val elementSerializer: KSerializer<E>,
) : KoneIterableSerializationStrategyTemplate<E, KoneGrowableMutableNoddedList<E>>(), KSerializer<KoneGrowableMutableNoddedList<E>> {
    private val actualSerializer = KoneArrayGrowableNoddedListSerializer(elementSerializer)
    
    override val descriptor: SerialDescriptor = KoneListDeclarationDescriptor("KoneGrowableMutableNoddedList", actualSerializer)
    override fun deserialize(decoder: Decoder): KoneGrowableMutableNoddedList<E> = actualSerializer.deserialize(decoder)
}

// endregion