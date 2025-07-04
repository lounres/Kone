/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array.relations

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.contentEquals
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultEquality


// TODO: Add Equalities for other types of Kone arrays

internal class KoneUIntArrayEquality(val elementEquality: Equality<UInt>) : Equality<KoneUIntArray> {
    override fun KoneUIntArray.equalsTo(other: KoneUIntArray): Boolean =
        this contentEquals other
}

public fun KoneUIntArray.Companion.equality(elementEquality: Equality<UInt> = defaultEquality()): Equality<KoneUIntArray> =
    KoneUIntArrayEquality(elementEquality)