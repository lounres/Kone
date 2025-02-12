/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.interop

import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.emptyKoneList
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableList
import dev.lounres.kone.collections.utils.toOptimizedList


// region Conversions
public inline fun <reified Element> Iterable<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    if (this is Collection<Element>) return this.toKoneMutableArray()
    
    val result = KoneArrayGrowableList<Element>()
    for (element in this) result.add(element)
    return result.toKoneMutableArray()
}
public inline fun <reified Element> Collection<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val iterator = iterator()
    return KoneMutableArray(size.toUInt()) { iterator.next() }
}

public inline fun <reified Element> Iterable<Element>.toKoneArray(): KoneArray<Element> {
    if (this is Collection<Element>) return this.toKoneArray()
    
    val result = KoneArrayGrowableList<Element>()
    for (element in this) result.add(element)
    return result.toKoneArray()
}
public inline fun <reified Element> Collection<Element>.toKoneArray(): KoneArray<Element> {
    val iterator = iterator()
    return KoneArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    if (this is Collection<Byte>) return this.toKoneMutableByteArray()
    
    val result = KoneArrayGrowableList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneMutableByteArray()
}
public fun Collection<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Byte>.toKoneByteArray(): KoneByteArray {
    if (this is Collection<Byte>) return this.toKoneByteArray()
    
    val result = KoneArrayGrowableList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneByteArray()
}
public fun Collection<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    if (this is Collection<Short>) return this.toKoneMutableShortArray()
    
    val result = KoneArrayGrowableList<Short>()
    for (element in this) result.add(element)
    return result.toKoneMutableShortArray()
}
public fun Collection<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Short>.toKoneShortArray(): KoneShortArray {
    if (this is Collection<Short>) return this.toKoneShortArray()
    
    val result = KoneArrayGrowableList<Short>()
    for (element in this) result.add(element)
    return result.toKoneShortArray()
}
public fun Collection<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    if (this is Collection<Int>) return this.toKoneMutableIntArray()
    
    val result = KoneArrayGrowableList<Int>()
    for (element in this) result.add(element)
    return result.toKoneMutableIntArray()
}
public fun Collection<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Int>.toKoneIntArray(): KoneIntArray {
    if (this is Collection<Int>) return this.toKoneIntArray()
    
    val result = KoneArrayGrowableList<Int>()
    for (element in this) result.add(element)
    return result.toKoneIntArray()
}
public fun Collection<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    if (this is Collection<Long>) return this.toKoneMutableLongArray()
    
    val result = KoneArrayGrowableList<Long>()
    for (element in this) result.add(element)
    return result.toKoneMutableLongArray()
}
public fun Collection<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Long>.toKoneLongArray(): KoneLongArray {
    if (this is Collection<Long>) return this.toKoneLongArray()
    
    val result = KoneArrayGrowableList<Long>()
    for (element in this) result.add(element)
    return result.toKoneLongArray()
}
public fun Collection<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    if (this is Collection<Float>) return this.toKoneMutableFloatArray()
    
    val result = KoneArrayGrowableList<Float>()
    for (element in this) result.add(element)
    return result.toKoneMutableFloatArray()
}
public fun Collection<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Float>.toKoneFloatArray(): KoneFloatArray {
    if (this is Collection<Float>) return this.toKoneFloatArray()
    
    val result = KoneArrayGrowableList<Float>()
    for (element in this) result.add(element)
    return result.toKoneFloatArray()
}
public fun Collection<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    if (this is Collection<Double>) return this.toKoneMutableDoubleArray()
    
    val result = KoneArrayGrowableList<Double>()
    for (element in this) result.add(element)
    return result.toKoneMutableDoubleArray()
}
public fun Collection<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    if (this is Collection<Double>) return this.toKoneDoubleArray()
    
    val result = KoneArrayGrowableList<Double>()
    for (element in this) result.add(element)
    return result.toKoneDoubleArray()
}
public fun Collection<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    if (this is Collection<UByte>) return this.toKoneMutableUByteArray()
    
    val result = KoneArrayGrowableList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneMutableUByteArray()
}
public fun Collection<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    if (this is Collection<UByte>) return this.toKoneUByteArray()
    
    val result = KoneArrayGrowableList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneUByteArray()
}
public fun Collection<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    if (this is Collection<UShort>) return this.toKoneMutableUShortArray()
    
    val result = KoneArrayGrowableList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneMutableUShortArray()
}
public fun Collection<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    if (this is Collection<UShort>) return this.toKoneUShortArray()
    
    val result = KoneArrayGrowableList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneUShortArray()
}
public fun Collection<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    if (this is Collection<UInt>) return this.toKoneMutableUIntArray()
    
    val result = KoneArrayGrowableList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneMutableUIntArray()
}
public fun Collection<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    if (this is Collection<UInt>) return this.toKoneUIntArray()
    
    val result = KoneArrayGrowableList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneUIntArray()
}
public fun Collection<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    if (this is Collection<ULong>) return this.toKoneMutableULongArray()
    
    val result = KoneArrayGrowableList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneMutableULongArray()
}
public fun Collection<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray(size.toUInt()) { iterator.next() }
}

public fun Iterable<ULong>.toKoneULongArray(): KoneULongArray {
    if (this is Collection<ULong>) return this.toKoneULongArray()
    
    val result = KoneArrayGrowableList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneULongArray()
}
public fun Collection<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray(size.toUInt()) { iterator.next() }
}

public fun <Element> Iterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    if (this is Collection<Element>) return this.toKoneMutableList()
    
    val result = KoneArrayResizableList<Element>()
    for (element in this) result.add(element)
    return result
}

public fun <Element> Collection<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneArrayResizableList(size.toUInt()) { iterator.next() }
}

public fun <Element> Iterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    if (this is Collection<Element>) return this.toKoneSettableList()
    
    val result = KoneArrayResizableList<Element>()
    for (element in this) result.add(element)
    return KoneSettableList(result.size) { result[it] }
}

public fun <Element> Collection<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList(size.toUInt()) { iterator.next() }
}

public fun <Element> Iterable<Element>.toKoneList(): KoneList<Element> =
    if(this is Collection<Element>) this.toKoneList()
    else this.toKoneMutableList()

public fun <Element> Collection<Element>.toKoneList(): KoneList<Element> =
    if (isEmpty()) emptyKoneList()
    else this.toKoneMutableList().toOptimizedList()

// TODO: Review the following converters

//public fun <Element> Iterable<Element>.toKoneMutableSet(elementEquality: Equality<Element> = defaultEquality()): KoneMutableSet<Element> {
//    if (this is Collection<Element>) return this.toKoneMutableSet(elementContext = elementEquality)
//
//    val result = koneMutableSetOf(elementEquality = elementEquality)
//    for (element in this) result.add(element)
//    return result
//}
//
//public inline fun <reified Element> Iterable<Element>.toKoneMutableReifiedSet(): KoneMutableReifiedSet<Element> =
//    toKoneMutableReifiedSet(defaultReifiedEquality())
//
//public fun <Element> Iterable<Element>.toKoneMutableReifiedSet(elementContext: ReifiedEquality<Element>): KoneMutableReifiedSet<Element> {
//    if (this is Collection<Element>) return this.toKoneMutableReifiedSet(elementContext = elementContext)
//
//    val result = koneMutableReifiedSetOf<Element>(elementContext = elementContext)
//    for (element in this) result.add(element)
//    return result
//}
//
//public fun <Element> Collection<Element>.toKoneMutableSet(elementContext: Equality<Element> = defaultEquality()): KoneMutableSet<Element> =
//    if (elementContext is Hashing<Element>)
//        KoneHashResizableSet(elementContext = elementContext)
//            .apply {
//                val iterator = this@toKoneMutableSet.iterator()
//                addSeveral(this@toKoneMutableSet.size.toUInt()) { iterator.next() }
//            }
//    else {
//        val backingList = KoneArrayGrowableList<Element>()
//        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
//        KoneListBackedMutableSet(elementContext, KoneArrayResizableLinkedList<Element>().apply { addAllFrom(backingList) })
//    }
//
//public inline fun <reified Element> Collection<Element>.toKoneMutableReifiedSet(): KoneMutableReifiedSet<Element> =
//    toKoneMutableReifiedSet(elementContext = defaultReifiedEquality())
//
//public fun <Element> Collection<Element>.toKoneMutableReifiedSet(elementContext: ReifiedEquality<Element>): KoneMutableReifiedSet<Element> =
//    if (elementContext is ReifiedHashing<Element>)
//        KoneHashResizableReifiedSet(elementContext = elementContext)
//            .apply {
//                val iterator = this@toKoneMutableReifiedSet.iterator()
//                addSeveral(this@toKoneMutableReifiedSet.size.toUInt()) { iterator.next() }
//            }
//    else {
//        val backingList = KoneArrayGrowableList<Element>()
//        for (element in this) if (elementContext { element !in backingList }) backingList.add(element)
//        KoneListBackedMutableReifiedSet(elementContext, KoneArrayResizableLinkedList<Element>().apply { addAllFrom(backingList) })
//    }
//
//public fun <Element> Iterable<Element>.toKoneSet(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
//    if (this is Collection<Element>) this.toKoneSet(elementContext = elementContext)
//    else this.toKoneMutableSet(elementEquality = elementContext)
//
//public inline fun <reified Element> Iterable<Element>.toKoneReifiedSet(): KoneReifiedSet<Element> =
//    toKoneReifiedSet(elementContext = defaultReifiedEquality())
//
//public fun <Element> Iterable<Element>.toKoneReifiedSet(elementContext: ReifiedEquality<Element>): KoneReifiedSet<Element> =
//    if (this is Collection<Element>) this.toKoneReifiedSet(elementContext = elementContext)
//    else this.toKoneMutableReifiedSet(elementContext = elementContext)
//
//public fun <Element> Collection<Element>.toKoneSet(elementContext: Equality<Element> = defaultEquality()): KoneSet<Element> =
//    if (size == 0) emptyKoneSet()
//    else this.toKoneMutableSet(elementContext = elementContext)
//
//public inline fun <reified Element> Collection<Element>.toKoneReifiedSet(): KoneReifiedSet<Element> =
//    toKoneReifiedSet(defaultReifiedEquality())
//
//public fun <Element> Collection<Element>.toKoneReifiedSet(elementContext: ReifiedEquality<Element>): KoneReifiedSet<Element> =
//    if (size == 0) emptyKoneReifiedSet()
//    else this.toKoneMutableReifiedSet(elementContext = elementContext)
// endregion