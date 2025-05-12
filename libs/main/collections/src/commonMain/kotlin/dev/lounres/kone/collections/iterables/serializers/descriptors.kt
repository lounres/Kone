/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.iterables.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor


internal fun KoneIterableDescriptor(
    serialName: String,
    elementSerializer: KSerializer<*>,
) : SerialDescriptor = SerialDescriptor(serialName, ListSerializer(elementSerializer).descriptor)

internal fun KoneIterableDeclarationDescriptor(
    declarationName: String,
    elementSerializer: KSerializer<*>,
) : SerialDescriptor = KoneIterableDescriptor(
    serialName = "dev.lounres.kone.collections.iterables.$declarationName",
    elementSerializer = elementSerializer,
)