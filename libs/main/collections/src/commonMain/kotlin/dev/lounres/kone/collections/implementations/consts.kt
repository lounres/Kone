/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.firstThat
import kotlin.math.ceil
import kotlin.math.floor


internal const val MAX_CAPACITY = 0b10000000000000000000000000000000u

@PublishedApi
internal /*const*/ val POWERS_OF_2: KoneUIntArray = KoneUIntArray(33u) { if (it == 0u) 0u else 1u shl (it.toInt() - 1) }

@PublishedApi
internal fun powerOf2GreaterOrEqualTo(size: UInt): UInt =
    if (size > MAX_CAPACITY) throw IllegalArgumentException("Kone collection implementations can not allocate array of size more than 2^31")
    else POWERS_OF_2.firstThat { it >= size }

@PublishedApi
internal fun powerOf2IndexGreaterOrEqualTo(size: UInt): UInt =
    if (size > MAX_CAPACITY) throw IllegalArgumentException("Kone collection implementations can not allocate array of size more than 2^31")
    else POWERS_OF_2.firstIndexThat { _, power -> power >= size }

public const val DEFAULT_HASH_TABLE_LOAD_FACTOR: Float = 0.75f

internal fun calculateHashTableCapacity(size: UInt, loadFactor: Float): UInt = ceil(size.toFloat() / loadFactor).toUInt()
internal fun calculateHashTableSize(capacity: UInt, loadFactor: Float): UInt = floor(capacity.toFloat() * loadFactor).toUInt()