/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.iterables.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SealedSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind


@OptIn(SealedSerializationApi::class)
internal class KoneIterableDescriptor(
    override val serialName: String,
    private val elementDescriptor: SerialDescriptor,
) : SerialDescriptor {
    override val kind: SerialKind get() = StructureKind.LIST
    override val elementsCount: Int = 1
    
    override fun getElementName(index: Int): String = index.toString()
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")
    
    override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return false
    }
    
    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return emptyList()
    }
    
    override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return elementDescriptor
    }
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneIterableDescriptor) return false
        if (elementDescriptor == other.elementDescriptor && serialName == other.serialName) return true
        return false
    }
    
    override fun hashCode(): Int {
        return elementDescriptor.hashCode() * 31 + serialName.hashCode()
    }
    
    override fun toString(): String = "$serialName($elementDescriptor)"
}

internal fun KoneIterableDeclarationDescriptor(
    declarationName: String,
    elementDescriptor: SerialDescriptor,
) : SerialDescriptor = KoneIterableDescriptor(
    serialName = "dev.lounres.kone.collections.iterables.$declarationName",
    elementDescriptor = elementDescriptor,
)