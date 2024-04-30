/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.serializers

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind


//@OptIn(ExperimentalSerializationApi::class)
//internal sealed class KoneIterableCollectionSerialDescriptor(val elementDescriptor: SerialDescriptor) : SerialDescriptor {
//    override val kind: SerialKind = StructureKind.LIST
//    override val elementsCount: Int = 1
//
//    override fun getElementName(index: Int): String = index.toString()
//    override fun getElementIndex(name: String): Int =
//        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid list index")
//
//    override fun isElementOptional(index: Int): Boolean {
//        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
//        return false
//    }
//
//    override fun getElementAnnotations(index: Int): List<Annotation> {
//        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
//        return emptyList()
//    }
//
//    override fun getElementDescriptor(index: Int): SerialDescriptor {
//        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
//        return elementDescriptor
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (other !is KoneIterableCollectionSerialDescriptor) return false
//        if (elementDescriptor == other.elementDescriptor && serialName == other.serialName) return true
//        return false
//    }
//
//    override fun hashCode(): Int {
//        return elementDescriptor.hashCode() * 31 + serialName.hashCode()
//    }
//
//    override fun toString(): String = "$serialName($elementDescriptor)"
//}