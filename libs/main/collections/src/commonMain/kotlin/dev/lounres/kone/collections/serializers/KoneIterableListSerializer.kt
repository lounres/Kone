/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.serializers

import dev.lounres.kone.collections.KoneIterableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


//internal class KoneIterableListSerializer<E> : KSerializer<KoneIterableList<E>> {
//    override val descriptor: SerialDescriptor = KoneIterableCollectionSerialDescriptor
//
//    override fun serialize(encoder: Encoder, value: KoneIterableList<E>) {
//
//        TODO("Not yet implemented")
//    }
//
//    override fun deserialize(decoder: Decoder): KoneIterableList<E> {
//        TODO("Not yet implemented")
//    }
//}