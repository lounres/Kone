/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry.serialization

import dev.lounres.kone.registry.MutableRegistry
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


public interface RegistrySerializableKey<T> : RegistryKey<T> {
    public val serializer: KSerializer<T>
}

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
    
    override fun serialize(encoder: Encoder, value: Registry) {
        encoder.encodeStructure(descriptor) {
            var registrationIndex = 0
            for ((keyName, key) in serializableKeys) {
                val value = if (key in value) value[key] else continue
                encodeStringElement(descriptor, registrationIndex++, keyName)
                @Suppress("UNCHECKED_CAST")
                encodeSerializableElement(descriptor, registrationIndex++, key.serializer as KSerializer<Any?>, value)
            }
        }
    }
    
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