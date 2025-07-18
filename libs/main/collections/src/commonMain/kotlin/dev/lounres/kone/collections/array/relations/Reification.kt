/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.array.relations

import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.reificationException


internal class KoneMutableArrayReification<Element>(
    val elementReification: Reification<Element>
) : Reification<KoneMutableArray<Element>> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableArray<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableArray<Element>> =
        if (element is KoneMutableArray<*> && element.all { it in elementReification }) Some(element as KoneMutableArray<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableArray<Element>? =
        if (element is KoneMutableArray<*> && element.all { it in elementReification }) element as KoneMutableArray<Element>
        else null
    override fun reify(element: Any?): KoneMutableArray<Element> =
        if (element is KoneMutableArray<*> && element.all { it in elementReification }) element as KoneMutableArray<Element>
        else reificationException()
}

public fun <Element> KoneMutableArray.Companion.reification(elementReification: Reification<Element>): Reification<KoneMutableArray<Element>> =
    KoneMutableArrayReification(elementReification)

internal class KoneArrayReification<Element>(
    val elementReification: Reification<Element>
) : Reification<KoneArray<Element>> {
    override fun contains(element: Any?): Boolean =
        element is KoneArray<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneArray<Element>> =
        if (element is KoneArray<*> && element.all { it in elementReification }) Some(element as KoneArray<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneArray<Element>? =
        if (element is KoneArray<*> && element.all { it in elementReification }) element as KoneArray<Element>
        else null
    override fun reify(element: Any?): KoneArray<Element> =
        if (element is KoneArray<*> && element.all { it in elementReification }) element as KoneArray<Element>
        else reificationException()
}

public fun <Element> KoneArray.Companion.reification(elementReification: Reification<Element>): Reification<KoneArray<Element>> =
    KoneArrayReification(elementReification)

internal class KoneMutableBooleanArrayReification(
    val elementReification: Reification<Boolean>
) : Reification<KoneMutableBooleanArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableBooleanArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableBooleanArray> =
        if (element is KoneMutableBooleanArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableBooleanArray? =
        if (element is KoneMutableBooleanArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableBooleanArray =
        if (element is KoneMutableBooleanArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableBooleanArray.Companion.reification(elementReification: Reification<Boolean>): Reification<KoneMutableBooleanArray> =
    KoneMutableBooleanArrayReification(elementReification)

internal class KoneBooleanArrayReification(
    val elementReification: Reification<Boolean>
) : Reification<KoneBooleanArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneBooleanArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneBooleanArray> =
        if (element is KoneBooleanArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneBooleanArray? =
        if (element is KoneBooleanArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneBooleanArray =
        if (element is KoneBooleanArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneBooleanArray.Companion.reification(elementReification: Reification<Boolean>): Reification<KoneBooleanArray> =
    KoneBooleanArrayReification(elementReification)

internal class KoneMutableCharArrayReification(
    val elementReification: Reification<Char>
) : Reification<KoneMutableCharArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableCharArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableCharArray> =
        if (element is KoneMutableCharArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableCharArray? =
        if (element is KoneMutableCharArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableCharArray =
        if (element is KoneMutableCharArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableCharArray.Companion.reification(elementReification: Reification<Char>): Reification<KoneMutableCharArray> =
    KoneMutableCharArrayReification(elementReification)

internal class KoneCharArrayReification(
    val elementReification: Reification<Char>
) : Reification<KoneCharArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneCharArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneCharArray> =
        if (element is KoneCharArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneCharArray? =
        if (element is KoneCharArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneCharArray =
        if (element is KoneCharArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneCharArray.Companion.reification(elementReification: Reification<Char>): Reification<KoneCharArray> =
    KoneCharArrayReification(elementReification)

internal class KoneMutableByteArrayReification(
    val elementReification: Reification<Byte>
) : Reification<KoneMutableByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableByteArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableByteArray> =
        if (element is KoneMutableByteArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableByteArray? =
        if (element is KoneMutableByteArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableByteArray =
        if (element is KoneMutableByteArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableByteArray.Companion.reification(elementReification: Reification<Byte>): Reification<KoneMutableByteArray> =
    KoneMutableByteArrayReification(elementReification)

internal class KoneByteArrayReification(
    val elementReification: Reification<Byte>
) : Reification<KoneByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneByteArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneByteArray> =
        if (element is KoneByteArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneByteArray? =
        if (element is KoneByteArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneByteArray =
        if (element is KoneByteArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneByteArray.Companion.reification(elementReification: Reification<Byte>): Reification<KoneByteArray> =
    KoneByteArrayReification(elementReification)

internal class KoneMutableShortArrayReification(
    val elementReification: Reification<Short>
) : Reification<KoneMutableShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableShortArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableShortArray> =
        if (element is KoneMutableShortArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableShortArray? =
        if (element is KoneMutableShortArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableShortArray =
        if (element is KoneMutableShortArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableShortArray.Companion.reification(elementReification: Reification<Short>): Reification<KoneMutableShortArray> =
    KoneMutableShortArrayReification(elementReification)

internal class KoneShortArrayReification(
    val elementReification: Reification<Short>
) : Reification<KoneShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneShortArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneShortArray> =
        if (element is KoneShortArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneShortArray? =
        if (element is KoneShortArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneShortArray =
        if (element is KoneShortArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneShortArray.Companion.reification(elementReification: Reification<Short>): Reification<KoneShortArray> =
    KoneShortArrayReification(elementReification)

internal class KoneMutableIntArrayReification(
    val elementReification: Reification<Int>
) : Reification<KoneMutableIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableIntArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableIntArray> =
        if (element is KoneMutableIntArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableIntArray? =
        if (element is KoneMutableIntArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableIntArray =
        if (element is KoneMutableIntArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableIntArray.Companion.reification(elementReification: Reification<Int>): Reification<KoneMutableIntArray> =
    KoneMutableIntArrayReification(elementReification)

internal class KoneIntArrayReification(
    val elementReification: Reification<Int>
) : Reification<KoneIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneIntArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneIntArray> =
        if (element is KoneIntArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneIntArray? =
        if (element is KoneIntArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneIntArray =
        if (element is KoneIntArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneIntArray.Companion.reification(elementReification: Reification<Int>): Reification<KoneIntArray> =
    KoneIntArrayReification(elementReification)

internal class KoneMutableLongArrayReification(
    val elementReification: Reification<Long>
) : Reification<KoneMutableLongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableLongArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableLongArray> =
        if (element is KoneMutableLongArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableLongArray? =
        if (element is KoneMutableLongArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableLongArray =
        if (element is KoneMutableLongArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableLongArray.Companion.reification(elementReification: Reification<Long>): Reification<KoneMutableLongArray> =
    KoneMutableLongArrayReification(elementReification)

internal class KoneLongArrayReification(
    val elementReification: Reification<Long>
) : Reification<KoneLongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneLongArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneLongArray> =
        if (element is KoneLongArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneLongArray? =
        if (element is KoneLongArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneLongArray =
        if (element is KoneLongArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneLongArray.Companion.reification(elementReification: Reification<Long>): Reification<KoneLongArray> =
    KoneLongArrayReification(elementReification)

internal class KoneMutableFloatArrayReification(
    val elementReification: Reification<Float>
) : Reification<KoneMutableFloatArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableFloatArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableFloatArray> =
        if (element is KoneMutableFloatArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableFloatArray? =
        if (element is KoneMutableFloatArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableFloatArray =
        if (element is KoneMutableFloatArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableFloatArray.Companion.reification(elementReification: Reification<Float>): Reification<KoneMutableFloatArray> =
    KoneMutableFloatArrayReification(elementReification)

internal class KoneFloatArrayReification(
    val elementReification: Reification<Float>
) : Reification<KoneFloatArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneFloatArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneFloatArray> =
        if (element is KoneFloatArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneFloatArray? =
        if (element is KoneFloatArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneFloatArray =
        if (element is KoneFloatArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneFloatArray.Companion.reification(elementReification: Reification<Float>): Reification<KoneFloatArray> =
    KoneFloatArrayReification(elementReification)

internal class KoneMutableDoubleArrayReification(
    val elementReification: Reification<Double>
) : Reification<KoneMutableDoubleArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableDoubleArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableDoubleArray> =
        if (element is KoneMutableDoubleArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableDoubleArray? =
        if (element is KoneMutableDoubleArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableDoubleArray =
        if (element is KoneMutableDoubleArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableDoubleArray.Companion.reification(elementReification: Reification<Double>): Reification<KoneMutableDoubleArray> =
    KoneMutableDoubleArrayReification(elementReification)

internal class KoneDoubleArrayReification(
    val elementReification: Reification<Double>
) : Reification<KoneDoubleArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneDoubleArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneDoubleArray> =
        if (element is KoneDoubleArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneDoubleArray? =
        if (element is KoneDoubleArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneDoubleArray =
        if (element is KoneDoubleArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneDoubleArray.Companion.reification(elementReification: Reification<Double>): Reification<KoneDoubleArray> =
    KoneDoubleArrayReification(elementReification)

internal class KoneMutableUByteArrayReification(
    val elementReification: Reification<UByte>
) : Reification<KoneMutableUByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableUByteArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableUByteArray> =
        if (element is KoneMutableUByteArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableUByteArray? =
        if (element is KoneMutableUByteArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableUByteArray =
        if (element is KoneMutableUByteArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableUByteArray.Companion.reification(elementReification: Reification<UByte>): Reification<KoneMutableUByteArray> =
    KoneMutableUByteArrayReification(elementReification)

internal class KoneUByteArrayReification(
    val elementReification: Reification<UByte>
) : Reification<KoneUByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneUByteArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneUByteArray> =
        if (element is KoneUByteArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneUByteArray? =
        if (element is KoneUByteArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneUByteArray =
        if (element is KoneUByteArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneUByteArray.Companion.reification(elementReification: Reification<UByte>): Reification<KoneUByteArray> =
    KoneUByteArrayReification(elementReification)

internal class KoneMutableUShortArrayReification(
    val elementReification: Reification<UShort>
) : Reification<KoneMutableUShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableUShortArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableUShortArray> =
        if (element is KoneMutableUShortArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableUShortArray? =
        if (element is KoneMutableUShortArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableUShortArray =
        if (element is KoneMutableUShortArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableUShortArray.Companion.reification(elementReification: Reification<UShort>): Reification<KoneMutableUShortArray> =
    KoneMutableUShortArrayReification(elementReification)

internal class KoneUShortArrayReification(
    val elementReification: Reification<UShort>
) : Reification<KoneUShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneUShortArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneUShortArray> =
        if (element is KoneUShortArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneUShortArray? =
        if (element is KoneUShortArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneUShortArray =
        if (element is KoneUShortArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneUShortArray.Companion.reification(elementReification: Reification<UShort>): Reification<KoneUShortArray> =
    KoneUShortArrayReification(elementReification)

internal class KoneMutableUIntArrayReification(
    val elementReification: Reification<UInt>
) : Reification<KoneMutableUIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableUIntArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableUIntArray> =
        if (element is KoneMutableUIntArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableUIntArray? =
        if (element is KoneMutableUIntArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableUIntArray =
        if (element is KoneMutableUIntArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableUIntArray.Companion.reification(elementReification: Reification<UInt>): Reification<KoneMutableUIntArray> =
    KoneMutableUIntArrayReification(elementReification)

internal class KoneUIntArrayReification(
    val elementReification: Reification<UInt>
) : Reification<KoneUIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneUIntArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneUIntArray> =
        if (element is KoneUIntArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneUIntArray? =
        if (element is KoneUIntArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneUIntArray =
        if (element is KoneUIntArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneUIntArray.Companion.reification(elementReification: Reification<UInt>): Reification<KoneUIntArray> =
    KoneUIntArrayReification(elementReification)

internal class KoneMutableULongArrayReification(
    val elementReification: Reification<ULong>
) : Reification<KoneMutableULongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneMutableULongArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableULongArray> =
        if (element is KoneMutableULongArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableULongArray? =
        if (element is KoneMutableULongArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneMutableULongArray =
        if (element is KoneMutableULongArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneMutableULongArray.Companion.reification(elementReification: Reification<ULong>): Reification<KoneMutableULongArray> =
    KoneMutableULongArrayReification(elementReification)

internal class KoneULongArrayReification(
    val elementReification: Reification<ULong>
) : Reification<KoneULongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneULongArray && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneULongArray> =
        if (element is KoneULongArray && element.all { it in elementReification }) Some(element)
        else None
    override fun reifyOrNull(element: Any?): KoneULongArray? =
        if (element is KoneULongArray && element.all { it in elementReification }) element
        else null
    override fun reify(element: Any?): KoneULongArray =
        if (element is KoneULongArray && element.all { it in elementReification }) element
        else reificationException()
}

public fun KoneULongArray.Companion.reification(elementReification: Reification<ULong>): Reification<KoneULongArray> =
    KoneULongArrayReification(elementReification)