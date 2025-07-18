/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array.relations

import dev.lounres.kone.collections.array.*
import dev.lounres.kone.collections.list.KoneList
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
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableArray<Element>> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableArray<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableArray<Element>? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableArray<Element>
        else null
    override fun reify(element: Any?): KoneMutableArray<Element> =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableArray<Element>
        else reificationException()
}

public fun <Element> KoneMutableArray.Companion.reification(elementReification: Reification<Element>): Reification<KoneMutableArray<Element>> =
    KoneMutableArrayReification(elementReification)

internal class KoneArrayReification<Element>(
    val elementReification: Reification<Element>
) : Reification<KoneArray<Element>> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneArray<Element>> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneArray<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneArray<Element>? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneArray<Element>
        else null
    override fun reify(element: Any?): KoneArray<Element> =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneArray<Element>
        else reificationException()
}

public fun <Element> KoneArray.Companion.reification(elementReification: Reification<Element>): Reification<KoneArray<Element>> =
    KoneArrayReification(elementReification)

internal class KoneMutableBooleanArrayReification(
    val elementReification: Reification<Boolean>
) : Reification<KoneMutableBooleanArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableBooleanArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableBooleanArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableBooleanArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableBooleanArray
        else null
    override fun reify(element: Any?): KoneMutableBooleanArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableBooleanArray
        else reificationException()
}

public fun KoneMutableBooleanArray.Companion.reification(elementReification: Reification<Boolean>): Reification<KoneMutableBooleanArray> =
    KoneMutableBooleanArrayReification(elementReification)

internal class KoneBooleanArrayReification(
    val elementReification: Reification<Boolean>
) : Reification<KoneBooleanArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneBooleanArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneBooleanArray)
        else None
    override fun reifyOrNull(element: Any?): KoneBooleanArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneBooleanArray
        else null
    override fun reify(element: Any?): KoneBooleanArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneBooleanArray
        else reificationException()
}

public fun KoneBooleanArray.Companion.reification(elementReification: Reification<Boolean>): Reification<KoneBooleanArray> =
    KoneBooleanArrayReification(elementReification)

internal class KoneMutableByteArrayReification(
    val elementReification: Reification<Byte>
) : Reification<KoneMutableByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableByteArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableByteArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableByteArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableByteArray
        else null
    override fun reify(element: Any?): KoneMutableByteArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableByteArray
        else reificationException()
}

public fun KoneMutableByteArray.Companion.reification(elementReification: Reification<Byte>): Reification<KoneMutableByteArray> =
    KoneMutableByteArrayReification(elementReification)

internal class KoneByteArrayReification(
    val elementReification: Reification<Byte>
) : Reification<KoneByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneByteArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneByteArray)
        else None
    override fun reifyOrNull(element: Any?): KoneByteArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneByteArray
        else null
    override fun reify(element: Any?): KoneByteArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneByteArray
        else reificationException()
}

public fun KoneByteArray.Companion.reification(elementReification: Reification<Byte>): Reification<KoneByteArray> =
    KoneByteArrayReification(elementReification)

internal class KoneMutableShortArrayReification(
    val elementReification: Reification<Short>
) : Reification<KoneMutableShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableShortArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableShortArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableShortArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableShortArray
        else null
    override fun reify(element: Any?): KoneMutableShortArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableShortArray
        else reificationException()
}

public fun KoneMutableShortArray.Companion.reification(elementReification: Reification<Short>): Reification<KoneMutableShortArray> =
    KoneMutableShortArrayReification(elementReification)

internal class KoneShortArrayReification(
    val elementReification: Reification<Short>
) : Reification<KoneShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneShortArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneShortArray)
        else None
    override fun reifyOrNull(element: Any?): KoneShortArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneShortArray
        else null
    override fun reify(element: Any?): KoneShortArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneShortArray
        else reificationException()
}

public fun KoneShortArray.Companion.reification(elementReification: Reification<Short>): Reification<KoneShortArray> =
    KoneShortArrayReification(elementReification)

internal class KoneMutableIntArrayReification(
    val elementReification: Reification<Int>
) : Reification<KoneMutableIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableIntArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableIntArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableIntArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableIntArray
        else null
    override fun reify(element: Any?): KoneMutableIntArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableIntArray
        else reificationException()
}

public fun KoneMutableIntArray.Companion.reification(elementReification: Reification<Int>): Reification<KoneMutableIntArray> =
    KoneMutableIntArrayReification(elementReification)

internal class KoneIntArrayReification(
    val elementReification: Reification<Int>
) : Reification<KoneIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneIntArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneIntArray)
        else None
    override fun reifyOrNull(element: Any?): KoneIntArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneIntArray
        else null
    override fun reify(element: Any?): KoneIntArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneIntArray
        else reificationException()
}

public fun KoneIntArray.Companion.reification(elementReification: Reification<Int>): Reification<KoneIntArray> =
    KoneIntArrayReification(elementReification)

internal class KoneMutableLongArrayReification(
    val elementReification: Reification<Long>
) : Reification<KoneMutableLongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableLongArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableLongArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableLongArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableLongArray
        else null
    override fun reify(element: Any?): KoneMutableLongArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableLongArray
        else reificationException()
}

public fun KoneMutableLongArray.Companion.reification(elementReification: Reification<Long>): Reification<KoneMutableLongArray> =
    KoneMutableLongArrayReification(elementReification)

internal class KoneLongArrayReification(
    val elementReification: Reification<Long>
) : Reification<KoneLongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneLongArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneLongArray)
        else None
    override fun reifyOrNull(element: Any?): KoneLongArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneLongArray
        else null
    override fun reify(element: Any?): KoneLongArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneLongArray
        else reificationException()
}

public fun KoneLongArray.Companion.reification(elementReification: Reification<Long>): Reification<KoneLongArray> =
    KoneLongArrayReification(elementReification)

internal class KoneMutableFloatArrayReification(
    val elementReification: Reification<Float>
) : Reification<KoneMutableFloatArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableFloatArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableFloatArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableFloatArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableFloatArray
        else null
    override fun reify(element: Any?): KoneMutableFloatArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableFloatArray
        else reificationException()
}

public fun KoneMutableFloatArray.Companion.reification(elementReification: Reification<Float>): Reification<KoneMutableFloatArray> =
    KoneMutableFloatArrayReification(elementReification)

internal class KoneFloatArrayReification(
    val elementReification: Reification<Float>
) : Reification<KoneFloatArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneFloatArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneFloatArray)
        else None
    override fun reifyOrNull(element: Any?): KoneFloatArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneFloatArray
        else null
    override fun reify(element: Any?): KoneFloatArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneFloatArray
        else reificationException()
}

public fun KoneFloatArray.Companion.reification(elementReification: Reification<Float>): Reification<KoneFloatArray> =
    KoneFloatArrayReification(elementReification)

internal class KoneMutableDoubleArrayReification(
    val elementReification: Reification<Double>
) : Reification<KoneMutableDoubleArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableDoubleArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableDoubleArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableDoubleArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableDoubleArray
        else null
    override fun reify(element: Any?): KoneMutableDoubleArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableDoubleArray
        else reificationException()
}

public fun KoneMutableDoubleArray.Companion.reification(elementReification: Reification<Double>): Reification<KoneMutableDoubleArray> =
    KoneMutableDoubleArrayReification(elementReification)

internal class KoneDoubleArrayReification(
    val elementReification: Reification<Double>
) : Reification<KoneDoubleArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneDoubleArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneDoubleArray)
        else None
    override fun reifyOrNull(element: Any?): KoneDoubleArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneDoubleArray
        else null
    override fun reify(element: Any?): KoneDoubleArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneDoubleArray
        else reificationException()
}

public fun KoneDoubleArray.Companion.reification(elementReification: Reification<Double>): Reification<KoneDoubleArray> =
    KoneDoubleArrayReification(elementReification)

internal class KoneMutableUByteArrayReification(
    val elementReification: Reification<UByte>
) : Reification<KoneMutableUByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableUByteArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableUByteArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableUByteArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableUByteArray
        else null
    override fun reify(element: Any?): KoneMutableUByteArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableUByteArray
        else reificationException()
}

public fun KoneMutableUByteArray.Companion.reification(elementReification: Reification<UByte>): Reification<KoneMutableUByteArray> =
    KoneMutableUByteArrayReification(elementReification)

internal class KoneUByteArrayReification(
    val elementReification: Reification<UByte>
) : Reification<KoneUByteArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneUByteArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneUByteArray)
        else None
    override fun reifyOrNull(element: Any?): KoneUByteArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneUByteArray
        else null
    override fun reify(element: Any?): KoneUByteArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneUByteArray
        else reificationException()
}

public fun KoneUByteArray.Companion.reification(elementReification: Reification<UByte>): Reification<KoneUByteArray> =
    KoneUByteArrayReification(elementReification)

internal class KoneMutableUShortArrayReification(
    val elementReification: Reification<UShort>
) : Reification<KoneMutableUShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableUShortArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableUShortArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableUShortArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableUShortArray
        else null
    override fun reify(element: Any?): KoneMutableUShortArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableUShortArray
        else reificationException()
}

public fun KoneMutableUShortArray.Companion.reification(elementReification: Reification<UShort>): Reification<KoneMutableUShortArray> =
    KoneMutableUShortArrayReification(elementReification)

internal class KoneUShortArrayReification(
    val elementReification: Reification<UShort>
) : Reification<KoneUShortArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneUShortArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneUShortArray)
        else None
    override fun reifyOrNull(element: Any?): KoneUShortArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneUShortArray
        else null
    override fun reify(element: Any?): KoneUShortArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneUShortArray
        else reificationException()
}

public fun KoneUShortArray.Companion.reification(elementReification: Reification<UShort>): Reification<KoneUShortArray> =
    KoneUShortArrayReification(elementReification)

internal class KoneMutableUIntArrayReification(
    val elementReification: Reification<UInt>
) : Reification<KoneMutableUIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableUIntArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableUIntArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableUIntArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableUIntArray
        else null
    override fun reify(element: Any?): KoneMutableUIntArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableUIntArray
        else reificationException()
}

public fun KoneMutableUIntArray.Companion.reification(elementReification: Reification<UInt>): Reification<KoneMutableUIntArray> =
    KoneMutableUIntArrayReification(elementReification)

internal class KoneUIntArrayReification(
    val elementReification: Reification<UInt>
) : Reification<KoneUIntArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneUIntArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneUIntArray)
        else None
    override fun reifyOrNull(element: Any?): KoneUIntArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneUIntArray
        else null
    override fun reify(element: Any?): KoneUIntArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneUIntArray
        else reificationException()
}

public fun KoneUIntArray.Companion.reification(elementReification: Reification<UInt>): Reification<KoneUIntArray> =
    KoneUIntArrayReification(elementReification)

internal class KoneMutableULongArrayReification(
    val elementReification: Reification<ULong>
) : Reification<KoneMutableULongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMutableULongArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneMutableULongArray)
        else None
    override fun reifyOrNull(element: Any?): KoneMutableULongArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableULongArray
        else null
    override fun reify(element: Any?): KoneMutableULongArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneMutableULongArray
        else reificationException()
}

public fun KoneMutableULongArray.Companion.reification(elementReification: Reification<ULong>): Reification<KoneMutableULongArray> =
    KoneMutableULongArrayReification(elementReification)

internal class KoneULongArrayReification(
    val elementReification: Reification<ULong>
) : Reification<KoneULongArray> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneULongArray> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneULongArray)
        else None
    override fun reifyOrNull(element: Any?): KoneULongArray? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneULongArray
        else null
    override fun reify(element: Any?): KoneULongArray =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneULongArray
        else reificationException()
}

public fun KoneULongArray.Companion.reification(elementReification: Reification<ULong>): Reification<KoneULongArray> =
    KoneULongArrayReification(elementReification)