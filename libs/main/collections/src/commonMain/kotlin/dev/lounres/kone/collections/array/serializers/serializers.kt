/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("FunctionName")

package dev.lounres.kone.collections.array.serializers

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.array.KoneMutableArray
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass


// region Default serializers for common interfaces

internal class DefaultKoneArraySerializer<ElementKlass : Any, Element : ElementKlass?>(
    kClass: KClass<ElementKlass>,
    val elementSerializer: KSerializer<Element>,
) : KSerializer<KoneArray<Element>> {
    val arraySerializer = ArraySerializer<ElementKlass, Element>(kClass, elementSerializer)
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.collections.array.KoneArray", arraySerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: KoneArray<Element>) {
        encoder.encodeSerializableValue(arraySerializer, value.array as Array<Element>)
    }
    
    override fun deserialize(decoder: Decoder): KoneArray<Element> =
        KoneArray(decoder.decodeSerializableValue(arraySerializer))
}

@Deprecated("", replaceWith = ReplaceWith("KoneArray.serializer<T, E>(elementSerializer)", "dev.lounres.kone.collections.KoneArray", "dev.lounres.kone.collections.serializer"))
public inline fun <reified T : Any, reified E : T?> KoneArraySerializer(elementSerializer: KSerializer<E>): KSerializer<KoneArray<E>> =
    KoneArraySerializer<T, E>(T::class, elementSerializer)

public inline fun <reified T : Any, reified E : T?> KoneArray.Companion.serializer(elementSerializer: KSerializer<E>): KSerializer<KoneArray<E>> =
    KoneArraySerializer<T, E>(T::class, elementSerializer)

@Deprecated("", replaceWith = ReplaceWith("KoneArray.serializer<T, E>(kClass, elementSerializer)", "dev.lounres.kone.collections.KoneArray", "dev.lounres.kone.collections.serializer"))
public fun <T : Any, E : T?> KoneArraySerializer(
    kClass: KClass<T>,
    elementSerializer: KSerializer<E>
): KSerializer<KoneArray<E>> = DefaultKoneArraySerializer<T, E>(kClass, elementSerializer)

public fun <T : Any, E : T?> KoneArray.Companion.serializer(
    kClass: KClass<T>,
    elementSerializer: KSerializer<E>
): KSerializer<KoneArray<E>> = DefaultKoneArraySerializer<T, E>(kClass, elementSerializer)

internal class DefaultKoneMutableArraySerializer<ElementKlass : Any, Element : ElementKlass?>(
    kClass: KClass<ElementKlass>,
    val elementSerializer: KSerializer<Element>,
) : KSerializer<KoneMutableArray<Element>> {
    val arraySerializer = ArraySerializer<ElementKlass, Element>(kClass, elementSerializer)
    
    override val descriptor: SerialDescriptor = SerialDescriptor("dev.lounres.kone.collections.array.KoneMutableArray", arraySerializer.descriptor)
    
    override fun serialize(encoder: Encoder, value: KoneMutableArray<Element>) {
        encoder.encodeSerializableValue(arraySerializer, value.array)
    }
    
    override fun deserialize(decoder: Decoder): KoneMutableArray<Element> =
        KoneMutableArray(decoder.decodeSerializableValue(arraySerializer))
}

@Deprecated("", replaceWith = ReplaceWith("KoneMutableArray.serializer<T, E>(elementSerializer)", "dev.lounres.kone.collections.KoneMutableArray", "dev.lounres.kone.collections.serializer"))
public inline fun <reified T : Any, reified E : T?> KoneMutableArraySerializer(elementSerializer: KSerializer<E>): KSerializer<KoneMutableArray<E>> =
    KoneMutableArraySerializer<T, E>(T::class, elementSerializer)

public inline fun <reified T : Any, reified E : T?> KoneMutableArray.Companion.serializer(elementSerializer: KSerializer<E>): KSerializer<KoneMutableArray<E>> =
    KoneMutableArraySerializer<T, E>(T::class, elementSerializer)

@Deprecated("", replaceWith = ReplaceWith("KoneMutableArray.serializer<T, E>(kClass, elementSerializer)", "dev.lounres.kone.collections.KoneMutableArray", "dev.lounres.kone.collections.serializer"))
public fun <T : Any, E : T?> KoneMutableArraySerializer(
    kClass: KClass<T>,
    elementSerializer: KSerializer<E>
): KSerializer<KoneMutableArray<E>> = DefaultKoneMutableArraySerializer<T, E>(kClass, elementSerializer)

public fun <T : Any, E : T?> KoneMutableArray.Companion.serializer(
    kClass: KClass<T>,
    elementSerializer: KSerializer<E>
): KSerializer<KoneMutableArray<E>> = DefaultKoneMutableArraySerializer<T, E>(kClass, elementSerializer)

// endregion