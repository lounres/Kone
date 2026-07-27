/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry.serialization

import dev.lounres.kone.registry.MutableProviderRegistry
import dev.lounres.kone.registry.MutableRegistry
import dev.lounres.kone.registry.ProviderRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryKey
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure


/**
 * A [RegistryKey] that has an associated [KSerializer] for serialization.
 * 
 * Keys implementing this interface can be used with the registry serializers to enable
 * automatic serialization and deserialization of registry values.
 * 
 * @param T The type of value associated with this key.
 */
public interface RegistrySerializableKey<T> : RegistryKey<T> {
    /**
     * The [KSerializer] used to serialize and deserialize values associated with this key.
     */
    public val serializer: KSerializer<T>
}

/**
 * A [KSerializer] for [Registry] that can serialize and deserialize registry contents.
 * 
 * This serializer works with [RegistrySerializableKey] instances to properly serialize
 * registry values using their associated serializers.
 * 
 * @param serializableKeys A map from string names to [RegistrySerializableKey] instances that this serializer should handle.
 */
@OptIn(InternalSerializationApi::class)
public class RegistrySerializer(
    private val serializableKeys: Map<String, RegistrySerializableKey<*>>,
) : KSerializer<Registry> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("dev.lounres.kone.registry.serialization.RegistrySerializer", StructureKind.MAP) {
        element(
            elementName = "key",
            descriptor = String.serializer().descriptor,
        )
        element(
            elementName = "value",
            descriptor = buildSerialDescriptor("dev.lounres.kone.registry.serialization.RegistrySerializer:value", SerialKind.CONTEXTUAL),
        )
    }
    
    /**
     * Serializes a [Registry] to the specified [encoder].
     * 
     * @param encoder The [Encoder] to write the serialized data to.
     * @param value The [Registry] to serialize.
     */
    override fun serialize(encoder: Encoder, value: Registry) {
        encoder.encodeStructure(descriptor) {
            var registrationIndex = 0
            for ((val keyName = key, val key = value) in serializableKeys) {
                val value = if (key in value) value[key] else continue
                encodeStringElement(descriptor, registrationIndex++, keyName)
                @Suppress("UNCHECKED_CAST")
                encodeSerializableElement(descriptor, registrationIndex++, key.serializer as KSerializer<Any?>, value)
            }
        }
    }
    
    /**
     * Deserializes a [Registry] from the specified [decoder].
     * 
     * @param decoder The [Decoder] to read the serialized data from.
     * @return The deserialized [Registry].
     * @throws IllegalArgumentException if an unregistered key is encountered during deserialization.
     */
    override fun deserialize(decoder: Decoder): Registry =
        decoder.decodeStructure(descriptor) {
            val result = MutableRegistry()
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                repeat(size) {
                    val keyName = decodeStringElement(descriptor, it * 2)
                    @Suppress("UNCHECKED_CAST")
                    val key = serializableKeys.getOrElse(keyName) { error("Unregistered key for deserialization: $keyName") } as RegistrySerializableKey<Any?>
                    val value = decodeSerializableElement(descriptor, it * 2 + 1, key.serializer)
                    result[key] = value
                }
            } else {
                while (true) {
                    val keyNameIndex = decodeElementIndex(descriptor)
                    if (keyNameIndex == CompositeDecoder.DECODE_DONE) break
                    val keyName = decodeStringElement(descriptor, keyNameIndex)
                    @Suppress("UNCHECKED_CAST")
                    val key = serializableKeys.getOrElse(keyName) { error("Unregistered key for deserialization: $keyName") } as RegistrySerializableKey<Any?>
                    decodeElementIndex(descriptor).also {
                        require(it == keyNameIndex + 1) { "Value must follow key in a map, index for key: $keyNameIndex, returned index for value: $it" }
                    }
                    val value = decodeSerializableElement(descriptor, keyNameIndex + 1, key.serializer)
                    result[key] = value
                }
            }
            result
        }
}

/**
 * A [KSerializer] for [ProviderRegistry] that can serialize and deserialize provider registry contents.
 * 
 * This serializer works with [RegistrySerializableKey] instances to properly serialize
 * provider registry values using their associated serializers.
 * 
 * @param serializableKeys A map from string names to [RegistrySerializableKey] instances that this serializer should handle.
 */
@OptIn(InternalSerializationApi::class)
public class ProviderRegistrySerializer(
    private val serializableKeys: Map<String, RegistrySerializableKey<*>>,
) : KSerializer<ProviderRegistry> {
    override val descriptor: SerialDescriptor = buildSerialDescriptor("dev.lounres.kone.registry.serialization.RegistrySerializer", StructureKind.MAP) {
        element(
            elementName = "key",
            descriptor = String.serializer().descriptor,
        )
        element(
            elementName = "value",
            descriptor = buildSerialDescriptor("dev.lounres.kone.registry.serialization.RegistrySerializer:value", SerialKind.CONTEXTUAL),
        )
    }
    
    /**
     * Serializes a [ProviderRegistry] to the specified [encoder].
     * 
     * @param encoder The [Encoder] to write the serialized data to.
     * @param value The [ProviderRegistry] to serialize.
     */
    override fun serialize(encoder: Encoder, value: ProviderRegistry) {
        encoder.encodeStructure(descriptor) {
            var registrationIndex = 0
            for ((val keyName = key, val key = value) in serializableKeys) {
                val value = if (key in value) value[key] else continue
                encodeStringElement(descriptor, registrationIndex++, keyName)
                @Suppress("UNCHECKED_CAST")
                encodeSerializableElement(descriptor, registrationIndex++, key.serializer as KSerializer<Any?>, value)
            }
        }
    }
    
    /**
     * Deserializes a [ProviderRegistry] from the specified [decoder].
     * 
     * @param decoder The [Decoder] to read the serialized data from.
     * @return The deserialized [ProviderRegistry].
     * @throws IllegalArgumentException if an unregistered key is encountered during deserialization.
     */
    override fun deserialize(decoder: Decoder): ProviderRegistry =
        decoder.decodeStructure(descriptor) {
            val result = MutableProviderRegistry()
            if (decodeSequentially()) {
                val size = decodeCollectionSize(descriptor)
                repeat(size) {
                    val keyName = decodeStringElement(descriptor, it * 2)
                    @Suppress("UNCHECKED_CAST")
                    val key = serializableKeys.getOrElse(keyName) { error("Unregistered key for deserialization: $keyName") } as RegistrySerializableKey<Any?>
                    val value = decodeSerializableElement(descriptor, it * 2 + 1, key.serializer)
                    result[key] = value
                }
            } else {
                while (true) {
                    val keyNameIndex = decodeElementIndex(descriptor)
                    if (keyNameIndex == CompositeDecoder.DECODE_DONE) break
                    val keyName = decodeStringElement(descriptor, keyNameIndex)
                    @Suppress("UNCHECKED_CAST")
                    val key = serializableKeys.getOrElse(keyName) { error("Unregistered key for deserialization: $keyName") } as RegistrySerializableKey<Any?>
                    decodeElementIndex(descriptor).also {
                        require(it == keyNameIndex + 1) { "Value must follow key in a map, index for key: $keyNameIndex, returned index for value: $it" }
                    }
                    val value = decodeSerializableElement(descriptor, keyNameIndex + 1, key.serializer)
                    result[key] = value
                }
            }
            result
        }
}