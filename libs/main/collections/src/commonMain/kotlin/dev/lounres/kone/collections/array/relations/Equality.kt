/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array.relations

import dev.lounres.kone.collections.array.*
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.neq


internal class KoneArrayEquality<Element>(val elementEquality: Equality<Element>) : Equality<KoneArray<Element>> {
    override fun KoneArray<Element>.equalsTo(other: KoneArray<Element>): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun <Element> KoneArray.Companion.equality(elementEquality: Equality<Element> = Equality.defaultFor()): Equality<KoneArray<Element>> =
    KoneArrayEquality(elementEquality)

internal class KoneMutableArrayEquality<Element>(val elementEquality: Equality<Element>) : Equality<KoneMutableArray<Element>> {
    override fun KoneMutableArray<Element>.equalsTo(other: KoneMutableArray<Element>): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun <Element> KoneMutableArray.Companion.equality(elementEquality: Equality<Element> = Equality.defaultFor()): Equality<KoneMutableArray<Element>> =
    KoneMutableArrayEquality(elementEquality)

internal class KoneBooleanArrayEquality(val elementEquality: Equality<Boolean>) : Equality<KoneBooleanArray> {
    override fun KoneBooleanArray.equalsTo(other: KoneBooleanArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneBooleanArray.Companion.equality(elementEquality: Equality<Boolean> = Equality.defaultFor()): Equality<KoneBooleanArray> =
    KoneBooleanArrayEquality(elementEquality)

internal class KoneMutableBooleanArrayEquality(val elementEquality: Equality<Boolean>) : Equality<KoneMutableBooleanArray> {
    override fun KoneMutableBooleanArray.equalsTo(other: KoneMutableBooleanArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableBooleanArray.Companion.equality(elementEquality: Equality<Boolean> = Equality.defaultFor()): Equality<KoneMutableBooleanArray> =
    KoneMutableBooleanArrayEquality(elementEquality)

internal class KoneCharArrayEquality(val elementEquality: Equality<Char>) : Equality<KoneCharArray> {
    override fun KoneCharArray.equalsTo(other: KoneCharArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneCharArray.Companion.equality(elementEquality: Equality<Char> = Equality.defaultFor()): Equality<KoneCharArray> =
    KoneCharArrayEquality(elementEquality)

internal class KoneMutableCharArrayEquality(val elementEquality: Equality<Char>) : Equality<KoneMutableCharArray> {
    override fun KoneMutableCharArray.equalsTo(other: KoneMutableCharArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableCharArray.Companion.equality(elementEquality: Equality<Char> = Equality.defaultFor()): Equality<KoneMutableCharArray> =
    KoneMutableCharArrayEquality(elementEquality)

internal class KoneByteArrayEquality(val elementEquality: Equality<Byte>) : Equality<KoneByteArray> {
    override fun KoneByteArray.equalsTo(other: KoneByteArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneByteArray.Companion.equality(elementEquality: Equality<Byte> = Equality.defaultFor()): Equality<KoneByteArray> =
    KoneByteArrayEquality(elementEquality)

internal class KoneMutableByteArrayEquality(val elementEquality: Equality<Byte>) : Equality<KoneMutableByteArray> {
    override fun KoneMutableByteArray.equalsTo(other: KoneMutableByteArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableByteArray.Companion.equality(elementEquality: Equality<Byte> = Equality.defaultFor()): Equality<KoneMutableByteArray> =
    KoneMutableByteArrayEquality(elementEquality)

internal class KoneShortArrayEquality(val elementEquality: Equality<Short>) : Equality<KoneShortArray> {
    override fun KoneShortArray.equalsTo(other: KoneShortArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneShortArray.Companion.equality(elementEquality: Equality<Short> = Equality.defaultFor()): Equality<KoneShortArray> =
    KoneShortArrayEquality(elementEquality)

internal class KoneMutableShortArrayEquality(val elementEquality: Equality<Short>) : Equality<KoneMutableShortArray> {
    override fun KoneMutableShortArray.equalsTo(other: KoneMutableShortArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableShortArray.Companion.equality(elementEquality: Equality<Short> = Equality.defaultFor()): Equality<KoneMutableShortArray> =
    KoneMutableShortArrayEquality(elementEquality)

internal class KoneIntArrayEquality(val elementEquality: Equality<Int>) : Equality<KoneIntArray> {
    override fun KoneIntArray.equalsTo(other: KoneIntArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneIntArray.Companion.equality(elementEquality: Equality<Int> = Equality.defaultFor()): Equality<KoneIntArray> =
    KoneIntArrayEquality(elementEquality)

internal class KoneMutableIntArrayEquality(val elementEquality: Equality<Int>) : Equality<KoneMutableIntArray> {
    override fun KoneMutableIntArray.equalsTo(other: KoneMutableIntArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableIntArray.Companion.equality(elementEquality: Equality<Int> = Equality.defaultFor()): Equality<KoneMutableIntArray> =
    KoneMutableIntArrayEquality(elementEquality)

internal class KoneLongArrayEquality(val elementEquality: Equality<Long>) : Equality<KoneLongArray> {
    override fun KoneLongArray.equalsTo(other: KoneLongArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneLongArray.Companion.equality(elementEquality: Equality<Long> = Equality.defaultFor()): Equality<KoneLongArray> =
    KoneLongArrayEquality(elementEquality)

internal class KoneMutableLongArrayEquality(val elementEquality: Equality<Long>) : Equality<KoneMutableLongArray> {
    override fun KoneMutableLongArray.equalsTo(other: KoneMutableLongArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableLongArray.Companion.equality(elementEquality: Equality<Long> = Equality.defaultFor()): Equality<KoneMutableLongArray> =
    KoneMutableLongArrayEquality(elementEquality)

internal class KoneFloatArrayEquality(val elementEquality: Equality<Float>) : Equality<KoneFloatArray> {
    override fun KoneFloatArray.equalsTo(other: KoneFloatArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneFloatArray.Companion.equality(elementEquality: Equality<Float> = Equality.defaultFor()): Equality<KoneFloatArray> =
    KoneFloatArrayEquality(elementEquality)

internal class KoneMutableFloatArrayEquality(val elementEquality: Equality<Float>) : Equality<KoneMutableFloatArray> {
    override fun KoneMutableFloatArray.equalsTo(other: KoneMutableFloatArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableFloatArray.Companion.equality(elementEquality: Equality<Float> = Equality.defaultFor()): Equality<KoneMutableFloatArray> =
    KoneMutableFloatArrayEquality(elementEquality)

internal class KoneDoubleArrayEquality(val elementEquality: Equality<Double>) : Equality<KoneDoubleArray> {
    override fun KoneDoubleArray.equalsTo(other: KoneDoubleArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneDoubleArray.Companion.equality(elementEquality: Equality<Double> = Equality.defaultFor()): Equality<KoneDoubleArray> =
    KoneDoubleArrayEquality(elementEquality)

internal class KoneMutableDoubleArrayEquality(val elementEquality: Equality<Double>) : Equality<KoneMutableDoubleArray> {
    override fun KoneMutableDoubleArray.equalsTo(other: KoneMutableDoubleArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableDoubleArray.Companion.equality(elementEquality: Equality<Double> = Equality.defaultFor()): Equality<KoneMutableDoubleArray> =
    KoneMutableDoubleArrayEquality(elementEquality)

internal class KoneUByteArrayEquality(val elementEquality: Equality<UByte>) : Equality<KoneUByteArray> {
    override fun KoneUByteArray.equalsTo(other: KoneUByteArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneUByteArray.Companion.equality(elementEquality: Equality<UByte> = Equality.defaultFor()): Equality<KoneUByteArray> =
    KoneUByteArrayEquality(elementEquality)

internal class KoneMutableUByteArrayEquality(val elementEquality: Equality<UByte>) : Equality<KoneMutableUByteArray> {
    override fun KoneMutableUByteArray.equalsTo(other: KoneMutableUByteArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableUByteArray.Companion.equality(elementEquality: Equality<UByte> = Equality.defaultFor()): Equality<KoneMutableUByteArray> =
    KoneMutableUByteArrayEquality(elementEquality)

internal class KoneUShortArrayEquality(val elementEquality: Equality<UShort>) : Equality<KoneUShortArray> {
    override fun KoneUShortArray.equalsTo(other: KoneUShortArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneUShortArray.Companion.equality(elementEquality: Equality<UShort> = Equality.defaultFor()): Equality<KoneUShortArray> =
    KoneUShortArrayEquality(elementEquality)

internal class KoneMutableUShortArrayEquality(val elementEquality: Equality<UShort>) : Equality<KoneMutableUShortArray> {
    override fun KoneMutableUShortArray.equalsTo(other: KoneMutableUShortArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableUShortArray.Companion.equality(elementEquality: Equality<UShort> = Equality.defaultFor()): Equality<KoneMutableUShortArray> =
    KoneMutableUShortArrayEquality(elementEquality)

internal class KoneUIntArrayEquality(val elementEquality: Equality<UInt>) : Equality<KoneUIntArray> {
    override fun KoneUIntArray.equalsTo(other: KoneUIntArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneUIntArray.Companion.equality(elementEquality: Equality<UInt> = Equality.defaultFor()): Equality<KoneUIntArray> =
    KoneUIntArrayEquality(elementEquality)

internal class KoneMutableUIntArrayEquality(val elementEquality: Equality<UInt>) : Equality<KoneMutableUIntArray> {
    override fun KoneMutableUIntArray.equalsTo(other: KoneMutableUIntArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableUIntArray.Companion.equality(elementEquality: Equality<UInt> = Equality.defaultFor()): Equality<KoneMutableUIntArray> =
    KoneMutableUIntArrayEquality(elementEquality)

internal class KoneULongArrayEquality(val elementEquality: Equality<ULong>) : Equality<KoneULongArray> {
    override fun KoneULongArray.equalsTo(other: KoneULongArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneULongArray.Companion.equality(elementEquality: Equality<ULong> = Equality.defaultFor()): Equality<KoneULongArray> =
    KoneULongArrayEquality(elementEquality)

internal class KoneMutableULongArrayEquality(val elementEquality: Equality<ULong>) : Equality<KoneMutableULongArray> {
    override fun KoneMutableULongArray.equalsTo(other: KoneMutableULongArray): Boolean {
        if (this.size != other.size) return false
        for (index in 0u ..< this.size) if (elementEquality { this[index] neq other[index] }) return false
        return true
    }
}

public fun KoneMutableULongArray.Companion.equality(elementEquality: Equality<ULong> = Equality.defaultFor()): Equality<KoneMutableULongArray> =
    KoneMutableULongArrayEquality(elementEquality)