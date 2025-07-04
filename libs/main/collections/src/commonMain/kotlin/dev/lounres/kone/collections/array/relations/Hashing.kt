/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.array.relations

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.array.contentHashCode
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.defaultHashing


// TODO: Add Hashings for other types of Kone arrays

internal class KoneUIntArrayHashing(val elementHashing: Hashing<UInt>) : Hashing<KoneUIntArray> {
    override fun KoneUIntArray.hash(): Int = contentHashCode()
}

public fun KoneUIntArray.Companion.hashing(elementHashing: Hashing<UInt> = defaultHashing()): Hashing<KoneUIntArray> =
    KoneUIntArrayHashing(elementHashing)