/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalSerializationApi::class)

package dev.lounres.kone.collections.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind


internal open class KoneCollectionDescriptor internal constructor(
    final override val serialName: String,
    val elementDescriptor: SerialDescriptor,
) : SerialDescriptor {
    final override val kind: SerialKind get() = StructureKind.LIST
    final override val elementsCount: Int get() = 1
    
    final override fun getElementName(index: Int): String = index.toString()
    final override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")
    
    final override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return false
    }
    
    final override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return emptyList()
    }
    
    final override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return elementDescriptor
    }
    
    final override fun equals(other: Any?): Boolean =
        when {
            other === this -> true
            other !is KoneCollectionDescriptor -> false
            serialName == other.serialName
                    && elementDescriptor == other.elementDescriptor -> true
            else -> false
        }
    
    final override fun hashCode(): Int = elementDescriptor.hashCode() * 31 + serialName.hashCode()
    
    final override fun toString(): String = "$serialName($elementDescriptor)"
}

internal open class KoneMapDescriptor(
    final override val serialName: String,
    val keyDescriptor: SerialDescriptor,
    val valueDescriptor: SerialDescriptor,
) : SerialDescriptor {
    final override val kind: SerialKind get() = StructureKind.MAP
    final override val elementsCount: Int get() = 2
    
    final override fun getElementName(index: Int): String = index.toString()
    final override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid map index")
    
    final override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return false
    }
    
    final override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return emptyList()
    }
    
    final override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return when (index % 2) {
            0 -> keyDescriptor
            1 -> valueDescriptor
            else -> error("Unreached")
        }
    }
    
    final override fun equals(other: Any?): Boolean =
        when {
            other === this -> true
            other !is KoneMapDescriptor -> false
            serialName == other.serialName && keyDescriptor == other.keyDescriptor && valueDescriptor == other.valueDescriptor -> true
            else -> false
        }
    
    final override fun hashCode(): Int =
        (keyDescriptor.hashCode() * 31 + valueDescriptor.hashCode()) * 31 + serialName.hashCode()
    
    final override fun toString(): String = "$serialName($keyDescriptor, $valueDescriptor)"
}