/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.interop

import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.array.generate
import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneSequence
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.KoneSettableList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.generate
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableList
import dev.lounres.kone.collections.list.implementations.generate
import dev.lounres.kone.collections.noNextElementInIteratorException
import dev.lounres.kone.collections.utils.toOptimizedList
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.isSome


// region Conversions

// region Arrays

// region Generic

public inline fun <reified Element> Iterable<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val result = KoneArrayGrowableList<Element>()
    for (element in this) result.add(element)
    return result.toKoneMutableArray()
}
public inline fun <reified Element> Collection<Element>.toKoneMutableArray(): KoneMutableArray<Element> {
    val iterator = iterator()
    return KoneMutableArray.generate(size.toUInt()) { iterator.next() }
}

public inline fun <reified Element> Iterable<Element>.toKoneArray(): KoneArray<Element> {
    val result = KoneArrayGrowableList<Element>()
    for (element in this) result.add(element)
    return result.toKoneArray()
}
public inline fun <reified Element> Collection<Element>.toKoneArray(): KoneArray<Element> {
    val iterator = iterator()
    return KoneArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region Byte

public fun Iterable<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val result = KoneArrayGrowableList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneMutableByteArray()
}
public fun Collection<Byte>.toKoneMutableByteArray(): KoneMutableByteArray {
    val iterator = iterator()
    return KoneMutableByteArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<Byte>.toKoneByteArray(): KoneByteArray {
    val result = KoneArrayGrowableList<Byte>()
    for (element in this) result.add(element)
    return result.toKoneByteArray()
}
public fun Collection<Byte>.toKoneByteArray(): KoneByteArray {
    val iterator = iterator()
    return KoneByteArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region Short

public fun Iterable<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val result = KoneArrayGrowableList<Short>()
    for (element in this) result.add(element)
    return result.toKoneMutableShortArray()
}
public fun Collection<Short>.toKoneMutableShortArray(): KoneMutableShortArray {
    val iterator = iterator()
    return KoneMutableShortArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<Short>.toKoneShortArray(): KoneShortArray {
    val result = KoneArrayGrowableList<Short>()
    for (element in this) result.add(element)
    return result.toKoneShortArray()
}
public fun Collection<Short>.toKoneShortArray(): KoneShortArray {
    val iterator = iterator()
    return KoneShortArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region Int

public fun Iterable<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val result = KoneArrayGrowableList<Int>()
    for (element in this) result.add(element)
    return result.toKoneMutableIntArray()
}
public fun Collection<Int>.toKoneMutableIntArray(): KoneMutableIntArray {
    val iterator = iterator()
    return KoneMutableIntArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<Int>.toKoneIntArray(): KoneIntArray {
    val result = KoneArrayGrowableList<Int>()
    for (element in this) result.add(element)
    return result.toKoneIntArray()
}
public fun Collection<Int>.toKoneIntArray(): KoneIntArray {
    val iterator = iterator()
    return KoneIntArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region Long

public fun Iterable<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val result = KoneArrayGrowableList<Long>()
    for (element in this) result.add(element)
    return result.toKoneMutableLongArray()
}
public fun Collection<Long>.toKoneMutableLongArray(): KoneMutableLongArray {
    val iterator = iterator()
    return KoneMutableLongArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<Long>.toKoneLongArray(): KoneLongArray {
    val result = KoneArrayGrowableList<Long>()
    for (element in this) result.add(element)
    return result.toKoneLongArray()
}
public fun Collection<Long>.toKoneLongArray(): KoneLongArray {
    val iterator = iterator()
    return KoneLongArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region Float

public fun Iterable<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val result = KoneArrayGrowableList<Float>()
    for (element in this) result.add(element)
    return result.toKoneMutableFloatArray()
}
public fun Collection<Float>.toKoneMutableFloatArray(): KoneMutableFloatArray {
    val iterator = iterator()
    return KoneMutableFloatArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<Float>.toKoneFloatArray(): KoneFloatArray {
    val result = KoneArrayGrowableList<Float>()
    for (element in this) result.add(element)
    return result.toKoneFloatArray()
}
public fun Collection<Float>.toKoneFloatArray(): KoneFloatArray {
    val iterator = iterator()
    return KoneFloatArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region Double

public fun Iterable<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val result = KoneArrayGrowableList<Double>()
    for (element in this) result.add(element)
    return result.toKoneMutableDoubleArray()
}
public fun Collection<Double>.toKoneMutableDoubleArray(): KoneMutableDoubleArray {
    val iterator = iterator()
    return KoneMutableDoubleArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val result = KoneArrayGrowableList<Double>()
    for (element in this) result.add(element)
    return result.toKoneDoubleArray()
}
public fun Collection<Double>.toKoneDoubleArray(): KoneDoubleArray {
    val iterator = iterator()
    return KoneDoubleArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region UByte

public fun Iterable<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val result = KoneArrayGrowableList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneMutableUByteArray()
}
public fun Collection<UByte>.toKoneMutableUByteArray(): KoneMutableUByteArray {
    val iterator = iterator()
    return KoneMutableUByteArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<UByte>.toKoneUByteArray(): KoneUByteArray {
    val result = KoneArrayGrowableList<UByte>()
    for (element in this) result.add(element)
    return result.toKoneUByteArray()
}
public fun Collection<UByte>.toKoneUByteArray(): KoneUByteArray {
    val iterator = iterator()
    return KoneUByteArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region UShort

public fun Iterable<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val result = KoneArrayGrowableList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneMutableUShortArray()
}
public fun Collection<UShort>.toKoneMutableUShortArray(): KoneMutableUShortArray {
    val iterator = iterator()
    return KoneMutableUShortArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<UShort>.toKoneUShortArray(): KoneUShortArray {
    val result = KoneArrayGrowableList<UShort>()
    for (element in this) result.add(element)
    return result.toKoneUShortArray()
}
public fun Collection<UShort>.toKoneUShortArray(): KoneUShortArray {
    val iterator = iterator()
    return KoneUShortArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region UInt

public fun Iterable<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val result = KoneArrayGrowableList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneMutableUIntArray()
}
public fun Collection<UInt>.toKoneMutableUIntArray(): KoneMutableUIntArray {
    val iterator = iterator()
    return KoneMutableUIntArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<UInt>.toKoneUIntArray(): KoneUIntArray {
    val result = KoneArrayGrowableList<UInt>()
    for (element in this) result.add(element)
    return result.toKoneUIntArray()
}
public fun Collection<UInt>.toKoneUIntArray(): KoneUIntArray {
    val iterator = iterator()
    return KoneUIntArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// region ULong

public fun Iterable<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val result = KoneArrayGrowableList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneMutableULongArray()
}
public fun Collection<ULong>.toKoneMutableULongArray(): KoneMutableULongArray {
    val iterator = iterator()
    return KoneMutableULongArray.generate(size.toUInt()) { iterator.next() }
}

public fun Iterable<ULong>.toKoneULongArray(): KoneULongArray {
    val result = KoneArrayGrowableList<ULong>()
    for (element in this) result.add(element)
    return result.toKoneULongArray()
}
public fun Collection<ULong>.toKoneULongArray(): KoneULongArray {
    val iterator = iterator()
    return KoneULongArray.generate(size.toUInt()) { iterator.next() }
}

// endregion

// endregion

// region Lists

public fun <Element> Iterable<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val result = KoneArrayResizableList<Element>()
    for (element in this) result.add(element)
    return result
}

public fun <Element> Collection<Element>.toKoneMutableList(): KoneMutableList<Element> {
    val iterator = iterator()
    return KoneArrayResizableList.generate(size.toUInt()) { iterator.next() }
}

public fun <Element> Iterable<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val result = KoneArrayResizableList<Element>()
    for (element in this) result.add(element)
    return KoneSettableList.generate(result.size) { result[it] }
}

public fun <Element> Collection<Element>.toKoneSettableList(): KoneSettableList<Element> {
    val iterator = iterator()
    return KoneSettableList.generate(size.toUInt()) { iterator.next() }
}

public fun <Element> Iterable<Element>.toKoneList(): KoneList<Element> =
    this.toKoneMutableList().toOptimizedList()

public fun <Element> Collection<Element>.toKoneList(): KoneList<Element> =
    if (isEmpty()) KoneList.empty()
    else this.toKoneMutableList()

// endregion

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

// region Wrappers

private class IteratorAsKoneIteratorWrapper<Element>(
    private val source: Iterator<Element>,
) : KoneIterator<Element> {
    private var cache: Maybe<Element> = None
    
    override fun hasNext(): Boolean = cache.isSome() || source.hasNext()
    override fun getNext(): Element {
        if (!hasNext()) noNextElementInIteratorException()
        return when (val cacheValue = cache) {
            None -> source.next().also { cache = Some(it) }
            is Some<Element> -> cacheValue.value
        }
    }
    override fun moveNext() {
        when (cache) {
            None -> { val _ = source.next() }
            is Some<Element> -> cache = None
        }
    }
}

public fun <Element> Iterator<Element>.asKoneIterator(): KoneIterator<Element> =
    IteratorAsKoneIteratorWrapper(this)

public fun <Element> Iterator<Element>.asKoneSequence(): KoneSequence<Element> {
    val wrapper = asKoneIterator()
    return KoneSequence { wrapper }
}

public fun <Element> Iterable<Element>.asKoneSequence(): KoneSequence<Element> = KoneSequence { iterator().asKoneIterator() }

public fun <Element> Sequence<Element>.asKoneSequence(): KoneSequence<Element> = KoneSequence { iterator().asKoneIterator() }

// endregion