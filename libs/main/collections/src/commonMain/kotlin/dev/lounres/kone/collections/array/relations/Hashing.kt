/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array.relations

import dev.lounres.kone.collections.array.*
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.hash


internal class KoneMutableArrayHashing<Element>(val elementHashing: Hashing<Element>) : Hashing<KoneMutableArray<Element>> {
    override fun KoneMutableArray<Element>.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun <Element> KoneMutableArray.Companion.hashing(elementHashing: Hashing<Element> = Hashing.defaultFor()): Hashing<KoneMutableArray<Element>> =
    KoneMutableArrayHashing(elementHashing)

internal class KoneArrayHashing<Element>(val elementHashing: Hashing<Element>) : Hashing<KoneArray<Element>> {
    override fun KoneArray<Element>.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun <Element> KoneArray.Companion.hashing(elementHashing: Hashing<Element> = Hashing.defaultFor()): Hashing<KoneArray<Element>> =
    KoneArrayHashing(elementHashing)

internal class KoneMutableBooleanArrayHashing(val elementHashing: Hashing<Boolean>) : Hashing<KoneMutableBooleanArray> {
    override fun KoneMutableBooleanArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableBooleanArray.Companion.hashing(elementHashing: Hashing<Boolean> = Hashing.defaultFor()): Hashing<KoneMutableBooleanArray> =
    KoneMutableBooleanArrayHashing(elementHashing)

internal class KoneBooleanArrayHashing(val elementHashing: Hashing<Boolean>) : Hashing<KoneBooleanArray> {
    override fun KoneBooleanArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneBooleanArray.Companion.hashing(elementHashing: Hashing<Boolean> = Hashing.defaultFor()): Hashing<KoneBooleanArray> =
    KoneBooleanArrayHashing(elementHashing)

internal class KoneMutableCharArrayHashing(val elementHashing: Hashing<Char>) : Hashing<KoneMutableCharArray> {
    override fun KoneMutableCharArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableCharArray.Companion.hashing(elementHashing: Hashing<Char> = Hashing.defaultFor()): Hashing<KoneMutableCharArray> =
    KoneMutableCharArrayHashing(elementHashing)

internal class KoneCharArrayHashing(val elementHashing: Hashing<Char>) : Hashing<KoneCharArray> {
    override fun KoneCharArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneCharArray.Companion.hashing(elementHashing: Hashing<Char> = Hashing.defaultFor()): Hashing<KoneCharArray> =
    KoneCharArrayHashing(elementHashing)

internal class KoneMutableByteArrayHashing(val elementHashing: Hashing<Byte>) : Hashing<KoneMutableByteArray> {
    override fun KoneMutableByteArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableByteArray.Companion.hashing(elementHashing: Hashing<Byte> = Hashing.defaultFor()): Hashing<KoneMutableByteArray> =
    KoneMutableByteArrayHashing(elementHashing)

internal class KoneByteArrayHashing(val elementHashing: Hashing<Byte>) : Hashing<KoneByteArray> {
    override fun KoneByteArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneByteArray.Companion.hashing(elementHashing: Hashing<Byte> = Hashing.defaultFor()): Hashing<KoneByteArray> =
    KoneByteArrayHashing(elementHashing)

internal class KoneMutableIntArrayHashing(val elementHashing: Hashing<Int>) : Hashing<KoneMutableIntArray> {
    override fun KoneMutableIntArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableIntArray.Companion.hashing(elementHashing: Hashing<Int> = Hashing.defaultFor()): Hashing<KoneMutableIntArray> =
    KoneMutableIntArrayHashing(elementHashing)

internal class KoneIntArrayHashing(val elementHashing: Hashing<Int>) : Hashing<KoneIntArray> {
    override fun KoneIntArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneIntArray.Companion.hashing(elementHashing: Hashing<Int> = Hashing.defaultFor()): Hashing<KoneIntArray> =
    KoneIntArrayHashing(elementHashing)

internal class KoneMutableLongArrayHashing(val elementHashing: Hashing<Long>) : Hashing<KoneMutableLongArray> {
    override fun KoneMutableLongArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableLongArray.Companion.hashing(elementHashing: Hashing<Long> = Hashing.defaultFor()): Hashing<KoneMutableLongArray> =
    KoneMutableLongArrayHashing(elementHashing)

internal class KoneLongArrayHashing(val elementHashing: Hashing<Long>) : Hashing<KoneLongArray> {
    override fun KoneLongArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneLongArray.Companion.hashing(elementHashing: Hashing<Long> = Hashing.defaultFor()): Hashing<KoneLongArray> =
    KoneLongArrayHashing(elementHashing)

internal class KoneMutableFloatArrayHashing(val elementHashing: Hashing<Float>) : Hashing<KoneMutableFloatArray> {
    override fun KoneMutableFloatArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableFloatArray.Companion.hashing(elementHashing: Hashing<Float> = Hashing.defaultFor()): Hashing<KoneMutableFloatArray> =
    KoneMutableFloatArrayHashing(elementHashing)

internal class KoneFloatArrayHashing(val elementHashing: Hashing<Float>) : Hashing<KoneFloatArray> {
    override fun KoneFloatArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneFloatArray.Companion.hashing(elementHashing: Hashing<Float> = Hashing.defaultFor()): Hashing<KoneFloatArray> =
    KoneFloatArrayHashing(elementHashing)

internal class KoneMutableDoubleArrayHashing(val elementHashing: Hashing<Double>) : Hashing<KoneMutableDoubleArray> {
    override fun KoneMutableDoubleArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableDoubleArray.Companion.hashing(elementHashing: Hashing<Double> = Hashing.defaultFor()): Hashing<KoneMutableDoubleArray> =
    KoneMutableDoubleArrayHashing(elementHashing)

internal class KoneDoubleArrayHashing(val elementHashing: Hashing<Double>) : Hashing<KoneDoubleArray> {
    override fun KoneDoubleArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneDoubleArray.Companion.hashing(elementHashing: Hashing<Double> = Hashing.defaultFor()): Hashing<KoneDoubleArray> =
    KoneDoubleArrayHashing(elementHashing)

internal class KoneMutableUByteArrayHashing(val elementHashing: Hashing<UByte>) : Hashing<KoneMutableUByteArray> {
    override fun KoneMutableUByteArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableUByteArray.Companion.hashing(elementHashing: Hashing<UByte> = Hashing.defaultFor()): Hashing<KoneMutableUByteArray> =
    KoneMutableUByteArrayHashing(elementHashing)

internal class KoneUByteArrayHashing(val elementHashing: Hashing<UByte>) : Hashing<KoneUByteArray> {
    override fun KoneUByteArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneUByteArray.Companion.hashing(elementHashing: Hashing<UByte> = Hashing.defaultFor()): Hashing<KoneUByteArray> =
    KoneUByteArrayHashing(elementHashing)

internal class KoneMutableUShortArrayHashing(val elementHashing: Hashing<UShort>) : Hashing<KoneMutableUShortArray> {
    override fun KoneMutableUShortArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableUShortArray.Companion.hashing(elementHashing: Hashing<UShort> = Hashing.defaultFor()): Hashing<KoneMutableUShortArray> =
    KoneMutableUShortArrayHashing(elementHashing)

internal class KoneUShortArrayHashing(val elementHashing: Hashing<UShort>) : Hashing<KoneUShortArray> {
    override fun KoneUShortArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneUShortArray.Companion.hashing(elementHashing: Hashing<UShort> = Hashing.defaultFor()): Hashing<KoneUShortArray> =
    KoneUShortArrayHashing(elementHashing)

internal class KoneMutableUIntArrayHashing(val elementHashing: Hashing<UInt>) : Hashing<KoneMutableUIntArray> {
    override fun KoneMutableUIntArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableUIntArray.Companion.hashing(elementHashing: Hashing<UInt> = Hashing.defaultFor()): Hashing<KoneMutableUIntArray> =
    KoneMutableUIntArrayHashing(elementHashing)

internal class KoneUIntArrayHashing(val elementHashing: Hashing<UInt>) : Hashing<KoneUIntArray> {
    override fun KoneUIntArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneUIntArray.Companion.hashing(elementHashing: Hashing<UInt> = Hashing.defaultFor()): Hashing<KoneUIntArray> =
    KoneUIntArrayHashing(elementHashing)

internal class KoneMutableULongArrayHashing(val elementHashing: Hashing<ULong>) : Hashing<KoneMutableULongArray> {
    override fun KoneMutableULongArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneMutableULongArray.Companion.hashing(elementHashing: Hashing<ULong> = Hashing.defaultFor()): Hashing<KoneMutableULongArray> =
    KoneMutableULongArrayHashing(elementHashing)

internal class KoneULongArrayHashing(val elementHashing: Hashing<ULong>) : Hashing<KoneULongArray> {
    override fun KoneULongArray.hash(): Int {
        var hash = 1
        for (index in 0u ..< size) elementHashing {
            hash = 31 * hash + this[index].hash()
        }
        return hash
    }
}

public fun KoneULongArray.Companion.hashing(elementHashing: Hashing<ULong> = Hashing.defaultFor()): Hashing<KoneULongArray> =
    KoneULongArrayHashing(elementHashing)